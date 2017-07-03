$(document).ready(function () {
    $(document).on('submit','#createModeratorRequestForm', function (event) {
        event.preventDefault();
        $('#success').addClass('hide');
        $('#failer').addClass('hide');
        $('#bettingGroupNameDiv').removeClass('has-error').find('span').remove();
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: $('#createModeratorRequestForm').attr('action'),
            data: $('#createModeratorRequestForm').serialize(),
            type: 'POST',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if ($(response).find('.has-error').length == 0) {
                    window.setTimeout(function () {
                        $('#success').removeClass('hide');
                        $('#createModeratorRequest').modal('hide');
                        toastr.success($('#successMessage').val());
                    }, 2000);
                } else $('#failer').removeClass('hide');
                window.setTimeout(function () {
                    $('#responseReplace').replaceWith(response);
                    $("#bettingGroupName").focus().select();
                }, 1000);
                reloadSidebar();
            },
            fail: function (e) {
                console.log(e);
            },
            error: function (e) {
                console.log(e);
            }
        });
    });

    $(document).on('click','#showPopup', function (e) {
        $("#createModeratorRequest").find("input[type=text], textarea").val("");
        $("#createModeratorRequest").find(".form-group").removeClass("has-error");
        $("#createModeratorRequest").find(".help-block").addClass("hide");
        $("#bettingGroupNameDiv").append("<span class='glyphicon glyphicon-envelope form-control-feedback'></span>");
    });
});
