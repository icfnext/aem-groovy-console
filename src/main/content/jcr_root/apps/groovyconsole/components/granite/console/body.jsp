<%@include file="/libs/foundation/global.jsp" %><%
%><body>
    <div data-role="globalheader" data-title="Groovy Console" data-theme="a"></div>
    <div data-role="panel" data-id="menu">
        <sling:include path=".scripts.html"/>
    </div>
    <div data-role="panel" data-id="main">
		<sling:include path=".editor.html"/><!-- /page -->
    </div>
    <cq:includeClientLib categories="granite_groovyconsole_console" />
</body>