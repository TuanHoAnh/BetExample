$(".bet-button").on("click", function () {
    var competitionAliasKey = $("#competitionAliasKey").val();
    var groupId = $("#groupId").val();
    var bettingMatchId=$(this).attr("betting-match-id");
    $.ajax({
        url: "/competitions/"+competitionAliasKey+"/bettings/"+groupId+"/matches/"+bettingMatchId+"/bet",
        type: "GET",
        success: function (data) {
            $("#bettingMatchDetailForBet").replaceWith(data);
            $("#bettingMatchId").val(bettingMatchId);
        },
        fail: function (e) {
            console.log("Fail when get bet detail: ",e);
        },
        error: function (e) {
            console.log("Error when get bet detail: ", e);
        }
    });
});

$(document).on('click', '.bet-competitor', function () {

    var competitionAliasKey = $("#competitionAliasKey").val();
    var groupId = $("#groupId").val();
    var bettingMatchId=$("#bettingMatchId").val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var competitorId = $(this).attr("competitor-id");
    $.ajax({
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        url: "/competitions/"+competitionAliasKey+"/bettings/"+groupId+"/matches/"+bettingMatchId+"/bet/" + competitorId,
        type: "POST",
        success: function (data) {

            $("#bettingMatchDetailForBet").replaceWith(data);
            if($("#matchExpiredMessage").val()){
                toastr.error($("#matchExpiredMessage").val());
            }
        },
        fail: function (e) {
            console.log("Fail when select competitor: ", e);
        },
        error: function (e) {
            console.log("Error when select competitor: ", e);
        }
    });
});

$(".users-list").append("<li>\
        <a href=\"javascript:void(0)\" class=\"uppercase\">View All Users</a>\
    </li>")
/**
 * Created by thaoho on 4/26/2017.
 */
