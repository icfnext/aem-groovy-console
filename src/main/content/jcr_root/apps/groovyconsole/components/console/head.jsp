<%@include file="/libs/foundation/global.jsp" %>

<head>
    <title>Groovy Console</title>

    <cq:includeClientLib categories="cq.wcm.edit" />

    <script src="/apps/groovyconsole/docroot/js/jquery-1.7.1.min.js" type="text/javascript"></script>
    <script src="/apps/groovyconsole/docroot/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/apps/groovyconsole/docroot/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
    <script src="/apps/groovyconsole/docroot/js/jquery-ui-resize-plugin.js" type="text/javascript"></script>
    <script src="/apps/groovyconsole/docroot/js/ace-noconflict.js" type="text/javascript" charset="utf-8"></script>
    <script src="/apps/groovyconsole/docroot/js/mode-groovy-noconflict.js" type="text/javascript" charset="utf-8"></script>
    <script src="/apps/groovyconsole/docroot/js/themes-noconflict.js" type="text/javascript" charset="utf-8"></script>
    <script src="/apps/groovyconsole/docroot/js/theme-textmate-uncompressed.js" type="text/javascript" charset="utf-8"></script>
    <script src="/apps/groovyconsole/docroot/js/console.js" type="text/javascript"></script>

    <script type="text/javascript">
        $(function() {
            initialize('${resource.path}.html');
        });
    </script>

    <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/bootstrap.min.css" type="text/css" type="text/css" />
    <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/bootstrap-responsive.min.css" type="text/css" type="text/css" />
    <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" type="text/css" />

    <!--
    <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/smoothness/jquery-ui-1.8.18.custom.css" type="text/css" />
    <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/jquery.ui.selectmenu.css" type="text/css" />
    <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" type="text/css" />
     -->
</head>