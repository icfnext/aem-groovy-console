GroovyConsole.localStorage = function () {

    var EDITOR_HEIGHT = 'groovyconsole.editor.height';
    var EDITOR_DATA = 'groovyconsole.editor.data';
    var THEME = 'groovyconsole.theme';
    var SCRIPT_NAME = 'groovyconsole.script.name';

    function getValue(name, defaultValue) {
        if (Modernizr.localstorage) {
            return window.localStorage[name] || defaultValue || '';
        }

        return defaultValue || '';
    }

    function saveValue(name, value) {
        if (Modernizr.localstorage) {
            window.localStorage[name] = value;
        }
    }

    function clearValue (name) {
        if (Modernizr.localstorage) {
            window.localStorage[name] = '';
        }
    }

    return {
        saveEditorHeight: function (value) {
            saveValue(EDITOR_HEIGHT, value);
        },

        getEditorHeight: function () {
            return getValue(EDITOR_HEIGHT, $('#editor').css('height'));
        },

        saveEditorData: function (value) {
            saveValue(EDITOR_DATA, value);
        },

        getEditorData: function () {
            return getValue(EDITOR_DATA, '');
        },

        saveTheme: function (value) {
            saveValue(THEME, value);
        },

        getTheme: function () {
            return getValue(THEME, 'ace/theme/idle_fingers');
        },

        saveScriptName: function (scriptPath) {
            var scriptName;

            if (scriptPath.indexOf('.groovy') > 0) {
                var nameStart = scriptPath.lastIndexOf('/') + 1;
                var nameEnd = scriptPath.indexOf('.');

                scriptName = scriptPath.substring(nameStart, nameEnd);
            } else {
                scriptName = scriptPath;
            }

            saveValue(SCRIPT_NAME, scriptName);
        },

        getScriptName: function () {
            return getValue(SCRIPT_NAME, '');
        },

        clearScriptName: function () {
            return clearValue(SCRIPT_NAME);
        }
    };
}();