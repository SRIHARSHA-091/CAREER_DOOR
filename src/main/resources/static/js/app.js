const API = '/api';
const token = () => localStorage.getItem('accessToken');
const role = () => localStorage.getItem('role') || '';
const authHeaders = (json = true) => { const h = {}; if (json) h['Content-Type'] = 'application/json'; if (token()) h.Authorization = `Bearer ${token()}`; return h; };
async function api(path, options = {}) { const response = await fetch(`${API}${path}`, { ...options, headers: { ...authHeaders(options.body !== undefined), ...(options.headers || {}) } }); if (response.status === 401) { localStorage.clear(); window.location.href = '/login.html'; return null; } const text = await response.text(); let data = null; try { data = text ? JSON.parse(text) : null; } catch { data = text; } if (!response.ok) { const message = data?.message || data?.error || data?.detail || text || `Request failed (${response.status})`; throw new Error(message); } return data; }
function escapeHtml(value = '') { return String(value).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }
function requireAuth() { if (!token()) { window.location.href = '/login.html'; return false; } return true; }
function logout() { localStorage.clear(); window.location.href = '/login.html'; }
function showNotice(id, message, kind = '') { const el = document.getElementById(id); if (!el) return; el.className = `notice ${kind}`; el.textContent = message; el.hidden = false; }
