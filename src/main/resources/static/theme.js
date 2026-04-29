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