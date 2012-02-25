<%@include file="/libs/foundation/global.jsp" %>

<cq:defineObjects />

<html>
    <head>
        <title>Groovy Console</title>
        
        <script src="/apps/groovyconsole/docroot/js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-resize-plugin.js" type="text/javascript"></script>
        
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/smoothness/jquery-ui-1.8.18.custom.css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" />
    </head>
    <body>
        <div class="header row">
            <h1><span class="title">Groovy Console<span></h1>

            <div id="toolbar">
                <button id="new-script" >New Script</button>
                <button id="open-script">Open Script</button>
                <button id="save-script">Save Script</button>

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
        <script src="/apps/groovyconsole/docroot/ace/theme-tomorrow_night-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/ace/mode-groovy-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/console-main.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                initialize('${resource.path}.html');
            });
        </script>
    </body>
</html>