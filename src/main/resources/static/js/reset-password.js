/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

$(document).ready(function () {
    $('#cancelChangePassword').on('click', function () {
        clearFormDataAndAlert();
    });

//Reset popup form when user click outside popup
    $('#forgotPasswordModal').on('hidden.bs.modal', function () {
        clearFormDataAndAlert();
    });

    function clearFormDataAndAlert() {
        $('#reset-password-form').trigger('reset');
        $('#reset-password-form .alert').addClass('hide');
        $('#submitChangePassword').removeAttr('disabled');
    }

    $('#reset-password-form').on('submit', function (event) {
        event.preventDefault();

        $('#modalSuccess').addClass('hide');
        $('#modalFail').addClass('hide');
        $('#modalUserNotActivate').addClass('hide');

        var email = $('#email').val();
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var data = {'email': email};

        $.ajax({
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            url: '/user/reset-password-init',
            data: JSON.stringify(data),
            success: function (data) {
                if (data.success) {

                    if (data.data.second) {
                        $('#modalSuccess').removeClass('hide');
                        $('#submitChangePassword').prop('disabled', true);
                        window.setTimeout(function () {
                            $('#forgotPasswordModal').modal('hide');
                        }, 6000);
                    }
                    //if user is not active registration link
                    else {
                        $('#modalUserNotActivate').removeClass('hide');
                    }

                } else {
                    $('#modalFail').removeClass('hide');
                }
            },
            error: function (e) {
                console.log("Error while send email reset-password to server: " + e);
            }
        });
    });
});

