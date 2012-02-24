<%@include file="/libs/foundation/global.jsp" %>

<cq:defineObjects />

<html>
    <head>
        <title>Groovy Console</title>
        
        <script src="/apps/groovyconsole/docroot/js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
        
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/smoothness/jquery-ui-1.8.18.custom.css" type="text/css" />
    </head>
    <body>
        <div id="loadingDiv">
            <img src="/apps/groovyconsole/docroot/images/ajax-spinner-blue.gif">
        </div>

        <h1><a href="${currentPage.path}.html">Groovy Console</a></h1>
        
        <div id="toolbar">
            <button>New Script</button>
            <button>Open Script</button>
            <button id="run" style="float:right;">Run Script</button>
        </div>

        <pre id="editor"></pre>

        <div id="tabs">
            <ul>
                <li><a href="#tabs-result">Result</a></li>
                <li><a href="#tabs-output">Output</a></li>
                <li><a href="#tabs-stacktrace">Stacktrace</a></li>
                <li><a href="#tabs-about">About</a></li>
            </ul>

            <div id="tabs-result">
                <pre id="result" class="border hidden"></pre>
                <pre id="result-time" class="hidden"></pre>
            </div>

            <div id="tabs-output">
                <pre id="output" class="border hidden"></pre>
            </div>

            <div id="tabs-stacktrace">
                <pre id="stacktrace" class="border hidden"></pre>
            </div>
            
            <div id="tabs-about">
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