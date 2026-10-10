/**
 * CampusConnect - Common Utilities
 */

const CC = {
    showAlert(elementId, message, type = 'error') {
        const el = document.getElementById(elementId);
        if (!el) return;
        el.textContent = message;
        el.className = `alert alert-${type}`;
        el.style.display = 'block';
    },

    hideAlert(elementId) {
        const el = document.getElementById(elementId);
        if (el) {
            el.style.display = 'none';
            el.textContent = '';
        }
    },

    clearAllAlerts() {
        ['errorMessage', 'successMessage', 'validationMessage'].forEach(id => this.hideAlert(id));
    },

    formatDate(dateStr) {
        if (!dateStr) return '';
        const options = { year: 'numeric', month: 'short', day: 'numeric', weekday: 'short' };
        try {
            const d = new Date(dateStr + 'T00:00:00');
            return d.toLocaleDateString('en-US', options);
        } catch (e) {
            return dateStr;
        }
    },

    formatTime(timeStr) {
        if (!timeStr) return '';
        const parts = timeStr.split(':');
        let hours = parseInt(parts[0], 10);
        let minutes = parts[1];
        let ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12;
        return `${hours}:${minutes} ${ampm}`;
    },

    getCategoryBadgeClass(category) {
        const cat = (category || '').toLowerCase();
        if (cat.includes('tech')) return 'badge-technical';
        if (cat.includes('cult')) return 'badge-cultural';
        if (cat.includes('sport')) return 'badge-sports';
        return 'badge-workshops';
    }
};
