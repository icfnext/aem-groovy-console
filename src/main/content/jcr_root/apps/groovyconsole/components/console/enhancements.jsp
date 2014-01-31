<%@include file="/libs/foundation/global.jsp" %>

<div class="accordion-group">
    <div class="accordion-heading">
        <a class="accordion-toggle" href="#enhancements" data-toggle="collapse" data-target="#enhancements" data-parent="#accordion">Enhancements</a>
    </div>
    <div id="enhancements" class="accordion-body collapse">
        <div class="accordion-inner">
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
        </div>
    </div>
</div>