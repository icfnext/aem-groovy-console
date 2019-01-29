package com.icfolson.aem.groovy.console.extension.impl

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.icfolson.aem.groovy.console.api.StarImport
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import com.icfolson.aem.prosper.specs.ProsperSpec
import spock.lang.Ignore

import java.text.SimpleDateFormat

class DefaultExtensionServiceSpec extends ProsperSpec {

    static final def SELECTORS = ["mobile"]

    static final def PARAMETERS = [firstName: "Clarence", lastName: "Wiggum"]

    class FirstStarImportExtensionProvider implements StarImportExtensionProvider {

        @Override
        Set<StarImport> getStarImports() {
            [InputStream, SimpleDateFormat].collect { clazz ->
                new StarImport(clazz.package.name)
            } as Set
        }
    }

    class SecondStarImportExtensionProvider implements StarImportExtensionProvider {

        @Override
        Set<StarImport> getStarImports() {
            [new StarImport(BigDecimal.getPackage().name)] as Set
        }
    }

    class FirstBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
            [
                parameterNames: new BindingVariable(scriptContext.request.parameterMap.keySet()),
                selectors: new BindingVariable([])
            ]
        }
    }

    class SecondBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
            [
                path: new BindingVariable(scriptContext.request.requestPathInfo.resourcePath),
                selectors: new BindingVariable(scriptContext.request.requestPathInfo.selectors as List)
            ]
        }
    }

    class TestScriptMetaClassExtensionProvider implements ScriptMetaClassExtensionProvider {

        @Override
        Closure getScriptMetaClass(ScriptContext scriptContext) {
            def closure = {

            }

            closure
        }
    }

    @Ignore
    def "get star imports"() {
        setup:
        def extensionService = new DefaultExtensionService()
        def firstProvider = new FirstStarImportExtensionProvider()
        def secondProvider = new SecondStarImportExtensionProvider()

        when:
        extensionService.bindStarImportExtensionProvider(firstProvider)
        extensionService.bindStarImportExtensionProvider(secondProvider)

        then:
        extensionService.compilationCustomizers.size() == 1

        and:

        extensionService.starImports.size() == 3
        extensionService.starImports*.packageName.containsAll(
            [InputStream, SimpleDateFormat, BigDecimal]*.getPackage().name)

        when:
        extensionService.unbindStarImportExtensionProvider(firstProvider)

        then:
        extensionService.compilationCustomizers.size() == 1

        and:
        extensionService.starImports.size() == 1
        extensionService.starImports[0].packageName == BigDecimal.getPackage().name
    }

    def "get binding"() {
        setup:
        def request = requestBuilder.build {
            selectors = SELECTORS
            parameters = PARAMETERS
        }

        def response = responseBuilder.build()

        def scriptContext = new ScriptContext(request, response, null, null, null)

        def extensionService = new DefaultExtensionService()
        def firstProvider = new FirstBindingExtensionProvider()
        def secondProvider = new SecondBindingExtensionProvider()

        when:
        extensionService.bindBindingExtensionProvider(firstProvider)
        extensionService.bindBindingExtensionProvider(secondProvider)

        then:
        extensionService.getBindingVariables(scriptContext)[
            "selectors"].value == request.requestPathInfo.selectors as List
        extensionService.getBindingVariables(scriptContext)["parameterNames"].value == request.parameterMap.keySet()
        extensionService.getBindingVariables(scriptContext)["path"].value == "/"

        when:
        extensionService.unbindBindingExtensionProvider(secondProvider)

        then:
        extensionService.getBindingVariables(scriptContext)["selectors"].value == []

        and:
        !extensionService.getBindingVariables(scriptContext)["path"]
    }

    def "get script metaclasses"() {
        setup:
        def request = requestBuilder.build {
            selectors = SELECTORS
            parameters = PARAMETERS
        }

        def scriptContext = new ScriptContext(request)

        def extensionService = new DefaultExtensionService()
        def firstProvider = new TestScriptMetaClassExtensionProvider()
        def secondProvider = new TestScriptMetaClassExtensionProvider()

        when:
        extensionService.bindScriptMetaClassExtensionProvider(firstProvider)
        extensionService.bindScriptMetaClassExtensionProvider(secondProvider)

        then:
        extensionService.getScriptMetaClasses(scriptContext).size() == 2

        when:
        extensionService.unbindScriptMetaClassExtensionProvider(secondProvider)

        then:
        extensionService.getScriptMetaClasses(scriptContext).size() == 1
    }
}
