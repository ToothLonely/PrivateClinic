// Compare the compiled Wasm client with the accepted prototype. Run with the same Playwright as measure.cjs.
const { chromium } = require('playwright');
const assert = require('node:assert/strict');
const { mkdir } = require('node:fs/promises');
const path = require('node:path');
const { pathToFileURL } = require('node:url');
const { useApplicationViewport } = require('./measure.cjs');

async function compare(url, directory) {
  await mkdir(directory, { recursive: true });
  const browser = await chromium.launch({ executablePath: process.env.CHROMIUM_PATH,
    args: ['--use-angle=swiftshader', '--enable-unsafe-swiftshader'] });
  try {
    const app = await browser.newPage(), reference = await browser.newPage();
    const errors = [];
    app.on('pageerror', error => errors.push(error.message));
    const ax = await app.context().newCDPSession(app);
    await ax.send('Accessibility.enable');
    async function control(name, roles = ['tab', 'button', 'textbox', 'radio']) {
      // Compose exposes accessible controls even though the application is painted on a canvas.
      for (let attempt = 0; attempt < 60; attempt++) {
        assert.deepEqual(errors, []);
        const { nodes } = await ax.send('Accessibility.getFullAXTree');
        const node = nodes.find(n => !n.ignored && roles.includes(n.role?.value) &&
          (name instanceof RegExp ? name.test(n.name?.value || '') : n.name?.value === name));
        if (node?.backendDOMNodeId) {
          const box = await ax.send('DOM.getBoxModel', { backendNodeId: node.backendDOMNodeId }).catch(() => null);
          if (box) return box.model.border;
        }
        await app.waitForTimeout(150);
      }
      throw Error('В работающем клиенте отсутствует элемент «' + name + '»');
    }
    async function compareBounds(name, selector, { roles, axes } = {}) {
      const expected = await reference.locator(selector).boundingBox();
      const q = await control(name, roles);
      const actual = { x: q[0], y: q[1], width: q[4] - q[0], height: q[5] - q[1] };
      for (const key of axes || Object.keys(actual)) {
        assert.ok(Math.abs(actual[key] - expected[key]) <= 1,
          name + ' ' + key + ': ' + actual[key] + ' vs ' + expected[key]);
      }
      return { actual, expected };
    }
    async function click(name) {
      const q = await control(name);
      await app.mouse.click((q[0] + q[4]) / 2, (q[1] + q[5]) / 2, { delay: 35 });
    }
    async function openReference(screen) {
      const ref = pathToFileURL(path.join(__dirname, 'index.html'));
      ref.search = new URLSearchParams({ screen, example: 'empty', theme: 'system' }).toString();
      await reference.goto(ref.href);
      await useApplicationViewport(reference);
    }
    const textPosition = { roles: ['StaticText'], axes: ['x', 'y'] };
    const headingPosition = { roles: ['heading'], axes: ['x', 'y', 'height'] };
    for (const [width, height] of [[390, 844], [1280, 880]]) {
      await app.setViewportSize({ width, height });
      await reference.setViewportSize({ width, height });
      for (const theme of ['light', 'dark']) {
        await app.emulateMedia({ colorScheme: theme });
        await reference.emulateMedia({ colorScheme: theme });
        await app.goto(url);
        for (const [screen, label] of Object.entries({ catalog: 'Клиники', booking: 'Запись', profile: 'Профиль' })) {
          await click(label);
          // Wait for the destination to enter the accessibility tree and for its transition to finish.
          await control({ catalog: 'Название или адрес', booking: 'Записаться', profile: 'Личные данные' }[screen]);
          await app.mouse.move(0, height / 2);
          await app.waitForTimeout(450);
          const file = path.join(directory, width + '-' + theme + '-' + screen);
          await openReference(screen);
          const appPng = await app.screenshot({ path: file + '.png' });
          const refPng = await reference.screenshot({ path: file + '-reference.png' });
          const { actual, expected } = await compareBounds(label, 'nav [aria-current]');
          if (screen === 'booking') await compareBounds('Записаться', '.book');
          if (screen === 'catalog') {
            await compareBounds('Название или адрес', '.search');
            await compareBounds('Феникс Медика', '#results .row:first-child strong', textPosition);
            await compareBounds('DL Med', '#results .row:nth-child(2) strong', textPosition);
          }
          if (screen === 'profile') {
            await compareBounds('Анна', '.identity h1', headingPosition);
            await compareBounds('Личные данные', '.item:first-child');
            await compareBounds('Выйти', '.sign-out');
          }
          // Sample interiors, away from text and antialiased corners, at DPR 1.
          const locations = [[2, 2], [5, height - 4], [width / 2, height - (width < 680 ? 16 : 18)],
            [expected.x + 16, expected.y + expected.height / 2]];
          const samples = await reference.evaluate(async ({ images, locations }) => {
            return Promise.all(images.map(async encoded => {
              const image = await createImageBitmap(await (await fetch('data:image/png;base64,' + encoded)).blob());
              const canvas = new OffscreenCanvas(image.width, image.height);
              const ctx = canvas.getContext('2d');
              ctx.drawImage(image, 0, 0);
              return locations.map(([x, y]) => Array.from(ctx.getImageData(x, y, 1, 1).data));
            }));
          }, { images: [appPng, refPng].map(img => img.toString('base64')), locations });
          assert.deepEqual(samples[0], samples[1], file + ' — цвета шапки, фона, панели и выбранной вкладки');
          if (screen === 'booking') {
            await click('Записаться');
            await control('Перейти в каталог');
            await reference.locator('.book').click();
            await compareBounds('Перейти в каталог', 'dialog[open] .booking-color');
            await app.screenshot({ path: file + '-dialog.png' });
            await reference.screenshot({ path: file + '-dialog-reference.png' });
            await click('Перейти в каталог');
            await control('Название или адрес');
          }
          console.log(width, height, theme, screen, JSON.stringify(actual));
        }
        for (const [screen, tab, open, title] of [
          ['personal', 'Профиль', 'Личные данные', 'Личные данные'],
          ['saved', 'Профиль', /^Избранное/, 'Избранное'],
          ['history', 'Запись', 'История приёмов', 'История приёмов'],
          ['studies', 'Запись', 'Исследования', 'Исследования'],
          ['labs', 'Запись', 'Анализы', 'Анализы'],
        ]) {
          await click(tab);
          await click(open);
          await control(title, ['heading']);
          await app.mouse.move(0, height / 2);
          await app.waitForTimeout(450);
          await openReference(screen);
          const { actual } = await compareBounds('Назад', '.icon-back');
          await compareBounds(title, '.record-heading h1', headingPosition);
          if (screen === 'personal') {
            await compareBounds('Фамилия, имя, отчество', 'dt:first-child', textPosition);
            await compareBounds('Электронная почта', 'dt:nth-child(3)', textPosition);
            await control('anna@example.com', ['StaticText']);
          } else if (screen === 'saved') {
            await compareBounds('Стомион', '.row strong', textPosition);
          } else {
            await compareBounds('Функция появится позже.', '.later', textPosition);
          }
          const file = path.join(directory, width + '-' + theme + '-' + screen);
          await app.screenshot({ path: file + '.png' });
          await reference.screenshot({ path: file + '-reference.png' });
          await click('Назад');
          await control(open);
          console.log(width, height, theme, screen, JSON.stringify(actual));
        }
      }
    }
    assert.deepEqual(errors, []);
  } finally { await browser.close(); }
}

const [url, directory] = process.argv.slice(2);
if (!url || !directory) throw Error('node check-shell.cjs <адрес собранного клиента> <папка для снимков>');
compare(url, directory).catch(error => { console.error(error); process.exitCode = 1; });
