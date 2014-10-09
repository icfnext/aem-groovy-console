GroovyConsole.Audit = function () {

    var table;

    return {
        initialize: function () {
            table = $('.audit').DataTable({
                ajax: '/bin/groovyconsole/audit.json',
                columns: [
                    {
                        className: 'open-record',
                        orderable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-eye-open"></span>'
                    },
                    { data: 'date' },
                    {
                        data: 'script',
                        orderable: false
                    },
                    {
                        data: 'exception',
                        orderable: false
                    }
                ],
                order: [[1, 'desc']],
                oLanguage: {
                    sSearch: 'Script Contains: '
                },
                rowCallback: function (row, data) {
                    $('td:eq(1)', row).html('<a href="' + data.link + '">' + data.date + '</a>');
                    $('td:eq(2)', row).html('<code>' + data.script + '</code>');

                    if (data.exception.length) {
                        $('td:eq(3)', row).html('<span class="label label-danger">' + data.exception + '</span>');
                    }
                }
            });

            $('.audit tbody').on('click', 'td.open-record', function () {
                var tr = $(this).closest('tr');
                var row = table.row(tr);
                var script = row.data().relativePath;

                $.getJSON('/bin/groovyconsole/audit.json?script=' + script, function (response) {
                    editor.getSession().setValue(response.script);

                    GroovyConsole.reset();
                    GroovyConsole.showAlerts(response);

                    $('html, body').animate({ scrollTop: 0 });
                });
            });
        },

        refreshAuditRecords: function () {
            table.ajax.url('/bin/groovyconsole/audit.json').load();
        },

        loadAuditRecords: function (startDate, endDate) {
            table.ajax.url('/bin/groovyconsole/audit.json?startDate=' + startDate + '&endDate=' + endDate).load();
        }
    };
}();

$(function () {
    $('#date-range').daterangepicker({
        maxDate: moment()
    }).on('apply.daterangepicker', function(e, picker) {
        var startDate = picker.startDate.format('YYYY-MM-DD');
        var endDate = picker.endDate.format('YYYY-MM-DD');

        GroovyConsole.Audit.loadAuditRecords(startDate, endDate);
    });

    GroovyConsole.Audit.initialize();
});