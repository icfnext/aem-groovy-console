package com.icfolson.aem.groovy.console.extension.impl

import javax.jcr.Session

import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.framework.BundleContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.extension.builders.NodeBuilder
import com.icfolson.aem.groovy.extension.builders.PageBuilder

@Service(BindingExtensionProvider)
@Component(immediate = true)
class DefaultBindingExtensionProvider implements BindingExtensionProvider {

    @Reference
    private QueryBuilder queryBuilder

    private BundleContext bundleContext

    @Override
    Binding getBinding(SlingHttpServletRequest request) {
        new Binding()
    }

    @Override
    Map<String, BindingVariable> getBindingVariables(SlingHttpServletRequest request,
        SlingHttpServletResponse response, PrintStream printStream) {
        def resourceResolver = request.resourceResolver
        def session = resourceResolver.adaptTo(Session)

        def bindingVariables = [
            log: new BindingVariable(LoggerFactory.getLogger("groovyconsole"), Logger,
                "http://www.slf4j.org/api/org/slf4j/Logger.html"),
            session: new BindingVariable(session, Session,
                "https://docs.adobe.com/docs/en/spec/javax.jcr/javadocs/jcr-2.0/javax/jcr/Session.html"),
            slingRequest: new BindingVariable(request, SlingHttpServletRequest,
                "https://sling.apache.org/apidocs/sling10/org/apache/sling/api/SlingHttpServletRequest.html"),
            slingResponse: new BindingVariable(response, SlingHttpServletResponse,
                "https://sling.apache.org/apidocs/sling10/org/apache/sling/api/SlingHttpServletResponse.html"),
            pageManager: new BindingVariable(resourceResolver.adaptTo(PageManager), PageManager),
            resourceResolver: new BindingVariable(resourceResolver, ResourceResolver,
                "https://sling.apache.org/apidocs/sling10/org/apache/sling/api/resource/ResourceResolver.html"),
            queryBuilder: new BindingVariable(queryBuilder, QueryBuilder,
                "https://helpx.adobe.com/experience-manager/6-4/sites/developing/using/reference-materials/javadoc" +
                    "/com/day/cq/search/QueryBuilder.html"),
            nodeBuilder: new BindingVariable(new NodeBuilder(session), NodeBuilder,
                "http://code.digitalatolson.com/aem-groovy-extension/groovydocs/com/icfolson/aem/groovy/extension" +
                    "/builders/NodeBuilder.html"),
            pageBuilder: new BindingVariable(new PageBuilder(session), PageBuilder,
                "http://code.digitalatolson.com/aem-groovy-extension/groovydocs/com/icfolson/aem/groovy/extension" +
                    "/builders/PageBuilder.html"),
            bundleContext: new BindingVariable(bundleContext, BundleContext,
                "http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleContext.html")
        ]

        bindingVariables
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }
}