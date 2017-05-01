package com.icfolson.aem.groovy.console.services.impl

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import com.icfolson.aem.groovy.console.extension.impl.DefaultExtensionService
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver

import java.text.SimpleDateFormat

class DefaultExtensionServiceSpec extends ProsperSpec {

    static final def SELECTORS = ["mobile"]

    static final def PARAMETERS = [firstName: "Clarence", lastName: "Wiggum"]

    class FirstStarImportExtensionProvider implements StarImportExtensionProvider {

        @Override
        Set<String> getStarImports() {
            [InputStream.class.package.name, SimpleDateFormat.class.package.name] as Set
        }
    }

    class SecondStarImportExtensionProvider implements StarImportExtensionProvider {

        @Override
        Set<String> getStarImports() {
            [BigDecimal.class.package.name] as Set
        }
    }

    class FirstBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Binding getBinding(SlingHttpServletRequest request) {
            new Binding([
                parameterNames: request.parameterMap.keySet(),
                selectors: []
            ])
        }

        @Override
        Binding getBinding(ResourceResolver resourceResolver) {
            return null
        }
    }

    class SecondBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Binding getBinding(SlingHttpServletRequest request) {
            new Binding([
                path: request.requestPathInfo.resourcePath,
                selectors: request.requestPathInfo.selectors as List
            ])
        }

        @Override
        Binding getBinding(ResourceResolver resourceResolver) {
            return null
        }
    }

    class TestScriptMetaClassExtensionProvider implements ScriptMetaClassExtensionProvider {

        @Override
        Closure getScriptMetaClass(SlingHttpServletRequest request) {
            def closure = {

            }

            closure
        }
    }

    def "get star imports"() {
        setup:
        def extensionService = new DefaultExtensionService()
        def firstProvider = new FirstStarImportExtensionProvider()
        def secondProvider = new SecondStarImportExtensionProvider()

        when:
        extensionService.bindStarImportExtensionProvider(firstProvider)
        extensionService.bindStarImportExtensionProvider(secondProvider)

        then:
        extensionService.starImports.size() == 3
        extensionService.starImports.containsAll([InputStream, SimpleDateFormat, BigDecimal]*.package.name)

        when:
        extensionService.unbindStarImportExtensionProvider(firstProvider)

        then:
        extensionService.starImports.size() == 1
        extensionService.starImports[0] == BigDecimal.package.name
    }

    def "get binding"() {
        setup:
        def request = requestBuilder.build {
            selectors = SELECTORS
            parameters = PARAMETERS
        }

        def extensionService = new DefaultExtensionService()
        def firstProvider = new FirstBindingExtensionProvider()
        def secondProvider = new SecondBindingExtensionProvider()

        when:
        extensionService.bindBindingExtensionProvider(firstProvider)
        extensionService.bindBindingExtensionProvider(secondProvider)

        then:
        extensionService.getBinding(request)["selectors"] == request.requestPathInfo.selectors as List
        extensionService.getBinding(request)["parameterNames"] == request.parameterMap.keySet()
        extensionService.getBinding(request)["path"] == "/"

        when:
        extensionService.unbindBindingExtensionProvider(secondProvider)

        then:
        extensionService.getBinding(request)["selectors"] == []

        when:
        extensionService.getBinding(request)["path"]

        then:
        thrown(MissingPropertyException)
    }

    def "get script metaclasses"() {
        setup:
        def request = requestBuilder.build {
            selectors = SELECTORS
            parameters = PARAMETERS
        }

        def extensionService = new DefaultExtensionService()
        def firstProvider = new TestScriptMetaClassExtensionProvider()
        def secondProvider = new TestScriptMetaClassExtensionProvider()

        when:
        extensionService.bindScriptMetaClassExtensionProvider(firstProvider)
        extensionService.bindScriptMetaClassExtensionProvider(secondProvider)

        then:
        extensionService.getScriptMetaClasses(request).size() == 2

        when:
        extensionService.unbindScriptMetaClassExtensionProvider(secondProvider)

        then:
        extensionService.getScriptMetaClasses(request).size() == 1
    }
}
