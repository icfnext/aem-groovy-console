<%@include file="/libs/foundation/global.jsp" %>

<div class="accordion-group">
    <div class="accordion-heading">
        <a class="accordion-toggle" href="#methods" data-toggle="collapse" data-target="#methods" data-parent="#accordion">Methods</a>
    </div>
    <div id="methods" class="accordion-body collapse">
        <div class="accordion-inner">
            <ul>
                <li>getPage(String path) - <span class="muted">Get the <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html" target="_blank">Page</a> for the given path, or null if it does not exist.</span></li>
                <li>getNode(String path) - <span class="muted">Get the <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html" target="_blank">Node</a> for the given path.  Throws <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/RepositoryException.html">javax.jcr.RepositoryException</a> if it does not exist.</span></li>
                <li>getResource(String path) - <span class="muted">Get the <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/resource/Resource.html" target="_blank">Resource</a> for the given path, or null if it does not exist.</li>
                <li>getService(Class&lt;ServiceType&gt; serviceType) - <span class="muted">Get the OSGi service instance for the given type, e.g. <a href="http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/workflow/WorkflowService.html" target="_blank">com.day.cq.workflow.WorkflowService</a>.</span></li>
                <li>getService(String className) - <span class="muted">Get the OSGi service instance for the given class name.</span></li>
                <li>getServices(Class&lt;ServiceType&gt; serviceType, String filter) - <span class="muted">Get OSGi services for the given type and filter expression.</span></li>
                <li>getServices(String className, String filter) - <span class="muted">Get OSGi services for the given class name and filter expression.</span></li>
                <li>copy "sourceAbsolutePath" to "destinationAbsolutePath" - <span class="muted">Groovy DSL syntax for copying a node, equivalent to calling <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Workspace.html#copy(java.lang.String, java.lang.String)" target="_blank">session.workspace.copy(sourceAbsolutePath, destinationAbsolutePath)</a>.</span></li>
                <li>move "sourceAbsolutePath" to "destinationAbsolutePath" - <span class="muted">Groovy DSL syntax for moving a node, equivalent to calling <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html#move(java.lang.String, java.lang.String)" target="_blank">session.move(sourceAbsolutePath, destinationAbsolutePath)</a>, except that the Session is saved automatically when the move is completed.</span></li>
                <li>rename node to "newname" - <span class="muted">Groovy DSL syntax for renaming a node, similar to calling <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html#move(java.lang.String, java.lang.String)" target="_blank">session.move(sourceAbsolutePath, destinationAbsolutePath)</a> with the new node name, except that the renamed node will retain its order and the Session is saved automatically when the rename is completed.</span></li>
                <li>save() - <span class="muted">Save the current JCR session.</span></li>
                <li>activate(String path) - <span class="muted">Activate the node at the given path.</span></li>
                <li>deactivate(String path) - <span class="muted">Deactivate the node at the given path.</span></li>
                <li>doWhileDisabled(String componentClassName, Closure closure) - <span class="muted">Execute the provided closure while the specified OSGi component is disabled.</span></li>
                <li>createQuery(Map predicates) - <span class="muted">Create a <a href="http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/search/Query.html" target="_blank">Query</a> instance from the <a href="http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/search/QueryBuilder.html" target="_blank">QueryBuilder</a> for the current JCR session.</span></li>
            </ul>
        </div>
    </div>
</div>