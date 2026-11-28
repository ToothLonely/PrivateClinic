const $ = id => document.getElementById(id);
const icons = {
  clinic: '<path d="M3 21V9h5V4h8v5h5v12H3Zm7 0v-4h4v4M10.5 8h3m-1.5-1.5v3M6 13v1m12-1v1"/>',
  tooth: '<path d="M12 5c-4-3-9-2-9 3 0 3 2 6 3 10 .9 4 3 4 4-1 .6-3 3.4-3 4 0 1 5 3 5 4 1 1-4 3-7 3-10 0-5-5-6-9-3Z"/>',
  pin: '<path d="M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.6"/>',
  logout: '<path d="M10 4H5v16h5m-1-8h12m-5-5 5 5-5 5"/>',
  calendar: '<rect x="3" y="5" width="18" height="16" rx="3"/><path d="M7 3v5m10-5v5M3 11h18"/>',
  user: '<circle cx="12" cy="7" r="4"/><path d="M4 21v-2c0-7 16-7 16 0v2Z"/>',
  search: '<circle cx="10.5" cy="10.5" r="6.5"/><path d="m16 16 5 5"/>',
  down: '<path d="m6 9 6 6 6-6"/>', back: '<path d="M20 12H4m7-7-7 7 7 7"/>',
  heart: '<path d="M12 21 3.5 12C-1 5 7 0 12 7c5-7 13-2 8.5 5Z"/>',
  call: '<path d="m4 3 4-.5 2 5-3 2c2 4 4 6 8 8l2-3 5 2-.5 4c-1 5-9 1-14-4S0 4 4 3Z"/>'
};
const icon = name => '<svg viewBox="0 0 24 24" aria-hidden="true">'+icons[name]+'</svg>';
const pages = {catalog:'Клиники', detail:'Карточка клиники', saved:'Избранное', profile:'Профиль', personal:'Личные данные', registration:'Регистрация', history:'История приёмов', studies:'Исследования', labs:'Анализы', booking:'Запись', login:'Вход', code:'Код из письма', slot:'После MVP · Приём', confirmed:'Подробности записи', reschedule:'Перенос приёма'};
const clinics = [
  ['Феникс Медика','Многопрофильная клиника','50 лет ВЛКСМ, 91'],
  ['DL Med','Многопрофильная клиника','50 лет ВЛКСМ, 18В'],
  ['Стомион','Стоматология','45-я Параллель, 22Г'],
  ['Сантерра','Медицинский центр','Серова, 154'],
  ['Тринити-Дент','Стоматология','Шпаковская, 76/1'],
  ['Клиника Семейная','Многопрофильная клиника','Серова, 252'],
  ['Никольс.Мед','Медицинский центр','Мира, 126'],
  ['Айвазовский','Стоматология','Рогожникова, 3'],
  ['Доктор плюс','Медицинский центр','Ленина, 127'],
  ['Хорошая стоматология','Стоматология','Мира, 242'],
  ['Медстудио','Стоматология','Партизанская, 2Б'],
  ['Амадея','Медицинский центр','45-я Параллель, 2']
];
const types = ['Все типы','Многопрофильная клиника','Стоматология','Медицинский центр','Диагностика','Медицинская косметология','Детская медицина','Офтальмология','Реабилитация','Специализированная клиника'];
const shortTypes = ['Все типы','Общий профиль','Стоматология','Медцентр','Диагностика','Косметология','Детские','Глазные','Реабилитация','Узкий профиль'];
const radii = ['Весь город','До 5 км','До 10 км','До 15 км'];
const demoDistances = [1,4,2,7,9,6,12,14,16,3,11,13];
const params = new URLSearchParams(location.search);
const platform = $('platform');
platform.value = params.get('platform') === 'mobile' ? 'mobile' : 'web';
$('phone').dataset.platform = platform.value;
const requestedScreen = params.get('screen');
let page = Object.hasOwn(pages, requestedScreen) ? requestedScreen : ({unavailable:'booking', cancel:'confirmed', settings:'profile', medical:'booking', location:'catalog'}[requestedScreen] || 'catalog');
const systemTheme = matchMedia('(prefers-color-scheme: dark)');
const themeNames = {system:'Системная',light:'Светлая',dark:'Тёмная'};
let themeChoice = params.get('theme');
if (!Object.hasOwn(themeNames, themeChoice)) {
  try { themeChoice = localStorage.getItem('clinic-theme'); } catch {}
}
if (!Object.hasOwn(themeNames, themeChoice)) themeChoice = 'system';
function applyTheme() {
  const resolved = themeChoice === 'system' ? (systemTheme.matches ? 'dark' : 'light') : themeChoice;
  $('phone').dataset.theme = resolved;
  $('theme').value = themeChoice;
  document.querySelectorAll('[data-mode]').forEach(button=>{
    button.setAttribute('aria-pressed', String(button.dataset.mode === themeChoice));
  });
}
function chooseTheme(value) {
  themeChoice = value;
  try { localStorage.setItem('clinic-theme',value); } catch {}
  applyTheme();
}
systemTheme.addEventListener('change',applyTheme);
let current = 0, origin = 'catalog', type = 0, radius = 0, pendingRadius = 0;
let geo = false, query = '', email = '', time = '10:30';
const sampleVisits = [
  {id:1, day:26, time:'10:30', role:'Стоматолог', doctor:'Вера Андреевна Лескова', clinic:'Клиника «Линия»', address:'ул. Мира, 18'},
  {id:2, day:27, time:'15:00', role:'Уролог', doctor:'Илья Павлович Горин', clinic:'Центр «Север»', address:'ул. Ленина, 42'},
  {id:3, day:28, time:'09:00', role:'Гинеколог', doctor:'Анна Олеговна Ветрова', clinic:'Клиника «Ива»', address:'ул. Пушкина, 7'}
];
const futureScreen = ['confirmed','cancel','reschedule'].includes(requestedScreen);
const previewCount = {empty:0, upcoming:1, multiple:3}[params.get('example')] ?? (futureScreen ? 3 : 0);
let selectedId = 1;
let nextId = 4;
let draftDay = 26;
let draftTime = '10:30';
const demoAccount = {
  first: 'Анна', last: 'Соколова', middle: 'Андреевна', email: 'anna@example.com',
  favorites: new Set([2]), photo: '', visits:sampleVisits.slice(0,previewCount).map(visit=>({...visit}))
};
const accounts = new Map([[demoAccount.email, demoAccount]]);
let account = demoAccount;
let favorites = account.favorites;

