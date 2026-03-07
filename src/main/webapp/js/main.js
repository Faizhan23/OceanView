/**
 * Ocean View Resort – Main JavaScript
 * Client-side enhancement only. No business logic.
 */

document.addEventListener('DOMContentLoaded', function () {

    // ── Date Validation for Reservation Form ──────────────────────────────────
    const today    = new Date().toISOString().split('T')[0];
    const checkIn  = document.getElementById('checkIn');
    const checkOut = document.getElementById('checkOut');

    if (checkIn)  checkIn.min  = today;
    if (checkOut) checkOut.min = today;

    if (checkIn && checkOut) {
        checkIn.addEventListener('change', function () {
            if (checkOut.value && checkOut.value <= this.value) {
                checkOut.value = '';
            }
            checkOut.min = this.value;
        });
    }

    // ── Auto-dismiss alerts after 5 seconds ──────────────────────────────────
    setTimeout(function () {
        document.querySelectorAll('.alert-success, .alert-info').forEach(function (el) {
            el.style.transition = 'opacity 0.5s';
            el.style.opacity = '0';
            setTimeout(function () { el.remove(); }, 500);
        });
    }, 5000);

    // ── Confirm Dangerous Actions ─────────────────────────────────────────────
    document.querySelectorAll('[data-confirm]').forEach(function (el) {
        el.addEventListener('click', function (e) {
            if (!confirm(el.dataset.confirm)) {
                e.preventDefault();
            }
        });
    });

    // ── Table Row Click (navigate to detail) ─────────────────────────────────
    document.querySelectorAll('tr[data-href]').forEach(function (row) {
        row.style.cursor = 'pointer';
        row.addEventListener('click', function () {
            window.location.href = row.dataset.href;
        });
    });
});
