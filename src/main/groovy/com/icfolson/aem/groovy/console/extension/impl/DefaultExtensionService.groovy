package com.icfolson.aem.groovy.console.extension.impl

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.CompilationCustomizerExtensionProvider
import com.icfolson.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.icfolson.aem.groovy.console.api.StarImport
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import com.icfolson.aem.groovy.console.api.context.ScriptContext
import com.icfolson.aem.groovy.console.extension.ExtensionService
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy

import java.util.concurrent.CopyOnWriteArrayList

@Component(service = ExtensionService, immediate = true)
@Slf4j("LOG")
class DefaultExtensionService implements ExtensionService {

    private volatile List<BindingExtensionProvider> bindingExtensionProviders = new CopyOnWriteArrayList<>()

    private volatile List<StarImportExtensionProvider> starImportExtensionProviders = new CopyOnWriteArrayList<>()

    private volatile List<ScriptMetaClassExtensionProvider> scriptMetaClassExtensionProviders = new CopyOnWriteArrayList<>()

    private volatile List<CompilationCustomizerExtensionProvider> compilationCustomizerExtensionProviders =
        new CopyOnWriteArrayList<>()

    @Override
    Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
        def bindingVariables = [:] as Map<String, BindingVariable>

        bindingExtensionProviders.each { extension ->
            extension.getBindingVariables(scriptContext).each { name, variable ->
                if (bindingVariables[name]) {
                    LOG.debug("binding variable {} is currently bound to value {}, overriding with value = {}", name,
                        bindingVariables[name], variable.value)
                }

                bindingVariables[name] = variable
            }
        }

        bindingVariables
    }

    @Override
    List<Closure> getScriptMetaClasses(ScriptContext scriptContext) {
        scriptMetaClassExtensionProviders*.getScriptMetaClass(scriptContext)
    }

    @Override
    Set<StarImport> getStarImports() {
        starImportExtensionProviders.collectMany { it.starImports } as Set<StarImport>
    }

    @Override
    List<CompilationCustomizer> getCompilationCustomizers() {
        def importPackageNames = starImports.collect { it.packageName }

        def compilationCustomizers = []

        if (importPackageNames) {
            compilationCustomizers.add(new ImportCustomizer().addStarImports(importPackageNames as String[]))
        }

        compilationCustomizerExtensionProviders.each { provider ->
            compilationCustomizers.addAll(provider.compilationCustomizers)
        }

        compilationCustomizers
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    @Synchronized
    void bindBindingExtensionProvider(BindingExtensionProvider extension) {
        bindingExtensionProviders.add(extension)

        LOG.info("added binding extension : {}", extension.class.name)
    }

    @Synchronized
    void unbindBindingExtensionProvider(BindingExtensionProvider extension) {
        bindingExtensionProviders.remove(extension)

        LOG.info("removed binding extension : {}", extension.class.name)
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    @Synchronized
    void bindStarImportExtensionProvider(StarImportExtensionProvider extension) {
        starImportExtensionProviders.add(extension)

        LOG.info("added star import extension : {} with imports : {}", extension.class.name, extension.starImports)
    }

    @Synchronized
    void unbindStarImportExtensionProvider(StarImportExtensionProvider extension) {
        starImportExtensionProviders.remove(extension)

        LOG.info("removed star import extension : {} with imports : {}", extension.class.name, extension.starImports)
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    @Synchronized
    void bindScriptMetaClassExtensionProvider(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensionProviders.add(extension)

        LOG.info("added script metaclass extension : {}", extension.class.name)
    }

    @Synchronized
    void unbindScriptMetaClassExtensionProvider(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensionProviders.remove(extension)

        LOG.info("removed script metaclass extension : {}", extension.class.name)
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    @Synchronized
    void bindCompilationCustomizerExtensionProvider(CompilationCustomizerExtensionProvider extension) {
        compilationCustomizerExtensionProviders.add(extension)

        LOG.info("adding compilation customizer extension : {}", extension.class.name)
    }

    @Synchronized
    void unbindCompilationCustomizerExtensionProvider(CompilationCustomizerExtensionProvider extension) {
        compilationCustomizerExtensionProviders.remove(extension)

        LOG.info("removed compilation customizer extension : {}", extension.class.name)
    }
}