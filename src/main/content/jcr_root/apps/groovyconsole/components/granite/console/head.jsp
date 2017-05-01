<%@include file="/libs/foundation/global.jsp" %><%
%><%@page import="com.day.cq.wcm.api.WCMMode,
                  com.day.cq.widget.HtmlLibraryManager" %><%
%><head>
    <title>CQ Groovy Console</title>
    <meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1">
    <c:if test="<%= WCMMode.fromRequest(slingRequest) != WCMMode.DISABLED %>">
        <cq:includeClientLib categories="cq.wcm.edit" />
    </c:if>
</head>