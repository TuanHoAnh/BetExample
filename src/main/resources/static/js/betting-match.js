$('#createBettingMatchLink').on('click', function () {
    $('#publishsuccess').addClass('hide');
    $('#publishfail').addClass('hide');
    $('#updatefail').addClass('hide');
    $('#success').addClass('hide');
    $('#failer').addClass('hide');
    $('#checkDateTime').addClass('hide');
    $('#updateFailTimeAndActivate').addClass('hide');
    $('#matchId').removeClass('has-error').find('span').remove();
    $('#balance1').removeClass('has-error').find('span').remove();
    $('#balance2').removeClass('has-error').find('span').remove();
    $('#bettingAmount').removeClass('has-error').find('span').remove();
    $('#date').removeClass('has-error').find('span').remove();
    $('#time').removeClass('has-error').find('span').remove();
    $('#checkTimeAndActivate').find('p').remove();


    $('#balance1').val("");
    $('#balance2').val("");
    $('#bettingAmount').val("");
    $('#date').val("");
    $('#time').val("");

});

$('#updateBettingMatchLink').on('click', function () {

    $('#publishsuccess').addClass('hide');
    $('#publishfail').addClass('hide');
    $('#updatefail').addClass('hide');
    $('#success').addClass('hide');
    $('#failer').addClass('hide');
    $('#updateFailTimeAndActivate').addClass('hide');
    $('#checkDateTime').addClass('hide');
    $('#matchId').removeClass('has-error').find('span').remove();
    $('#balance1').removeClass('has-error').find('span').remove();
    $('#balance2').removeClass('has-error').find('span').remove();
    $('#bettingAmount').removeClass('has-error').find('span').remove();
    $('#date').removeClass('has-error').find('span').remove();
    $('#time').removeClass('has-error').find('span').remove();
    $('#checkTimeAndActivate').find('p').remove();

    $('#balance1').val("");
    $('#balance2').val("");
    $('#bettingAmount').val("");
    $('#date').val("");
    $('#time').val("");

});

$('#activateBettingMatch').on('click', function () {

    $('#publishsuccess').addClass('hide');
    $('#publishfail').addClass('hide');
    $('#updatefail').addClass('hide');
    $('#success').addClass('hide');
    $('#failer').addClass('hide');
    $('#updateFailTimeAndActivate').addClass('hide');
    $('#checkDateTime').addClass('hide');
    $('#matchId').removeClass('has-error').find('span').remove();
    $('#balance1').removeClass('has-error').find('span').remove();
    $('#balance2').removeClass('has-error').find('span').remove();
    $('#bettingAmount').removeClass('has-error').find('span').remove();
    $('#date').removeClass('has-error').find('span').remove();
    $('#time').removeClass('has-error').find('span').remove();
    $('#checkTimeAndActivate').find('p').remove();

    $('#balance1').val("");
    $('#balance2').val("");
    $('#bettingAmount').val("");
    $('#date').val("");
    $('#time').val("");
});

$('#createBettingMach').on('submit', function (event) {
    event.preventDefault();
    $('#publishsuccess').addClass('hide');
    $('#publishfail').addClass('hide');
    $('#updatefail').addClass('hide');
    $('#success').addClass('hide');
    $('#failer').addClass('hide');
    $('#updateFailTimeAndActivate').addClass('hide');
    $('#matchId').removeClass('has-error').find('span').remove();
    $('#balance1').removeClass('has-error').find('span').remove();
    $('#balance2').removeClass('has-error').find('span').remove();
    $('#bettingAmount').removeClass('has-error').find('span').remove();
    $('#date').removeClass('has-error').find('span').remove();
    $('#time').removeClass('has-error').find('span').remove();
    $('#checkTimeAndActivate').find('p').remove();



    var groupId = $('#groupId').val();
    var aliasKey = $('#aliasKey').val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type: 'POST',
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        url: '/competitions/' + aliasKey + '/bettings/' + groupId + '/matches',
        data: $('#createBettingMach').serialize(),
        success: function (data) {
            if ($(data).find('.has-error').length > 0) {
                $('#match-content').replaceWith(data);
                $(".preventzero").keyup(function () {
                    var value = $(this).val();
                    value = value.replace(/^(0*)/, "");
                    $(this).val(value);
                });
                $('#datepicker').datepicker({
                    autoclose: true,
                    format: "yyyy-mm-dd"
                });
                $('.clockpicker').clockpicker({
                    placement: 'top',
                    align: 'left',
                    autoclose: true
                });
            } else {
                window.setTimeout(function () {
                    $('#createBettingMatchModal').modal('hide');
                    toastr.success($('#SuccessMessage').val());
                }, 2000);
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

$('#activateBettingMatch').click(function () {
    $('#checkTimeAndActivate').addClass('hide');
    $('#publishsuccess').addClass('hide');
    $('#publishfail').addClass('hide');
    $('#success').addClass('hide');
    $('#failer').addClass('hide');
    $('#matchId').removeClass('has-error').find('span').remove();
    $('#balance1').removeClass('has-error').find('span').remove();
    $('#balance2').removeClass('has-error').find('span').remove();
    $('#bettingAmount').removeClass('has-error').find('span').remove();
    $('#date').removeClass('has-error').find('span').remove();
    $('#time').removeClass('has-error').find('span').remove();
    $('#checkTimeAndActivate').find('p').remove();

    var activate = document.getElementById('activate').value = 'true';
    var groupId = $('#groupId').val();
    var aliasKey = $('#aliasKey').val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type: 'POST',
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        url: '/competitions/' + aliasKey + '/bettings/' + groupId + '/matches',
        data: $('#createBettingMach').serialize(),
        success: function (data) {
            if ($(data).find('.has-error').length > 0) {
                $('#match-content').replaceWith(data);
                $(".preventzero").keyup(function () {
                    var value = $(this).val();
                    value = value.replace(/^(0*)/, "");
                    $(this).val(value);
                });
                $('#datepicker').datepicker({
                    autoclose: true,
                    format: "yyyy-mm-dd"
                });
                $('.clockpicker').clockpicker({
                    placement: 'top',
                    align: 'left',
                    autoclose: true
                });
            } else {
                window.setTimeout(function () {
                    $('#createBettingMatchModal').modal('hide');
                    toastr.success($('#publishSuccessMessage').val());
                }, 2000);
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
$(function(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var groupId = $('#groupId').val();
    var aliasKey = $('#aliasKey').val();
    $.ajax({
        type:'GET',
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        url: '/competitions/' + aliasKey + '/bettings/'+groupId+'/matches-by-round?round='+ $('#roundName').val(),
        dataType: "json",
        success: function (matches) {
            if (matches) {
                for (var match in matches) {
                    $("#name").append("<option value='" + matches[match].id + "'>" + matches[match].name + "</option>");
                }
            }
        },
        fail: function (e) {
            console.log(e);
        },
        error: function (e) {
            console.log(e);
        }
    });
    $(document).on("change","#roundName", function() {
        var roundName = $('#roundName').val();
        $("#name").html("");
        $.ajax({
            type:'GET',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            url: '/competitions/' + aliasKey + '/bettings/' + groupId +'/matches-by-round?round=' +roundName,
            dataType: "json",
            success: function (matches) {
                if (matches) {
                    for (var match in matches) {
                        $("#name").append("<option value='" + matches[match].id + "'>" + matches[match].name + "</option>");
                    }
                }
            },
            fail: function (e) {
                console.log(e);
            },
            error: function (e) {
                console.log(e);
            }
        })
    });
});



