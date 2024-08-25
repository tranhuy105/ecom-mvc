$(document).ready(function() {
    $('#mainSearchBar').on('submit', function(event) {
        event.preventDefault();

        const query = $('#searchInput').val();
        const baseUrl = '/site/products';

        const url = new URL(window.location.href);
        const params = new URLSearchParams(url.search);

        if (query) {
            params.set('q', query);
        } else {
            params.delete('q');
        }

        window.location.href = baseUrl + '?' + params.toString();
    });
});