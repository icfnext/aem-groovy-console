package com.citytechinc.cqlibrary.groovyconsole.mock

import javax.jcr.Node

import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceMetadata
import org.apache.sling.api.resource.ResourceResolver

import com.day.cq.wcm.api.Page
import com.day.cq.wcm.core.impl.PageImpl

class MockResource implements Resource {

    def node

    MockResource(node) {
        this.node = node
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == Node) {
            result = node
        } else if (type == Page && 'cq:Page' == getResourceType()) {
            result = new PageImpl(this)
        } else {
            result = null
        }

        return result
    }

    @Override
    public String getPath() {
        return node.path
    }

    @Override
    public String getName() {
        return node.name
    }

    @Override
    public Resource getParent() {
        return new MockResource(node.parent)
    }

    @Override
    public Iterator<Resource> listChildren() {
        return node.nodes.collect { new MockResource(it) }.iterator()
    }

    @Override
    public Resource getChild(String relPath) {
        return node.hasNode(relPath) ? new MockResource(node.getNode(relPath)) : null
    }

    @Override
    public String getResourceType() {
        return node.get('sling:resourceType') ?: node.primaryNodeType.name
    }

    @Override
    public String getResourceSuperType() {
        return node.get('sling:resourceSuperType')
    }

    @Override
    public boolean isResourceType(String resourceType) {
        return getResourceType() == resourceType
    }

    @Override
    public ResourceMetadata getResourceMetadata() {
        throw new UnsupportedOperationException()
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return new MockResourceResolver(node.session)
    }
}
