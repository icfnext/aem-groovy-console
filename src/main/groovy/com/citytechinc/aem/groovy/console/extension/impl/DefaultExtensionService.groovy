package com.citytechinc.aem.groovy.console.extension.impl

import com.citytechinc.aem.groovy.console.api.BindingExtensionProvider
import com.citytechinc.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.citytechinc.aem.groovy.console.api.StarImportExtensionProvider
import com.citytechinc.aem.groovy.console.extension.ExtensionService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest

@Service(ExtensionService)
@Component(immediate = true)
@Slf4j("LOG")
class DefaultExtensionService implements ExtensionService {

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = BindingExtensionProvider,
        policy = ReferencePolicy.DYNAMIC)
    List<BindingExtensionProvider> bindingExtensionProviders = []

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = StarImportExtensionProvider,
        policy = ReferencePolicy.DYNAMIC)
    List<StarImportExtensionProvider> starImportExtensionProviders = []

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        referenceInterface = ScriptMetaClassExtensionProvider, policy = ReferencePolicy.DYNAMIC)
    List<ScriptMetaClassExtensionProvider> scriptMetaClassExtensionProviders = []

    @Override
    Set<String> getStarImports() {
        starImportExtensionProviders.collectMany { it.starImports } as Set
    }

    @Override
    Binding getBinding(SlingHttpServletRequest request) {
        def bindings = [:]

        bindingExtensionProviders.each { extension ->
            def binding = extension.getBinding(request)

            binding.variables.each { key, value ->
                if (bindings[key]) {
                    LOG.warn "binding variable {} is already bound to value {}, cannot assign value = {}", key,
                        bindings[key], value
                } else {
                    bindings[key] = value
                }
            }
        }

        new Binding(bindings)
    }

    @Override
    List<Closure> getScriptMetaClasses(SlingHttpServletRequest request) {
        scriptMetaClassExtensionProviders*.getScriptMetaClass(request)
    }

    void bindBindingExtensionProvider(BindingExtensionProvider extension) {
        bindingExtensionProviders.add(extension)

        LOG.info "added binding extension = {}", extension.class.name
    }

    void unbindBindingExtensionProvider(BindingExtensionProvider extension) {
        bindingExtensionProviders.remove(extension)

        LOG.info "removed binding extension = {}", extension.class.name
    }

    void bindStarImportExtensionProvider(StarImportExtensionProvider extension) {
        starImportExtensionProviders.add(extension)

        LOG.info "added star import extension = {}", extension.class.name
    }

    void unbindStarImportExtensionProvider(StarImportExtensionProvider extension) {
        starImportExtensionProviders.remove(extension)

        LOG.info "removed star import extension = {}", extension.class.name
    }

    void bindScriptMetaClassExtensionProvider(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensionProviders.add(extension)

        LOG.info "added script metaclass extension = {}", extension.class.name
    }

    void unbindScriptMetaClassExtensionProvider(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensionProviders.remove(extension)

        LOG.info "removed script metaclass extension = {}", extension.class.name
    }
}