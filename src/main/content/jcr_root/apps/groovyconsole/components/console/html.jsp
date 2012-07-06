<%@include file="/libs/foundation/global.jsp" %>

<cq:defineObjects />

<html>
    <head>
        <title>Groovy Console</title>

        <script src="/apps/groovyconsole/docroot/js/jquery-1.3.2.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-1.7.2.custom.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/codemirror.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/mirrorframe.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/main.js" type="text/javascript"></script>

        <script type="text/javascript">
            $(function() {
                initialize('${resource.path}.html');
            });
        </script>

        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/redmond/jquery-ui-1.7.1.custom.css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" />
    </head>
    <body>
        <div id="loadingDiv">
            <img src="/apps/groovyconsole/docroot/images/ajax-spinner-blue.gif">
        </div>

        <h1><a href="${currentPage.path}.html">Groovy Console</a></h1>

        <form method="POST">
            <div id="textarea-container-script" class="border">
                <textarea id="script" name="script" cols="140" rows="40"></textarea>
            </div>

            <div id="button-bar">
                <div id="actionsBreadcrumb">
                    <span class="actionsBreadcrumbHead">Actions &nbsp;&#x27A4;</span>
                    <span class="actionsBreadcrumbChild" id="run"><a href="javascript:void(0)">Run script</a></span>
                    <span class="actionsBreadcrumbLastChild" id="new"><a href="${currentPage.path}.html">New script</a></span>
                </div>
            </div>
        </form>

        <div id="tabs">
            <ul>
                <li><a href="#tabs-result">Result</a></li>
                <li><a href="#tabs-output">Output</a></li>
                <li><a href="#tabs-stacktrace">Stacktrace</a></li>
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
        </div>

        <cq:include script="about.jsp" />
    </body>
</html>