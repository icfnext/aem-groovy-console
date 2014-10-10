GroovyConsole.Audit = function () {

    var table;

    function showAlert(selector, text) {
        var alert = $('#history ' + selector);

        alert.text(text).fadeIn('fast');

        setTimeout(function () {
            alert.fadeOut('slow');
        }, 3000);
    }

    return {
        initialize: function () {
            table = $('.audit').DataTable({
                ajax: '/bin/groovyconsole/audit.json',
                columns: [
                    {
                        className: 'open-record',
                        orderable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-upload" title="Load Script"></span>'
                    },
                    { data: 'date' },
                    {
                        data: 'scriptPreview',
                        orderable: false
                    },
                    {
                        data: 'exception',
                        orderable: false
                    },
                    {
                        className: 'delete-record',
                        orderable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-trash" title="Delete Record"></span>'
                    }
                ],
                order: [[1, 'desc']],
                oLanguage: {
                    sSearch: 'Script Contains: '
                },
                rowCallback: function (row, data) {
                    $('td:eq(1)', row).html('<a href="' + data.link + '">' + data.date + '</a>');
                    $('td:eq(2)', row).html('<code>' + data.scriptPreview + '</code>');

                    if (data.exception.length) {
                        $('td:eq(3)', row).html('<span class="label label-danger">' + data.exception + '</span>');
                    } else {
                        $('td:eq(3)', row).html('<span class="label label-success">OK</span>');
                    }
                }
            });

            var tableBody = $('.audit tbody');

            tableBody.on('click', 'td.open-record', function () {
                var tr = $(this).closest('tr');
                var script = table.row(tr).data().relativePath;

                $.getJSON('/bin/groovyconsole/audit.json?script=' + script, function (response) {
                    editor.getSession().setValue(response.script);

                    GroovyConsole.reset();
                    GroovyConsole.showAlerts(response);

                    $('html, body').animate({ scrollTop: 0 });
                });
            });

            tableBody.on('click', 'td.delete-record', function () {
                var tr = $(this).closest('tr');
                var script = table.row(tr).data().relativePath;

                $.ajax({
                    url: '/bin/groovyconsole/audit.json?script=' + script,
                    type: 'DELETE'
                }).done(function () {
                    showAlert('.alert-success', 'Audit record deleted successfully.');

                    GroovyConsole.Audit.refreshAuditRecords();
                }).fail(function () {
                    showAlert('.alert-danger', 'Error deleting audit record.');
                });
            });
        },

        refreshAuditRecords: function () {
            table.ajax.reload();
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