<%@include file="/libs/foundation/global.jsp" %>

<body style="padding-top: 40px;">
    <cq:include script="header.jsp" />

    <div class="container">
        <cq:include script="toolbar.jsp" />

        <div class="alert alert-success" id="message-success" style="display: none;">
            <span class="message"></span>
        </div>

        <div class="alert alert-error" id="message-error" style="display: none;">
            <span class="message"></span>
        </div>

        <div id="editor" class="ace_editor_wrapper"></div>

        <cq:include script="footer.jsp" />
    </div>
</body>