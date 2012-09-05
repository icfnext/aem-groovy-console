<%@include file="/libs/foundation/global.jsp" %>

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
    <li>getPage(String path) - <span class="muted">get the <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html">Page</a> for the given path, or null if it does not exist.</span></li>
    <li>getNode(String path) - <span class="muted">get the <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html">Node</a> for the given path.  Throws <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/RepositoryException.html">javax.jcr.RepositoryException</a> if it does not exist.</span></li>
    <li>save() - <span class="muted">save the current JCR session.</span></li>
    <li>getService(Class&lt;ServiceType&gt; serviceType) - <span class="muted">get the service instance for the given type, e.g. <a href="http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/workflow/WorkflowService.html">com.day.cq.workflow.WorkflowService</a>.</span></li>
</ul>
<h4>Enhancements</h4>
<ul>
    <li><a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html">javax.jcr.Node</a>
        <ul>
            <li>iterator() - <span class="muted">allows usage of Groovy closure operators (each, eachWithIndex) to iterate over child nodes of the current node.</span></li>
            <li>get(String propertyName) - <span class="muted">get the named property value, with the return type determined dynamically by <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Property.html#getType()">Property.getType()</a>.</span></li>
            <li>set(String propertyName, Object value) - <span class="muted">set the named property value.  An array value argument can be used to set multi-valued properties.</span></li>
            <li>getOrAddNode(String name) - <span class="muted">get the named child node if it exists; otherwise, add it.</span></li>
            <li>getOrAddNode(String name, String primaryNodeTypeName) - <span class="muted">get the named child node if it exists; otherwise, add it with the given node type.</span></li>
            <li>removeNode(String name) - <span class="muted">remove the child node with the given name, returning true if the node was removed.</span></li>
            <li>recurse(Closure closure) - <span class="muted">recursively invoke this closure on each descendant node of the current node.</span></li>
            <li>recurse(String primaryNodeTypeName, Closure closure) - <span class="muted">recursively invoke this closure on each descendant node of the current node that matches the given node type.</span></li>
            <li>recurse(Collection&lt;String&gt; primaryNodeTypeNames, Closure closure) - <span class="muted">recursively invoke this closure on each descendant node of the current node that matches any of the given node types.</span></li>
        </ul>
    </li>
    <li><a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html">com.day.cq.wcm.api.Page</a>
        <ul>
            <li>iterator() - <span class="muted">allows usage of Groovy closure operators (each, eachWithIndex) to iterate over child pages of the current page.</span></li>
            <li>recurse(Closure closure) - <span class="muted">recursively invoke this closure on each descendant page of the current page.</span></li>
            <li>getNode() - <span class="muted">get the jcr:content node of the current page, returning null if it does not exist.</span></li>
            <li>get(String propertyName) - <span class="muted">get the named property value from the jcr:content node of the current page, with the return type determined dynamically by <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Property.html#getType()">Property.getType()</a>.</span></li>
            <li>set(String propertyName, Object value) - <span class="muted">set the named property value on the jcr:content node of the current page.  An array value argument can be used to set multi-valued properties.</span></li>
        </ul>
    </li>
</ul>