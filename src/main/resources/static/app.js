async function api(url, options = {}) {
  const response = await fetch(url, {
    credentials: 'same-origin',
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });

  if (!response.ok) {
    const text = await response.text();
    let message = text || 'Request failed';
    try {
      const data = JSON.parse(text);
      if (data.errors) {
        message = Object.entries(data.errors).map(([field, error]) => `${field}: ${error}`).join('; ');
      } else if (data.message) {
        message = data.message;
      } else if (data.error) {
        message = data.error;
      }
    } catch (e) {}
    throw new Error(message);
  }

  if (response.status === 204) return null;
  return response.json();
}

function byId(id) {
  return document.getElementById(id);
}

async function currentUser() {
  try {
    return await api('/api/auth/me');
  } catch (e) {
    return { authenticated: false };
  }
}

async function renderNavbar() {
  const nav = byId('navlinks');
  if (!nav) return;

  const me = await currentUser();
  let html = `
    <a href="/index.html">Home</a>
    <a href="/rooms.html">Rooms</a>
    <a href="/reviews.html">Reviews</a>
  `;

  if (me.authenticated) {
    html += `<a href="/bookings.html">My bookings</a>`;
  }

  if (me.authenticated && me.role === 'ADMIN') {
    html += `<a href="/admin.html">Admin Panel</a><a href="/api-links.html">API</a>`;
  }

  if (!me.authenticated) {
    html += `<a href="/login.html">Login</a><a href="/register.html">Register</a>`;
  } else {
    html += `<span class="muted">${me.name} (${me.role})</span><button class="nav-button" onclick="logout()">Logout</button>`;
  }

  nav.innerHTML = html;
}

async function logout() {
  try {
    await api('/api/auth/logout', { method: 'POST' });
  } finally {
    location.href = '/index.html';
  }
}

function money(value) {
  return Number(value || 0).toLocaleString('ru-RU') + ' ₸';
}

function msg(id, text, type = '') {
  const element = byId(id);
  if (!element) return;
  element.className = type ? `message ${type}` : 'message';
  element.textContent = text;
}

function backHome() {
  location.href = '/index.html';
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

document.addEventListener('DOMContentLoaded', renderNavbar);
