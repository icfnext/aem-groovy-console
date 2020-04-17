GroovyConsole.ScheduledJobs = function () {

    var JOBS_URL = '/bin/groovyconsole/jobs.json';

    var table;

    return {
        initialize: function () {
            table = $('.scheduled-jobs').DataTable({
                ajax: JOBS_URL,
                columns: [
                    {
                        className: 'edit-scheduled-job',
                        orderable: false,
                        searchable: false,
                        data: null,
                        defaultContent: '<span class="glyphicon glyphicon-pencil" title="Edit Scheduled Job"></span>'
                    },
                    {
                        data: 'jobTitle'
                    },
                    {
                        data: 'jobDescription',
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

                $.getJSON(JOBS_URL, {'scheduledJobId': data.scheduledJobId}, function (response) {
                    scriptEditor.getSession().setValue(response.script);

                    if (response.data.length) {
                        dataEditor.getSession().setValue(response.data);

                        GroovyConsole.showData();
                    } else {
                        GroovyConsole.hideData();
                    }

                    GroovyConsole.showScheduler();
                    GroovyConsole.reset();

                    $('#scheduler-form input[name="scheduledJobId"]').val(data.scheduledJobId);
                    $('#scheduler-form input[name="jobTitle"]').val(response.jobTitle);
                    $('#scheduler-form input[name="jobDescription"]').val(response.jobDescription);
                    $('#scheduler-form input[name="cronExpression"]').val(response.cronExpression);
                    $('#scheduler-form select[name="mediaType"]').val(response.mediaType);
                    $('#scheduler-form input[type="checkbox"]').prop('checked', false);

                    $('html, body').animate({scrollTop: 0});
                });
            });

            tableBody.on('click', 'td.delete-scheduled-job', function () {
                var tr = $(this).closest('tr');
                var data = table.row(tr).data();

                $.ajax({
                    url: JOBS_URL + '?' + $.param({'scheduledJobId': data.scheduledJobId}),
                    traditional: true,
                    type: 'DELETE'
                }).done(function () {
                    GroovyConsole.ScheduledJobs.showAlert('.alert-success', 'Scheduled job deleted successfully.');
                    GroovyConsole.ScheduledJobs.refreshScheduledJobs();
                }).fail(function () {
                    GroovyConsole.ScheduledJobs.showAlert('.alert-danger', 'Error deleting scheduled job.');
                });
            });
        },

        refreshScheduledJobs: function () {
            table.ajax.url(JOBS_URL).load();
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
    GroovyConsole.ScheduledJobs.initialize();
});