/**
 * theme.js — VBS Shared Theme Manager
 * Drop this script into every page (ideally in <head>)
 * It will:
 *   1. Apply saved theme instantly on page load (no flash)
 *   2. Wire any button with id="themeToggleBtn" automatically
 */

(function () {

    const SESSION_KEY = 'vbsSession';
    const HOME_PATH = '/homef.html';

    const PROTECTED_PAGES = {
        'dashboardf.html': 'customer',
        'dashboardf-.html': 'customer',
        'passbookf.html': 'customer',
        'view-profilef.html': 'customer',
        'admin.html': 'admin',
        'admin-.html': 'admin',
        'adminhistory.html': 'admin'
    };

    function currentFileName() {
        const path = (window.location.pathname || '').toLowerCase();
        const parts = path.split('/');
        return parts[parts.length - 1] || '';
    }

    function readSession() {
        try {
            const raw = sessionStorage.getItem(SESSION_KEY);
            if (!raw) return null;
            return JSON.parse(raw);
        } catch {
            return null;
        }
    }

    function clearAuthStorage() {
        localStorage.removeItem('userId');
        localStorage.removeItem('role');
        sessionStorage.removeItem(SESSION_KEY);
    }

    function hasValidSession(requiredRole) {
        const session = readSession();
        const localUserId = localStorage.getItem('userId');
        const localRole = (localStorage.getItem('role') || '').toLowerCase();

        if (!session || !localUserId || !localRole) return false;

        const sessionUserId = String(session.userId || '');
        const sessionRole = String(session.role || '').toLowerCase();

        if (!sessionUserId || !sessionRole) return false;
        if (sessionUserId !== String(localUserId)) return false;
        if (sessionRole !== localRole) return false;
        if (requiredRole && sessionRole !== String(requiredRole).toLowerCase()) return false;

        return true;
    }

    function redirectHome() {
        if (!window.location.pathname.toLowerCase().endsWith('/homef.html')) {
            window.location.replace(HOME_PATH);
        }
    }

    function guardCurrentPage() {
        const page = currentFileName();
        const requiredRole = PROTECTED_PAGES[page];
        if (!requiredRole) return;

        if (!hasValidSession(requiredRole)) {
            clearAuthStorage();
            redirectHome();
        }
    }

    function startSession(role, userId) {
        const cleanRole = String(role || '').toLowerCase();
        const cleanUserId = String(userId || '').trim();
        if (!cleanRole || !cleanUserId) return;

        sessionStorage.setItem(SESSION_KEY, JSON.stringify({
            role: cleanRole,
            userId: cleanUserId,
            createdAt: Date.now()
        }));
    }

    function endSession() {
        clearAuthStorage();
        // Replace current history entry to prevent back-button access to protected pages
        window.history.replaceState({}, document.title, HOME_PATH);
    }

    window.VBSSession = {
        startSession,
        endSession,
        guardCurrentPage,
        hasValidSession
    };

    guardCurrentPage();
    window.addEventListener('pageshow', guardCurrentPage);

    /* ── Apply saved theme immediately (before paint) ── */
    const saved = localStorage.getItem('theme');
    if (saved === 'dark') {
        document.documentElement.classList.add('dark-pending');
    }

    /* ── Once DOM is ready, move class to body + wire toggle ── */
    function init() {

        /* Move class from <html> to <body> (our CSS uses body.dark) */
        if (document.documentElement.classList.contains('dark-pending')) {
            document.documentElement.classList.remove('dark-pending');
            document.body.classList.add('dark');
        }

        wireToggle();
    }

    function wireToggle() {
        const btn = document.getElementById('themeToggleBtn');
        if (!btn) return;

        updateIcon(btn);

        btn.addEventListener('click', () => {
            const isDark = document.body.classList.toggle('dark');
            localStorage.setItem('theme', isDark ? 'dark' : 'light');
            updateIcon(btn);
        });
    }

    function updateIcon(btn) {
        btn.textContent = document.body.classList.contains('dark') ? '☀️' : '🌙';
    }

    /* Run as early as possible */
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();

/* ── Global toast helper: showToast(message, type) and auto-wrap showMessage() ── */
(function () {
    const TOAST_ID = 'vbs-toast-container';

    function injectToastStyles() {
        if (document.getElementById('vbs-toast-styles')) return;
        const css = `
        #${TOAST_ID} { position: fixed; top: 1rem; right: 1rem; z-index: 99999; display:flex; flex-direction:column; gap:8px; }
        .vbs-toast { min-width:200px; max-width:360px; padding:10px 14px; border-radius:6px; color:#fff; box-shadow:0 6px 18px rgba(0,0,0,0.12); font-family:system-ui,Segoe UI,Roboto,Helvetica,Arial; opacity:0; transform:translateY(-8px); transition:opacity .18s ease,transform .18s ease; }
        .vbs-toast.show { opacity:1; transform:translateY(0); }
        .vbs-toast.error { background:#e74c3c; }
        .vbs-toast.success { background:#27ae60; }
        .vbs-toast.info { background:#2d9cdb; }
        `;
        const s = document.createElement('style');
        s.id = 'vbs-toast-styles';
        s.appendChild(document.createTextNode(css));
        (document.head || document.documentElement).appendChild(s);
    }

    function ensureContainer() {
        let c = document.getElementById(TOAST_ID);
        if (!c) {
            c = document.createElement('div');
            c.id = TOAST_ID;
            document.body.appendChild(c);
        }
        return c;
    }

    function showToast(message, type) {
        try {
            if (!message) return;
            injectToastStyles();
            const container = ensureContainer();
            const toast = document.createElement('div');
            toast.className = 'vbs-toast ' + (type === 'success' ? 'success' : (type === 'info' ? 'info' : 'error'));
            toast.textContent = String(message);
            container.appendChild(toast);
            // force reflow to allow transition
            void toast.offsetWidth;
            toast.classList.add('show');
            // remove after 4s
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => toast.remove(), 220);
            }, 4000);
        } catch (e) {
            // no-op
            console.warn('showToast error', e);
        }
    }

    // If pages define showMessage(id,msg,type) later, wrap it so toasts are shown too.
    function wrapShowMessageIfPresent() {
        try {
            if (window.__vbs_show_wrapped) return;
            if (typeof window.showMessage === 'function') {
                const orig = window.showMessage.bind(window);
                window.showMessage = function (id, msg, type) {
                    try { orig(id, msg, type); } catch (e) { /* ignore */ }
                    showToast(msg, type);
                };
                window.__vbs_show_wrapped = true;
            }
        } catch (e) { /* ignore */ }
    }

    // Keep trying to wrap for a short while (pages often define showMessage in inline scripts)
    function attemptWrap() {
        wrapShowMessageIfPresent();
        const max = 10; let tries = 0;
        const iv = setInterval(() => {
            tries += 1;
            wrapShowMessageIfPresent();
            if (window.__vbs_show_wrapped || tries >= max) clearInterval(iv);
        }, 200);
    }

    // Also scan for visible message elements on load (common ids: pageMessage, adminMessage, dashMessage)
    function scanInlineMessageElements() {
        try {
            const sels = ['.message.error', '.message.success', '.alert-danger', '.alert-success', '.text-danger', '#pageMessage', '#adminMessage', '#dashMessage', '.error', '.alert'];
            const seen = new Set();
            sels.forEach(sel => {
                document.querySelectorAll(sel).forEach(el => {
                    if (!el) return;
                    const txt = (el.textContent || el.innerText || '').trim();
                    if (!txt) return;
                    if (seen.has(txt)) return;
                    seen.add(txt);
                    // determine type
                    let type = 'error';
                    const cls = (el.className || '').toLowerCase();
                    if (cls.includes('success')) type = 'success';
                    else if (cls.includes('info')) type = 'info';
                    showToast(txt, type);
                });
            });
        } catch (e) { /* ignore */ }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            attemptWrap();
            scanInlineMessageElements();
        });
    } else {
        attemptWrap();
        scanInlineMessageElements();
    }

    // expose for debugging if needed
    window.vbsShowToast = showToast;

    function toastFromAlertMessage(message) {
        const text = String(message || '');
        const lower = text.toLowerCase();
        if (!text) return;
        if (/(success|saved|updated|deleted|added|created|sent|approved|welcome)/.test(lower)) {
            showToast(text, 'success');
            return;
        }
        if (/(error|failed|invalid|unauthorized|forbidden|missing|not found|please|unable|must|cannot|can't|wrong|problem)/.test(lower)) {
            showToast(text, 'error');
            return;
        }
        showToast(text, 'info');
    }

    if (!window.__vbs_alert_wrapped) {
        window.alert = function (message) {
            try {
                toastFromAlertMessage(message);
            } catch (e) {
                // ignore toast failures
            }
        };
        window.__vbs_alert_wrapped = true;
    }

})();