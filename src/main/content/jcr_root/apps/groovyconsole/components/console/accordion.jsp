<%@include file="/libs/foundation/global.jsp" %>

<div class="accordion" id="accordion">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-target="#result" data-parent="#accordion" href="#result">Result</a>
        </div>
        <div id="result" class="accordion-body collapse">
            <div class="accordion-inner"></div>
        </div>
    </div>
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-target="#output" data-parent="#accordion" href="#output">Output</a>
        </div>
        <div id="output" class="accordion-body collapse">
            <div class="accordion-inner"></div>
        </div>
    </div>
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-target="#stacktrace" data-parent="#accordion" href="#stacktrace">Stack Trace</a>
        </div>
        <div id="stacktrace" class="accordion-body collapse">
            <div class="accordion-inner"></div>
        </div>
    </div>
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-target="#help" data-parent="#accordion" href="#help">Help</a>
        </div>
        <div id="help" class="accordion-body collapse">
            <div class="accordion-inner">
                <h3>Bindings</h3>

                <ul>
                    <li>session - <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html">javax.jcr.Session</a></li>
                    <li>pageManager - <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/PageManager.html">com.day.cq.wcm.api.PageManager</a></li>
                    <li>resourceResolver - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/resource/ResourceResolver.html">org.apache.sling.api.resource.ResourceResolver</a></li>
                    <li>slingRequest - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/SlingHttpServletRequest.html">org.apache.sling.api.SlingHttpServletRequest</a></li>
                    <li>sling - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/scripting/SlingScriptHelper.html">org.apache.sling.api.scripting.SlingScriptHelper</a></li>
                    <li>log - <a href="http://www.slf4j.org/api/org/slf4j/Logger.html">org.slf4j.Logger</a></li>
                </ul>

                <h3>Methods</h3>

                <ul>
                    <li>getPage(path) - get the <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html">Page</a> for the given path, or null if it does not exist.</li>
                    <li>getNode(path) - get the <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html">Node</a> for the given path.  Throws <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/RepositoryException.html">javax.jcr.RepositoryException</a> if it does not exist.</li>
                </ul>
            </div>
        </div>
    </div>
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-target="#about" data-parent="#accordion" href="#about">About</a>
        </div>
        <div id="about" class="accordion-body collapse in">
            <div class="accordion-inner">
                <ul>
                    <li>Inspired by and heavily sourced from Guillaume Laforge's <a href="http://groovyconsole.appspot.com">Groovy web console</a></li>
                    <li>Programmed with <a href="http://groovy.codehaus.org">Groovy</a>, version 2.0.0</li>
                    <li>Code editing capabilities provided by <a href="http://ace.ajax.org/">Ace</a></li>
                    <li>Project hosted on <a href="https://github.com/Citytechinc/cq5-groovy-console">GitHub</a> for <a href="http://www.citytechinc.com">CITYTECH, Inc.</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>