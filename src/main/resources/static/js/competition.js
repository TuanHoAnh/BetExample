function onPublishButtonClick() {
    $('#is-pressed-publish').val('pressed');
}

function onCancelButtonClick() {
    removeFieldData();
    removeMessage();
}
function removeFieldData(){
    $('#comp-key-input').val('');
    $('#comp-name-input').val('');
    $('#comp-rounds-input').tagsinput('items').length = 0;
    $('#competitionRounds').removeClass('has-error').find('span').remove();
    $('#logo-input').val('');
    $('#competition-logo-upload').attr('src','/images/worldcup.jpg');

}

function removeMessage() {
    $('#competitionForm .error-msg').remove();
    $('#competitionNumberOfCompetitor').removeClass('has-error').find('a').remove();
    $('#competitionNumberOfMatch').removeClass('has-error').find('a').remove();
}
$(document).ready(function () {
    function addErrorField(field, msg) {
         field.addClass('has-error')
         .prepend("<div class='help-block error-msg' align='center'> <i class='fa fa-times-circle'></i>"+ ' ' + msg + "</div>");
    }

     var fileLogo;
     var validFile = true;

     function getExtension(filename) {
             var parts = filename.split('.');
             return parts[parts.length - 1];
     }

     $(document).on('change', '#logo-input', function(event) {
         $('#competitionLogo .error-msg').remove();
         fileLogo = event.target.files[0];
         validFile = true;
         if (!$(this).attr('accept').includes(getExtension(fileLogo.name))) {
                     addErrorField($('#invalid-logo-message'),' '+ $("#competitionForm").data("invalid-type-message"));
                     validFile = false;
         } else if (fileLogo.size > $(this).attr('max-size')) {
            addErrorField($('#invalid-logo-message'),' '+ $("#competitionForm").data("invalid-size-message"));
            validFile = false;
         }
         if (validFile) {
             var imageObjectUrl = window.URL || window.webkitURL;
             var imageLogoToBeValidateType = new Image();
             imageLogoToBeValidateType.onload = function () {
                 validFile = true;
                 $('#competition-logo-upload')
                     .attr('src', imageLogoToBeValidateType.src)
                     .width(100)
                     .height(100);
                 $('#logo-field').append("<span>"+fileLogo.name+"</span>");
             };
             imageLogoToBeValidateType.onerror = function () {
                 addErrorField($('#invalid-logo-message'),' '+ $("#competitionForm").data("invalid-type-message"));
                 validFile = false;
             };
             imageLogoToBeValidateType.src = imageObjectUrl.createObjectURL(fileLogo);
         }
     });

     function isEmpty(str) {
         return (!str || 0 === str.length);
     }
     $(document).on('submit', '#competitionForm', function (event){
         event.stopPropagation();
         event.preventDefault();
         removeMessage();
         var aliasKey = $('#comp-key-input').val();
         var name = $('#comp-name-input').val();
         var logo = $('#logo').val();
         var status = $('#status').val();
         var id = $('#id-competition').val();
         var round = $('#comp-rounds-input').tagsinput('items');
         var numberOfCompetitor = $('#number-of-competitor').val();
         var numberOfMatch = $('#number-of-match').val();
         var isPressedPublish = $('#is-pressed-publish').val();
         var token = $("meta[name='_csrf']").attr("content");
         var header = $("meta[name='_csrf_header']").attr("content");

         var form = new FormData();
         form.append("aliasKey", aliasKey);
         form.append("name", name);
         form.append("rounds", round);
         form.append("id", id);
         form.append("numberOfCompetitor", numberOfCompetitor);
         form.append("numberOfMatch", numberOfMatch);
         form.append("isPressedPublish", isPressedPublish);
         form.append("status", status);
         if (validFile) {
              form.append("logoFile", fileLogo);
         }
     $.ajax({
        type: 'POST',
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        url: '/competitions/save',
        dataType: 'json',
        cache: false,
        contentType: false,
        processData: false,
        data: form,
        success: function(data) {
            if (data.success) {
                if (isPressedPublish == 'pressed') {
                    $('#competitionForm').find('#btn-publish').css('display', 'none');
                    toastr.success(escapeHtml(name) + ' '+ $("#competitionForm").data("publish-message"));
                } else if(isEmpty(id)){
                    toastr.success(escapeHtml(name) + ' '+ $("#competitionForm").data("create-message"));
                } else {
                    toastr.success(escapeHtml(name) + ' '+ $("#competitionForm").data("update-message"));
                }

                window.location.hash = '/competitions/'+ aliasKey;
                reloadSidebar();
            } else {
                data.data && data.data.forEach(function(item) {
                switch(item.field) {
                    case 'aliasKey':
                        addErrorField($('#invalid-alias-key-message'), item.defaultMessage);
                        break;
                    case 'name':
                        addErrorField($('#invalid-name-message'), item.defaultMessage);
                        break;
                    case 'rounds':
                        addErrorField($('#invalid-rounds-message'), item.defaultMessage);
                        break;
                    case 'logo':
                        addErrorField($('#invalid-logo-message'), item.defaultMessage);
                        break;
                    case 'numberOfCompetitor':
                        addErrorField($('#competitionNumberOfCompetitor'), item.defaultMessage);
                        break;
                    case 'numberOfMatch':
                        addErrorField($('#competitionNumberOfMatch'), item.defaultMessage);
                        break;
                }
            });


            }
        },
        fail: function(e) {
            console.log(e);
        },
        error: function(e) {
            console.log(e);
        }
     });
     $('#is-pressed-publish').val('');
     });
});

