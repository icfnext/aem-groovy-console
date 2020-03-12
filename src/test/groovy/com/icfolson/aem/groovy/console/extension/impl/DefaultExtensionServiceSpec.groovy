package com.icfolson.aem.groovy.console.extension.impl

import com.google.common.io.ByteStreams
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.icfolson.aem.groovy.console.api.StarImport
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import com.icfolson.aem.groovy.console.api.context.impl.RequestScriptContext
import com.icfolson.aem.groovy.console.extension.ExtensionService
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.customizers.CompilationCustomizer

import java.text.SimpleDateFormat

class DefaultExtensionServiceSpec extends ProsperSpec {

    static final def SELECTORS = ["mobile"]

    static final def PARAMETERS = [firstName: "Clarence", lastName: "Wiggum"]

    static final def SCRIPT = "new SimpleDateFormat()"

    class TestStarImportExtensionProvider implements StarImportExtensionProvider {

        @Override
        Set<StarImport> getStarImports() {
            [new StarImport(SimpleDateFormat.package.name)] as Set
        }
    }

    class FirstBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
            [
                parameterNames: new BindingVariable((scriptContext as RequestScriptContext).request.parameterMap.keySet()),
                selectors: new BindingVariable([])
            ]
        }
    }

    class SecondBindingExtensionProvider implements BindingExtensionProvider {

        @Override
        Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
            [
                path: new BindingVariable((scriptContext as RequestScriptContext).request.requestPathInfo.resourcePath),
                selectors: new BindingVariable((scriptContext as RequestScriptContext).request.requestPathInfo.selectors as List)
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

    def "get compilation customizers"() {
        setup:
        def extensionService = new DefaultExtensionService()
        def firstProvider = new TestStarImportExtensionProvider()

        when:
        extensionService.bindStarImportExtensionProvider(firstProvider)

        then:
        extensionService.compilationCustomizers.size() == 1

        when:
        extensionService.unbindStarImportExtensionProvider(firstProvider)

        then:
        extensionService.compilationCustomizers.size() == 0
    }

    def "star imports"() {
        setup:
        def extensionService = new DefaultExtensionService()
        def starImportExtensionProvider = new TestStarImportExtensionProvider()

        when:
        extensionService.bindStarImportExtensionProvider(starImportExtensionProvider)

        and:
        runScriptWithExtensionService(extensionService)

        then:
        notThrown(MultipleCompilationErrorsException)

        when:
        extensionService.unbindStarImportExtensionProvider(starImportExtensionProvider)

        and:
        runScriptWithExtensionService(extensionService)

        then:
        thrown(MultipleCompilationErrorsException)
    }

    def "get binding"() {
        setup:
        def request = requestBuilder.build {
            path = "/"
            selectors = SELECTORS
            parameterMap = PARAMETERS
        }

        def response = responseBuilder.build()

        def scriptContext = new RequestScriptContext(request, response, null, null, null)

        def extensionService = new DefaultExtensionService()
        def firstProvider = new FirstBindingExtensionProvider()
        def secondProvider = new SecondBindingExtensionProvider()

        when:
        extensionService.bindBindingExtensionProvider(firstProvider)
        extensionService.bindBindingExtensionProvider(secondProvider)

        then:
        extensionService.getBindingVariables(scriptContext)["selectors"].value == request.requestPathInfo.selectors as List
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
            parameterMap = PARAMETERS
        }

        def scriptContext = new RequestScriptContext(request)

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

    private void runScriptWithExtensionService(ExtensionService extensionService) {
        def binding = new Binding(out: new PrintStream(ByteStreams.nullOutputStream()))

        def configuration = new CompilerConfiguration().addCompilationCustomizers(
            extensionService.compilationCustomizers as CompilationCustomizer[])

        new GroovyShell(binding, configuration).parse(SCRIPT).run()
    }
}
