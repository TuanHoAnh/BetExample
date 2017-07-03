/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */



$('body').on('click', 'a[href^="#/"]', function(event) {
    event.preventDefault();
    var hashString = this.hash.substring(1);
    if (!hashString) {
        return;
    }

    $('.content-wrapper').load(hashString, function (response, status) {
        if (status === 'error') {
            toastr.error('Resource not found!', 'Error!');
        }

        history.replaceState({}, '', '#' + hashString);
    });
});

function escapeHtml(str) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

$(document).ready(function (event) {
    $('[id="matchView"]').DataTable({
        oLanguage: {
            sSearch: "Filter records:"
        }
    });
});

$(document).ready(function (event) {
    reloadSidebar();

    var hash = window.location.hash;
    if (!hash) {
        return;
    }

    var contentUrl = hash.substring(1);
    if (contentUrl.includes("/popup/")){
        return;
    }

    if (contentUrl.includes("_=_")){
        return;
    }

    $('.content-wrapper').load(contentUrl, function(response, status) {
        if (status === 'error') {
            toastr.error('Resource not found!', 'Error!');
        }
    });

        $('#createModeratorRequestForm').on('submit', function (event) {
        event.preventDefault();

        $('#success').addClass('hide');
        $('#failer').addClass('hide');
        $('#bettingGroupNameDiv').removeClass('has-error').find('span').remove();

        var bettingGroupName = $('#bettingGroupName').val();
        var competitionId = $('#competitionId').val();
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var data = {
            "bettingGroupName": bettingGroupName,
            "competitionId": competitionId
        };
        $.ajax({
            type: 'POST',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            url: '/bettingGroup/submit-request',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            success: function (data) {
                if (data.success) {
                    $('#success').removeClass('hide');
                    window.setTimeout(function () {
                        $('#createModeratorRequest').modal('hide');

                    }, 1000);

                } else {
                    $('#failer').removeClass('hide');

                    data.data.forEach(function (item) {
                        if (item.field === 'bettingGroupName') {
                            $('#bettingGroupNameDiv')
                                .addClass('has-error')
                                .append("<span class='help-block'>" + item.defaultMessage + "</span>");
                        }
                    });
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

    $('.updateMatchForm').on('submit', function (event) {
        event.preventDefault();
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var aliasKey = $("#competitonAliasKey").val();
        var matchIdUpdate = $(this).find("input[name='matchId']").val();
        var data = {
            "aliasKey": aliasKey,
            "id": matchIdUpdate,
        };
        $.ajax({
            type: 'GET',
            contentType: 'charset=utf-8',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            url: '/competitions/view/' + aliasKey + '/createMatchPopup',
            data: data,
            success: function (responseData) {
                $('#createMatchPopup').html(responseData);
                $('#createMatchModal').modal('show');
            },
            fail: function (e) {
                console.log(e);
            },
            error: function (e) {
                console.log(e);
            }
        });
    });
});






















