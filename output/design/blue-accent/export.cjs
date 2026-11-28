// Run with Node and Playwright. The overview embeds these exact screenshots.
const { chromium } = require('playwright');
const { writeFile, mkdir } = require('node:fs/promises');
const path = require('node:path');
const { pathToFileURL } = require('node:url');
const assert = require('node:assert/strict');
const { useApplicationViewport } = require('./measure.cjs');

async function exportScreens() {
  const browser = await chromium.launch({headless:true,executablePath:process.env.CHROMIUM_PATH});
  try {
    const page = await browser.newPage({viewport:{width:920,height:880},deviceScaleFactor:1.5});
    const errors=[];
    page.on('pageerror', e => errors.push(e.message));
    const url=pathToFileURL(path.join(__dirname,'index.html')).href;
    await page.goto(url+'?layout=phone');
    const phone=page.locator('#phone');
    const screens=await page.locator('#page option').evaluateAll(options=>options.map(o=>[o.value,o.textContent]));
    const previews=screens.map(([key,label])=>({key,label,route:'screen='+key+(key==='booking'?'&example=multiple':'')}));
    previews.splice(1,0,{key:'catalog-types',label:'Клиники · выбор типа',route:'screen=catalog&menu=type'});
    previews.push({key:'booking-upcoming',label:'Текущие записи · один приём',route:'screen=booking&example=upcoming'});
    previews.push({key:'booking-empty',label:'Текущие записи · пустой список',route:'screen=booking&example=empty'});
    previews.push({key:'cancel',label:'Подтверждение отмены',route:'screen=cancel'});
    previews.push({key:'unavailable',label:'Диалог · функция сейчас недоступна',route:'screen=unavailable'});
    const mobileCount=previews.length;
    for(const preview of previews.slice()){
      if(['booking-upcoming','booking-empty','catalog-types'].includes(preview.key)) continue;
      previews.push({...preview,key:'wide-'+preview.key,route:preview.key==='booking'?'screen=booking':preview.route,wide:true});
    }
    const wideEnd=previews.length;
    for(const preview of previews.slice(0,mobileCount)){
      if(['catalog','profile','booking-empty','unavailable'].includes(preview.key)) {
        previews.push({...preview,key:'compact-web-'+preview.key});
      }
    }
    const rows=[];
    await mkdir(path.join(__dirname,'screens'),{recursive:true});
    for(const [index,{key,label,route,wide=false}] of previews.entries()){
      await page.setViewportSize({width:wide?1280:920,height:880});
      const platform=index<mobileCount?'mobile':'web';
      const query=route+'&platform='+platform+(wide?'':'&layout=phone');
      const pictures=[];
      for(const [theme,caption] of [['light','Светлая'],['dark','Тёмная']]){
        await page.goto(url+'?'+query+'&theme='+theme);
        const mainTab=['catalog','catalog-types','profile','booking','unavailable'].includes(new URLSearchParams(route).get('screen'));
        assert.equal(await page.locator('#phone header').isVisible(),mainTab);
        assert.equal(await page.locator('#phone footer').isVisible(),mainTab);
        const filename='screens/'+key+'-'+theme+'.png';
        await phone.screenshot({path:path.join(__dirname,filename)});
        pictures.push('<figure><a href="'+filename+'"><img src="'+filename+'" loading="lazy" alt="'+label+' · '+caption+'"></a><figcaption><a href="index.html?'+query.replaceAll('&','&amp;')+'&amp;theme='+theme+'">'+caption+' · открыть макет</a></figcaption></figure>');
      }
      rows.push('<section><h2>'+label+'</h2><div>'+pictures.join('')+'</div></section>');
    }
    await writeFile(path.join(__dirname,'overview.html'),'<!doctype html><html lang="ru"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Синий акцент · Экраны</title><style>*{box-sizing:border-box}body{margin:0;background:#eeece7;color:#182336;font:16px system-ui}main{max-width:780px;margin:auto;padding:24px 14px}h1{font-size:30px}section{margin:32px 0 48px}section div{display:grid;grid-template-columns:1fr 1fr;gap:16px}figure{margin:0}img{display:block;width:100%;border-radius:18px}figcaption{margin-top:6px;font-size:14px}a{color:#254d75}</style><main><a href="index.html">Открыть прототип</a><h1>Синий акцент</h1><a href="#mobile">Android и iOS</a> · <a href="#compact-web">Компактный веб</a> · <a href="#wide">Широкий веб</a><h2 id="mobile">Android и iOS · телефон</h2>'+rows.slice(0,mobileCount).join('')+'<h2 id="compact-web">Компактный веб</h2>'+rows.slice(wideEnd).join('')+'<h2 id="wide">Широкий веб</h2>'+rows.slice(mobileCount,wideEnd).join('')+'</main></html>');
    for(const width of [680,1280]){
      await page.setViewportSize({width,height:880});
      for(const theme of ['light','dark']){
        for(const screen of ['catalog','booking','profile']){
          await page.goto(url+'?platform=web&screen='+screen+'&theme='+theme);
          await useApplicationViewport(page);
          const header=await phone.locator('header').evaluate(el=>{
            const {borderRadius}=getComputedStyle(el);
            const center=box=>box.top+box.height/2;
            const bounds=el.getBoundingClientRect();
            return {height:bounds.height,borderRadius,brand:center(el.querySelector('.brand>b').getBoundingClientRect()),
              city:center(el.querySelector('#city').getBoundingClientRect())};
          });
          assert.deepEqual(header,{height:84,borderRadius:'0px 0px 24px 24px',brand:42,city:42});
          assert.equal(await page.getByRole('searchbox').isVisible(),screen==='catalog');
        }
      }
    }
    await page.setViewportSize({width:390,height:844});
    const scroller=page.locator('#scroll');
    for(const platform of ['web','mobile']){
      await page.goto(url+'?platform='+platform);
      await useApplicationViewport(page);
      assert.equal(await phone.locator('.brand>b').evaluate(el=>getComputedStyle(el).letterSpacing),
        platform==='web'?'-2px':'-1px');
    }
    assert.equal(await page.getByRole('searchbox').isVisible(),true);
    await scroller.hover();
    await page.mouse.wheel(0,270);
    await page.waitForFunction(()=>getComputedStyle(document.getElementById('search-box')).opacity==='0');
    const offset=await scroller.evaluate(el=>el.scrollTop);
    for(const leave of [
      page.locator('nav [data-go=profile]'),
      page.locator('nav [data-go=booking]'),
      page.locator('#results .row').nth(3),
    ]){
      await leave.click();
      await page.locator('nav [data-go=catalog]:visible, [data-go=catalog].icon-back:visible').click();
      assert.equal(await scroller.evaluate(el=>el.scrollTop),offset);
      assert.equal(await page.locator('#search-box').evaluate(el=>getComputedStyle(el).opacity),'0');
    }
    await scroller.hover();
    await page.mouse.wheel(0,-35);
    await page.waitForFunction(()=>getComputedStyle(document.getElementById('search-box')).opacity==='1');
    await page.setViewportSize({width:920,height:880});
    await page.goto(url+'?layout=phone');
    const scroll=page.locator('#scroll');
    for(const theme of ['light','dark']){
      await page.selectOption('#theme',theme);
      await scroll.hover();
      await page.mouse.wheel(0,270);
      await page.waitForFunction(()=>getComputedStyle(document.getElementById('search-box')).opacity==='0');
      await page.mouse.wheel(0,-160);
      await page.waitForFunction(()=>getComputedStyle(document.getElementById('search-box')).opacity==='1');
      await scroll.evaluate(el=>{el.scrollTop=0;});
    }
    await page.getByRole('searchbox').fill('Стомион');
    assert.equal(await page.locator('.row').count(),1);
    await page.locator('.row').click();
    assert.equal(await page.locator('h1').textContent(),'Стомион');
    await page.getByRole('button',{name:'Позвонить',exact:true}).click();
    assert.match(await page.locator('#message').textContent(),/набoр|набор/);
    await page.setViewportSize({width:360,height:720});
    await page.goto(url+'?layout=phone');
    assert(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));
    const typeMenu=page.locator('[data-filter="type"]');
    await typeMenu.locator('summary').click();
    const fullName='Специализированная клиника';
    const longOption=typeMenu.getByRole('button',{name:fullName,exact:true});
    assert(await longOption.evaluate(el=>el.scrollWidth<=el.clientWidth));
    await longOption.click();
    assert.equal(await typeMenu.locator('summary').innerText(),'Узкий профиль');
    assert.equal(await page.locator('#page').inputValue(),'catalog');
    await typeMenu.locator('summary').click();
    await typeMenu.getByRole('button',{name:'Стоматология',exact:true}).click();
    assert(await page.locator('.row').count()>0);
    assert(await page.locator('.row small').evaluateAll(rows=>rows.every(el=>el.textContent==='Стоматология')));
    await page.locator('[data-filter="radius"] summary').click();
    await page.getByRole('button',{name:'До 5 км',exact:true}).click();
    await page.getByRole('button',{name:'Разрешить',exact:true}).click();
    assert.equal(await page.locator('[data-filter="radius"] summary').innerText(),'До 5 км');
    await page.locator('.row').first().click();
    const favorite=page.locator('[data-save]');
    const before=await favorite.getAttribute('aria-pressed');
    await favorite.click();
    assert.notEqual(await favorite.getAttribute('aria-pressed'),before);
    await page.selectOption('#page','booking');
    await page.getByRole('button',{name:'Записаться',exact:true}).click();
    assert(await page.getByRole('dialog',{name:'Функция сейчас недоступна'}).isVisible());
    await page.getByRole('button',{name:'Перейти в каталог'}).click();
    assert.equal(await page.locator('#page').inputValue(),'catalog');
    await page.locator('nav').getByRole('button',{name:'Профиль'}).click();
    await page.getByRole('button',{name:'Тёмная',exact:true}).click();
    assert.equal(await phone.getAttribute('data-theme'),'dark');
    assert.equal(await page.locator('#page').inputValue(),'profile');
    await page.emulateMedia({colorScheme:'light'});
    await page.getByRole('button',{name:'Системная',exact:true}).click();
    assert.equal(await phone.getAttribute('data-theme'),'light');
    await page.emulateMedia({colorScheme:'dark'});
    await page.waitForFunction(()=>document.querySelector('#phone').dataset.theme==='dark');
    await page.selectOption('#page','login');
    await page.getByLabel('Электронная почта',{exact:true}).fill('test@example.com');
    await page.getByRole('button',{name:'Получить код'}).click();
    await page.getByLabel('Одноразовый код').fill('123456');
    await page.getByRole('button',{name:'Войти',exact:true}).click();
    await page.getByLabel('Фамилия',{exact:true}).fill('Лебедева');
    await page.getByLabel('Имя',{exact:true}).fill('Мария');
    await page.getByLabel('Отчество (при наличии)').fill('Игоревна');
    await page.getByRole('button',{name:'Продолжить'}).click();
    assert.equal(await page.locator('#page').inputValue(),'catalog');
    assert.equal(await page.locator('.permission').count(),0);
    await page.locator('nav').getByText('Профиль').click();
    assert.equal(await page.locator('#avatar').textContent(),'МЛ');
    assert.equal(await page.locator('#display-name').textContent(),'Мария');
    await page.getByRole('button',{name:'Личные данные'}).click();
    assert.equal(await page.locator('#personal-email').textContent(),'test@example.com');
    assert.equal(await page.locator('#full-name').textContent(),'Лебедева Мария Игоревна');
    assert.equal(await page.getByRole('button',{name:'Назад'}).innerText(),'');
    await page.selectOption('#page','slot');
    await page.getByRole('button',{name:'14:00',exact:true}).click();
    await page.getByRole('button',{name:'Выбрать'}).click();
    await page.getByRole('button',{name:'Назад'}).click();
    assert.match(await page.locator('.visit-slide').innerText(),/14:00/);
    for (const theme of ['light','dark']) {
      await page.selectOption('#theme',theme);
      const colors=await page.evaluate(()=>['header','nav','.book'].map(selector=>getComputedStyle(document.querySelector(selector)).backgroundColor));
      assert.equal(new Set(colors).size,1);
    }
    await page.goto(url+'?screen=booking&example=multiple');
    const button=name=>page.getByRole('button',{name,exact:true});
    const slides=page.locator('.visit-slide');
    assert.equal(await slides.count(),3);
    for(const section of ['История приёмов','Исследования','Анализы']){
      await button(section).click();
      assert.equal(await page.locator('h1').textContent(),section);
      await button('Назад').click();
      assert.equal(await page.locator('#page').inputValue(),'booking');
    }
    const track=page.locator('#visit-track');
    await track.hover();
    await page.mouse.wheel(await track.evaluate(el=>el.clientWidth),0);
    await page.locator('[data-slide="1"][aria-pressed=true]').waitFor();
    await slides.nth(1).click();
    assert.match(await page.locator('#content').innerText(),/Илья Павлович Горин/);
    const bookingColor=await page.locator('nav').evaluate(el=>getComputedStyle(el).backgroundColor);
    const bg=locator=>locator.evaluate(el=>getComputedStyle(el).backgroundColor);
    assert.equal(await bg(button('Перенести')),bookingColor);
    const cancelBounds=await button('Отменить').boundingBox();
    const phoneBounds=await phone.boundingBox();
    assert(Math.abs(phoneBounds.y+phoneBounds.height-cancelBounds.y-cancelBounds.height-16)<2);
    await button('Перенести').click();
    assert.equal(await page.locator('[data-day]').count(),16);
    assert.equal(await bg(page.locator('[data-day][aria-pressed=true]')),bookingColor);
    assert.equal(await bg(page.locator('[data-newtime][aria-pressed=true]')),bookingColor);
    assert(await button('29 ноября, мест нет').isDisabled());
    const grid=page.locator('.date-group').first();
    assert(await grid.evaluate(el=>new Set([...el.children].map(b=>b.offsetTop)).size===1));
    assert(await grid.evaluate(el=>el.clientWidth===el.parentElement.clientWidth));
    const dateStrip=page.locator('#dates');
    const groupWidth=await dateStrip.evaluate(el=>el.clientWidth);
    await dateStrip.hover();
    for(let group=1;group<4;group++){
      await page.mouse.wheel(groupWidth,0);
      await page.waitForFunction(group=>{
        const strip=document.getElementById('dates');
        return Math.abs(strip.scrollLeft-group*strip.clientWidth)<2;
      },group);
    }
    assert(await button('Сохранить').isDisabled());
    await button('11 декабря').click();
    assert(await button('Сохранить').isDisabled());
    assert.equal(await button('17:30').count(),0);
    await button('14:00').click();
    assert.equal(await bg(button('Сохранить')),bookingColor);
    await button('Сохранить').click();
    assert.equal(await page.locator('.visit-when').innerText(),'11 декабря · 14:00');
    await button('Отменить').click();
    assert.equal(await page.locator('#page').inputValue(),'confirmed');
    assert(await page.getByRole('dialog',{name:'Вы уверены, что хотите отменить запись?'}).isVisible());
    assert.equal(await bg(button('Нет, оставить')),bookingColor);
    const keepBounds=await button('Нет, оставить').boundingBox();
    const deleteBounds=await button('Да, отменить').boundingBox();
    assert.equal(keepBounds.y,deleteBounds.y);
    assert(keepBounds.x+keepBounds.width<=deleteBounds.x);
    await page.keyboard.press('Escape');
    assert.equal(await page.getByRole('dialog').count(),0);
    await button('Отменить').click();
    await button('Нет, оставить').click();
    assert.match(await page.locator('#content').innerText(),/Илья Павлович Горин/);
    await button('Назад').click();
    assert.equal(await slides.count(),3);
    assert.match(await slides.first().innerText(),/10:30/);
    await button('Показать запись 1').click();
    await page.locator('[data-slide="0"][aria-pressed=true]').waitFor();
    for(let count=3;count>0;count--){
      await slides.first().click();
      await button('Отменить').click();
      await button('Да, отменить').click();
      assert.equal(await slides.count(),count-1);
      if(count===2){
        assert.equal(await page.locator('.visit-dots').count(),0);
        assert(await track.evaluate(el=>el.scrollWidth===el.clientWidth));
      }
    }
    assert.equal(await page.locator('.no-visits').innerText(),'У вас нет записей');
    await button('Профиль').click();
    assert.doesNotMatch(await page.locator('#content').innerText(),/Местоположение|Медкарта|История приёмов|Исследования|Анализы/);
    await button('Клиники').click();
    await page.locator('[data-filter="radius"] summary').click();
    await button('До 10 км').click();
    await page.locator('[data-geo=no]').click();
    assert.equal(await page.locator('[data-filter="radius"] summary').innerText(),'Весь город');
    await page.goto(pathToFileURL(path.join(__dirname,'overview.html')).href);
    assert.equal(await page.locator('img').count(),previews.length*2);
    assert.deepEqual(errors,[]);
    console.log(previews.length*2+' PNG exported; wide web headers, platform tracking, mobile catalog return, themes, search, detail and overview checked.');
  } finally {
    await browser.close();
  }
}
exportScreens().catch(error=>{console.error(error); process.exitCode=1;});
