$(document).ready(function () {
    $('#addNewMatchPopup').click(function (event) {
        event.preventDefault();
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var aliasKey = $("#competitonAliasKey").val();
        var data = {
            "aliasKey": aliasKey,
            "id": "",
        };
        $.ajax({
            type: 'GET',
            contentType: 'charset=utf-8',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            url: '/competitions/' + aliasKey + '/createMatchPopup',
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

