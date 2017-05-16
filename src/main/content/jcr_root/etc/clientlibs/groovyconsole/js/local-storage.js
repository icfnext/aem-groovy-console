GroovyConsole.localStorage = function () {

    var SCRIPT_EDITOR = 'script';
    var DATA_EDITOR = 'data';
    var EDITOR_HEIGHT = 'groovyconsole.editor.height';
    var EDITOR_CONTENT = 'groovyconsole.editor.content';
    var THEME = 'groovyconsole.theme';
    var SCRIPT_NAME = 'groovyconsole.script.name';

    function getValue(propertyName, defaultValue, editorName) {
        var name = propertyName;

        if (typeof editorName !== 'undefined') {
            name = propertyName + '.' + editorName;
        }

        if (Modernizr.localstorage) {
            return window.localStorage[name] || defaultValue || '';
        }

        return defaultValue || '';
    }

    function saveValue(propertyName, value, editorName) {
        var name = propertyName;

        if (typeof editorName !== 'undefined') {
            name = propertyName + '.' + editorName;
        }

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
        saveScriptEditorHeight: function (value) {
            saveValue(EDITOR_HEIGHT, value, SCRIPT_EDITOR);
        },

        saveDataEditorHeight: function (value) {
            saveValue(EDITOR_HEIGHT, value, DATA_EDITOR);
        },

        getScriptEditorHeight: function () {
            return getValue(EDITOR_HEIGHT, $('#script-editor').css('height'), SCRIPT_EDITOR);
        },

        getDataEditorHeight: function () {
            return getValue(EDITOR_HEIGHT, $('#data-editor').css('height'), DATA_EDITOR);
        },

        saveScriptEditorContent: function (value) {
            saveValue(EDITOR_CONTENT, value, SCRIPT_EDITOR);
        },

        saveDataEditorContent: function (value) {
            saveValue(EDITOR_CONTENT, value, DATA_EDITOR);
        },

        getScriptEditorContent: function () {
            return getValue(EDITOR_CONTENT, '', SCRIPT_EDITOR);
        },

        getDataEditorContent: function () {
            return getValue(EDITOR_CONTENT, '', DATA_EDITOR);
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