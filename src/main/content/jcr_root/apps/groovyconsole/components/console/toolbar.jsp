<%@include file="/libs/foundation/global.jsp" %><%
%><%@page import="com.day.cq.wcm.api.WCMMode" %>

<div class="btn-toolbar">
    <div class="btn-group">
        <a class="btn btn-success" href="#" id="run-script">
            <i class="icon-play icon-white"></i> <span id="run-script-text">Run Script</span>
        </a>
    </div>

    <div class="btn-group">
        <a class="btn" href="#" id="new-script"><i class="icon-pencil"></i> New</a>

        <c:if test="<%= WCMMode.fromRequest(slingRequest) != WCMMode.DISABLED %>">
        	<a class="btn" href="#" id="open-script"><i class="icon-folder-open"></i> Open</a>
        	<a class="btn" href="#" id="save-script"><i class="icon-hdd"></i> Save</a>
        </c:if>
    </div>

    <div id="loader">
        <img src="/etc/groovyconsole/clientlibs/img/ajax-loader.gif">
    </div>

    <div id="btn-group-adapters" class="btn-group pull-right">
        <button class="btn dropdown-toggle" data-toggle="dropdown">Adapters <span class="caret"></span></button>
        <ul class="dropdown-menu" id="dropdown-adapters"></ul>
    </div>

    <div id="btn-group-services" class="btn-group pull-right">
        <button class="btn dropdown-toggle" data-toggle="dropdown">Services <span class="caret"></span></button>
        <ul class="dropdown-menu" id="dropdown-services"></ul>
    </div>
</div>