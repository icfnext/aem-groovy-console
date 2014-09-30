package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.api.BindingExtensionProvider
import com.citytechinc.cq.groovyconsole.api.ScriptMetaClassExtensionProvider
import com.citytechinc.cq.groovyconsole.api.StarImportExtensionProvider
import com.citytechinc.cq.groovyconsole.services.ExtensionService
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
    List<BindingExtensionProvider> bindingExtensions = []

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = StarImportExtensionProvider,
        policy = ReferencePolicy.DYNAMIC)
    List<StarImportExtensionProvider> starImportExtensions = []

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        referenceInterface = ScriptMetaClassExtensionProvider, policy = ReferencePolicy.DYNAMIC)
    List<ScriptMetaClassExtensionProvider> scriptMetaClassExtensions = []

    @Override
    Set<String> getStarImports() {
        starImportExtensions.collectMany { it.starImports } as Set
    }

    @Override
    Binding getBinding(SlingHttpServletRequest request) {
        def bindings = [:]

        bindingExtensions.each { extension ->
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
        scriptMetaClassExtensions*.getScriptMetaClass(request)
    }

    void bindBindingExtensions(BindingExtensionProvider extension) {
        bindingExtensions.add(extension)

        LOG.info "added binding extension = {}", extension.class.name
    }

    void unbindBindingExtensions(BindingExtensionProvider extension) {
        bindingExtensions.remove(extension)

        LOG.info "removed binding extension = {}", extension.class.name
    }

    void bindStarImportExtensions(StarImportExtensionProvider extension) {
        starImportExtensions.add(extension)

        LOG.info "added star import extension = {}", extension.class.name
    }

    void unbindStarImportExtensions(StarImportExtensionProvider extension) {
        starImportExtensions.remove(extension)

        LOG.info "removed star import extension = {}", extension.class.name
    }

    void bindScriptMetaClassExtensions(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensions.add(extension)

        LOG.info "added script metaclass extension = {}", extension.class.name
    }

    void unbindScriptMetaClassExtensions(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensions.remove(extension)

        LOG.info "removed script metaclass extension = {}", extension.class.name
    }
}