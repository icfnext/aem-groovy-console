var GroovyConsole = function () {

    return {
        initializeEditor: function () {
            window.editor = ace.edit('editor');

            var GroovyMode = ace.require('ace/mode/groovy').Mode;

            editor.getSession().setMode(new GroovyMode());
            editor.renderer.setShowPrintMargin(false);

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
            editor.getSession().getDocument().on('change', function () {
                GroovyConsole.localStorage.saveEditorData(editor.getSession().getDocument().getValue());
            });
        },

        initializeThemeMenu: function () {
            var theme = GroovyConsole.localStorage.loadTheme();

            editor.setTheme(theme);

            var selectedElement = $.grep($('#dropdown-themes li'), function (element) {
                return $(element).find('a').data('target') == theme;
            });

            if (selectedElement.length) {
                $(selectedElement).addClass('active');
            }

            $('#dropdown-themes li').click(function () {
                var theme = $(this).find('a').data('target');

                editor.setTheme(theme);

                $('#dropdown-themes li').removeClass('active');
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

                // GroovyConsole.reset();
                GroovyConsole.disableToolbar();
                GroovyConsole.showOpenDialog();
            });

            $('#save-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                GroovyConsole.reset();

                var script = editor.getSession().getValue();

                if (script.length) {
                    GroovyConsole.disableToolbar();
                    GroovyConsole.showSaveDialog();
                } else {
                    GroovyConsole.showError('Script is empty.');
                }
            });

            $('#run-script').click(function (event) {
                if ($('#run-script').hasClass('disabled')) {
                    return;
                }

                GroovyConsole.reset();

                var script = editor.getSession().getValue();

                if (script.length) {
                    editor.setReadOnly(true);

                    GroovyConsole.disableToolbar();

                    $('#run-script-text').text('Running...');

                    $.post('/bin/groovyconsole/post.json', {
                        script: script
                    }).done(function (data) {
                        var result = data.executionResult;
                        var output = data.outputText;
                        var stacktrace = data.stacktraceText;
                        var runtime = data.runningTime;

                        if (stacktrace && stacktrace.length) {
                            $('#stacktrace').text(stacktrace).fadeIn('fast');
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
                    }).fail(function () {
                        GroovyConsole.showError('Script execution failed.  Check error.log file.');
                    }).always(function () {
                        editor.setReadOnly(false);

                        GroovyConsole.enableToolbar();

                        $('#run-script-text').text('Run Script');
                    });
                } else {
                    GroovyConsole.showError('Script is empty.');
                }
            });
        },

        disableToolbar: function () {
            $('.btn-toolbar .btn').addClass('disabled');
            $('#loader').fadeIn('fast');
        },

        enableToolbar: function () {
            $('#loader').fadeOut('fast');
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
            GroovyConsole.reset();

            $.get('/crx/server/crx.default/jcr%3aroot' + scriptPath + '/jcr%3Acontent/jcr:data').done(function (script) {
                GroovyConsole.showSuccess('Script loaded successfully.');

                editor.getSession().setValue(script);

                var scriptName = scriptPath.substring(scriptPath.lastIndexOf('/') + 1);

                $('#script-name').text(scriptName).fadeIn('fast');
            }).fail(function () {
                GroovyConsole.showError('Load failed, check error.log file.');
            }).always(function () {
                GroovyConsole.enableToolbar();
            });
        },

        saveScript: function (fileName) {
            $.post('/bin/groovyconsole/save', {
                fileName: fileName,
                scriptContent: editor.getSession().getValue()
            }).done(function () {
                GroovyConsole.showSuccess('Script saved successfully.');
            }).fail(function () {
                GroovyConsole.showError('Save failed, check error.log file.');
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

        reset: function () {
            // clear messages
            $('#message-success .message').text('');
            $('#message-success').fadeOut('fast');
            $('#message-error .message').text('');
            $('#message-error').fadeOut('fast');
            $('#script-name').text('').fadeOut('fast');

            // clear results
            $('#stacktrace').text('').fadeOut('fast');
            $('#result').fadeOut('fast');
            $('#result pre').text('');
            $('#output').fadeOut('fast');
            $('#output pre').text('');
            $('#running-time').fadeOut('fast');
            $('#running-time pre').text('');
        }
    };
}();

GroovyConsole.localStorage = new function () {
    var LS_EDITOR_HEIGHT = 'GroovyConsole.editorHeight';
    var LS_EDITOR_DATA = 'GroovyConsole.editorData';
    var THEME = 'GroovyConsole.theme';

    this.loadValue = function (name, defaultValue) {
        if (Modernizr.localstorage) {
            return window.localStorage[name] || defaultValue || '';
        }

        return defaultValue || '';
    };

    this.saveValue = function (name, value) {
        if (Modernizr.localstorage) {
            window.localStorage[name] = value;
        }
    };

    this.saveEditorHeight = function (value) {
        this.saveValue(LS_EDITOR_HEIGHT, value);
    };

    this.loadEditorHeight = function () {
        return this.loadValue(LS_EDITOR_HEIGHT, $('#editor').css('height'));
    };

    this.saveEditorData = function (value) {
        this.saveValue(LS_EDITOR_DATA, value);
    };

    this.loadEditorData = function () {
        return this.loadValue(LS_EDITOR_DATA, '');
    };

    this.saveTheme = function (value) {
        this.saveValue(THEME, value);
    };

    this.loadTheme = function () {
        return this.loadValue(THEME, 'ace/theme/idle_fingers');
    };
};

$(function () {
    GroovyConsole.initializeEditor();
    GroovyConsole.initializeThemeMenu();
    GroovyConsole.initializeButtons();
});