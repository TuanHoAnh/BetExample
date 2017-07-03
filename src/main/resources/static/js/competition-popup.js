/**
 * Created by hungptnguyen on 4/19/2017.
 */
$(document).ready(function () {
    $(document).on('click', '#btn-publish-competition', function (event) {
        event.stopPropagation();
        event.preventDefault();
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var aliasKey = $("#competitonAliasKey").val();
        var name = $("#competition-name").val();
        $.ajax({
            type: 'POST',
            beforeSend: function(request) {
                request.setRequestHeader(header, token);
            },
            url:'/competitions/' + aliasKey + '/publish',
            dataType: 'json',
            cache: false,
            contentType: false,
            processData: false,
            success: function(data) {
                if (data.success) {
                    $('#publishCompetitionForm').find('#btn-publish-competition').css('display', 'none');
                    toastr.success(name+' '+ $("#publishCompetitionForm").data("publish-success-message"));
                } else {
                    toastr.error(name+' '+ $("#publishCompetitionForm").data("publish-fail-message"));
                }
            },
            fail: function(e) {
                console.log(e);
            },
            error: function(e) {
                console.log(e);
            }
        });
    });

});
