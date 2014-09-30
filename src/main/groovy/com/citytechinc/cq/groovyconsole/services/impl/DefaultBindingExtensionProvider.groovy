package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.cq.groovyconsole.api.BindingExtensionProvider
import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory

import javax.jcr.Session

@Service(BindingExtensionProvider)
@Component(immediate = true)
class DefaultBindingExtensionProvider implements BindingExtensionProvider {

    @Reference
    QueryBuilder queryBuilder

    BundleContext bundleContext

    @Override
    Binding getBinding(SlingHttpServletRequest request) {
        def resourceResolver = request.resourceResolver
        def session = resourceResolver.adaptTo(Session)

        def map = [
            log             : LoggerFactory.getLogger("groovyconsole"),
            session         : session,
            slingRequest    : request,
            pageManager     : resourceResolver.adaptTo(PageManager),
            resourceResolver: resourceResolver,
            queryBuilder    : queryBuilder,
            nodeBuilder     : new NodeBuilder(session),
            pageBuilder     : new PageBuilder(session),
            bundleContext   : bundleContext
        ]

        new Binding(map)
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }
}