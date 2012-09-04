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
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" href="#help" data-toggle="collapse" data-target="#help" data-parent="#accordion">Help</a>
        </div>
        <div id="help" class="accordion-body collapse">
            <div class="accordion-inner">
                <cq:include script="help.jsp" />
            </div>
        </div>
    </div>
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" href="#about" data-toggle="collapse" data-target="#about" data-parent="#accordion">About</a>
        </div>
        <div id="about" class="accordion-body collapse in">
            <div class="accordion-inner">
                <cq:include script="about.jsp" />
            </div>
        </div>
    </div>
</div>