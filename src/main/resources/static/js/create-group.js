$(function () {
    var aliasKey;
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var competitionAliasKey = "[[${competitionAliasKey}]]";
    $(document).on("click", "#createBettingGroupButton", function () {
        aliasKey = $("#competitonAliasKey").val();
        $.ajax({
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            url: "/competitions/" + aliasKey + "/bettings/create",
            method: "GET",
            success: function (data) {
                $("#createBettingGroupFragment").replaceWith(data);
                $("#competitionAliasKeyBettingGroup").val(aliasKey);
            },
            fail: function (e) {
                console.log(e);
            },
            error: function (e) {
                console.log(e);
            }
        });
    });
    $(document).on('input', "#moderatorNameBettingGroup", function () {
        var username = $(this).val();
        $("#moderatorList").html("");
        $.ajax({
            url: "/competitions/" + aliasKey + "/bettings/moderators?username=" + username,
            method: 'GET',
            dataType: "json",
            success: function (data) {
                var userList = data
                for (var user in userList) {
                    if (userList[user].username === username) {
                        $("#moderatorIdBettingGroup").val(userList[user].id);
                        return;
                    }
                    $("#moderatorList").append("<option value=\"" + userList[user].username + "\" ></option>");
                }
                $("#moderatorIdBettingGroup").val("");
            },
            fail: function (e) {
                console.log(e);
            },
            error: function (e) {
                console.log(e);
            }
        });
    });

    $(document).on('submit', "#createBettingGroupForm", function (event) {
        event.preventDefault();
        $('#bettingGroupNameGroup').removeClass('has-error').find('span').remove();
        $('#moderatorBettingGroupGroup').removeClass('has-error').find('span').remove();
        var $form = $(this);
        $.ajax({
            type: 'POST',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            url: $form.attr('action'),
            data: $form.serialize(),
            success: function (response) {
                if ($(response).find('.has-error').length) {
                    $("#createBettingGroupFragment").replaceWith(response);
                } else {
                    createBettingGroupSuccessCase();
                    reloadSidebar();
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
    function createBettingGroupSuccessCase() {
        toastr.success($('#success').val());
        window.setTimeout(function () {
            $('#createBettingGroupPopup').modal('hide');
        }, 2000);
    }
});
