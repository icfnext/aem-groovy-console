<%@include file="/libs/foundation/global.jsp" %>

<body>
    <cq:include script="header.jsp" />

    <div class="container">
        <cq:include script="toolbar.jsp" />

        <div class="alert alert-success" id="message-success" role="alert" style="display: none;">
            <span class="message"></span>
        </div>

        <div class="alert alert-danger" id="message-error" role="alert" style="display: none;">
            <span class="message"></span>
        </div>

        <div id="editor" class="ace_editor_wrapper"></div>

        <pre id="stacktrace" class="alert-danger" style="display: none;"></pre>

        <div id="result" class="alert alert-success" role="alert" style="display: none;">
            <h6>Result</h6>
            <pre></pre>
        </div>

        <div id="output" class="alert alert-success" role="alert" style="display: none;">
            <h6>Output</h6>
            <pre></pre>
        </div>

        <div id="running-time" class="alert alert-info" role="alert" style="display: none;">
            <h6>Running Time</h6>
            <pre></pre>
        </div>

        <div class="panel-group" id="info">
            <cq:include script="history.jsp"/>
            <cq:include script="bindings.jsp" />
            <cq:include script="imports.jsp" />
            <cq:include script="methods.jsp" />
            <cq:include script="enhancements.jsp" />
            <cq:include script="builders.jsp" />
            <cq:include script="about.jsp" />
        </div>
    </div>
</body>