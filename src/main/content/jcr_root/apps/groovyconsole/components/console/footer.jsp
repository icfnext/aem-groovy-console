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
	<span></span>
</div>

<div class="accordion" id="accordion">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" href="#help" data-toggle="collapse" data-target="#help" data-parent="#accordion">Help</a>
        </div>
        <div id="help" class="accordion-body collapse">
            <div class="accordion-inner">
                <h4>Bindings</h4>
                <ul>
                    <li>session - <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html">javax.jcr.Session</a></li>
                    <li>pageManager - <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/PageManager.html">com.day.cq.wcm.api.PageManager</a></li>
                    <li>resourceResolver - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/resource/ResourceResolver.html">org.apache.sling.api.resource.ResourceResolver</a></li>
                    <li>slingRequest - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/SlingHttpServletRequest.html">org.apache.sling.api.SlingHttpServletRequest</a></li>
                    <li>sling - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/scripting/SlingScriptHelper.html">org.apache.sling.api.scripting.SlingScriptHelper</a></li>
                    <li>log - <a href="http://www.slf4j.org/api/org/slf4j/Logger.html">org.slf4j.Logger</a></li>
                </ul>
                <h4>Methods</h4>
                <ul>
                    <li>getPage(path) - get the <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html">Page</a> for the given path, or null if it does not exist.</li>
                    <li>getNode(path) - get the <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html">Node</a> for the given path.  Throws <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/RepositoryException.html">javax.jcr.RepositoryException</a> if it does not exist.</li>
                </ul>
            </div>
        </div>
    </div>
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" href="#about" data-toggle="collapse" data-target="#about" data-parent="#accordion">About</a>
        </div>
        <div id="about" class="accordion-body collapse in">
            <div class="accordion-inner">
                <ul>
                    <li>Inspired by Guillaume Laforge's <a href="http://groovyconsole.appspot.com">Groovy web console</a></li>
                    <li>Programmed with <a href="http://groovy.codehaus.org">Groovy</a>, version 2.0.0</li>
                    <li>Code editing capabilities provided by <a href="http://ace.ajax.org/">Ace</a></li>
                    <li>Project hosted on <a href="https://github.com/Citytechinc/cq5-groovy-console">GitHub</a> for <a href="http://www.citytechinc.com">CITYTECH, Inc.</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>