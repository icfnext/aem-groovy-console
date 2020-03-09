GroovyConsole.Scheduler = function () {

    var SCHEDULER_URL = '/bin/groovyconsole/jobs.json';

    var table;

    return {
        initialize: function () {
            table = $('.scheduled-jobs').DataTable({
                ajax: SCHEDULER_URL,
                columns: [
                    {
                        className: 'edit-scheduled-job',
                        orderable: false,
                        searchable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-pencil" title="Edit Scheduled Job"></span>'
                    },
                    {
                        data: 'title'
                    },
                    {
                        data: 'description',
                        orderable: false
                    },
                    {
                        data: 'script',
                        orderable: false
                    },
                    {
                        data: 'cronExpression',
                        orderable: false,
                        searchable: false
                    },
                    {
                        data: 'nextExecutionDate',
                        searchable: false
                    },
                    {
                        className: 'delete-scheduled-job',
                        orderable: false,
                        searchable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-trash" title="Delete Scheduled Job"></span>'
                    }
                ],
                language: {
                    emptyTable: 'No scheduled jobs found.',
                    search: 'Contains: ',
                    zeroRecords: 'No matching scheduled jobs found.',
                    info: 'Showing _START_ to _END_ of _TOTAL_ jobs',
                    infoEmpty: '',
                    infoFiltered: '(filtered from _MAX_ total jobs)'
                },
                rowCallback: function (row, data) {
                    $('td:eq(3)', row).html('<code>' + data.scriptPreview + '</code><div class="hidden">' + data.script + '</div>');
                    $('td:eq(3)', row).popover({
                        container: 'body',
                        content: '<pre>' + data.script + '</pre>',
                        html: true,
                        placement: 'top',
                        trigger: 'hover'
                    });
                }
            });

            var tableBody = $('.scheduled-jobs tbody');

            tableBody.on('click', 'td.edit-scheduled-job', function () {
                var tr = $(this).closest('tr');
                var data = table.row(tr).data();

                $.getJSON(SCHEDULER_URL, {'id': data.id}, function (response) {
                    scriptEditor.getSession().setValue(response.script);

                    if (response.data.length) {
                        dataEditor.getSession().setValue(response.data);

                        GroovyConsole.showData();
                    } else {
                        GroovyConsole.hideData();
                    }

                    GroovyConsole.showScheduler();
                    GroovyConsole.reset();

                    $('#scheduler-form input[name="title"]').val(response.title);
                    $('#scheduler-form input[name="description"]').val(response.description);
                    $('#scheduler-form input[name="cronExpression"]').val(response.cronExpression);
                    $('#scheduler-form input[type="checkbox"]').prop('checked', false);

                    $('html, body').animate({scrollTop: 0});
                });
            });

            tableBody.on('click', 'td.delete-scheduled-job', function () {
                var tr = $(this).closest('tr');
                var data = table.row(tr).data();

                $.ajax({
                    url: SCHEDULER_URL + '?' + $.param({'id': data.id}),
                    traditional: true,
                    type: 'DELETE'
                }).done(function () {
                    GroovyConsole.Scheduler.showAlert('.alert-success', 'Scheduled job deleted successfully.');
                    GroovyConsole.Scheduler.refreshScheduledJobs();
                }).fail(function () {
                    GroovyConsole.Scheduler.showAlert('.alert-danger', 'Error deleting scheduled job.');
                });
            });
        },

        refreshScheduledJobs: function () {
            table.ajax.url(SCHEDULER_URL).load();
        },

        showAlert: function (selector, text) {
            var alert = $('#scheduled-jobs ' + selector);

            alert.text(text).fadeIn('fast');

            setTimeout(function () {
                alert.fadeOut('slow');
            }, 3000);
        }
    };
}();

$(function () {
    GroovyConsole.Scheduler.initialize();
});