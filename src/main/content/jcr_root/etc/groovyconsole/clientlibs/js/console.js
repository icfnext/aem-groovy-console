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

            var scriptName = GroovyConsole.localStorage.loadScriptName();

            if (scriptName.length) {
                GroovyConsole.setScriptName(scriptName);
            }

            editor.getSession().setValue(GroovyConsole.localStorage.loadEditorData());
            editor.getSession().getDocument().on('change', function () {
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

        initializeAdaptersMenu: function () {
            $.getJSON('/bin/groovyconsole/adapters', function (adapters) {
                $.each(adapters, function (className, script) {
                    var li = $('<li><a href="#">' + className + '</a></li>');

                    li.click(function () {
                        editor.getSession().setValue(script);
                    });

                    $('#dropdown-adapters').append(li);
                });

                $('#btn-group-adapters').fadeIn('fast');
            });
        },

        initializeButtons: function () {
            $('#new-script').click(function () {
                if ($(this).hasClass('disabled')) {
                    return;
                }

                GroovyConsole.reset();
                GroovyConsole.clearScriptName();

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

                    GroovyConsole.disableToolbar();

                    $('#run-script-text').text('Running...');

                    $.post(CQ.shared.HTTP.getContextPath() + '/bin/groovyconsole/post.json', {
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

            $.get(CQ.shared.HTTP.getContextPath() + '/crx/server/crx.default/jcr%3aroot' + scriptPath + '/jcr%3Acontent/jcr:data').done(function (script) {
                GroovyConsole.showSuccess('Script loaded successfully.');

                editor.getSession().setValue(script);

                var scriptName = scriptPath.substring(scriptPath.lastIndexOf('/') + 1);

                GroovyConsole.setScriptName(scriptName);
            }).fail(function () {
                GroovyConsole.showError('Load failed, check error.log file.');
            }).always(function () {
                GroovyConsole.enableToolbar();
            });
        },

        saveScript: function (fileName) {
            GroovyConsole.reset();

            $.post(CQ.shared.HTTP.getContextPath() + '/bin/groovyconsole/save', {
                fileName: fileName,
                script: editor.getSession().getValue()
            }).done(function (data) {
                GroovyConsole.showSuccess('Script saved successfully.');
                GroovyConsole.setScriptName(data.scriptName);
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

        setScriptName: function (scriptName) {
            $('#script-name').text(scriptName).fadeIn('fast');

            GroovyConsole.localStorage.saveScriptName(scriptName);
        },

        clearScriptName: function () {
            $('#script-name').text('').fadeOut('fast');

            GroovyConsole.localStorage.clearScriptName();
        },

        reset: function () {
            // clear messages
            $('#message-success .message').text('');
            $('#message-success').fadeOut('fast');
            $('#message-error .message').text('');
            $('#message-error').fadeOut('fast');

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
    var EDITOR_HEIGHT = 'groovyconsole.editor.height';
    var EDITOR_DATA = 'groovyconsole.editor.data';
    var SCRIPT_NAME = 'groovyconsole.script.name';
    var THEME = 'groovyconsole.theme';

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

    this.clearValue = function (name) {
        if (Modernizr.localstorage) {
            window.localStorage[name] = '';
        }
    };

    this.saveEditorHeight = function (value) {
        this.saveValue(EDITOR_HEIGHT, value);
    };

    this.loadEditorHeight = function () {
        return this.loadValue(EDITOR_HEIGHT, $('#editor').css('height'));
    };

    this.saveEditorData = function (value) {
        this.saveValue(EDITOR_DATA, value);
    };

    this.loadEditorData = function () {
        return this.loadValue(EDITOR_DATA, '');
    };

    this.saveScriptName = function (scriptName) {
        this.saveValue(SCRIPT_NAME, scriptName);
    };

    this.loadScriptName = function () {
        return this.loadValue(SCRIPT_NAME, '');
    };

    this.clearScriptName = function () {
        return this.clearValue(SCRIPT_NAME);
    }

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
    GroovyConsole.initializeAdaptersMenu();
    GroovyConsole.initializeButtons();
});