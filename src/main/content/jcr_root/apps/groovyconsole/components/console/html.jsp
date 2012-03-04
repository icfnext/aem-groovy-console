<%@include file="/libs/foundation/global.jsp" %>

<cq:defineObjects />

<html>
    <head>
        <title>Groovy Console</title>
        
        <script src="/apps/groovyconsole/docroot/js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-resize-plugin.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery.ui.selectmenu.js" type="text/javascript"></script>
        
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/smoothness/jquery-ui-1.8.18.custom.css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/jquery.ui.selectmenu.css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" type="text/css" />
        
        <cq:includeClientLib categories="cq.wcm.edit" />
    </head>
    <body>
        <div class="header row">
            <h1><span class="title">Groovy Console<span></h1>

            <div id="toolbar">
                <button id="new-script" >New Script</button>
                <button id="open-script">Open Script</button>
                <button id="save-script">Save Script</button>
                
                <label for="editor-theme" id="theme-label">Editor Theme:</label>
                <select id="editor-theme" name="editor-theme">
                    <option value="ace/theme/chrome">Chrome</option>
                    <option value="ace/theme/clouds">Clouds</option>
                    <option value="ace/theme/clouds_midnight">Clouds Midnight</option>
                    <option value="ace/theme/cobalt">Cobalt</option>
                    <option value="ace/theme/crimson_editor">Crimson Editor</option>
                    <option value="ace/theme/dawn">Dawn</option>
                    <option value="ace/theme/dreamweaver">Dreamweaver</option>
                    <option value="ace/theme/eclipse">Eclipse</option>
                    <option value="ace/theme/idle_fingers">idleFingers</option>
                    <option value="ace/theme/kr_theme">krTheme</option>
                    <option value="ace/theme/merbivore">Merbivore</option>
                    <option value="ace/theme/merbivore_soft">Merbivore Soft</option>
                    <option value="ace/theme/mono_industrial">Mono Industrial</option>
                    <option value="ace/theme/monokai">Monokai</option>
                    <option value="ace/theme/pastel_on_dark">Pastel on dark</option>
                    <option value="ace/theme/solarized_dark">Solarized Dark</option>
                    <option value="ace/theme/solarized_light">Solarized Light</option>
                    <option value="ace/theme/textmate">TextMate</option>
                    <option value="ace/theme/twilight">Twilight</option>
                    <option value="ace/theme/tomorrow">Tomorrow</option>
                    <option value="ace/theme/tomorrow_night" selected="selected">Tomorrow Night</option>
                    <option value="ace/theme/tomorrow_night_blue">Tomorrow Night Blue</option>
                    <option value="ace/theme/tomorrow_night_bright">Tomorrow Night Bright</option>
                    <option value="ace/theme/tomorrow_night_eighties">Tomorrow Night 80s</option>
                    <option value="ace/theme/vibrant_ink">Vibrant Ink</option>
                </select>

                <div id="loadingDiv"><img src="/apps/groovyconsole/docroot/images/ajax-loader-1.gif"></div>

                <button id="run-script" style="float:right;">Run Script</button>
            </div>
        </div>
        
        <pre id="editor" class="row"></pre>

        <div id="tabs" class="row tab">
            <ul>
                <li><a href="#tabs-result">Result</a></li>
                <li><a href="#tabs-output">Output</a></li>
                <li><a href="#tabs-stacktrace">Stacktrace</a></li>
                <li><a href="#tabs-about">About</a></li>
            </ul>

            <div id="tabs-result" class="tab">
                <pre id="result-time" class="hidden"></pre>
                <pre id="result" class="border hidden"></pre>
            </div>

            <div id="tabs-output" class="tab">
                <pre id="output" class="border hidden"></pre>
            </div>

            <div id="tabs-stacktrace" class="tab">
                <pre id="stacktrace" class="border hidden"></pre>
            </div>
            
            <div id="tabs-about" class="tab">
                <cq:include script="about.jsp" />
                <div style="clear:both;"></div>
            </div>
        </div>

        <script src="/apps/groovyconsole/docroot/ace/ace-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/ace/mode-groovy-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/ace/themes-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/ace/theme-textmate-uncompressed.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/console-main.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                initialize('${resource.path}.html');
            });
        </script>
    </body>
</html>