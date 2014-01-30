<%@include file="/libs/foundation/global.jsp" %>

<pre id="stacktrace" class="prettyprint alert-error" style="display: none;"></pre>

<div id="result" class="alert alert-success" style="display: none;">
    <h6>Result</h6>
    <pre></pre>
</div>
<div id="output" class="alert alert-success" style="display: none;">
    <h6>Output</h6>
    <pre></pre>
</div>
<div id="running-time" class="alert alert-info" style="display: none;">
    <h6>Running Time</h6>
    <pre></pre>
</div>
<div class="accordion" id="accordion">
    <cq:include script="bindings.jsp" />
    <cq:include script="imports.jsp" />
    <cq:include script="methods.jsp" />
    <cq:include script="enhancements.jsp" />
    <cq:include script="builders.jsp" />
    <cq:include script="about.jsp" />
</div>