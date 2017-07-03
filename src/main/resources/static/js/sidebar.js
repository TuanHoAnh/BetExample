function updateMainContent(html) {
    var mainHeader = $(html).find('.content-header');
    var mainContent = $(html).find('.content');

    if(!!mainHeader.length){
        $('.content-header').replaceWith(mainHeader);
    }

    if(!!mainContent.length){
        $('.content').replaceWith(mainContent);
    } else {
        window.location.pathname = "/signin?timeout";
    }
}

function loadContent(url) {
        $.ajax({
           type: 'GET',
           beforeSend: setRequestHeader,
           url: url,
           success: function (response) {
               updateMainContent(response);
           },
           fail: function (e) {
               console.log(e);
           },
           error: function (e) {
               console.log(e);
           }
        });
}

$(window).on('hashchange', function() {
    var hashString = window.location.hash.substring(1);

    if(!window.location.hash){
        loadContent(window.location.pathname);
    }

    if (!hashString) {
        return;
    }

    loadContent(hashString);
});

function setRequestHeader(request) {
    var token = $("meta[name='_csrf']").attr("content")
    var header = $("meta[name='_csrf_header']").attr("content");
    request.setRequestHeader(header, token);
}

function collapse() {
   $('#add-group-competition').removeClass('add-dropdown');
   $('#add-group-competition').addClass('add-dropdown-collapse');
   $('#add-group-competition').removeClass('pull-right');
}

function uncollapse() {
   $('#add-group-competition').addClass('add-dropdown');
   $('#add-group-competition').removeClass('add-dropdown-collapse');
   $('#add-group-competition').addClass('pull-right');
}



function reloadSidebar() {
    $.ajax({
       type: 'GET',
       beforeSend: setRequestHeader,
       url: "/sidebar?hashUrl=" + encodeURIComponent(window.location.hash),
       success: function (response) {
            $('#sidebarCompetitions').replaceWith(response);
       },
       fail: function (e) {
           console.log(e);
       },
       error: function (e) {
           console.log(e);
       }
    });
}

function resetSidebar() {
    $.ajax({
       type: 'GET',
       beforeSend: setRequestHeader,
       url: "/sidebar?hashUrl=",
       success: function (response) {
            $('#sidebarCompetitions').replaceWith(response);
       },
       fail: function (e) {
           console.log(e);
       },
       error: function (e) {
           console.log(e);
       }
    });
}

$(document).ready(function () {

   $('.sidebar-toggle').mouseup(function (e) {
        setTimeout(function(){
            if ($('body').hasClass('sidebar-open sidebar-collapse')) {
                $('body').removeClass('sidebar-open');
                collapse();
            } else if ($('body').hasClass('sidebar-collapse sidebar-open')) {
                $('body').removeClass('sidebar-collapse');
                uncollapse();
            } else if ($('.sidebar-mini').hasClass('sidebar-collapse')) {
                collapse();
            } else {
                uncollapse();
            }
        }, 1);

   });

    $(document).on("click", ".treeview", function (e) {
        var url = '/competitions/' + $(this).data("alias-key");

        if (!$(this).hasClass("active") && !$(this).hasClass("active-competition")) {
            $(this).removeClass('active').addClass('active-competition active').siblings().removeClass('active-competition');
            window.location.hash = url;
            reloadSidebar();
        } else if (url != window.location.hash) {
            window.location.hash = url;
        }

    });

    $('body').on("click", ".group-name", function (e) {
        e.stopPropagation();
        $(this).addClass('active').siblings().removeClass('active');
        $(this).closest('li.treeview').removeClass('active').addClass('active-competition active');
        $(this).closest('li.treeview').find('ul').css("display", "block");
        $(this).closest('li.treeview').siblings().removeClass('active-competition').removeClass('active');
        $(this).closest('li.treeview').siblings().find('ul').css("display", "none");
    });

    $('body').on("click", ".notifications-menu", function (e) {
        resetSidebar();
    });

    $('body').on("click", ".pull-left", function (e) {
        resetSidebar();
    });
});

