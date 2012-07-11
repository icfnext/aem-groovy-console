showOpenDialog = function() {
    var dialog = CQ.WCM.getDialog("/apps/groovyconsole/components/console/opendialog");
    dialog.show();
}

showSaveDialog = function() {
    var dialog = CQ.WCM.getDialog("/apps/groovyconsole/components/console/savedialog");
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
            alert('server-side failure with status code ' + response.status);
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
            alert('Save operation failed - status: ' + result.status);
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
    window.editor = ace.edit("editor");

    var GroovyMode = ace.require("ace/mode/groovy").Mode;

    editor.getSession().setMode(new GroovyMode());
    editor.renderer.setShowPrintMargin(false);
    editor.setTheme("ace/theme/tomorrow_night");

    /*
    $('#editor').resizable({
        handles: 's',
        alsoResizeReverse: ".tab",
        resize: function(event, ui) {
            editor.resize();
        }
    });

    $(window).resize(function() {
        editor.resize();
    });
    */

    /*
    $('#loadingDiv').hide().ajaxStart(function() {
        $(this).show();
        $("#run-script").attr('disabled', 'disabled');
    }).ajaxStop(function() {
        $(this).hide();
        $("#run-script").removeAttr('disabled').removeClass('ui-state-hover');
    });
    */

    // buttons
    $("#new-script").click(function() {
        editor.getSession().setValue("");
    });

    $("#open-script").click(function() {
        showOpenDialog();
    });

    $("#save-script").click(function() {
        showSaveDialog();
    });

    $("#run-script").click(function(event) {
    	$('#result .accordion-inner').text('');
        $('#result').collapse('hide');
        $('#output .accordion-inner').text('');
        $('#output').collapse('hide');
        $('#stacktrace .accordion-inner').text('');
        $('#stacktrace').collapse('hide');
        // $('#result-time .accordion-inner').text('').fadeOut();

        var script = editor.getSession().getValue();

        if (script.length) {
        	$.ajax({
                type: 'POST',
                url: path,
                data: { script: script },
                dataType: 'json',
                success: function(data) {
                    var result = data.executionResult;
                    var output = data.outputText;
                    var stacktrace = data.stacktraceText;
                    var runtime = data.runningTime;

                    if (result && result.length > 0) {
                        $('#result .accordion-inner').text(result);
                        $('#result').collapse('toggle');
                    }

                    if (output && output.length > 0) {
                    	$('#output .accordion-inner').text(output);
                        $('#output').collapse('toggle');
                    }

                    if (stacktrace && stacktrace.length > 0) {
                    	$('#stacktrace .accordion-inner').text(stacktrace);
                        $('#stacktrace').collapse('toggle');
                    }

                    if (runtime && runtime.length > 0) {
                        // $('#result-time').text("Execution time: " + runtime).fadeIn();
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                	$('.alert-error .message').text('Error interacting with the CQ5 server.  Check error log.');
                	$('.alert-error').fadeIn();
                }
            });
        } else {
        	$('.alert-error .message').text('Script is empty.  Please try again.');
        	$('.alert-error').fadeIn();
        }
    });
}