GroovyConsole.localStorage = function () {

    var EDITOR_HEIGHT = 'groovyconsole.editor.height';
    var EDITOR_DATA = 'groovyconsole.editor.data';
    var SCRIPT_NAME = 'groovyconsole.script.name';
    var THEME = 'groovyconsole.theme';

    function loadValue(name, defaultValue) {
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

    function clearValue(name) {
        if (Modernizr.localstorage) {
            window.localStorage[name] = '';
        }
    }

    return {
        saveEditorHeight: function (value) {
            saveValue(EDITOR_HEIGHT, value);
        },

        loadEditorHeight: function () {
            return loadValue(EDITOR_HEIGHT, $('#editor').css('height'));
        },

        saveEditorData: function (value) {
            saveValue(EDITOR_DATA, value);
        },

        loadEditorData: function () {
            return loadValue(EDITOR_DATA, '');
        },

        saveScriptName: function (scriptName) {
            saveValue(SCRIPT_NAME, scriptName);
        },

        loadScriptName: function () {
            return loadValue(SCRIPT_NAME, '');
        },

        clearScriptName: function () {
            return clearValue(SCRIPT_NAME);
        },

        saveTheme: function (value) {
            saveValue(THEME, value);
        },

        loadTheme: function () {
            return loadValue(THEME, 'ace/theme/idle_fingers');
        }
    };
}();