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
      await reference.bringToFront();
      await reference.goto(ref.href);
      await useApplicationViewport(reference);
    }
    async function capture(page, path) {
      await page.bringToFront();
      await page.evaluate(() => new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve))));
      return page.screenshot({ path });
    }
    async function measurePixels(appPng, refPng, { locations = [], areas = [] }) {
      return reference.evaluate(async ({ images, locations, areas }) => {
        return Promise.all(images.map(async encoded => {
          const image = await createImageBitmap(await (await fetch('data:image/png;base64,' + encoded)).blob());
          const canvas = new OffscreenCanvas(image.width, image.height);
          const ctx = canvas.getContext('2d');
          ctx.drawImage(image, 0, 0);
          const samples = locations.map(([x, y]) => Array.from(ctx.getImageData(x, y, 1, 1).data));
          const ink = areas.map(({ left, top, width, height, color, tolerance = 0 }) => {
            const { data } = ctx.getImageData(left, top, width, height);
            let minX = width, minY = height, maxX = -1, maxY = -1;
            for (let y = 0; y < height; y++) for (let x = 0; x < width; x++) {
              const i = (y * width + x) * 4;
              if (color.every((channel, offset) => Math.abs(channel - data[i + offset]) <= tolerance)) {
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
              }
            }
            return maxX < 0 ? null : [left + minX, top + minY, left + maxX, top + maxY];
          });
          return { samples, ink };
        }));
      }, { images: [appPng, refPng].map(img => img.toString('base64')), locations, areas });
    }
    async function compareWideHeader(width, screen, appPng, refPng) {
      const expected = await reference.locator('header').evaluate(el => {
        const { height, borderRadius } = getComputedStyle(el);
        return { height, borderRadius };
      });
      assert.deepEqual(expected, { height: '84px', borderRadius: '0px 0px 24px 24px' });
      if (screen === 'catalog') {
        await compareBounds('Название или адрес', '.search');
      } else {
        const { nodes } = await ax.send('Accessibility.getFullAXTree');
        assert.ok(!nodes.some(node => !node.ignored && node.role?.value === 'textbox' &&
          node.name?.value === 'Название или адрес'), 'Поиск должен быть только у каталога');
      }
      const [brand, city] = await Promise.all([
        reference.locator('.brand>b').boundingBox(), reference.locator('#city').boundingBox(),
      ]);
      const color = await reference.locator('header').evaluate(el => getComputedStyle(el).color.match(/\d+/g).map(Number));
      const left = Math.floor(brand.x) - 4;
      const split = left + (width < 800 ? 41 : 53);
      const right = Math.floor(city.x) - 4;
      const areas = [
        { left, width: split - left },
        { left: split, width: Math.floor(brand.x + brand.width) - split },
        { left: right, width: Math.ceil(city.width) + 8 },
      ].map(area => ({ ...area, top: 14, height: 55, color, tolerance: 20 }));
      const [{ samples: appEdges, ink: appInk }, { samples: refEdges, ink: refInk }] = await measurePixels(appPng, refPng, {
        locations: [[0, 1], [width - 1, 1], [0, 83], [width - 1, 83], [width / 2, 83], [width / 2, 84],
          ...Array.from({ length: 24 }, (_, x) => [x, 80]),
          ...Array.from({ length: 24 }, (_, x) => [width - 1 - x, 80])],
        areas,
      });
      assert.deepEqual(appEdges.slice(0, 6), refEdges.slice(0, 6), 'Границы шапки ' + width + ' ' + screen);
      for (const start of [6, 30]) {
        const solidEdge = (samples) => samples.slice(start, start + 24)
          .findIndex(pixel => pixel.every((channel, i) => channel === samples[0][i]));
        assert.ok(solidEdge(appEdges) >= 0 && Math.abs(solidEdge(appEdges) - solidEdge(refEdges)) <= 1,
          'Радиус шапки ' + width + ' ' + screen);
      }
      for (const [index, label] of ['ЧП', 'Частный приём', 'Ставрополь'].entries()) {
        const actual = appInk[index], referenceInk = refInk[index];
        assert.ok(actual && referenceInk, label + ' должен быть виден в шапке');
        const middle = bounds => (bounds[1] + bounds[3]) / 2;
        assert.ok(Math.abs(middle(actual) - middle(referenceInk)) <= 2,
          label + ' по вертикали: ' + middle(actual) + ' vs ' + middle(referenceInk));
      }
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
          const appPng = await capture(app, file + '.png');
          const refPng = await capture(reference, file + '-reference.png');
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
            if (width === 1280) {
              const counter = reference.locator('.item[data-go=saved] small');
              const style = await counter.evaluate(el => {
                const { fontSize, fontWeight, color } = getComputedStyle(el);
                return { fontSize, fontWeight, color };
              });
              assert.equal(style.fontSize, '13.3333px');
              assert.equal(style.fontWeight, '620');
              const rect = await counter.boundingBox();
              const [{ ink: [actual] }, { ink: [expected] }] = await measurePixels(appPng, refPng, {
                areas: [{ left: Math.floor(rect.x) - 2, top: Math.floor(rect.y) - 2,
                  width: Math.ceil(rect.width) + 4, height: Math.ceil(rect.height) + 4,
                  color: style.color.match(/\d+/g).map(Number), tolerance: 36 }],
              });
              assert.ok(actual && expected && actual.every((value, index) => Math.abs(value - expected[index]) <= 1),
                'Размер и положение счётчика: ' + actual + ' vs ' + expected);
              console.log(width, height, theme, 'favorites-counter', JSON.stringify({ actual, expected }));
            }
          }
          // Sample interiors, away from text and antialiased corners, at DPR 1.
          const locations = [[2, 2], [5, height - 4], [width / 2, height - (width < 680 ? 16 : 18)],
            [expected.x + 16, expected.y + expected.height / 2]];
          const [{ samples: actualColors }, { samples: expectedColors }] = await measurePixels(appPng, refPng, { locations });
          assert.deepEqual(actualColors, expectedColors, file + ' — цвета шапки, фона, панели и выбранной вкладки');
          if (width >= 680) await compareWideHeader(width, screen, appPng, refPng);
          if (screen === 'booking') {
            await click('Записаться');
            await control('Перейти в каталог');
            await openReference('unavailable');
            await compareBounds('Перейти в каталог', 'dialog[open] .booking-color');
            const appDialog = await capture(app, file + '-dialog.png');
            const refDialog = await capture(reference, file + '-dialog-reference.png');
            if (width === 1280) {
              const points = [545, 555, 560, 570].map(y => [width / 2, y]);
              const [{ samples: actualShadow }, { samples: expectedShadow }] =
                await measurePixels(appDialog, refDialog, { locations: points });
              actualShadow.forEach((color, point) => color.forEach((channel, i) => {
                assert.ok(Math.abs(channel - expectedShadow[point][i]) <= 2,
                  'Тень диалога ' + theme + ' при ' + points[point] + ': ' + color + ' vs ' + expectedShadow[point]);
              }));
              console.log(width, height, theme, 'dialog-shadow', JSON.stringify({ actualShadow, expectedShadow }));
            }
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
          await capture(app, file + '.png');
          await capture(reference, file + '-reference.png');
          await click('Назад');
          await control(open);
          console.log(width, height, theme, screen, JSON.stringify(actual));
        }
      }
    }
    await app.setViewportSize({ width: 680, height: 880 });
    await reference.setViewportSize({ width: 680, height: 880 });
    for (const theme of ['light', 'dark']) {
      await app.emulateMedia({ colorScheme: theme });
      await reference.emulateMedia({ colorScheme: theme });
      await app.goto(url);
      for (const [screen, label, ready] of [
        ['catalog', 'Клиники', 'Название или адрес'],
        ['booking', 'Запись', 'Записаться'],
        ['profile', 'Профиль', 'Личные данные'],
      ]) {
        if (screen !== 'catalog') await click(label);
        await control(ready);
        await app.waitForTimeout(450);
        await openReference(screen);
        const file = path.join(directory, '680-' + theme + '-' + screen);
        await compareWideHeader(680, screen, await capture(app, file + '.png'),
          await capture(reference, file + '-reference.png'));
        console.log(680, 880, theme, screen, 'header OK');
      }
    }
    // A taller compact viewport exposes all cards without changing the catalog layout.
    await app.setViewportSize({ width: 390, height: 1550 });
    await reference.setViewportSize({ width: 390, height: 1550 });
    for (const theme of ['light', 'dark']) {
      await app.emulateMedia({ colorScheme: theme });
      await reference.emulateMedia({ colorScheme: theme });
      await app.goto(url);
      await openReference('catalog');
      const names = await reference.locator('#results .row strong').allTextContents();
      assert.equal(names.length, 12);
      const offsets = [];
      for (const [index, name] of names.entries()) {
        const selector = '#results .row:nth-child(' + (index + 1) + ') strong';
        const { actual, expected } = await compareBounds(name, selector, textPosition);
        offsets.push({ name, y: actual.y, expectedY: expected.y });
      }
      const file = path.join(directory, '390-' + theme + '-catalog-full');
      await capture(app, file + '.png');
      await capture(reference, file + '-reference.png');
      console.log(390, 1550, theme, 'catalog-rows', JSON.stringify(offsets));
    }
    assert.deepEqual(errors, []);
  } finally { await browser.close(); }
}

const [url, directory] = process.argv.slice(2);
if (!url || !directory) throw Error('node check-shell.cjs <адрес собранного клиента> <папка для снимков>');
compare(url, directory).catch(error => { console.error(error); process.exitCode = 1; });
