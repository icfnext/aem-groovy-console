var GroovyConsole = function () {

    function showSuccess(message) {
        $('#message-success .message').text(message);
        $('#message-success').fadeIn('fast');

        setTimeout(function () {
            $('#message-success').fadeOut('slow');
        }, 3000);
    }

    function showError(message) {
        $('#message-error .message').text(message);
        $('#message-error').fadeIn('fast');

        setTimeout(function () {
            $('#message-error').fadeOut('slow');
        }, 3000);
    }

    function reset() {
        // clear messages
        $('#message-success .message,#message-error .message').text('');
        $('#message-success,#message-error').fadeOut('fast');

        // clear results
        $('#stacktrace').text('').fadeOut('fast');
        $('#result,#output,#running-time').fadeOut('fast');
        $('#result pre,#output pre,#running-time pre').text('');
    }

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

            editor.getSession().setValue(GroovyConsole.localStorage.loadEditorData());
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

        initializeServicesList: function () {
            $.getJSON('/bin/groovyconsole/services', function (services) {
                $('#services-list').typeahead({
                    source: Object.keys(services),
                    updater: function (key) {
                        var declaration = services[key];

                        editor.navigateFileEnd();

                        if (editor.getCursorPosition().column > 0) {
                            editor.insert('\n\n');
                        }

                        editor.insert(declaration);

                        return '';
                    }
                });

                $('#btn-group-services').fadeIn('fast');
            });
        },

        initializeButtons: function () {
            $('#new-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                reset();

                editor.getSession().setValue('');
            });

            $('#open-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                GroovyConsole.disableToolbar();
                GroovyConsole.showOpenDialog();
            });

            $('#save-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                var script = editor.getSession().getValue();

                if (script.length) {
                    GroovyConsole.disableToolbar();
                    GroovyConsole.showSaveDialog();
                } else {
                    showError('Script is empty.');
                }
            });

            $('#run-script').click(function () {
                if ($('#run-script').hasClass('disabled')) {
                    return;
                }

                reset();

                var script = editor.getSession().getValue();

                if (script.length) {
                    editor.setReadOnly(true);

                    GroovyConsole.disableToolbar();

                    $('#run-script-text').text('Running...');

                    $.post(CQ.shared.HTTP.getContextPath() + '/bin/groovyconsole/post.json', {
                        script: script
                    }).done(function (data) {
                        var result = data.executionResult;
                        var output = data.outputText;
                        var stackTrace = data.stackTraceText;
                        var runtime = data.runningTime;

                        if (stackTrace && stackTrace.length) {
                            $('#stacktrace').text(stackTrace).fadeIn('fast');
                        } else {
                            if (runtime && runtime.length) {
                                $('#running-time pre').text(runtime);
                                $('#running-time').fadeIn('fast');
                            }

                            if (result && result.length) {
                                $('#result pre').text(result);
                                $('#result').fadeIn('fast');
                            }

                            if (output && output.length) {
                                $('#output pre').text(output);
                                $('#output').fadeIn('fast');
                            }
                        }
                    }).fail(function (jqXHR) {
                        if (jqXHR.status == 403) {
                            showError('You do not have permission to run scripts in the Groovy Console.');
                        } else {
                            showError('Script execution failed.  Check error.log file.');
                        }
                    }).always(function () {
                        editor.setReadOnly(false);

                        GroovyConsole.enableToolbar();

                        $('#run-script-text').text('Run Script');
                    });
                } else {
                    showError('Script is empty.');
                }
            });
        },

        disableToolbar: function () {
            $('.btn-toolbar .btn').addClass('disabled');
            $('#loader').css('visibility', 'visible');
        },

        enableToolbar: function () {
            $('#loader').css('visibility', 'hidden');
            $('.btn-toolbar .btn').removeClass('disabled');
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
            reset();

            $.get(CQ.shared.HTTP.getContextPath() + '/crx/server/crx.default/jcr%3aroot' + scriptPath + '/jcr%3Acontent/jcr:data').done(function (script) {
                showSuccess('Script loaded successfully.');

                editor.getSession().setValue(script);
            }).fail(function () {
                showError('Load failed, check error.log file.');
            }).always(function () {
                GroovyConsole.enableToolbar();
            });
        },

        saveScript: function (fileName) {
            reset();

            $.post(CQ.shared.HTTP.getContextPath() + '/bin/groovyconsole/save', {
                fileName: fileName,
                script: editor.getSession().getValue()
            }).done(function (data) {
                showSuccess('Script saved successfully.');
            }).fail(function () {
                showError('Save failed, check error.log file.');
            }).always(function () {
                GroovyConsole.enableToolbar();
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
    GroovyConsole.initializeServicesList();
    GroovyConsole.initializeButtons();
});