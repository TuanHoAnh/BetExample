$(document).ready(function () {

    var fileLogo;
    var validFile = false;
    var logoPath = "";
    var competitorId = null;

    function updateCompetitorTable() {
        var  aliasKey =  $('#competitonAliasKey').val() ;
        $("#competitorListBlock").load('/competitor/getList/'+aliasKey);
    }

    function checkExtension(file){
        return file['type'].split('/')[0] === 'image';
    }

    function addErrorField(field, msg) {
        field.addClass('has-error')
            .prepend("<div align='center' class='error-msg help-block'><i class='fa fa-times-circle'></i>" +' '+ msg + "</div>");
    }

    function getExtension(filename) {
        var parts = filename.split('.');
        return parts[parts.length - 1];
    }

    function resetCompetitorFormError() {
        $("#competitor-form .error-msg").remove();
        $('#competitorName').removeClass('has-error');
        $('#create-competitor-successfully').find('span').remove();
        $('#competitorLogo').removeClass('has-error');
    }

    function resetCompetitorFormField() {
        $('#nameCompetitor').val('');
        $('#competitor-logo-upload')
            .attr('src', '/images/default/competitor-logo.jpg')
            .width(100)
            .height(100);
        $('#competitor-logo').val(null);
        competitorId = null;
        fileLogo = null;
        logoPath = null;
        validFile = false;
    }

    function resetCompetitorModalTitle() {
        $('#competitorModal .modal-title').addClass('hidden');
    }

    function setCompetitorModalTitle(isCreate) {
        resetCompetitorModalTitle();

        if(!!isCreate) {
            $('#competitorModal .modal-title.create').removeClass('hidden');
        } else {
            $('#competitorModal .modal-title.update').removeClass('hidden');
        }
    }

    function updateCompetitorForm(Competitor) {
        $('#competitor-form').data("competitor-id", Competitor.id);
        var competitorName = !!Competitor.name ? Competitor.name : "";
        $('#competitor-form').find('#nameCompetitor').val(competitorName);
        $('#competitor-form').find('#competitor-logo-upload').attr("src", Competitor.logo);
        logoPath = Competitor.logo;
    }

    $(document).on('change', '#competitor-logo', function (event) {
        $('#competitorLogo .error-msg ').remove();
        $('#competitorLogo').removeClass('has-error');

        fileLogo = event.target.files[0];
        validFile = true;
        var reader = new FileReader();



        if (!checkExtension(fileLogo)) {
            addErrorField($('#competitorLogo'),' '+ $("#competitor-form").data("logo-type-error"));
            $('#competitor-logo').val(null);
            $('#competitor-logo-upload')
                .attr('src', '/images/default/competitor-logo.jpg')
                .width(100)
                .height(100);
            validFile = false;
        }

        if (validFile) {
            var imageObjectUrl = window.URL || window.webkitURL;
            var imageLogoToBeValidateType = new Image();
            imageLogoToBeValidateType.onload = function () {
                $('#competitor-logo-upload')
                    .attr('src', imageLogoToBeValidateType.src)
                    .width(100)
                    .height(100);
            };
            imageLogoToBeValidateType.onerror = function () {
                addErrorField($('#competitorLogo'), ' ' + $("#competitor-form").data("logo-type-error"));
                $('#competitor-logo').val(null);
                validFile = false;
            };
            imageLogoToBeValidateType.src = imageObjectUrl.createObjectURL(fileLogo);
        }

        if ((fileLogo.size > $(this).attr('max-size')) && validFile) {
            addErrorField($('#competitorLogo'),' '+  $("#competitor-form").data("logo-size-error"));
            validFile = false;
        }

    });


    function updateCompetitorForm(Competitor) {
        $('#competitor-form').data("competitor-id", Competitor.id);
        var competitorName = Competitor.name;
        $('#competitor-form').find('#nameCompetitor').val(competitorName);
        $('#competitor-form').find('#competitor-logo-upload').attr("src", Competitor.logo);

        logoPath = Competitor.logo;
    }
    $(document).on('shown.bs.modal', "#competitorModal", function (event) {
        event.preventDefault();
        competitorId = $(event.relatedTarget).data('competitor-id');
        setCompetitorModalTitle(!competitorId);

        if (!!competitorId) {
            $.ajax({
                type: 'GET',
                beforeSend: setRequestHeader,
                url: '/api/competitor/' + competitorId,
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                success: function (response) {
                    if (!!response.success) {
                        updateCompetitorForm(response.data);
                    }
                },
                fail: function (e) {
                    console.log(e);
                },
                error: function (e) {
                    console.log(e);
                }
            });
        }
        else {
            $('#modal-header').find('#popup-title').text($("#modal-header").data("create-title"));
        }
    });

    $(document).on("hidden.bs.modal", "#competitorModal", function () {
        resetCompetitorFormError();
        resetCompetitorFormField()
        resetCompetitorModalTitle()
        loadContent(window.location.hash.substring(1));
    });;

    $(document).on('submit', '#competitor-form', function (event) {
        event.preventDefault();
        resetCompetitorFormError();

        var form = new FormData();
        form.append("name", $('#nameCompetitor').val());
        form.append("competitionId", $('#competition-id').val());

        if (!!competitorId){
            form.append("id", competitorId);
        }
        if (validFile){
            form.append("logoFile", fileLogo);
        }
        if (!$.isEmptyObject(logoPath)){
            form.append("logo", logoPath);
        }

        $.ajax({
            type: 'POST',
            beforeSend: setRequestHeader,
            url: '/competitor/save',
            dataType: 'json',
            cache: false,
            contentType: false,
            processData: false,
            data: form,
            success: function (data) {
                if (data.success) {
                    $('#competitorModal').modal('hide');
                    toastr.success(escapeHtml($('#nameCompetitor').val()) + ' '+ $("#competitor-form").data(competitorId==null ? "create-successfully" : "update-successfully"));
                    resetCompetitorFormField();
                } else {

                    if (data !== null) {
                        data.data.forEach(function (item) {
                            if (item.field === 'name') {
                                $('#competitorName')
                                    .addClass('has-error')
                                    .prepend("<div align='center' class='error-msg help-block'><i class='fa fa-times-circle'></i>"+ ' ' + item.defaultMessage + "</div>");
                            }

                            if (item.code === 'ValidCompetitor') {
                                $('#competitorName')
                                    .addClass('has-error')
                                    .prepend("<div align='center' class='error-msg help-block'><i class='fa fa-times-circle'></i>"+ ' ' + item.defaultMessage + "</div>");
                            }
                        });
                    }
                }
            },
            fail: function (e) {
                console.log("Fail load list" + e);
            },
            error: function (e) {
                console.log("error load list" + e);
            }
        });
    });
});
