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
            console.log('server-side failure with status code ' + response.status);
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
        failure: function ( result, request ) {
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
    
    // Resizing
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
    
    $('#loadingDiv').hide().ajaxStart(function() {
        $(this).show();
        $("#run-script").attr('disabled', 'disabled');
    }).ajaxStop(function() {
        $(this).hide();
        $("#run-script").removeAttr('disabled').removeClass('ui-state-hover');
    });
    
    // Tabs
    $('#tabs').tabs().tabs('select', 3);


    // Buttons
    $("#new-script").button({
        icons: {
            primary: "ui-icon-document"
        },
        text: false
    }).click(function(event) {
        editor.getSession().setValue("");
    });
    
    $("#open-script").button({
        icons: {
            primary: "ui-icon-folder-open"
        },
        text: false
    }).click(function(event) {
        showOpenDialog();
    });
    
    $("#save-script").button({
        icons: {
            primary: "ui-icon-disk"
        },
        text: false
    }).click(function(event) {
        showSaveDialog();
    });
    
    $("#run-script").button({
        icons: {
            secondary: "ui-icon-play"
        },
        text: true
    }).click(function(event) {
        $('#output').text('').fadeOut();
        $('#result').text('').fadeOut();
        $('#stacktrace').text('').fadeOut();
        $('#result-time').text('').fadeOut();

        $.ajax({
            type: 'POST',
            url: path,
            data: {
                script: editor.getSession().getValue()
            },
            dataType: 'json',
            success: function(data) {
                var result = data.executionResult;
                var output = data.outputText;
                var stackTrace = data.stacktraceText;
                var runTime = data.runningTime;

                if (result && result.length > 0) {
                    $('#tabs').tabs('select', 0);
                    $('#result').text(result).fadeIn();
                } else {
                    $('#result').fadeOut();
                }
                
                if (output && output.length > 0) {
                    $('#tabs').tabs('select', 1);
                    $('#output').text(output).fadeIn();
                } else {
                    $('#output').fadeOut();
                }

                if (stackTrace && stackTrace.length > 0) {
                    $('#tabs').tabs('select', 2);
                    $('#stacktrace').text(stackTrace).fadeIn();
                } else {
                    $('#stacktrace').fadeOut();
                }
                
                if (runTime && runTime.length > 0) {
                    $('#result-time').text("Execution time: " + runTime).fadeIn();
                }

            },

            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert('Error interacting with the CQ5 server: ' + errorThrown);
            }
        });
    });
    
    $("#editor-theme").selectmenu({ style: "dropdown", width:170 }).change(function(){
        editor.setTheme(this.value);
    });
}