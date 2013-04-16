<%@include file="/libs/foundation/global.jsp" %><%
%><%@page import="com.day.cq.wcm.api.WCMMode" %>

<head>
    <title>Groovy Console</title>

    <c:if test="<%= WCMMode.fromRequest(slingRequest) != WCMMode.DISABLED %>">
        <cq:includeClientLib categories="cq.wcm.edit" />
    </c:if>

	<cq:includeClientLib categories="groovyconsole" />
</head>