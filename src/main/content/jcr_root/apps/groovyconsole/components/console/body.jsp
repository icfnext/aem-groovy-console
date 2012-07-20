<%@include file="/libs/foundation/global.jsp" %>

<body style="padding-top: 40px;">
    <cq:include script="header.jsp" />

    <div class="container">
        <cq:include script="toolbar.jsp" />

        <div class="alert alert-error" style="display: none;">
            <span class="message"></span>
        </div>

        <pre id="editor" class="pre-scrollable"></pre>

        <cq:include script="footer.jsp" />
    </div>
</body>