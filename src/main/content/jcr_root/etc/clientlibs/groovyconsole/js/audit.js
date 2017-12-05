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
                        defaultContent: '<span class="glyphicon glyphicon-upload" title="Load Script"></span>'
                    },
                    {
                        data: 'date',
                        searchable: false
                    },
                    {
                        data: 'script',
                        orderable: false
                    },
                    {
                        data: 'exception',
                        orderable: false
                    },
                    {
                        className: 'delete-record',
                        orderable: false,
                        searchable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-trash" title="Delete Record"></span>'
                    }
                ],
                order: [[1, 'desc']],
                language: {
                    emptyTable: 'No audit records found.',
                    search: 'Script Contains: ',
                    zeroRecords: 'No matching audit records found.',
                    info: 'Showing _START_ to _END_ of _TOTAL_ records',
                    infoEmpty: '',
                    infoFiltered: '(filtered from _MAX_ total records)'
                },
                initComplete: function (settings, json) {

                },
                rowCallback: function (row, data) {
                    $('td:eq(1)', row).html('<a href="' + data.link + '">' + data.date + '</a>');
                    $('td:eq(2)', row).html('<code>' + data.scriptPreview + '</code><div class="hidden">' + data.script + '</div>');
                    $('td:eq(2)', row).popover({
                        container: 'body',
                        content: '<pre>' + data.script + '</pre>',
                        html: true,
                        placement: 'top',
                        trigger: 'hover'
                    });

                    if (data.exception.length) {
                        $('td:eq(3)', row).html('<span class="label label-danger">' + data.exception + '</span>');
                    }
                }
            });

            var tableBody = $('.audit tbody');

            tableBody.on('click', 'td.open-record', function () {
                var tr = $(this).closest('tr');
                var data = table.row(tr).data();
                var params = $.param({ userId: data.userId, script: data.relativePath });

                $.getJSON('/bin/groovyconsole/audit.json?' + params, function (response) {
                    scriptEditor.getSession().setValue(response.script);

                    if (response.data.length) {
                        dataEditor.getSession().setValue(response.data);

                        GroovyConsole.showData();
                    } else {
                        GroovyConsole.hideData();
                    }

                    GroovyConsole.reset();
                    GroovyConsole.showResult(response);

                    $('html, body').animate({ scrollTop: 0 });
                });
            });

            tableBody.on('click', 'td.delete-record', function () {
                var tr = $(this).closest('tr');
                var data = table.row(tr).data();
                var params = $.param({ userId: data.userId, script: data.relativePath });

                $.ajax({
                    url: '/bin/groovyconsole/audit.json?' + params,
                    type: 'DELETE'
                }).done(function () {
                    GroovyConsole.Audit.showAlert('.alert-success', 'Audit record deleted successfully.');
                    GroovyConsole.Audit.refreshAuditRecords();
                }).fail(function () {
                    GroovyConsole.Audit.showAlert('.alert-danger', 'Error deleting audit record.');
                });
            });

            $('#delete-all-modal .btn-warning').click(function () {
                $.ajax({
                    url: '/bin/groovyconsole/audit.json',
                    type: 'DELETE'
                }).done(function () {
                    GroovyConsole.Audit.showAlert('.alert-success', 'Audit records deleted successfully.');
                    GroovyConsole.Audit.refreshAuditRecords();
                }).fail(function () {
                    GroovyConsole.Audit.showAlert('.alert-danger', 'Error deleting audit records.');
                }).always(function () {
                    $('#delete-all-modal').modal('hide');
                });
            });
        },

        initializeDatePicker: function () {
            var dateRange = $('#date-range');

            dateRange.daterangepicker({
                maxDate: moment()
            }).on('apply.daterangepicker', function(e, picker) {
                var startDate = picker.startDate.format('YYYY-MM-DD');
                var endDate = picker.endDate.format('YYYY-MM-DD');

                GroovyConsole.Audit.loadAuditRecords(startDate, endDate);
            });

            $('#date-range-clear').click(function () {
                dateRange.val('');

                GroovyConsole.Audit.refreshAuditRecords();
            });
        },

        refreshAuditRecords: function () {
            table.ajax.url('/bin/groovyconsole/audit.json').load(function (json) {
                if (json.data.length) {
                    $('.delete-all').removeClass('hidden');
                } else {
                    $('.delete-all').addClass('hidden');
                }
            });
        },

        loadAuditRecords: function (startDate, endDate) {
            table.ajax.url('/bin/groovyconsole/audit.json?startDate=' + startDate + '&endDate=' + endDate).load();
        },

        showAlert: function (selector, text) {
            var alert = $('#history ' + selector);

            alert.text(text).fadeIn('fast');

            setTimeout(function () {
                alert.fadeOut('slow');
            }, 3000);
        }
    };
}();

$(function () {
    GroovyConsole.Audit.initialize();
    GroovyConsole.Audit.initializeDatePicker();
});