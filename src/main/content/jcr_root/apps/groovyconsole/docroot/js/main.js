function initialize(path) {
    var editor = CodeMirror.fromTextArea('script', {
        height: '300px',
        parserfile: ['tokenizejavascript.js', 'parsejavascript.js'],
        stylesheet: '/apps/groovyconsole/docroot/css/jscolors.css',
        path: '/apps/groovyconsole/docroot/js/',
        continuousScanning: 500,
        lineNumbers: true,
        textWrapping: false,
        tabMode: 'spaces',
        submitFunction: function() {
            $('#run').click();
        }
    });

    $(function() {
        $('#tabs').tabs();
        $('#textarea-container-script').resizable({ handles: 's', alsoResize: 'iframe' });
    });

    $('#run').click(function(event) {
        $('#output').text('');
        $('#result').text('');
        $('#stacktrace').text('');

        $.ajax({
            type: 'POST',
            url: path,
            data: {
                script: editor.getCode()
            },
            dataType: 'json',

            success: function(data) {
                var result = data.executionResult;
                var output = data.outputText;
                var stackTrace = data.stacktraceText;

                if (output && output.length > 0) {
                    $('#tabs').tabs('select', 1);
                    $('#output').text(output).fadeIn();
                } else {
                    $('#output').fadeOut();
                }

                if (result && result.length > 0) {
                    $('#tabs').tabs('select', 0);
                    $('#result').text(result).fadeIn();
                } else {
                    $('#result').fadeOut();
                }

                if (stackTrace && stackTrace.length > 0) {
                    $('#tabs').tabs('select', 2);
                    $('#stacktrace').text(stackTrace).fadeIn();
                } else {
                    $('#stacktrace').fadeOut();
                }
            },

            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert('Error interacting with the CQ5 server: ' + errorThrown);
            }
        });
    });

    $('#loadingDiv').hide().ajaxStart(function() {
        $(this).show();
    }).ajaxStop(function() {
        $(this).hide();
    });
}