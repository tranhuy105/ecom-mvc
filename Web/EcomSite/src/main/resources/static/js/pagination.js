$(document).ready(function() {
    $('.pagination .page-link').on('click', function(e) {
        e.preventDefault();
        const page = $(this).data('page');

        if (page !== undefined) {
            const currentUrl = new URL(window.location.href);
            const currentParams = new URLSearchParams(currentUrl.search);
            currentParams.set('page', page);
            window.location.href = currentUrl.pathname + '?' + currentParams.toString();
        }
    });

    $(document).ready(function() {
        $('#clearButton').on('click', function() {
            let url = new URL(window.location.href);
            url.search = '';
            window.location.href = url.toString();
        });
    });
});