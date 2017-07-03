/**
 * Created by hungptnguyen on 4/24/2017.
 */
$(document).ready(function (event) {
    var url = window.location.href;
    var requestUrl = url.substring(url.indexOf("/competitions"));
    var dataChart = [];
    $.getJSON(requestUrl + "/total-amount-players", function (dataResponse) {
        if (dataResponse.success){
            var data = $.parseJSON(JSON.stringify(dataResponse));
            $.each(data.data,function (key,val) {
                dataChart.push({y: key, loss: val});
            });
            var line = new Morris.Line({
                element: 'line-chart-betting-players',
                resize: true,
                data: dataChart,
                xkey: 'y',
                ykeys: ['loss'],
                labels: [$('#chart-loss').data('info')],
                lineColors: ['#3c8dbc'],
                hideHover: 'auto',
                parseTime: false
            });
        }
    });

    $('#example2').DataTable({
        "paging": true,
        "lengthChange": false,
        "searching": true,
        "ordering": true,
        "info": true,
        "autoWidth": false,
        "pageLength": 5,
        "language": {
            "info": $('#info-table').data('info'),
            "paginate": {
                "previous": $('#table-previous').data('info'),
                "next": $('#table-next').data('info')
            }
        }
    });
});



