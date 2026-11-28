// Prints the resolved design for an exact application viewport; shared Playwright setup with export.cjs.
const { chromium } = require('playwright');
const { parseArgs } = require('node:util');
const { pathToFileURL } = require('node:url');
const path = require('node:path');
async function useApplicationViewport(page) {
  // Only remove the gallery viewer and simulated OS frame. Preserve all application CSS.
  await page.addStyleTag({ content: 'body,main{margin:0;padding:0;max-width:none;display:block}aside,.status{display:none}#phone{width:100%;height:100dvh;min-height:0;border-radius:0;box-shadow:none}' });
  await page.evaluate(async () => {
    dispatchEvent(new Event('resize'));
    await document.fonts.ready;
    await new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)));
  });
}

async function main() {
  const { values } = parseArgs({ options: {
    screen: { type: 'string', default: 'catalog' }, theme: { type: 'string', default: 'light' },
    platform: { type: 'string', default: 'web' },
    width: { type: 'string', default: '390' }, height: { type: 'string', default: '844' },
    selector: { type: 'string', default: '#phone, #phone *' }, menu: { type: 'string' },
    example: { type: 'string' }, png: { type: 'string' },
    click: { type: 'string', multiple: true, default: [] }, focus: { type: 'string' },
    query: { type: 'string' },
  } });
  const width = Number(values.width), height = Number(values.height);
  if (![width, height].every(n => Number.isInteger(n) && n > 0)) throw Error('Expected positive integer viewport dimensions');
  if (!['light', 'dark'].includes(values.theme)) throw Error('Use light or dark');
  if (!['web', 'mobile'].includes(values.platform)) throw Error('Use web or mobile');
  const browser = await chromium.launch({ executablePath: process.env.CHROMIUM_PATH });
  try {
    const page = await browser.newPage({ viewport: { width, height }, deviceScaleFactor: 1, reducedMotion: 'reduce' });
    const url = pathToFileURL(path.join(__dirname, 'index.html'));
    for (const key of ['screen', 'theme', 'platform', 'menu', 'example']) if (values[key]) url.searchParams.set(key, values[key]);
    await page.goto(url.href);
    const screen = await page.locator('#phone').getAttribute('data-screen');
    if (screen !== ({ unavailable: 'booking', cancel: 'confirmed' }[values.screen] ?? values.screen)) {
      throw Error('Unknown screen: ' + values.screen);
    }
    await useApplicationViewport(page);
    if (values.query !== undefined) await page.locator('#query').fill(values.query);
    for (const selector of values.click) await page.locator(selector).click();
    if (values.focus) await page.locator(values.focus).focus();
    await page.evaluate(() => new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve))));
    const elements = await page.locator(values.selector).evaluateAll(nodes => {
      const properties = ('display position boxSizing padding margin gap rowGap columnGap alignItems justifyContent width height minWidth maxWidth minHeight maxHeight aspectRatio ' +
        'gridTemplateColumns flexDirection overflowX overflowY scrollSnapType opacity color backgroundColor backgroundImage ' +
        'borderTop borderRight borderBottom borderLeft borderRadius boxShadow outline outlineOffset fontFamily fontSize ' +
        'fontWeight lineHeight letterSpacing textAlign fill stroke strokeWidth strokeLinecap strokeLinejoin transform transition outlineStyle ' +
        'overflowWrap whiteSpace textOverflow scrollSnapAlign cursor pointerEvents zIndex top right bottom left').split(' ');
      function styles(css) { return Object.fromEntries(properties.map(p => [p, css[p]])); }
      function selector(node) {
        if (node.id) return '#' + CSS.escape(node.id);
        if (!node.parentElement) return node.localName;
        const parent = node.parentElement;
        return selector(parent) + ' > ' + node.localName + ':nth-child(' + (Array.from(parent.children).indexOf(node) + 1) + ')';
      }
      return nodes.filter(node => node.getClientRects().length && node.checkVisibility()).map(node => {
        const rect = node.getBoundingClientRect();
        const pseudo = {};
        for (const name of ['::before', '::after', '::placeholder', '::backdrop']) {
          const css = getComputedStyle(node, name);
          const applicable = (name === '::placeholder' && node.hasAttribute('placeholder')) ||
            (name === '::backdrop' && node.matches('dialog[open]')) ||
            (['::before', '::after'].includes(name) && !['none', 'normal'].includes(css.content));
          if (applicable) pseudo[name] = { content: css.content, ...styles(css) };
        }
        return { selector: selector(node), tag: node.localName, classes: node.getAttribute('class'),
          label: node.getAttribute('aria-label') ?? (node.childElementCount ? undefined : node.textContent),
          bounds: { x: rect.x, y: rect.y, width: rect.width, height: rect.height }, ...styles(getComputedStyle(node)), pseudo };
      });
    });
    if (!elements.length) throw Error('No visible element matches ' + values.selector);
    if (values.png) await page.locator('#phone').screenshot({ path: values.png });
    console.log(JSON.stringify({ screen: await page.locator('#phone').getAttribute('data-screen'), requestedScreen: values.screen, theme: await page.locator('#phone').getAttribute('data-theme'), platform: values.platform, width, height, dpr: 1, unit: 'CSS px', elements }, null, 2));
  } finally { await browser.close(); }
}
module.exports = { useApplicationViewport };
if (require.main === module) main().catch(error => { console.error(error.message); process.exitCode = 1; });
