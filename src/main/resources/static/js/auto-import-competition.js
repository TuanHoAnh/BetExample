var $autoCompetitionBtn = $('#auto-competition-button');

// set processing spinner btn
function processSpinner() {

    $autoCompetitionBtn.text($autoCompetitionBtn.data('loading-text'));
    $autoCompetitionBtn.prepend("<i class='fa fa-spinner fa-spin'></i>");
    //disable close pop up buttons
    $(".import-popup").prop('disabled', true);
    $(".import-popup").css("cursor", "pointer");
}

function afterProcessSpinner() {
    $autoCompetitionBtn.text($autoCompetitionBtn.data('save-text'));
    $autoCompetitionBtn.find('i').remove();
    //disable close pop up buttons
    $(".import-popup").prop('disabled', false);
}

$('#competitionsYear').change(function (event) {
    $('#auto-import-comp-id-input').val($(this).val());
    $('#auto-import-comp-name-input').val($("#competitionsYear option:selected").text());
});

$('#importYear').change(function (event) {
    var year = $(this).val(),
        footballToken = $("meta[name='football_token']").attr("content");
    processSpinner();
    $('#competitionsYear').find('option').remove();
    $('#competitionsYear').append("<option selected disabled></option>");
    $('#competitionsYear').prop('disabled',true);
    $.ajax({
        type: 'GET',
        headers: {'X-Auth-Token': footballToken},
        url: 'http://api.football-data.org/v1/competitions/?season=' + year,
        dataType: 'json',
        success: function (data) {
            afterProcessSpinner();
            $('#competitionsYear').prop('disabled',false);
            if (data) {
                data.forEach(function (item) {
                    $('#competitionsYear').append("<option class='competition-add' value='" + item.id + "'>" + item.caption + "</option>");
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


$('#autoImportCompetitionForm').on('submit', function (event) {
    event.preventDefault();

    $('.form-group').removeClass('has-error').find('span').remove();
    processSpinner();

    $('#autoImportCompetitionModal').data('bs.modal').options.backdrop = 'static';

    var aliasKey = $('#import-comp-key-input').val(),
        name = $('#auto-import-comp-name-input').val(),
        id = $('#auto-import-comp-id-input').val(),
        token = $("meta[name='_csrf']").attr("content"),
        header = $("meta[name='_csrf_header']").attr("content");

    var data = {
        "aliasKey": aliasKey,
        "competitionName": name,
        "competitionId": id
    };

    $.ajax({
        type: 'POST',
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        url: $(this).attr('action'),
        data: $('#autoImportCompetitionForm').serialize(),
        success: function (response) {
            afterProcessSpinner();
            if ($(response).find('.has-error').length > 0) {
                $('#autoImportCompetitionForm').replaceWith(response);
            } else if ($(response).find('.error-toastr').length > 0) {
                toastr.error($(response).find("#import-toastr-message div").first().data("message"), "");
                $('#autoImportCompetitionModal').modal('hide');
            } else {
                var url = '/competitions/' + $("#auto-import-comp-key-input").val();
                window.location.hash = url;
                reloadSidebar();
                toastr.success(escapeHtml($("#auto-import-comp-name-input").val()) + " " + $(response).find("#import-toastr-message div").first().data("message"), "");
                $('#autoImportCompetitionModal').modal('hide');
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