function useAccount(next) {
  account = next;
  selectedId = account.visits[0]?.id;
  favorites = next.favorites;
}
let previousY = 0, travel = 0, catalogY = 0;
let catalogSearchVisible = true;
const go = (label,to,cls='secondary') => '<button class="'+cls+'" data-go="'+to+'">'+label+'</button>';
const back = to => `<button class="back icon-back" data-go="${to}" aria-label="Назад">${icon('back')}</button>`;
const disc = name => '<span class="disc">'+icon(name)+'</span>';
const kind = i => clinics[i][1]==='Стоматология' ? 'tooth' : 'clinic';
function rows(ids) {
  return ids.map(i=>'<button class="row" data-clinic="'+i+'">'+disc(kind(i))+'<span><strong>'+clinics[i][0]+'</strong><small>'+clinics[i][1]+'</small><span class="address">'+icon('pin')+clinics[i][2]+'</span></span></button>').join('');
}
function results() {
  const term = query.trim().toLocaleLowerCase('ru');
  return clinics.map((_,i)=>i).filter(i=>(!type||clinics[i][1]===types[type])&&(!radius||demoDistances[i]<=radius*5)&&[clinics[i][0],clinics[i][2]].join(' ').toLocaleLowerCase('ru').includes(term));
}
function list() {
  return rows(results()) || '<h2>Клиник не найдено</h2><button class="secondary" data-reset>Сбросить поиск и фильтры</button>';
}
function options(values,selected,key) {
  return values.map((v,i)=>'<button class="choice" data-'+key+'="'+i+'" aria-pressed="'+(i===selected)+'">'+v+'</button>').join('');
}
function dropdown(key, label, values, selected, caption) {
  return `<details class="filter" name="clinic-filter" data-filter="${key}">
    <summary aria-label="${label} — ${values[selected]}"><span>${caption}</span>${icon('down')}</summary>
    <div class="filter-menu" role="group" aria-label="${label}">${options(values, selected, key)}</div>
  </details>`;
}

