const assert = require('node:assert/strict');
const { chromium } = require('playwright');

const [url] = process.argv.slice(2);
if (!url) throw Error('node check-booking-keyboard.cjs <адрес собранного клиента>');

async function check() {
  const browser = await chromium.launch({ executablePath: process.env.CHROMIUM_PATH,
    args: ['--use-angle=swiftshader', '--enable-unsafe-swiftshader'] });
  try {
    const page = await browser.newPage({ viewport: { width: 390, height: 844 }, colorScheme: 'light' });
    const session = await page.context().newCDPSession(page);
    await session.send('Accessibility.enable');

    async function waitForControl(name, present = true) {
      for (let attempt = 0; attempt < 45; attempt++) {
        const { nodes } = await session.send('Accessibility.getFullAXTree');
        const element = nodes.find(node => !node.ignored && node.name?.value === name &&
          ['tab', 'button'].includes(node.role?.value));
        if (Boolean(element) === present) return element;
        await page.waitForTimeout(100);
      }
      throw Error(present ? 'Контрол не появился: ' + name : 'Контрол остался видимым: ' + name);
    }

    async function bounds(name) {
      const node = await waitForControl(name);
      const { model } = await session.send('DOM.getBoxModel', { backendNodeId: node.backendDOMNodeId });
      const [left, top, right, , , bottom] = model.border;
      return { left, x: (left + right) / 2, y: (top + bottom) / 2 };
    }

    await page.goto(url);
    const appointments = await bounds('Запись');
    await page.mouse.click(appointments.x, appointments.y);
    await page.waitForTimeout(500);
    const book = await bounds('Записаться');

    async function bookingHasFocus() {
      const encoded = (await page.screenshot()).toString('base64');
      return page.evaluate(async ({ encoded, x, y }) => {
        const blob = await (await fetch('data:image/png;base64,' + encoded)).blob();
        const image = await createImageBitmap(blob);
        const context = new OffscreenCanvas(image.width, image.height).getContext('2d');
        context.drawImage(image, 0, 0);
        // The prototype puts a 3 px focus outline 2 px outside the button.
        for (let offset = 2; offset <= 5; offset++) {
          const [r, g, b] = context.getImageData(Math.round(x) - offset, Math.round(y), 1, 1).data;
          if (r === 73 && g === 127 && b === 204) return true;
        }
        return false;
      }, { encoded, x: book.left, y: book.y });
    }

    for (let attempt = 0; attempt < 16; attempt++) {
      await page.keyboard.press('Tab');
      await page.waitForTimeout(120);
      if (await bookingHasFocus()) break;
    }
    assert.ok(await bookingHasFocus(), 'Tab должен показать фокус на кнопке «Записаться»');
    await page.keyboard.press('Enter');
    await waitForControl('Перейти в каталог');
    await page.keyboard.press('Escape');
    await waitForControl('Перейти в каталог', false);
    await page.waitForTimeout(200);
    assert.ok(await bookingHasFocus(), 'Escape должен вернуть фокус на кнопку «Записаться»');
    await page.keyboard.press('Enter');
    await waitForControl('Перейти в каталог');
  } finally {
    await browser.close();
  }
}

check().catch(error => { console.error(error); process.exitCode = 1; });
