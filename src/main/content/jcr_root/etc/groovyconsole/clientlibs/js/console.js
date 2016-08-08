var GroovyConsole = function () {

    var resultDataTable;

    return {
        initializeEditor: function () {
            window.editor = ace.edit('editor');

            var GroovyMode = ace.require('ace/mode/groovy').Mode;

            editor.getSession().setMode(new GroovyMode());
            editor.setShowPrintMargin(false);

            var editorDiv = $('#editor');

            editorDiv.resizable({
                resize: function () {
                    editor.resize(true);

                    GroovyConsole.localStorage.saveEditorHeight(editorDiv.height());
                },
                handles: 's'
            });

            editorDiv.css('height', GroovyConsole.localStorage.getEditorHeight());

            var auditRecord = window.auditRecord;

            if (auditRecord) {
                // script loaded from audit
                editor.getSession().setValue(auditRecord.script);

                GroovyConsole.showResult(auditRecord);
            } else {
                editor.getSession().setValue(GroovyConsole.localStorage.getEditorData());
            }

            editor.on('change', function () {
                GroovyConsole.localStorage.saveEditorData(editor.getSession().getDocument().getValue());
            });
        },

        initializeThemeMenu: function () {
            var theme = GroovyConsole.localStorage.getTheme();

            editor.setTheme(theme);

            var themes = $('#dropdown-themes li');

            var selectedElement = $.grep(themes, function (element) {
                return $(element).find('a').data('target') == theme;
            });

            if (selectedElement.length) {
                $(selectedElement).addClass('active');
            }

            themes.click(function () {
                var theme = $(this).find('a').data('target');

                editor.setTheme(theme);

                themes.removeClass('active');
                $(this).addClass('active');

                GroovyConsole.localStorage.saveTheme(theme);
            });
        },

        initializeButtons: function () {
            $('#new-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                GroovyConsole.localStorage.clearScriptName();
                GroovyConsole.reset();

                editor.getSession().setValue('');
            });

            $('#open-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                GroovyConsole.disableButtons();
                GroovyConsole.showOpenDialog();
            });

            $('#save-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                var script = editor.getSession().getValue();

                if (script.length) {
                    GroovyConsole.disableButtons();
                    GroovyConsole.showSaveDialog();
                } else {
                    GroovyConsole.showError('Script is empty.');
                }
            });

            $('#run-script').click(function () {
                if ($('#run-script').hasClass('disabled')) {
                    return;
                }

                GroovyConsole.reset();

                var script = editor.getSession().getValue();

                if (script.length) {
                    editor.setReadOnly(true);

                    GroovyConsole.showLoader();
                    GroovyConsole.disableButtons();

                    $('#run-script-text').text('Running...');

                    $.post(CQ.shared.HTTP.getContextPath() + '/bin/groovyconsole/post.json', {
                        script: script
                    }).done(function (response) {
                        GroovyConsole.showResult(response);
                    }).fail(function (jqXHR) {
                        if (jqXHR.status == 403) {
                            GroovyConsole.showError('You do not have permission to run scripts in the Groovy Console.');
                        } else {
                            GroovyConsole.showError('Script execution failed.  Check error.log file.');
                        }
                    }).always(function () {
                        editor.setReadOnly(false);

                        GroovyConsole.hideLoader();
                        GroovyConsole.enableButtons();
                        GroovyConsole.Audit.refreshAuditRecords();

                        $('#run-script-text').text('Run Script');
                    });
                } else {
                    GroovyConsole.showError('Script is empty.');
                }
            });
            $('body').keydown(function (e) {
                if(e.ctrlKey && e.keyCode == 13){
                    $("#run-script").click();
                }
            }
        },

        reset: function () {
            // clear messages
            $('#message-success .message,#message-error .message').text('');
            $('#message-success,#message-error').fadeOut('fast');

            // clear results
            $('#stacktrace').text('').fadeOut('fast');
            $('#result,#result-table,#output,#running-time').fadeOut('fast');
            $('#result pre,#output pre,#running-time pre').text('');

            var resultTableData = $('#result-table').find('th');

            // destroy datatable and remove columns if it exists
            if (resultDataTable && resultTableData.length) {
                resultTableData.remove();
                resultDataTable.destroy();
            }
        },

        showResult: function (response) {
            var result = response.result;
            var output = response.output;
            var exceptionStackTrace = response.exceptionStackTrace;
            var runningTime = response.runningTime;

            if (exceptionStackTrace && exceptionStackTrace.length) {
                $('#stacktrace').text(exceptionStackTrace).fadeIn('fast');
            } else {
                if (!GroovyConsole.showTable(response) && result && result.length) {
                    $('#result pre').text(result);
                    $('#result').fadeIn('fast');
                }

                if (output && output.length) {
                    $('#output pre').text(output);
                    $('#output').fadeIn('fast');
                }

                if (runningTime && runningTime.length) {
                    $('#running-time pre').text(runningTime);
                    $('#running-time').fadeIn('fast');
                }
            }
        },

        showTable: function (data) {
            var hasTable = false;

            try {
                var json = JSON.parse(data.result).table;

                var resultTableContainer = $('#result-table');

                var resultTable = resultTableContainer.find('table');
                var headerRow = resultTable.find('thead > tr');
                var columns = [];

                $.each(json.columns, function (i, columnName) {
                    headerRow.append('<th>' + columnName + '</th>');
                    columns.push({ title: columnName });
                });

                resultDataTable = resultTable.DataTable({
                    columns: columns,
                    data: json.rows,
                    language: {
                        search: 'Search: '
                    }
                });

                resultTableContainer.fadeIn('fast');

                hasTable = true;
            } catch (e) {

            }

            return hasTable;
        },

        destroyTable: function () {
            if (resultDataTable) {
                resultDataTable.destroy();
            }
        },

        disableButtons: function () {
            $('.btn').addClass('disabled');
        },

        enableButtons: function () {
            $('.btn').removeClass('disabled');
        },

        showLoader: function () {
            $('#loader').css('visibility', 'visible');
        },

        hideLoader: function () {
            $('#loader').css('visibility', 'hidden');
        },

        showSuccess: function (message) {
            $('#message-success .message').text(message);
            $('#message-success').fadeIn('fast');

            setTimeout(function () {
                $('#message-success').fadeOut('slow');
            }, 3000);
        },

        showError: function (message) {
            $('#message-error .message').text(message);
            $('#message-error').fadeIn('fast');

            setTimeout(function () {
                $('#message-error').fadeOut('slow');
            }, 3000);
        },

        showOpenDialog: function () {
            var dialog = CQ.WCM.getDialog('/apps/groovyconsole/components/console/opendialog');

            dialog.show();
        },

        showSaveDialog: function () {
            var dialog = CQ.WCM.getDialog('/apps/groovyconsole/components/console/savedialog');

            dialog.show();
        },

        loadScript: function (scriptPath) {
            GroovyConsole.reset();

            $.get(CQ.shared.HTTP.getContextPath() + scriptPath + '/jcr:content/jcr:data').done(function (script) {
                GroovyConsole.localStorage.saveScriptName(scriptPath);
                GroovyConsole.showSuccess('Script loaded successfully.');

                editor.getSession().setValue(script);
            }).fail(function () {
                GroovyConsole.showError('Load failed, check error.log file.');
            }).always(function () {
                GroovyConsole.enableButtons();
            });
        },

        saveScript: function (fileName) {
            GroovyConsole.reset();
            GroovyConsole.localStorage.saveScriptName(fileName);

            $.post(CQ.shared.HTTP.getContextPath() + '/bin/groovyconsole/save', {
                fileName: fileName,
                script: editor.getSession().getValue()
            }).done(function () {
                GroovyConsole.showSuccess('Script saved successfully.');
            }).fail(function () {
                GroovyConsole.showError('Save failed, check error.log file.');
            }).always(function () {
                GroovyConsole.enableButtons();
            });
        },

        refreshOpenDialog: function (dialog) {
            var tp, root;

            if (dialog.loadedFlag == null) {
                dialog.loadedFlag = true;
            } else {
                tp = dialog.treePanel;
                root = tp.getRootNode();

                tp.getLoader().load(root);
                root.expand();
            }
        }
    };
}();

$(function () {
    GroovyConsole.initializeEditor();
    GroovyConsole.initializeThemeMenu();
    GroovyConsole.initializeButtons();
});
