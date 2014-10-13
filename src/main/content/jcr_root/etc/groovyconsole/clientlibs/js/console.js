var GroovyConsole = function () {

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

            editorDiv.css('height', GroovyConsole.localStorage.loadEditorHeight());

            var auditRecord = window.auditRecord;

            if (auditRecord) {
                // script loaded from audit
                editor.getSession().setValue(auditRecord.script);

                GroovyConsole.showAlerts(auditRecord);
            } else {
                editor.getSession().setValue(GroovyConsole.localStorage.loadEditorData());
            }

            editor.on('change', function () {
                GroovyConsole.localStorage.saveEditorData(editor.getSession().getDocument().getValue());
            });
        },

        initializeThemeMenu: function () {
            var theme = GroovyConsole.localStorage.loadTheme();

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
                    }).done(function (data) {
                        GroovyConsole.showAlerts(data);
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
        },

        reset: function () {
            // clear messages
            $('#message-success .message,#message-error .message').text('');
            $('#message-success,#message-error').fadeOut('fast');

            // clear results
            $('#stacktrace').text('').fadeOut('fast');
            $('#result,#output,#running-time').fadeOut('fast');
            $('#result pre,#output pre,#running-time pre').text('');
        },

        showAlerts: function (response) {
            var result = response.result;
            var output = response.output;
            var exceptionStackTrace = response.exceptionStackTrace;
            var runningTime = response.runningTime;

            if (exceptionStackTrace && exceptionStackTrace.length) {
                $('#stacktrace').text(exceptionStackTrace).fadeIn('fast');
            } else {
                if (result && result.length) {
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

            $.get(CQ.shared.HTTP.getContextPath() + '/crx/server/crx.default/jcr%3aroot' + scriptPath + '/jcr%3Acontent/jcr:data').done(function (script) {
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