function catalogue() {
  const permission = pendingRadius ? `<div class="permission">
    <p>Для поиска в радиусе ${radii[pendingRadius].slice(3)} нужно местоположение.</p>
    <button class="secondary" data-geo="yes">Разрешить</button>
    <button class="back" data-geo="no">Весь город</button>
  </div>` : '';
  return `<div class="catalog-tools"><div class="filters">
    ${dropdown('type', 'Тип учреждения', types, type, shortTypes[type])}
    ${dropdown('radius', 'Радиус', radii, radius, radii[radius])}
  </div>${permission}</div><div id="results">${list()}</div>`;
}

function clinicDetails() {
  const [name, category, address] = clinics[current];
  return `<div class="clinic-heading">${back(origin)}<h1>${name}</h1>
    <button class="favorite" data-save aria-label="В избранном" aria-pressed="${favorites.has(current)}">${icon('heart')}</button></div>
    <div class="clinic-body"><section class="gallery" aria-label="Фотографии клиники">
      <div class="photo-strip" tabindex="0" role="group" aria-label="Фотографии филиала, горизонтальная прокрутка">${[1,2,3].map(n => `<span role="img" aria-label="Место для фотографии ${n}"></span>`).join('')}</div></section>
    <section class="clinic-info"><div><h2>Профиль клиники</h2><p>${category}</p>
      <h2>Адрес филиала</h2><p>${icon('pin')} Ставрополь, ${address}</p>
      <div class="contact-actions"><button class="secondary" data-contact="call">Позвонить</button>
      <button class="secondary" data-contact="site">Сайт клиники</button></div>
      <p class="notice" id="message" role="status"></p></div></section></div>`;
}

