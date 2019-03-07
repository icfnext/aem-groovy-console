package com.icfolson.aem.groovy.console.extension.impl

import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.extension.builders.NodeBuilder
import com.icfolson.aem.groovy.extension.builders.PageBuilder
import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.jcr.Session

@Component(service = BindingExtensionProvider, immediate = true)
class DefaultBindingExtensionProvider implements BindingExtensionProvider {

    @Reference
    private QueryBuilder queryBuilder

    private BundleContext bundleContext

    @Override
    Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
        def resourceResolver = scriptContext.request.resourceResolver
        def session = resourceResolver.adaptTo(Session)

        def bindingVariables = [
            log: new BindingVariable(LoggerFactory.getLogger("groovyconsole"), Logger,
                "http://www.slf4j.org/api/org/slf4j/Logger.html"),
            session: new BindingVariable(session, Session,
                "https://docs.adobe.com/docs/en/spec/javax.jcr/javadocs/jcr-2.0/javax/jcr/Session.html"),
            slingRequest: new BindingVariable(scriptContext.request, SlingHttpServletRequest,
                "https://sling.apache.org/apidocs/sling10/org/apache/sling/api/SlingHttpServletRequest.html"),
            slingResponse: new BindingVariable(scriptContext.response, SlingHttpServletResponse,
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
                "http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleContext.html"),
            out: new BindingVariable(scriptContext.printStream, PrintStream,
                "https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html")
        ]

        if (scriptContext.data) {
            try {
                def json = new JsonSlurper().parseText(scriptContext.data)

                bindingVariables["data"] = new BindingVariable(json, json.class)
            } catch (JsonException ignored) {
                // if data cannot be parsed as a JSON object, bind it as a String
                bindingVariables["data"] = new BindingVariable(scriptContext.data, String)
            }
        }

        bindingVariables
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }
}