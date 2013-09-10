<%@include file="/libs/foundation/global.jsp" %>

<h4>Bindings</h4>
<ul>
    <li>session - <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html" target="_blank">javax.jcr.Session</a></li>
    <li>pageManager - <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/PageManager.html" target="_blank">com.day.cq.wcm.api.PageManager</a></li>
    <li>resourceResolver - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/resource/ResourceResolver.html" target="_blank">org.apache.sling.api.resource.ResourceResolver</a></li>
    <li>slingRequest - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/SlingHttpServletRequest.html" target="_blank">org.apache.sling.api.SlingHttpServletRequest</a></li>
    <li>log - <a href="http://www.slf4j.org/api/org/slf4j/Logger.html" target="_blank">org.slf4j.Logger</a></li>
</ul>
<h4>Methods</h4>
<ul>
    <li>getPage(String path) - <span class="muted">Get the <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html" target="_blank">Page</a> for the given path, or null if it does not exist.</span></li>
    <li>getNode(String path) - <span class="muted">Get the <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html" target="_blank">Node</a> for the given path.  Throws <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/RepositoryException.html">javax.jcr.RepositoryException</a> if it does not exist.</span></li>
    <li>getResource(String path) - <span class="muted">Get the <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/resource/Resource.html" target="_blank">Resource</a> for the given path, or null if it does not exist.</li>
    <li>getService(Class&lt;ServiceType&gt; serviceType) - <span class="muted">Get the OSGi service instance for the given type, e.g. <a href="http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/workflow/WorkflowService.html" target="_blank">com.day.cq.workflow.WorkflowService</a>.</span></li>
    <li>copy "sourceAbsolutePath" to "destinationAbsolutePath" - <span class="muted">Groovy DSL syntax for copying a node, equivalent to calling <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Workspace.html#copy(java.lang.String, java.lang.String)" target="_blank">session.workspace.copy(sourceAbsolutePath, destinationAbsolutePath)</a>.</span></li>
    <li>move "sourceAbsolutePath" to "destinationAbsolutePath" - <span class="muted">Groovy DSL syntax for moving a node, equivalent to calling <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html#move(java.lang.String, java.lang.String)" target="_blank">session.move(sourceAbsolutePath, destinationAbsolutePath)</a>, except that the Session is saved automatically when the move is completed.</span></li>
    <li>save() - <span class="muted">Save the current JCR session.</span></li>
    <li>activate(String path) - <span class="muted">Activate the node at the given path.</span></li>
    <li>deactivate(String path) - <span class="muted">Deactivate the node at the given path.</span></li>
    <li>doWhileDisabled(String componentClassName, Closure closure) - <span class="muted">Execute the provided closure while the specified OSGi component is disabled.</span></li>
</ul>
<h4>Enhancements</h4>
<ul>
    <li><a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html">javax.jcr.Node</a>
        <ul>
            <li>iterator() - <span class="muted">Allows usage of Groovy closure operators (each, eachWithIndex) to iterate over child nodes of the current node.</span></li>
            <li>get(String propertyName) - <span class="muted">Get the named property value, with the return type determined dynamically by <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Property.html#getType()" target="_blank">Property.getType()</a>.</span></li>
            <li>set(String propertyName, Object value) - <span class="muted">Set the named property value.  An array value argument can be used to set multi-valued properties.</span></li>
            <li>getOrAddNode(String name) - <span class="muted">Get the named child node if it exists; otherwise, add it.</span></li>
            <li>getOrAddNode(String name, String primaryNodeTypeName) - <span class="muted">Get the named child node if it exists; otherwise, add it with the given node type.</span></li>
            <li>removeNode(String name) - <span class="muted">Remove the child node with the given name, returning true if the node was removed.</span></li>
            <li>recurse(Closure closure) - <span class="muted">Recursively invoke this closure on each descendant node of the current node.</span></li>
            <li>recurse(String primaryNodeTypeName, Closure closure) - <span class="muted">Recursively invoke this closure on each descendant node of the current node that matches the given node type.</span></li>
            <li>recurse(Collection&lt;String&gt; primaryNodeTypeNames, Closure closure) - <span class="muted">Recursively invoke this closure on each descendant node of the current node that matches any of the given node types.</span></li>
        </ul>
    </li>
    <li><a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html">com.day.cq.wcm.api.Page</a>
        <ul>
            <li>iterator() - <span class="muted">Allows usage of Groovy closure operators (each, eachWithIndex) to iterate over child pages of the current page.</span></li>
            <li>recurse(Closure closure) - <span class="muted">Recursively invoke this closure on each descendant page of the current page.</span></li>
            <li>getNode() - <span class="muted">Get the jcr:content node of the current page, returning null if it does not exist.</span></li>
            <li>get(String propertyName) - <span class="muted">Get the named property value from the jcr:content node of the current page, with the return type determined dynamically by <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Property.html#getType()" target="_blank">Property.getType()</a>.</span></li>
            <li>set(String propertyName, Object value) - <span class="muted">Set the named property value on the jcr:content node of the current page.  An array value argument can be used to set multi-valued properties.</span></li>
        </ul>
    </li>
</ul>
<h4>Builders</h4>
<p>Additional bindings are provided for the following builders.  <a href="http://groovy.codehaus.org/Builders" target="_blank">Builders</a> use a special syntax to create a structured tree of data (in this case, content in the JCR).</p>
<ul>
    <li>nodeBuilder - <span class="muted">Each "node" in the syntax tree corresponds to a Node in the repository.  A new Node is created only if there is no existing node for the current name.</span>
        <pre>
nodeBuilder.etc {
    satirists("sling:Folder") {
        bierce(firstName: "Ambrose", lastName: "Bierce", birthDate: Calendar.instance.updated(year: 1842, month: 5, date: 24))
        mencken(firstName: "H.L.", lastName: "Mencken", birthDate: Calendar.instance.updated(year: 1880, month: 8, date: 12))
        other("sling:Folder", "jcr:title": "Other")
    }
}</pre>
        <ul>
            <li>A single string argument represents the node type name for the node ("satirists").</li>
            <li>A map argument uses the provided key:value pairs to set property values on the node ("bierce" and "mencken").</li>
            <li>Both string and map arguments will set the node type and property value(s) for the node ("other").</li>
        </ul>
    </li>
    <li>pageBuilder - <span class="muted">Each "node" in the syntax tree corresponds to a cq:Page node, unless the node is a descendant of a "jcr:content" node, in which case nodes are treated in the same manner as described for the Node builder above.</span>
        <pre>
pageBuilder.content {
    beer {
        styles("Styles") {
            "jcr:content"("jcr:lastModifiedBy": "me", "jcr:lastModified": Calendar.instance) {
                data("sling:Folder")
            }
            dubbel("Dubbel")
            tripel("Tripel")
            saison("Saison")
        }
        breweries("Breweries", "jcr:lastModifiedBy": "me", "jcr:lastModified": Calendar.instance)
    }
}</pre>
        <ul>
            <li>A single string argument is used to set the page title rather than the node type ("styles").</li>
            <li>Descendants of "jcr:content" nodes are not created with the cq:Page type by default and can have their own node type specified as described for the Node builder ("data").</li>
            <li>Page properties can be passed directly as arguments on the page node without explicitly creating a jcr:content node first ("breweries").</li>
        </ul>
    </li>
</ul>