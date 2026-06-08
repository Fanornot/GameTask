// ============ НАСТРОЙКИ API ============
// ВАЖНО: Замени на свой IP для WiFi доступа!
const API_BASE = 'http://localhost:8080/api';
// const API_BASE = 'http://192.168.1.45:8080/api'; // Раскомментируй для WiFi

let currentRotation = 0;
let isSpinning = false;

// Универсальный fetch с JWT
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem('sks_token');
    const headers = {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` }),
        ...options.headers
    };

    console.log(`🚀 ${options.method || 'GET'} ${endpoint}`);

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });

        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            throw new Error(`Сервер вернул не JSON (${response.status}): ${text.substring(0, 100)}`);
        }

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || `Ошибка ${response.status}`);
        }

        return result.data !== undefined ? result.data : result;
    } catch (error) {
        console.error(`🔴 API Error [${endpoint}]:`, error);
        throw error;
    }
}

// ============ ИНИЦИАЛИЗАЦИЯ ============
function initApp() {
    if (localStorage.getItem('sks_token')) {
        showApp();
    } else {
        showAuth();
    }

    setupNavigation();
    spawnSonicRings();

    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('registerForm').addEventListener('submit', handleRegister);
    document.getElementById('claimDailyBtn').addEventListener('click', handleDailyClaim);
    document.getElementById('buyFreezeBtn').addEventListener('click', handleBuyFreeze);
    document.getElementById('spinFreeBtn').addEventListener('click', () => handleSpin(true));
    document.getElementById('spinPaidBtn').addEventListener('click', () => handleSpin(false));
    document.getElementById('showProbBtn').addEventListener('click', showProbabilities);
    document.getElementById('btnCreateQuest').addEventListener('click', handleCreateQuest);

    if (typeof initTowerBlocks === 'function') {
        setTimeout(initTowerBlocks, 100);
    }
}

// ============ АВТОРИЗАЦИЯ ============
function switchAuthTab(tab) {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));

    if (tab === 'login') {
        document.querySelectorAll('.auth-tab')[0].classList.add('active');
        document.getElementById('loginForm').classList.add('active');
    } else {
        document.querySelectorAll('.auth-tab')[1].classList.add('active');
        document.getElementById('registerForm').classList.add('active');
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    btn.disabled = true;
    btn.textContent = 'ВХОД...';

    try {
        const data = await apiFetch('/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                username: document.getElementById('loginUsername').value,
                password: document.getElementById('loginPassword').value
            })
        });
        localStorage.setItem('sks_token', data.token);
        showToast('success', 'Добро пожаловать!', `Вы вошли как ${data.username}`, '✅');
        showApp();
    } catch (err) {
        showToast('error', 'Ошибка входа', err.message, '❌');
    } finally {
        btn.disabled = false;
        btn.textContent = 'ВОЙТИ В СИСТЕМУ';
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    btn.disabled = true;
    btn.textContent = 'РЕГИСТРАЦИЯ...';

    try {
        const data = await apiFetch('/auth/register', {
            method: 'POST',
            body: JSON.stringify({
                username: document.getElementById('regUsername').value,
                email: document.getElementById('regEmail').value,
                password: document.getElementById('regPassword').value,
                consentGiven: document.getElementById('consent152').checked
            })
        });
        localStorage.setItem('sks_token', data.token);
        showToast('success', 'Регистрация успешна', 'Добро пожаловать в СКС Quest!', '🎉');
        showApp();
    } catch (err) {
        showToast('error', 'Ошибка регистрации', err.message, '❌');
    } finally {
        btn.disabled = false;
        btn.textContent = 'ЗАРЕГИСТРИРОВАТЬСЯ';
    }
}

function showAuth() {
    document.getElementById('authContainer').style.display = 'flex';
    document.getElementById('appContainer').style.display = 'none';
}

async function showApp() {
    document.getElementById('authContainer').style.display = 'none';
    document.getElementById('appContainer').style.display = 'block';
    await loadUserData();
    renderDaily();
    renderMarketplace();
    renderQuests();
    renderAchievements();
    renderLeaderboard(); // Пустой лидерборд
}

function logout() {
    localStorage.removeItem('sks_token');
    location.reload();
}

function switchRole(role) {
    showToast('success', 'Роль изменена (Демо)', `Теперь вы: ${role}`, '🔄');
    document.querySelectorAll('.role-marketing').forEach(el => {
        el.style.display = (role === 'MARKETING' || role === 'ADMIN') ? 'inline-block' : 'none';
    });
    document.querySelectorAll('.role-analyst').forEach(el => {
        el.style.display = (role === 'MARKETING_ANALYST' || role === 'ADMIN') ? 'inline-block' : 'none';
    });
}

// ============ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ (ВАЛЮТА: ОРИОНЫ 🌌) ============
async function loadUserData() {
    try {
        const user = await apiFetch('/users/me');
        document.getElementById('userNameDisplay').textContent = user.username;
        document.getElementById('balanceDisplay').textContent = user.bonusBalance.toLocaleString() + ' 🌌';
        document.getElementById('dashBalance').textContent = user.bonusBalance.toLocaleString() + ' 🌌';
        document.getElementById('dashStatus').textContent = user.loyaltyStatus;
    } catch (err) {
        console.error("Не удалось загрузить профиль", err);
    }
}

// ============ ЕЖЕДНЕВНАЯ НАГРАДА ============
function renderDaily() {
    const grid = document.getElementById('dailyGrid');
    grid.innerHTML = '';
    const rewards = [5, 5, 5, 5, 5, 5, 50];

    rewards.forEach((reward, index) => {
        const card = document.createElement('div');
        card.className = `day-card ${index === 0 ? 'current' : 'locked'}`;
        card.innerHTML = `
            <div class="day-num">День ${index + 1}</div>
            <div class="day-reward">+${reward} 🌌</div>
        `;
        grid.appendChild(card);
    });
}

async function handleDailyClaim() {
    const btn = document.getElementById('claimDailyBtn');
    btn.disabled = true;
    btn.textContent = 'ОБРАБОТКА...';

    try {
        const res = await apiFetch('/daily/checkin', { method: 'POST' });
        showToast('legendary', 'Награда получена!', `+${res.reward} Орионов! Стрик: ${res.currentStreak} дн.`, '🔥');
        spawnCollectRing(btn);
        await loadUserData();
        renderDaily();
    } catch (e) {
        showToast('error', 'Не удалось забрать награду', e.message, '❌');
    } finally {
        btn.disabled = false;
        btn.textContent = 'ЗАБРАТЬ НАГРАДУ';
    }
}

async function handleBuyFreeze() {
    try {
        await apiFetch('/daily/freeze', { method: 'POST' });
        showToast('success', 'Успех', 'Стрик заморожен!', '❄️');
        await loadUserData();
    } catch (e) {
        showToast('error', 'Ошибка', e.message, '❌');
    }
}

// ============ КАЗИНО (бывшее Колесо Фортуны) ============
async function handleSpin(isFree) {
    if (isSpinning) return;
    isSpinning = true;

    const wheel = document.getElementById('wheelElement');
    currentRotation += 2160 + Math.floor(Math.random() * 360); // 6 полных оборотов
    wheel.style.transform = `rotate(${currentRotation}deg)`;

    setTimeout(async () => {
        try {
            const res = await apiFetch(`/wheel/spin?free=${isFree}`, { method: 'POST' });
            showToast('legendary', '🎰 ДЖЕКПОТ!', res.rewardDescription, '💰');
            spawnCollectRing(wheel);
            await loadUserData();
        } catch (e) {
            showToast('error', 'Ошибка', e.message, '❌');
        }
        isSpinning = false;
        wheel.style.transition = 'none';
        wheel.style.transform = 'rotate(0deg)';
        setTimeout(() => { wheel.style.transition = 'transform 5s cubic-bezier(0.17, 0.67, 0.12, 0.99)'; }, 50);
    }, 5000);
}

async function showProbabilities() {
    const div = document.getElementById('probabilitiesDisplay');
    if (div.style.display === 'block') {
        div.style.display = 'none';
        return;
    }
    try {
        const probs = await apiFetch('/wheel/probabilities');
        div.innerHTML = '<h4 style="color:var(--gold); text-align:center; margin-bottom:1rem;">🎰 Шансы выпадения (Compliance)</h4>';
        for (const [k, v] of Object.entries(probs)) {
            if (k === '_info') continue;
            div.innerHTML += `<div style="display:flex; justify-content:space-between; padding: 0.5rem 0; border-bottom: 1px solid rgba(255,255,255,0.1);"><span>${k}</span><span style="color:var(--orion); font-weight:bold;">${v}</span></div>`;
        }
        div.style.display = 'block';
    } catch (e) {
        showToast('error', 'Ошибка', 'Не удалось загрузить шансы', '❌');
    }
}

// ============ КАТАЛОГ ПРИЗОВ (ВАЛЮТА: ОРИОНЫ) ============
async function renderMarketplace() {
    const grid = document.getElementById('marketplaceGrid');
    grid.innerHTML = '<div style="text-align:center; color:#888;">Загрузка...</div>';

    try {
        const prizes = await apiFetch('/prizes');
        const user = await apiFetch('/users/me');

        grid.innerHTML = '';
        prizes.forEach(prize => {
            const canAfford = user.bonusBalance >= prize.cost;
            const card = document.createElement('div');
            card.className = 'prize-card';
            card.innerHTML = `
                <div class="prize-icon">${prize.imageUrl || '🎁'}</div>
                <h3>${prize.name}</h3>
                <div style="font-size:0.8rem; color:#888; margin: 0.5rem 0;">${prize.description || ''}</div>
                <div class="prize-cost">${prize.cost} 🌌</div>
                <div class="prize-stock">Осталось: ${prize.stock} шт.</div>
                <button class="primary-btn" style="width:100%;" 
                    ${!canAfford ? 'disabled' : ''} 
                    onclick="purchasePrize(${prize.id})">
                    ${canAfford ? 'ПОЛУЧИТЬ' : `Ещё ${prize.cost - user.bonusBalance} 🌌`}
                </button>
            `;
            grid.appendChild(card);
        });
    } catch (e) {
        grid.innerHTML = `<div style="text-align:center; color:var(--red-bright);">Ошибка: ${e.message}</div>`;
    }
}

async function purchasePrize(id) {
    try {
        const res = await apiFetch(`/prizes/${id}/purchase`, { method: 'POST' });
        showToast('legendary', 'Поздравляем!', `Промокод: ${res.promoCode}`, '🎁');
        await loadUserData();
        renderMarketplace();
    } catch (e) {
        showToast('error', 'Недостаточно Орионов', e.message, '❌');
    }
}

// ============ КВЕСТЫ ============
async function renderQuests() {
    const list = document.getElementById('questList');
    list.innerHTML = '<div style="text-align:center; color:#888;">Загрузка...</div>';

    try {
        const quests = await apiFetch('/quests/active');
        list.innerHTML = '';

        quests.forEach(q => {
            const isReady = q.progress >= q.targetCount && !q.completed;
            const div = document.createElement('div');
            div.className = `quest-card ${q.completed ? 'completed' : ''}`;
            div.innerHTML = `
                <div>
                    <h4>${q.name} <span style="font-size:0.7rem; color:#888;">[${q.type}]</span></h4>
                    <div style="font-size: 0.8rem; color: var(--orion);">Награда: +${q.rewardAmount} 🌌</div>
                    <div style="font-size: 0.8rem; color: #888;">Прогресс: ${q.progress} / ${q.targetCount}</div>
                </div>
                <button class="claim-btn ${isReady ? 'visible' : ''}" onclick="claimQuest(${q.id})">ЗАБРАТЬ</button>
            `;
            list.appendChild(div);
        });
    } catch (e) {
        list.innerHTML = `<div style="text-align:center; color:var(--red-bright);">Ошибка: ${e.message}</div>`;
    }
}

async function claimQuest(id) {
    try {
        const res = await apiFetch(`/quests/${id}/claim`, { method: 'POST' });
        showToast('legendary', 'Квест выполнен!', `+${res.reward} Орионов`, '🏆');
        await loadUserData();
        renderQuests();
    } catch (e) {
        showToast('error', 'Ошибка', e.message, '❌');
    }
}

async function handleCreateQuest() {
    const data = {
        name: document.getElementById('cqName').value,
        type: document.getElementById('cqType').value,
        targetCount: parseInt(document.getElementById('cqAmount').value),
        rewardAmount: parseInt(document.getElementById('cqReward').value),
        targetAudience: "ALL"
    };

    if (!data.name) {
        showToast('error', 'Ошибка', 'Введите название', '❌');
        return;
    }

    try {
        await apiFetch('/marketing/quests', { method: 'POST', body: JSON.stringify(data) });
        showToast('success', 'Квест создан', 'Опубликовано', '📜');
        document.getElementById('cqName').value = '';
    } catch (e) {
        showToast('error', 'Ошибка', e.message, '❌');
    }
}

// ============ ДОСТИЖЕНИЯ ============
async function renderAchievements() {
    const grid = document.getElementById('achievementsGrid');
    try {
        const achievements = await apiFetch('/achievements');
        grid.innerHTML = '';
        achievements.forEach(ach => {
            const div = document.createElement('div');
            div.className = `achievement-card ${ach.unlocked ? 'unlocked' : ''}`;
            div.innerHTML = `
                <div class="ach-icon">${ach.unlocked ? ach.icon : '🔒'}</div>
                <div class="ach-title">${ach.name}</div>
                <div class="ach-desc">${ach.description}</div>
            `;
            grid.appendChild(div);
        });
    } catch (e) {
        grid.innerHTML = '<div style="text-align:center; color:#888;">Не удалось загрузить</div>';
    }
}

// ============ ЛИДЕРБОРД (ПУСТОЙ, ЗАПОЛНЯЕТСЯ АВТОМАТИЧЕСКИ) ============
async function renderLeaderboard() {
    const list = document.getElementById('leaderboardList');
    try {
        const lb = await apiFetch('/leaderboard/monthly');

        // Если лидерборд пустой — показываем красивое сообщение
        if (!lb || lb.length === 0) {
            list.innerHTML = `
                <div class="leaderboard-empty">
                    <div class="empty-icon">🏆</div>
                    <h3>Лидерборд пуст</h3>
                    <p>Стань первым! Выполняй квесты, крути казино и поднимайся в рейтинге.</p>
                    <p style="margin-top: 1rem; color: var(--gold);">Топ-10% игроков получат статус DIAMOND</p>
                </div>
            `;
            return;
        }

        list.innerHTML = '';
        lb.forEach(entry => {
            const div = document.createElement('div');
            div.className = `lb-entry ${entry.rank === 1 ? 'top-1' : (entry.rank === 2 ? 'top-2' : (entry.rank === 3 ? 'top-3' : ''))}`;
            div.innerHTML = `
                <div class="lb-rank">#${entry.rank}</div>
                <div class="lb-name">${entry.username} <span style="color:#888; font-size:0.8rem;">(${entry.league})</span></div>
                <div class="lb-score">${entry.totalBonusEarned} 🌌</div>
            `;
            list.appendChild(div);
        });
    } catch (e) {
        list.innerHTML = `
            <div class="leaderboard-empty">
                <div class="empty-icon">🏆</div>
                <h3>Лидерборд пуст</h3>
                <p>Стань первым!</p>
            </div>
        `;
    }
}

// ============ НАВИГАЦИЯ ============
function setupNavigation() {
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
            document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
            document.getElementById(btn.dataset.section).classList.add('active');
            btn.classList.add('active');
        });
    });
}

// ============ УТИЛИТЫ ============
function showToast(type, title, message, icon) {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<div style="font-size:1.5rem;">${icon}</div><div><div style="font-weight:bold;">${title}</div><div style="font-size:0.9rem; color:#aaa;">${message}</div></div>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100px)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

function spawnCollectRing(targetEl) {
    const rect = targetEl.getBoundingClientRect();
    const ring = document.createElement('div');
    ring.className = 'ring-collect';
    ring.style.left = (rect.left + rect.width/2 - 40) + 'px';
    ring.style.top = (rect.top + rect.height/2 - 40) + 'px';
    document.body.appendChild(ring);
    setTimeout(() => ring.remove(), 800);
}

function spawnSonicRings() {
    const container = document.getElementById('sonicRings');
    setInterval(() => {
        if (container.children.length > 5) return;
        const ring = document.createElement('div');
        ring.className = 'sonic-ring';
        ring.style.left = Math.random() * 100 + '%';
        ring.style.top = Math.random() * 100 + '%';
        container.appendChild(ring);
        setTimeout(() => ring.remove(), 15000);
    }, 4000);
}

// Запуск
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
} else {
    initApp();
}