"use strict";
// groovyconsole namespace
window._gc = window._gc || {};
_gc.themes = [
        {"path":"ace/theme/chrome","name":"Chrome"},
        {"path":"ace/theme/clouds","name":"Clouds"},
        {"path":"ace/theme/clouds_midnight","name":"Clouds Midnight"},
        {"path":"ace/theme/cobalt","name":"Cobalt"},
        {"path":"ace/theme/crimson_editor","name":"Crimson Editor"},
        {"path":"ace/theme/dawn","name":"Dawn"},
        {"path":"ace/theme/dreamweaver","name":"Dreamweaver"},
        {"path":"ace/theme/eclipse","name":"Eclipse"},
        {"path":"ace/theme/idle_fingers","name":"idleFingers"},
        {"path":"ace/theme/kr_theme","name":"krTheme"},
        {"path":"ace/theme/merbivore","name":"Merbivore"},
        {"path":"ace/theme/merbivore_soft","name":"Merbivore Soft"},
        {"path":"ace/theme/mono_industrial","name":"Mono Industrial"},
        {"path":"ace/theme/monokai","name":"Monokai"},
        {"path":"ace/theme/pastel_on_dark","name":"Pastel on dark"},
        {"path":"ace/theme/solarized_dark","name":"Solarized Dark"},
        {"path":"ace/theme/solarized_light","name":"Solarized Light"},
        {"path":"ace/theme/textmate","name":"TextMate"},
        {"path":"ace/theme/twilight","name":"Twilight"},
        {"path":"ace/theme/tomorrow","name":"Tomorrow"},
        {"path":"ace/theme/tomorrow_night","name":"Tomorrow Night"},
        {"path":"ace/theme/tomorrow_night_blue","name":"Tomorrow Night Blue"},
        {"path":"ace/theme/tomorrow_night_bright","name":"Tomorrow Night Bright"},
        {"path":"ace/theme/tomorrow_night_eighties","name":"Tomorrow Night 80s"},
        {"path":"ace/theme/vibrant_ink","name":"Vibrant Ink"}
]

$(function() {
	var tmpl = Handlebars.compile($('#editor-toolbar').html());
	$('#editor-header').append(tmpl(_gc));
	GroovyConsole.initializeThemeMenu();
});