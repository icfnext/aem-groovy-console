<%@include file="/libs/foundation/global.jsp" %>

<head>
    <title>Groovy Console</title>

    <cq:includeClientLib categories="cq.wcm.edit" />

    <script src="/apps/groovyconsole/docroot/js/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script src="/apps/groovyconsole/docroot/js/jquery.cookie.js" type="text/javascript"></script>
    <script src="/apps/groovyconsole/docroot/js/bootstrap.min.js" type="text/javascript"></script>
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
    <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/console.css" type="text/css" type="text/css" />
</head>