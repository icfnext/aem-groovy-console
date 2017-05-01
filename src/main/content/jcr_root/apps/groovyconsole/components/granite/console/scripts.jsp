<%@include file="/libs/foundation/global.jsp" %><%
%><div data-role="page" id="g-scripts">
    <div data-role="header">
        <h1 class="g-uppercase">Scripts</h1>
    </div>
    
    <div data-role="content" data-scroll="n" data-theme="c">
        <ul id="g-scripts-list" class="scripts" data-role="listview"></ul>
    </div>
    
    <div data-role="footer">
        <div class="g-buttonbar">
            <input id="script-search" type="search"/>
        </div>
    </div>
</div>
<script id="groovyconsole-scripts-list" type="text/x-handlebars-template" data-editor="${resource.path}.editor.html">
    <%@ include file="templates/scripts.htm" %>
</script>