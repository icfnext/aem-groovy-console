package com.citytechinc.cq.groovyconsole.servlets

import groovy.json.JsonBuilder
import org.apache.commons.lang3.StringUtils
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet
import org.osgi.framework.BundleContext
import org.osgi.framework.Constants
import org.osgi.service.component.ComponentConstants

import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/services")
class ServicesListServlet extends SlingSafeMethodsServlet {

    def bundleContext

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse
    response) throws ServletException, IOException {
        def services = getServicesMap()

        response.contentType = "application/json"

        new JsonBuilder(services).writeTo(response.writer)
    }

    def getServicesMap() {
        def services = [:] as TreeMap
        def allServices = [:]

        bundleContext.getAllServiceReferences(null, null).each { serviceReference ->
            def name = serviceReference.getProperty(ComponentConstants.COMPONENT_NAME)
            def objectClass = serviceReference.getProperty(Constants.OBJECTCLASS)

            objectClass.each { className ->
                def implementationClassNames = allServices[className] ?: []

                if (name) {
                    implementationClassNames.add(name)
                }

                allServices[className] = implementationClassNames
            }
        }

        allServices.each { className, implementationClassNames ->
            services[className] = getDeclaration(className, null)

            if (implementationClassNames.size() > 1) {
                implementationClassNames.each { implementationClassName ->
                    services[implementationClassName] = getDeclaration(className, implementationClassName)
                }
            }
        }

        services
    }

    def getDeclaration(className, implementationClassName) {
        def simpleName = className.tokenize('.').last()
        def variableName = StringUtils.uncapitalize(simpleName)
        def declaration

        if (implementationClassName) {
            def filter = "(${ComponentConstants.COMPONENT_NAME}=$implementationClassName)"

            declaration = "def $variableName = getServices(\"$className\", \"$filter\")[0]"
        } else {
            declaration = "def $variableName = getService(\"$className\")"
        }

        declaration
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }
}
