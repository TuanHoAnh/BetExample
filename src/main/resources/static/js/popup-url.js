/**
 * Created by hungptnguyen on 4/21/2017.
 */
//call pop-up by URL
$('.show-popup').click(function (event) {
    event.preventDefault();
    var contentUrlPopup = "/" + this.hash.substring(1);
    if (!contentUrlPopup) {
        return;
    }
    $('#import-popup').load(contentUrlPopup, function (response, status) {
        if (status === 'error') {
            return;
        }
        $('#import-popup').find('.modal').modal('show');
        history.replaceState({}, '', '#' + contentUrlPopup.substring(1));
    });
});
$(document).ready(function (event) {
    var hashPopup = window.location.hash;
    if (!hashPopup) {
        return;
    }
    if (hashPopup.length > 0 && hashPopup.includes("popup")) {
        var contentUrlPopup = "/" + hashPopup.substring(1);
        $('#import-popup').load(contentUrlPopup, function (response, status) {
            if (status === 'error') {

            } else {
                $('#import-popup').find('.modal').modal('show');
            }
        });
    }


});
//end call pop-up by URL
