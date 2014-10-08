GroovyConsole.Audit = function () {

    var table;

    return {
        initialize: function () {
            table = $('.audit').DataTable({
                ajax: '/bin/groovyconsole/audit.json',
                columns: [
                    {
                        orderable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-refresh"></span>'
                    },
                    { data: 'date' },
                    { data: 'script' },
                    { data: 'success' },
                    { data: 'permalink' }
                ],
                order: [[1, 'desc']],
                oLanguage: {
                    sSearch: 'Script Contains: '
                },
                rowCallback: function (row, data) {
                    var success = data.success;

                    var script = data.script;

                    $('td:eq(2)', row).html('<code>' + script + '</code>');

                    /*
                    var fileName = data.fileName;

                    if (fileName.length) {
                        $('td:eq(2)', row).html('<a href="' + data.fileHref + '">' + fileName + '</a>');
                    }
                    */

                    $('td:eq(3)', row).html(success ? 'Yes' : 'No');
                }
            });
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