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
    <li>getPage(String path) - get the <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html">Page</a> for the given path, or null if it does not exist.</li>
    <li>getNode(String path) - get the <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html">Node</a> for the given path.  Throws <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/RepositoryException.html">javax.jcr.RepositoryException</a> if it does not exist.</li>
    <li>save() - save the current JCR session.</li>
    <li>getService(Class&lt;ServiceType&gt; serviceType) - get the service instance for the given type, e.g. <a href="http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/workflow/WorkflowService.html">com.day.cq.workflow.WorkflowService</a>.</li>
</ul>
<h4>Enhancements</h4>
<ul>
    <li>javax.jcr.Node
        <ul>
            <li>iterator() - allows usage of Groovy closure operators (each, eachWithIndex) to iterate over child nodes of the current node.</li>
            <li>get(String propertyName) - get the named property value, with the return type determined dynamically by <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Property.html#getType()">Property.getType()</a>.</li>
            <li>set(String propertyName, Object value) - set the named property value.  An array value argument can be used to set multi-valued properties.</li>
            <li>getOrAddNode(String name) - get the named child node if it exists; otherwise, add it.</li>
            <li>getOrAddNode(String name, String primaryNodeTypeName) - get the named child node if it exists; otherwise, add it with the given node type.</li>
            <li>removeNode(String name) - remove the child node with the given name, returning true if the node was removed.</li>
            <li>recurse(Closure closure) - recursively invoke this closure on each descendant node of the current node.</li>
            <li>recurseWithType(String primaryNodeTypeName, Closure closure) - recursively invoke this closure on each descendant node of the current node that matches the given node type.</li>
            <li>recurseWithTypes(Collection&lt;String&rt; primaryNodeTypeNames, Closure closure) - recursively invoke this closure on each descendant node of the current node that matches any of the given node types.</li>
        </ul>
    </li>
    <li>com.day.cq.wcm.api.Page
        <ul>
            <li>iterator() - allows usage of Groovy closure operators (each, eachWithIndex) to iterate over child pages of the current page.</li>
            <li>recurse(Closure closure) - recursively invoke this closure on each descendant page of the current page.</li>
            <li>getNode() - get the jcr:content node of the current page, returning null if it does not exist.</li>
            <li>get(String propertyName) - get the named property value from the jcr:content node of the current page, with the return type determined dynamically by <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Property.html#getType()">Property.getType()</a>.</li>
            <li>set(String propertyName, Object value) - set the named property value on the jcr:content node of the current page.  An array value argument can be used to set multi-valued properties.</li>
        </ul>
    </li>
</ul>