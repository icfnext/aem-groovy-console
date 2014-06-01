<%@include file="/apps/groovyconsole/components/global.jsp" %>

<head>
    <title>Groovy Console</title>

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>

    <c:choose>
        <c:when test="${isAuthor}">
            <cq:includeClientLib categories="cq.wcm.edit,groovyconsole" />
        </c:when>
        <c:otherwise>
            <cq:includeClientLib categories="cq.shared,groovyconsole" />
        </c:otherwise>
    </c:choose>
</head>