$(function () {
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
});