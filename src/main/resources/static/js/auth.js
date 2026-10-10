/**
 * CampusConnect - Authentication & Navigation Script
 */

const Auth = {
    async getCurrentUser() {
        try {
            const res = await fetch('/api/auth/me', {
                headers: { 'Accept': 'application/json' }
            });
            if (!res.ok) return null;
            const data = await res.json();
            return data.data || null;
        } catch (e) {
            return null;
        }
    },

    async updateNavbar() {
        const user = await this.getCurrentUser();
        const navAuth = document.getElementById('navAuth');
        const navLinks = document.getElementById('navLinks');

        if (!navAuth) return;

        if (user) {
            let roleBadge = user.role === 'ADMIN' ? 'Admin' : 'Student';
            let specificLink = user.role === 'ADMIN'
                ? `<li class="nav-item"><a class="nav-link" id="adminDashboardLink" href="/admin-dashboard.html">Admin Dashboard</a></li>`
                : `<li class="nav-item"><a class="nav-link" id="myRegistrationsLink" href="/my-registrations.html">My Registrations</a></li>`;

            if (navLinks) {
                // Remove existing dynamic links if any
                const existingSpecific = document.getElementById('adminDashboardLink') || document.getElementById('myRegistrationsLink');
                if (existingSpecific) existingSpecific.parentElement.remove();
                navLinks.insertAdjacentHTML('beforeend', specificLink);
            }

            navAuth.innerHTML = `
                <span class="user-badge" id="userBadge">${user.fullName} (${roleBadge})</span>
                <button class="btn btn-outline btn-sm" id="logoutButton" onclick="Auth.logout()">Logout</button>
            `;
        } else {
            navAuth.innerHTML = `
                <a class="btn btn-outline btn-sm" id="navLoginBtn" href="/login.html">Login</a>
                <a class="btn btn-primary btn-sm" id="navRegisterBtn" href="/register.html">Register</a>
            `;
        }
    },

    async logout() {
        try {
            await fetch('/api/auth/logout', { method: 'POST' });
        } catch (e) {
            console.error(e);
        }
        window.location.href = '/login.html';
    },

    async requireAuth(requiredRole = null) {
        const user = await this.getCurrentUser();
        if (!user) {
            window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname + window.location.search);
            return null;
        }
        if (requiredRole && user.role !== requiredRole) {
            // Role forbidden
            alert('Access denied: You do not have permission to view this page.');
            window.location.href = user.role === 'ADMIN' ? '/admin-dashboard.html' : '/events.html';
            return null;
        }
        return user;
    }
};

document.addEventListener('DOMContentLoaded', () => {
    Auth.updateNavbar();
});
