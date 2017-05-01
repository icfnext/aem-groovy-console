package com.icfolson.aem.groovy.console.extension.impl

import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.extension.builders.NodeBuilder
import com.icfolson.aem.groovy.extension.builders.PageBuilder
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
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
        def binding = getBinding(request.resourceResolver)

        binding.setVariable("slingRequest", request)

        binding
    }

    @Override
    Binding getBinding(ResourceResolver resourceResolver) {
        def session = resourceResolver.adaptTo(Session)

        new Binding([
            log: LoggerFactory.getLogger("groovyconsole"),
            session: session,
            pageManager: resourceResolver.adaptTo(PageManager),
            resourceResolver: resourceResolver,
            queryBuilder: queryBuilder,
            nodeBuilder: new NodeBuilder(session),
            pageBuilder: new PageBuilder(session),
            bundleContext: bundleContext
        ])
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }
}