function profile() {
  return `<section class="account-section"><div class="identity"><span id="avatar" aria-hidden="true"></span><h1 id="display-name"></h1></div>
    </section><section class="preferences">${go('Личные данные', 'personal', 'item')}
    ${go('Избранное <small>'+favorites.size+'</small>', 'saved', 'item')}
    <h2 class="section-title theme-heading" id="theme-heading">Тема</h2>
    <div class="theme-options" role="group" aria-labelledby="theme-heading">${Object.entries(themeNames).map(([value,label])=>
      `<button class="theme-card" data-mode="${value}" aria-pressed="${themeChoice===value}"><span class="swatch" aria-hidden="true"></span>${label}</button>`).join('')}</div>
    <button class="sign-out logout" data-logout>${icon('logout')}Выйти</button></section>`;
}
function personalData() {
  return `<div class="record-heading">${back('profile')}<h1>Личные данные</h1></div>
    <dl class="personal-data"><dt>Фамилия, имя, отчество</dt><dd id="full-name"></dd>
    <dt>Электронная почта</dt><dd id="personal-email"></dd></dl>`;
}
function registration() {
  return `<h1>Введите данные</h1><form id="registration-form">
    <label class="field">Фамилия<input name="last" autocomplete="family-name" required maxlength="80"></label>
    <label class="field">Имя<input name="first" autocomplete="given-name" required maxlength="80"></label>
    <label class="field">Отчество (при наличии)<input name="middle" autocomplete="additional-name" maxlength="80"></label>
    </form>`;
}
function selectedVisit() {
  return account.visits.find(v => v.id === selectedId) || account.visits[0];
}
function visitCard(v) {
  return `<button class="visit-slide" data-visit="${v.id}">
    <span class="visit-date">${dateLabel(v.day)} <b>${v.time}</b></span>
    <strong>${v.role}</strong><span class="visit-address">${icon('pin')} ${v.address}</span>
  </button>`;
}
function bookings() {
  const visits = account.visits;
  const multiple = visits.length > 1;
  const frame = visits.length
    ? `<div class="visit-track" id="visit-track" aria-label="Текущие записи">${visits.map(visitCard).join('')}</div>`
    : '<div class="no-visits">У вас нет записей</div>';
  const dots = multiple ? `<div class="visit-dots">${visits.map((v,i)=>
    `<button data-slide="${i}" aria-label="Показать запись ${i+1}"></button>`).join('')}</div>` : '';
  return `<section><h2 class="section-title">Текущие записи</h2>
    ${frame}${dots}
    </section><button class="primary book" data-unavailable>Записаться</button><section class="records"><h2 class="section-title">Медкарта</h2>
    ${['history','studies','labs'].map(id=>go(pages[id],id,'item')).join('')}</section>`;
}
function initCarousel() {
  const track = $('visit-track');
  if (!track || account.visits.length < 2) return;
  const slides = [...track.children];
  function markVisible() {
    if (!track.isConnected) return;
    const i = Math.min(slides.length-1, Math.max(0, Math.round(track.scrollLeft/track.clientWidth)));
    selectedId = account.visits[i].id;
    document.querySelectorAll('[data-slide]').forEach((dot,n)=>dot.setAttribute('aria-pressed',String(i===n)));
  }
  track.scrollLeft = Math.max(0,account.visits.findIndex(v=>v.id===selectedId))*track.clientWidth;
  markVisible();
  track.addEventListener('scroll',markVisible,{passive:true});
}
function visitPage() {
  const v = selectedVisit();
  if (!v) return back('booking')+'<h1>У вас нет записей</h1>';
  const date = dateLabel(v.day)+' · '+v.time;
  if (page === 'reschedule') return schedulePicker();
  return `${back('booking')}<h1>${v.role}</h1><p class="visit-when">${date}</p>
    <dl class="personal-data"><dt>Врач</dt><dd>${v.doctor}</dd><dt>Клиника</dt><dd>${v.clinic}</dd>
    <dt>Адрес филиала</dt><dd>Ставрополь, ${v.address}</dd></dl>`;
}
// Each group of four dates is one page in the demo schedule, starting on 26 November.
const firstDay = 26;
const timePatterns = [
  ['09:00','09:30','10:30','11:00','12:30','14:00','15:00','16:30'],
  ['09:30','11:00','13:30','15:00','16:00','17:30'],
  ['09:00','10:00','11:30','12:00','14:00'],
  [],
  ['09:00','10:30','11:00','12:30','14:30','15:30','16:00','17:00']
];
function dateLabel(day, options={day:'numeric',month:'long'}) {
  return new Date(Date.UTC(2026,10,day)).toLocaleDateString('ru-RU',{...options,timeZone:'UTC'});
}
function availableTimes(day) {
  const current = selectedVisit();
  const times = new Set(timePatterns[(day-firstDay)%timePatterns.length]);
  if (current.day===day) times.add(current.time);
  for (const other of account.visits) {
    if (other.id!==current.id && other.doctor===current.doctor && other.day===day) times.delete(other.time);
  }
  return [...times].sort();
}
function schedulePicker() {
  const groups = Array.from({length:4},(_,group)=>{
    const days = Array.from({length:4},(_,index)=>{
      const day = firstDay + 4*group + index;
      const full = availableTimes(day).length===0;
      const label = dateLabel(day);
      return `<button class="date-slot" data-day="${day}" aria-label="${label}${full?', мест нет':''}" aria-pressed="${draftDay===day}" ${full?'disabled':''}>
        <strong>${dateLabel(day,{day:'numeric',month:'short'}).replace('.','')}</strong>
        <small>${full?'Мест нет':dateLabel(day,{weekday:'short'})}</small></button>`;
    });
    return '<div class="date-group">'+days.join('')+'</div>';
  });
  return `${back('confirmed')}
    <h1 class="date-heading">Дата</h1>
    <div id="dates">${groups.join('')}</div>
    <h2 class="time-heading">Время</h2><div id="times"></div>`;
}
function updateTimes() {
  const times = availableTimes(draftDay);
  $('times').innerHTML = times.map(value=>
    `<button class="time-slot" data-newtime="${value}" aria-pressed="${value===draftTime}">${value}</button>`).join('');
  updateSave();
}
function updateSave() {
  const visit = selectedVisit();
  document.querySelector('[data-reschedule]').disabled = !draftTime || (draftTime===visit.time && draftDay===visit.day);
}
function initDates() {
  const dates = $('dates');
  if (!dates) return;
  dates.scrollLeft = Math.floor((draftDay-firstDay)/4)*dates.clientWidth;
  updateTimes();
}
const cancelDialog = $('cancel-dialog');
function positionDialog(dialog) {
  const bounds = $('phone').getBoundingClientRect();
  dialog.style.left = (bounds.left+bounds.width/2)+'px';
  dialog.style.top = (bounds.top+bounds.height/2)+'px';
  dialog.style.width = Math.min(340,bounds.width-32)+'px';
}
function openDialog(dialog) {
  positionDialog(dialog);
  dialog.showModal();
}
window.addEventListener('resize',()=>{ const dialog=document.querySelector('dialog[open]'); if (dialog) positionDialog(dialog); });
window.addEventListener('scroll',()=>{ const dialog=document.querySelector('dialog[open]'); if (dialog) positionDialog(dialog); },{passive:true});
function content() {
  switch(page) {
    case 'catalog': return catalogue();
    case 'detail': return clinicDetails();
    case 'saved': return '<div class="record-heading">'+back('profile')+'<h1>Избранное</h1></div>'+(rows([...favorites])||'<p>Сохраняйте клиники из их карточек.</p>'+go('К клиникам','catalog'));
    case 'profile': return profile();
    case 'booking': return bookings();
    case 'personal': return personalData();
    case 'registration': return registration();
    case 'history': case 'studies': case 'labs':
      return '<div class="record-heading">'+back('booking')+'<h1>'+pages[page]+'</h1></div><p class="later muted">Функция появится позже.</p>';
    case 'login': return '<h1>Вход</h1><p class="login-gap" aria-hidden="true"></p><form id="login-form"><label class="field">Электронная почта<input type="email" id="email" placeholder="anna@example.com" required></label><button class="primary">Получить код</button></form>';
    case 'code': return back('login')+'<h1>Код из письма</h1><p>Введите код для <b id="account"></b>.</p><form id="code-form"><label class="field">Одноразовый код<input class="code" inputmode="numeric" autocomplete="one-time-code" pattern="[0-9]{6}" maxlength="6" placeholder="000000" required></label><button class="primary">Войти</button></form><p class="notice">Вход сохранится на этом устройстве.</p>';
    case 'slot': return '<h1>Записаться на приём</h1><h2>Первичная консультация</h2><p>Терапевт · Демо-клиника</p><h2>26 ноября</h2><div class="split">'+options(['09:00','10:30','14:00'],['09:00','10:30','14:00'].indexOf(time),'time')+'</div>';
    case 'confirmed': case 'reschedule': return visitPage();

  }
}
function showSearch(show) {
  $('search-box').classList.toggle('collapsed',!show);
  $('search-box').inert = !show;
}
function compactLayout() {
  return !wideWindow.matches || $('layout').value === 'phone';
}
function render(restoreY=0) {
  resetPull();
  applyTheme();
  $('phone').dataset.screen = page;
  $('page').value = page;
  showSearch(page !== 'catalog' || platform.value !== 'mobile' || !compactLayout() || catalogSearchVisible);
  $('search-box').hidden = page!=='catalog';
  $('query').value = query;
  $('content').innerHTML = content();
  $('visit-actions').hidden = page!=='confirmed' || !selectedVisit();
  const formActions=$('form-actions');
  formActions.hidden=!['registration','slot'].includes(page);
  formActions.innerHTML=page==='registration' ? '<button class="primary booking-color" form="registration-form">Продолжить</button>' :
    page==='slot' ? go('Выбрать','confirmed','primary booking-color') : '';
  $('save-bar').hidden = !$('dates');
  initDates();
  initCarousel();
  if ($('display-name')) {
    $('display-name').textContent = account.first;
    if (account.photo) {
      const photo = new Image();
      photo.alt = '';
      photo.src = account.photo;
      $('avatar').append(photo);
    } else {
      $('avatar').textContent = (account.first[0] + account.last[0]).toLocaleUpperCase('ru');
    }
  }
  if ($('full-name')) {
    $('full-name').textContent = [account.last, account.first, account.middle].filter(Boolean).join(' ');
    $('personal-email').textContent = account.email;
  }
  if ($('account')) $('account').textContent = email || 'anna@example.com';
  const mainTab = ['profile','catalog','booking'].includes(page);
  document.querySelector('#phone header').hidden = !mainTab;
  document.querySelector('footer').hidden = !mainTab;
  document.querySelector('nav').innerHTML = [['booking','calendar','Запись'],['catalog','clinic','Клиники'],['profile','user','Профиль']].map(([id,image,label])=>'<button data-go="'+id+'" '+(id===page?'aria-current="page"':'')+'>'+icon(image)+label+'</button>').join('');
  $('scroll').scrollTop = restoreY;
  previousY = $('scroll').scrollTop;
  travel = 0;
}
function navigate(to) {
  if (to==='reschedule' && selectedVisit()) {
    draftDay=selectedVisit().day;
    draftTime=selectedVisit().time;
  }
  if (page==='slot' && to==='confirmed') {
    const visit = {...sampleVisits[0], id:nextId++, time, role:'Терапевт', clinic:'Демо-клиника'};
    account.visits.push(visit);
    selectedId=visit.id;
  }
  if (page==='catalog') catalogY = $('scroll').scrollTop;
  pendingRadius = 0;
  page = to;
  render(to==='catalog'?catalogY:0);
}
$('query').addEventListener('input',e=>{
  query=e.target.value;
  $('results').innerHTML=list();
  $('scroll').scrollTop=0;
});
$('scroll').addEventListener('scroll',()=>{
  if(page!=='catalog' || !compactLayout()) return;
  const y=$('scroll').scrollTop, delta=y-previousY;
  previousY=y;
  if (delta === 0) return;
  travel=Math.sign(delta)===Math.sign(travel)?travel+delta:delta;
  if (y<2 || travel < -10) {
    catalogSearchVisible = true;
    showSearch(true);
  }
  else if (y>48 && travel>10) {
    if(document.activeElement===$('query')) $('query').blur();
    catalogSearchVisible = false;
    showSearch(false);
  }
},{passive:true});
function dismissFilter(event) {
  const open = document.querySelector('.filter[open]');
  if (open && !open.contains(event.target)) open.open = false;
}
document.addEventListener('click', dismissFilter);
document.addEventListener('focusin', dismissFilter);
document.addEventListener('keydown', event => {
  const open = document.querySelector('.filter[open]');
  if (event.key === 'Escape' && open) {
    open.open = false;
    open.querySelector('summary').focus();
  }
});
document.addEventListener('click',e=>{
  const btn=e.target.closest('button');
  if (!btn) return;
  const d=btn.dataset;
  if ('logout' in d) {
    email = '';
    useAccount(demoAccount);
    navigate('login');
    return;
  }
  if ('unavailable' in d) openDialog($('unavailable-dialog'));
  else if (d.go) {btn.closest('dialog')?.close(); navigate(d.go);}
  else if ('visit' in d) { selectedId=Number(d.visit); navigate('confirmed'); }
  else if ('slide' in d) {
    const track = $('visit-track');
    track.scrollTo({left:Number(d.slide)*track.clientWidth, behavior:matchMedia('(prefers-reduced-motion: reduce)').matches?'instant':'smooth'});
  }
  else if ('day' in d) {
    if (draftDay===Number(d.day)) return;
    draftDay = Number(d.day);
    draftTime = draftDay===selectedVisit().day ? selectedVisit().time : '';
    document.querySelectorAll('[data-day]').forEach(el=>el.setAttribute('aria-pressed',String(el===btn)));
    updateTimes();
  }
  else if ('newtime' in d) {
    draftTime = d.newtime;
    document.querySelectorAll('[data-newtime]').forEach(el=>el.setAttribute('aria-pressed',String(el===btn)));
    updateSave();
  }
  else if ('openCancel' in d) openDialog(cancelDialog);
  else if ('keep' in d) cancelDialog.close();
  else if ('reschedule' in d) {
    Object.assign(selectedVisit(),{day:draftDay,time:draftTime});
    account.visits.sort((a,b)=>a.day-b.day || a.time.localeCompare(b.time));
    navigate('confirmed');
  }
  else if ('cancel' in d) {
    cancelDialog.close();
    const index=account.visits.indexOf(selectedVisit());
    account.visits.splice(index,1);
    selectedId=account.visits[Math.min(index,account.visits.length-1)]?.id;
    navigate('booking');
  }
  else if ('clinic' in d) {current=Number(d.clinic); origin=page; navigate('detail');}
  else if ('save' in d) {if(favorites.has(current)) favorites.delete(current); else favorites.add(current); btn.setAttribute("aria-pressed", String(favorites.has(current)));}
  else if ('contact' in d) $('message').textContent=d.contact==='call'?'В приложении откроется набор номера филиала.':'В приложении откроется официальный сайт филиала.';
  else if ('reset' in d) {type=radius=pendingRadius=catalogY=0; catalogSearchVisible=true; query=''; render();}
  else if ('geo' in d) {
    geo = d.geo === 'yes';
    radius = geo ? (pendingRadius || radius) : 0;
    pendingRadius = 0;
    render();
  }
  else if ('radius' in d || 'type' in d) {
    const isRadius = 'radius' in d;
    pendingRadius = 0;
    if (isRadius) {
      const value = Number(d.radius);
      if (value && !geo) pendingRadius = value;
      else radius = value;
    } else type = Number(d.type);
    render();
    document.querySelector('[data-filter="'+(isRadius?'radius':'type')+'"] summary').focus({preventScroll:true});
  }
  else if ('mode' in d) chooseTheme(d.mode);
  else if ('time' in d) {time=['09:00','10:30','14:00'][Number(d.time)];render();}
});
document.addEventListener('submit',e=>{
  e.preventDefault();
  if(e.target.id==='login-form'){email=$('email').value.trim(); navigate('code');}
  if (e.target.id === 'code-form') {
    const existing = accounts.get(email.toLowerCase() || demoAccount.email);
    if (existing) {
      useAccount(existing);
      navigate('catalog');
    } else navigate('registration');
  }
  if (e.target.id === 'registration-form') {
    const form = e.target;
    const [first, last, middle] = ['first','last','middle'].map(key=>form.elements[key].value.trim());
    if (!first || !last) {
      const input = form.elements[first ? 'last' : 'first'];
      input.value = '';
      input.reportValidity();
      return;
    }
    const next = {first, last, middle, email:email || demoAccount.email, favorites:new Set(), photo:'', visits:[]};
    accounts.set(next.email.toLowerCase(), next);
    useAccount(next);
    navigate('catalog');
  }
});
$('page').innerHTML=Object.entries(pages).map(([id,label])=>'<option value="'+id+'">'+label+'</option>').join('');
$('city').innerHTML=icon('pin')+'Ставрополь';
$('lens').innerHTML=icon('search');
$('layout').value=params.get('layout')==='phone'?'phone':'auto';
const wideWindow=matchMedia($('adaptive-style').media);
// Preview the compact header gesture against local prototype data.
const scroller=$('scroll');
let headerPull;
function resetPull() {
  if (!headerPull) return;
  headerPull.heading.style.removeProperty('--pull-offset');
  headerPull.title.style.removeProperty('transform');
  headerPull.indicator.remove();
  headerPull=null;
}
scroller.addEventListener('touchstart',event=>{
  resetPull();
  if (event.touches.length!==1 || scroller.scrollTop>0 || (wideWindow.matches && $('layout').value!=='phone')) return;
  const heading=$('content').querySelector('.record-heading,.clinic-heading');
  if (!heading) return;
  const title=heading.querySelector('h1');
  const indicator=document.createElement('div');
  indicator.className='refresh-preview';
  indicator.hidden=true;
  indicator.textContent='Потяните для обновления';
  heading.after(indicator);
  const touch=event.touches[0];
  headerPull={heading,title,indicator,startY:touch.clientY,startX:touch.clientX,ready:false};
},{passive:true});
scroller.addEventListener('touchmove',event=>{
  if (!headerPull) return;
  const {heading,title,indicator,startX,startY}=headerPull;
  const touch=event.touches[0],distance=touch.clientY-startY;
  if (event.touches.length!==1 || distance<=0 || Math.abs(touch.clientX-startX)>distance) {resetPull(); return;}
  if (event.cancelable) event.preventDefault();
  const progress=Math.min(distance/80,1);
  const offset=(heading.querySelector('.icon-back').offsetWidth+parseFloat(getComputedStyle(heading).gap))*progress;
  const drop=(title.offsetHeight+10)*progress;
  title.style.transform='translate('+(-offset)+'px,'+drop+'px)';
  heading.style.setProperty('--pull-offset',drop+'px');
  indicator.hidden=distance<=80;
  headerPull.ready=distance>=130;
  indicator.textContent=headerPull.ready?'Отпустите для обновления':'Потяните для обновления';
},{passive:false});
scroller.addEventListener('touchend',()=>{
  const refresh=headerPull?.ready;
  resetPull();
  if (refresh) render();
});
scroller.addEventListener('touchcancel',resetPull);
function syncLayout(){
  resetPull();
  $('adaptive-style').media=$('layout').value==='phone'?'not all':wideWindow.media;
  previousY=$('scroll').scrollTop;
  travel=0;
  showSearch(platform.value !== 'mobile' || !compactLayout() || catalogSearchVisible);
}
syncLayout();
wideWindow.addEventListener('change',syncLayout);
$('layout').onchange=e=>{
  syncLayout();
  const url=new URL(location.href);
  url.searchParams.set('layout',e.target.value);
  history.replaceState(null,'',url);
};
platform.onchange=()=>{
  $('phone').dataset.platform = platform.value;
  syncLayout();
  const url = new URL(location.href);
  url.searchParams.set('platform', platform.value);
  history.replaceState(null, '', url);
};
$('page').onchange=e=>{
  if (['confirmed','reschedule'].includes(e.target.value) && !selectedVisit()) {
    const url=new URL(location.href);
    url.searchParams.set('screen',e.target.value);
    url.searchParams.set('theme',themeChoice);
    location.assign(url);
  } else navigate(e.target.value);
};
$('theme').onchange=e=>chooseTheme(e.target.value);
render();
if (requestedScreen==='unavailable') openDialog($('unavailable-dialog'));
if (requestedScreen==='cancel' && selectedVisit()) openDialog(cancelDialog);
if (page === 'catalog' && (params.get('menu') === 'type' || requestedScreen === 'filters')) {
  document.querySelector('[data-filter="type"]').open = true;
}
