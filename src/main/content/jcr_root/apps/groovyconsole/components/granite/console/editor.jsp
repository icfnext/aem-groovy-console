<%@include file="/libs/foundation/global.jsp" %><%
%><%@page import="com.day.cq.wcm.api.WCMMode" %><%
%><body>
<div data-role="page" id="g-editor">
	<div data-role="header" id="editor-header">
		<h1 id="script-name"></h1>
	</div>

	<div data-role="content" data-scroll="n" data-theme="b" class="editor-content">
        <div class="alert alert-success live" id="message-success" style="display: none;">
            <span class="message"></span>
        </div>
        <div class="alert alert-error live" id="message-error" style="display: none;">
            <span class="message"></span>
        </div>
        <div id="granite_editor" class="ace_editor_wrapper" data-script="${sling.request.requestPathInfo.suffix}"></div>
        <div id="loader">
            <img src="/etc/groovyconsole/clientlibs/img/ajax-loader.gif">
        </div>
        <pre id="stacktrace" class="prettyprint alert-error live" style="display: none;"></pre>
        <div id="result" class="alert alert-success live" style="display: none;">
            <h6>Result</h6>
            <pre></pre>
        </div>
        <div id="output" class="alert alert-success live" style="display: none;">
            <h6>Output</h6>
            <pre></pre>
        </div>
        <div id="chart" style="display: none;" class="live"></div>
        <div id="running-time" class="alert alert-info live" style="display: none;">
            <h6>Running Time</h6>
            <pre></pre>
        </div>
        <div class="accordion live" id="accordion">
            <cq:include script="bindings.jsp" />
            <cq:include script="imports.jsp" />
            <cq:include script="methods.jsp" />
            <cq:include script="enhancements.jsp" />
            <cq:include script="builders.jsp" />
            <cq:include script="about.jsp" />
        </div>
        <!-- /editor -->
    </div>
    
    <div data-role="footer">
        <div class="g-buttonbar">
            <a id="run-script"><i class="icon-play icon-white"></i>Run</a>
            <span class="divider"></span>
            <a id="new-script" data-iconpos="notext" data-icon="plus">New</a>
	        <c:if test="<%= WCMMode.fromRequest(slingRequest) != WCMMode.DISABLED %>">
            <a id="save-script" data-iconpos="notext" data-icon="save">Save</a>
	        </c:if>
        </div>
    </div>
</div>

<script id="editor-toolbar" type="text/x-handlebars-template" data-editor="${resource.path}.editor.html">
    <%@ include file="templates/toolbar.htm" %>
</script>
</body>