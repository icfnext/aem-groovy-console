package com.icfolson.aem.groovy.console.extension.impl

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import com.icfolson.aem.groovy.console.extension.impl.DefaultExtensionService
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.SlingHttpServletRequest

import java.text.SimpleDateFormat

class DefaultExtensionServiceSpec extends ProsperSpec {

    static final def SELECTORS = ["mobile"]

    static final def PARAMETERS = [firstName: "Clarence", lastName: "Wiggum"]

    class FirstStarImportExtensionProvider implements StarImportExtensionProvider {

        @Override
        Set<String> getStarImports() {
            [InputStream.getPackage().name, SimpleDateFormat.getPackage().name] as Set
        }
    }

    class SecondStarImportExtensionProvider implements StarImportExtensionProvider {

        @Override
        Set<String> getStarImports() {
            [BigDecimal.getPackage().name] as Set
        }
    }

    class FirstBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Binding getBinding(SlingHttpServletRequest request) {
            null
        }

        @Override
        Map<String, BindingVariable> getBindingVariables(SlingHttpServletRequest request) {
            [
                parameterNames: new BindingVariable(request.parameterMap.keySet()),
                selectors: new BindingVariable([])
            ]
        }
    }

    class SecondBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Binding getBinding(SlingHttpServletRequest request) {
            null
        }

        @Override
        Map<String, BindingVariable> getBindingVariables(SlingHttpServletRequest request) {
            [
                path: new BindingVariable(request.requestPathInfo.resourcePath),
                selectors: new BindingVariable(request.requestPathInfo.selectors as List)
            ]
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
        extensionService.starImports.containsAll([InputStream, SimpleDateFormat, BigDecimal]*.getPackage().name)

        when:
        extensionService.unbindStarImportExtensionProvider(firstProvider)

        then:
        extensionService.starImports.size() == 1
        extensionService.starImports[0] == BigDecimal.getPackage().name
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
        extensionService.getBindingVariables(request)["selectors"].value == request.requestPathInfo.selectors as List
        extensionService.getBindingVariables(request)["parameterNames"].value == request.parameterMap.keySet()
        extensionService.getBindingVariables(request)["path"].value == "/"

        when:
        extensionService.unbindBindingExtensionProvider(secondProvider)

        then:
        extensionService.getBindingVariables(request)["selectors"].value == []

        and:
        !extensionService.getBindingVariables(request)["path"]
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
