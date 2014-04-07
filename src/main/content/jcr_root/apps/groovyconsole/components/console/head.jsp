<%@include file="/libs/foundation/global.jsp" %><%
%><%@page import="com.day.cq.wcm.api.WCMMode" %>

<head>
    <title>CQ Groovy Console</title>

    <c:choose>
        <c:when test="<%= WCMMode.fromRequest(slingRequest) != WCMMode.DISABLED %>">
            <cq:includeClientLib categories="cq.wcm.edit,groovyconsole" />
        </c:when>
        <c:otherwise>
            <cq:includeClientLib categories="cq.shared,groovyconsole" />
        </c:otherwise>
    </c:choose>
</head>