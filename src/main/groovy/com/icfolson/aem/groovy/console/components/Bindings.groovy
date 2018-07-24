package com.icfolson.aem.groovy.console.components

import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import groovy.transform.Memoized
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.Self
import org.osgi.framework.BundleContext
import org.slf4j.Logger

import javax.inject.Inject
import javax.jcr.Session

@Model(adaptables = SlingHttpServletRequest)
class Bindings {

    @TupleConstructor
    static class BindingVariable {

        Class type

        String link
    }

    private static final Map<String, BindingVariable> DEFAULT_BINDING_VARIABLES = [
        "session": new BindingVariable(Session,
            "https://docs.adobe.com/docs/en/spec/javax.jcr/javadocs/jcr-2.0/javax/jcr/Session.html"),
        "pageManager": new BindingVariable(PageManager),
        "resourceResolver": new BindingVariable(ResourceResolver,
            "https://sling.apache.org/apidocs/sling10/org/apache/sling/api/resource/ResourceResolver.html"),
        "slingRequest": new BindingVariable(SlingHttpServletRequest,
            "https://sling.apache.org/apidocs/sling10/org/apache/sling/api/SlingHttpServletRequest.html"),
        "queryBuilder": new BindingVariable(QueryBuilder,
            "https://helpx.adobe.com/experience-manager/6-4/sites/developing/using/reference-materials/javadoc/com" +
                "/day/cq/search/QueryBuilder.html"),
        "bundleContext": new BindingVariable(BundleContext,
            "http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleContext.html"),
        "log": new BindingVariable(Logger, "http://www.slf4j.org/api/org/slf4j/Logger.html")
    ]

    @Self
    private SlingHttpServletRequest request

    @Inject
    private List<BindingExtensionProvider> bindingExtensionProviders

    @Memoized
    Map<String, BindingVariable> getBindingVariables() {
        def bindingVariables = DEFAULT_BINDING_VARIABLES

        bindingExtensionProviders.each { provider ->
            def binding = provider.getBinding(request)

            binding.variables.each { name, value ->
                bindingVariables[name as String] = new BindingVariable(value.class)
            }
        }

        bindingVariables
    }
}
