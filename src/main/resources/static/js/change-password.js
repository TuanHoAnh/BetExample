$('#changePasswordLink').on('click', function () {
    $('#success').addClass('hide');
    $('#failer').addClass('hide');

    $('#currentPassword').removeClass('has-error').find('span').remove();
    $('#newPassword').removeClass('has-error').find('span').remove();
    $('#confirmNewPassword').removeClass('has-error').find('span').remove();

    $('#currentPasswordField').val("");
    $('#newPasswordField').val("");
    $('#confirmNewPasswordField').val("");
});

var token = $("meta[name='_csrf']").attr("content"),
    header = $("meta[name='_csrf_header']").attr("content"),
    update_profile_url = $('#updateProfile').attr('action');

$('#changePassword').on('submit', function (event) {
    event.preventDefault();
    $('#success').addClass('hide');
    $('#failer').addClass('hide');
    $('#currentPassword').removeClass('has-error').find('span').remove();
    $('#newPassword').removeClass('has-error').find('span').remove();
    $('#confirmNewPassword').removeClass('has-error').find('span').remove();


    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type: 'POST',
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        url: '/user/change-password',
        data: $('#changePassword').serialize(),
        success: function (data) {
            if ($(data).find('.has-error').length > 0) {
                $('#change-password-content').replaceWith(data);
                $('#failer').removeClass('hide');
            } else {
                $('#success').removeClass('hide');
            }
        },
        fail: function (e) {
            console.log(e);
        },
        error: function (e) {
            console.log(e);
        }
    });
});
