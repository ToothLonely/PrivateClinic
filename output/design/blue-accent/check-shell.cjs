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
    async function control(name) {
      // Compose exposes accessible controls even though the application is painted on a canvas.
      for (let attempt = 0; attempt < 60; attempt++) {
        assert.deepEqual(errors, []);
        const { nodes } = await ax.send('Accessibility.getFullAXTree');
        const node = nodes.find(n => !n.ignored && n.name?.value === name &&
          ['tab', 'button', 'textbox', 'radio'].includes(n.role?.value));
        if (node?.backendDOMNodeId) {
          const { model } = await ax.send('DOM.getBoxModel', { backendNodeId: node.backendDOMNodeId });
          return model.border;
        }
        await app.waitForTimeout(150);
      }
      throw Error('В работающем клиенте отсутствует элемент «' + name + '»');
    }
    async function compareBounds(name, selector) {
      const expected = await reference.locator(selector).boundingBox();
      const q = await control(name);
      const actual = { x: q[0], y: q[1], width: q[4] - q[0], height: q[5] - q[1] };
      for (const key of Object.keys(actual)) {
        assert.ok(Math.abs(actual[key] - expected[key]) <= 1,
          name + ' ' + key + ': ' + actual[key] + ' vs ' + expected[key]);
      }
      return { actual, expected };
    }
    async function click(name) {
      const q = await control(name);
      await app.mouse.click((q[0] + q[4]) / 2, (q[1] + q[5]) / 2, { delay: 35 });
    }
    for (const [width, height] of [[390, 844], [1280, 880]]) {
      await app.setViewportSize({ width, height });
      await reference.setViewportSize({ width, height });
      for (const theme of ['light', 'dark']) {
        await app.emulateMedia({ colorScheme: theme });
        await app.goto(url);
        for (const [screen, label] of Object.entries({ catalog: 'Клиники', booking: 'Запись', profile: 'Профиль' })) {
          await click(label);
          // Wait for the destination to enter the accessibility tree and for its transition to finish.
          await control({ catalog: 'Название или адрес', booking: 'Записаться', profile: 'Личные данные' }[screen]);
          await app.mouse.move(0, height / 2);
          await app.waitForTimeout(450);
          const file = path.join(directory, width + '-' + theme + '-' + screen);
          const ref = pathToFileURL(path.join(__dirname, 'index.html'));
          ref.search = new URLSearchParams({ screen, example: 'empty', theme }).toString();
          await reference.goto(ref.href);
          await useApplicationViewport(reference);
          const appPng = await app.screenshot({ path: file + '.png' });
          const refPng = await reference.screenshot({ path: file + '-reference.png' });
          const { actual, expected } = await compareBounds(label, 'nav [aria-current]');
          if (screen === 'booking') await compareBounds('Записаться', '.book');
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
            await app.screenshot({ path: file + '-dialog.png' });
            await click('Перейти в каталог');
            await control('Название или адрес');
          }
          if (screen === 'profile') {
            await click('Личные данные');
            await click('Назад');
            await control('Личные данные');
          }
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
