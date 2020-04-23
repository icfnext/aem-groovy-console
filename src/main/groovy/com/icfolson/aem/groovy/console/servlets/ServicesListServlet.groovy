package com.icfolson.aem.groovy.console.servlets

import org.apache.commons.lang3.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component

import javax.servlet.Servlet
import javax.servlet.ServletException

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES
import static org.osgi.framework.Constants.OBJECTCLASS

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/services"
])
class ServicesListServlet extends AbstractJsonResponseServlet {

    private BundleContext bundleContext

    @Override
    protected void doGet(SlingHttpServletRequest request,
        SlingHttpServletResponse response) throws ServletException, IOException {
        writeJsonResponse(response, adaptersMap + servicesMap)
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }

    private Map<String, String> getAdaptersMap() {
        def adapters = [:] as TreeMap

        def serviceReferences = bundleContext.getServiceReferences(AdapterFactory.name, null).findAll { serviceReference ->
            def adaptableClasses = serviceReference.getProperty(ADAPTABLE_CLASSES) as String[]

            adaptableClasses && adaptableClasses.contains(ResourceResolver.name)
        }

        serviceReferences.each { serviceReference ->
            def adapterClasses = serviceReference.getProperty(ADAPTER_CLASSES)

            if (adapterClasses instanceof String[]) {
                adapterClasses.each { String adapterClassName ->
                    adapters[adapterClassName] = getAdapterDeclaration(adapterClassName)
                }
            } else {
                adapters[adapterClasses] = getAdapterDeclaration(adapterClasses as String)
            }
        }

        adapters
    }

    private Map<String, String> getServicesMap() {
        def services = [:] as TreeMap

        Map<String, List<String>> allServices = [:]

        bundleContext.getAllServiceReferences(null, null).each { serviceReference ->
            def name = serviceReference.getProperty("component.name") as String
            def objectClass = serviceReference.getProperty(OBJECTCLASS) as String[]

            objectClass.each { className ->
                def implementationClassNames = allServices[className] as List ?: []

                if (name) {
                    implementationClassNames.add(name)
                }

                allServices[className] = implementationClassNames
            }
        }

        allServices.each { className, implementationClassNames ->
            services[className] = getServiceDeclaration(className, null)

            if (implementationClassNames.size() > 1) {
                implementationClassNames.each { String implementationClassName ->
                    services[implementationClassName] = getServiceDeclaration(className, implementationClassName)
                }
            }
        }

        services
    }

    private static String getAdapterDeclaration(String className) {
        def simpleName = className.tokenize('.').last()
        def variableName = StringUtils.uncapitalize(simpleName)

        "def $variableName = resourceResolver.adaptTo($className)"
    }

    private static String getServiceDeclaration(String className, implementationClassName) {
        def simpleName = className.tokenize('.').last()
        def variableName = StringUtils.uncapitalize(simpleName)
        def declaration

        if (implementationClassName) {
            def filter = "(component.name=$implementationClassName)"

            declaration = "def $variableName = getServices(\"$className\", \"$filter\")[0]"
        } else {
            declaration = "def $variableName = getService(\"$className\")"
        }

        declaration
    }
}
