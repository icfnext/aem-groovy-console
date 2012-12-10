package com.citytechinc.groovyconsole.mock

import javax.jcr.Session
import javax.servlet.http.HttpServletRequest

import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl

class MockResourceResolver implements ResourceResolver {

    private final Session session

    MockResourceResolver(session) {
        this.session = session
    }

    public Resource getResource(String path) {
        session.nodeExists(path) ? new MockResource(session.getNode(path)) : null
    }

    public Resource getResource(Resource base, String path) {
        return getResource("${base.path}/$path")
    }

    public ResourceResolver clone(Map<String, Object> authenticationInfo) {
        throw new UnsupportedOperationException()
    }

    public Iterator<Resource> findResources(String query, String language) {
        throw new UnsupportedOperationException()
    }

    public String[] getSearchPath() {
        throw new UnsupportedOperationException()
    }

    public Iterator<Resource> listChildren(Resource parent) {
        throw new UnsupportedOperationException()
    }

    public Iterable<Resource> getChildren(Resource parent) {
        throw new UnsupportedOperationException()
    }

    public String map(String resourcePath) {
        throw new UnsupportedOperationException()
    }

    public String map(HttpServletRequest request, String resourcePath) {
        throw new UnsupportedOperationException()
    }

    public Iterator<Map<String, Object>> queryResources(String query, String language) {
        throw new UnsupportedOperationException()
    }

    public Resource resolve(HttpServletRequest request, String absPath) {
        throw new UnsupportedOperationException()
    }

    public Resource resolve(HttpServletRequest request) {
        throw new UnsupportedOperationException()
    }

    public Resource resolve(String absPath) {
        return getResource(absPath)
    }

    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == PageManager) {
            def factory = new PageManagerFactoryImpl()

            result = factory.getPageManager(this)
        } else if (type == Session) {
            result = session
        } else {
            result = null
        }

        result
    }

    public boolean isLive() {
        throw new UnsupportedOperationException()
    }

    public void close() {

    }

    public String getUserID() {
        throw new UnsupportedOperationException()
    }

    public Object getAttribute(String name) {
        throw new UnsupportedOperationException()
    }

    public Iterator<String> getAttributeNames() {
        throw new UnsupportedOperationException()
    }

    public void delete(Resource resource) {
        throw new UnsupportedOperationException()
    }

    public Resource create(Resource parent, String name, Map<String, Object> properties) {
        throw new UnsupportedOperationException()
    }

    public void revert() {
        throw new UnsupportedOperationException()
    }

    public void commit() {
        throw new UnsupportedOperationException()
    }

    public boolean hasChanges() {
        throw new UnsupportedOperationException()
    }
}
