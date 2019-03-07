$(function () {
    $.getJSON('/bin/groovyconsole/services', function (services) {
        $('#services-list').typeahead({
            source: Object.keys(services),
            updater: function (key) {
                var declaration = services[key];

                scriptEditor.navigateFileEnd();

                if (scriptEditor.getCursorPosition().column > 0) {
                    scriptEditor.insert('\n\n');
                }

                scriptEditor.insert(declaration);

                return '';
            }
        });

        $('#btn-group-services').fadeIn('fast');
    });
});