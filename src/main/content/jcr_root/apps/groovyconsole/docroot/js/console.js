showOpenDialog = function() {
    var dialog = CQ.WCM.getDialog('/apps/groovyconsole/components/console/opendialog');
    dialog.show();
}

showSaveDialog = function() {
    var dialog = CQ.WCM.getDialog('/apps/groovyconsole/components/console/savedialog');
    dialog.show();
}

loadScript = function(scriptPath) {
    CQ.Ext.Ajax.request({
        url: '/crx/server/crx.default/jcr%3aroot' + scriptPath + '/jcr%3Acontent/jcr:data',
        success: function(response, opts) {
            if (response && response.responseText) {
                editor.getSession().setValue(response.responseText);
            }
        },
        failure: function(response, opts) {
            showError('Load failed with status: ' + response.status);
        }
    });
}

saveScript = function(fileName) {
    var params = {};

    params['fileName'] = fileName;
    params['scriptContent'] = editor.getSession().getValue();

    CQ.Ext.Ajax.request({
        url: '/bin/groovyconsole/save',
        params: params,
        method: 'POST',
        failure: function (result, request) {
            showError('Save failed with status: ' + result.status);
        }
    });
}

refreshOpenDialog = function(dialog) {
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

initialize = function(path) {
    window.editor = ace.edit('editor');

    var GroovyMode = ace.require('ace/mode/groovy').Mode;

    editor.getSession().setMode(new GroovyMode());
    editor.renderer.setShowPrintMargin(false);

    var theme = $.cookie('theme');

    if (theme == null) {
        theme = 'ace/theme/solarized_dark';
    }

    editor.setTheme(theme);

    var selectedElement = $.grep($('#dropdown-themes li'), function(element) {
        return $(element).find('a').data('target') == theme;
    });

    if (selectedElement.length) {
        $(selectedElement).addClass('active');
    }

    $('#dropdown-themes li').click(function() {
        var theme = $(this).find('a').data('target');

        editor.setTheme(theme);

        $('#dropdown-themes li').removeClass('active');

        $(this).addClass('active');

        $.cookie('theme', theme, { expires: 365 });
    });

    // buttons
    $('#new-script').click(function() {
        if ($(this).hasClass('disabled')) {
            return;
        }

        resetConsole();

        editor.getSession().setValue('');
    });

    $('#open-script').click(function() {
        if ($(this).hasClass('disabled')) {
            return;
        }

        showOpenDialog();
    });

    $('#save-script').click(function() {
        if ($(this).hasClass('disabled')) {
            return;
        }

        showSaveDialog();
    });

    $('#run-script').click(function(event) {
        if ($('#run-script').hasClass('disabled')) {
            return;
        }

        resetConsole();

        var script = editor.getSession().getValue();

        if (script.length) {
            editor.setReadOnly(true);

            $('.btn-toolbar .btn').addClass('disabled');
            $('#run-script-text').text('Running...');
            $('#loader').fadeIn('fast');

            $.ajax({
                type: 'POST',
                url: path,
                data: { script: script },
                dataType: 'json'
            }).done(function(data) {
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
            }).fail(function() {
                showError('CQ5 server error.  Check error.log file.');
            }).always(function() {
                editor.setReadOnly(false);

                $('#loader').fadeOut('fast');
                $('#run-script-text').text('Run Script');
                $('.btn-toolbar .btn').removeClass('disabled');
            });
        } else {
            showError('Script is empty.  Please try again.');
        }
    });
}

function showError(message) {
    $('div.alert-error .message').text(message);
    $('div.alert-error').fadeIn('fast');
}

function resetConsole() {
    // clear errors
    $('div.alert-error .message').text('');
    $('div.alert-error').fadeOut('fast');

    // clear results
    $('#stacktrace').text('').fadeOut('fast');
    $('#result').fadeOut('fast');
    $('#result pre').text('');
    $('#output').fadeOut('fast');
    $('#output pre').text('');
    $('#running-time').fadeOut('fast');
    $('#running-time pre').text('');
}