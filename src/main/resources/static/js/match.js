/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */
$(document).ready(function () {
    function showUpdateFailMessage() {
        $('#updateMatchScore [name="update-fail"]').removeClass('hide');
    }

    function showStartTimeMessage() {
        $('#updateMatchScore [name="start-time-fail"]').removeClass('hide');
    }

    function showOtherMessage(message) {
        $('#updateMatchScore [name="other-fail"]').removeClass('hide')
            .prepend("<div class='alert alert-warning'><p>" + message.substr(1) + "</p></div>");
    }

    function setRequestHeader(request) {
        var token = $("meta[name='_csrf']").attr("content")
        var header = $("meta[name='_csrf_header']").attr("content");
        request.setRequestHeader(header, token);
    }

    function hideErrorMessage() {
        $('#updateMatchScore .message.error-message').addClass('hide');
    }

    function resetUpdateMatchScoreModal() {
        $('#updateMatchScore').removeData("match-id");
        $('#updateMatchScore [name="competitor1-name"]').val("?");
        $('#updateMatchScore [name="score1"]').val(null);
        $('#updateMatchScore [name="competitor2-name"]').val("?");
        $('#updateMatchScore [name="score2"]').val(null);
    }

    function updateMatchScoreForm(updateMatchScore) {
        $('#updateMatchScore').data("match-id", updateMatchScore.id);

        var competitor1Name = !!updateMatchScore.competitor1.name ? updateMatchScore.competitor1.name : "?";
        var competitor2Name = !!updateMatchScore.competitor2.name ? updateMatchScore.competitor2.name : "?";

        $('#updateMatchScore [name="competitor1-name"]').text(competitor1Name);
        $('#updateMatchScore [name="competitor2-name"]').text(competitor2Name);

        $('#updateMatchScore [name="score1"]').val(updateMatchScore.score1);
        $('#updateMatchScore [name="score2"]').val(updateMatchScore.score2);
    }

    function updateMatchScore(id, match) {
        var matchRow = $("#matchView #match-" + id);
        var score1 = $.isNumeric(match.score1) ? match.score1 : "?";
        var score2 = $.isNumeric(match.score2) ? match.score2 : "?";
        matchRow.find('[name="score"]').text(score1 + " - " + score2);
    }

    $(document).on("hidden.bs.modal", '#updateMatchScoreModal', function () {
        hideErrorMessage();
        resetUpdateMatchScoreModal();
    });

    $(document).on('shown.bs.modal', "#updateMatchScoreModal", function (event) {
        event.preventDefault();
        var matchId = $(event.relatedTarget).data('match-id');
        $.ajax({
            type: 'GET',
            beforeSend: setRequestHeader,
            url: '/api/match/' + matchId,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
                if (!!response.success) {
                    updateMatchScoreForm(response.data);
                }
            },
            fail: function (e) {
                showLoadFailMessage();
                console.log(e);
            },
            error: function (e) {
                showLoadFailMessage();
                console.log(e);
            }
        });
    });

    $(document).on('submit', '#updateMatchScore', function (event) {
        event.preventDefault();
        hideErrorMessage();

        var matchId = $('#updateMatchScore').data("match-id");
        var score1 = $('#updateMatchScore [name="score1"]').val();
        var score2 = $('#updateMatchScore [name="score2"]').val();

        var data = {
            "score1": score1,
            "score2": score2
        };

        // set processing spinner btn
        var submitBtn = $('#updateMatchScore [type="submit"]');
        var originalBtn = $('#updateMatchScore [type="submit"]').clone();
        submitBtn.text(submitBtn.data('loading-text'));
        submitBtn.prepend("<i class='fa fa-spinner fa-spin'></i>");

        //disable close pop up buttons
        $("#updateMatchScore button").prop('disabled', true);
        $("#updateMatchScore button").css("cursor", "pointer");

        $.ajax({
            type: 'PUT',
            beforeSend: setRequestHeader,
            url: '/api/match/update-match-score/' + matchId,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            success: function (response) {
                $("#updateMatchScore button").prop('disabled', false);
                submitBtn.replaceWith(originalBtn);

                if (!!response.success) {
                    $('#updateMatchScoreModal').modal('hide');
                    updateMatchScore(matchId, data);
                    toastr.success(
                        $("#updateMatchScore").data("success-message"),
                        $("#updateMatchScore").data("success-title")
                    );
                } else {
                    if ($.isArray(response.data)) {
                        showOtherMessage(response.data[0].defaultMessage);
                    }else {
                        showStartTimeMessage();
                    }
                }
            },
            fail: function (e) {
                showUpdateFailMessage();
                $("#updateMatchScore button").prop('disabled', true);
                submitBtn.replaceWith(originalBtn);
                console.log(e);
            },
            error: function (e) {
                showUpdateFailMessage();
                $("#updateMatchScore button").prop('disabled', true);
                submitBtn.replaceWith(originalBtn);
                console.log(e);
            }
        });
    });
});





