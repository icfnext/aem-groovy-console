package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.api.BindingExtensionService
import com.citytechinc.cq.groovyconsole.api.ScriptMetaClassExtensionService
import com.citytechinc.cq.groovyconsole.api.StarImportExtensionService
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

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = BindingExtensionService,
        policy = ReferencePolicy.DYNAMIC)
    List<BindingExtensionService> bindingExtensions = []

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = StarImportExtensionService,
        policy = ReferencePolicy.DYNAMIC)
    List<StarImportExtensionService> starImportExtensions = []

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = ScriptMetaClassExtensionService,
        policy = ReferencePolicy.DYNAMIC)
    List<ScriptMetaClassExtensionService> scriptMetaClassExtensions = []

    @Override
    Set<String> getStarImports() {
        starImportExtensions.collectMany { it.starImports } as Set
    }

    @Override
    Binding getBinding(SlingHttpServletRequest request) {
        def bindings = [:]

        bindingExtensions.each { extension ->
            def binding = extension.getBinding(request)

            binding.each { key, value ->
                if (bindings[key]) {
                    LOG.warn("binding variable {} is already bound to value {}, cannot assign value = {}", key,
                        bindings[key], value)
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

    void bindBindingExtensions(BindingExtensionService extension) {
        bindingExtensions.add(extension)
    }

    void unbindBindingExtensions(BindingExtensionService extension) {
        bindingExtensions.remove(extension)
    }

    void bindStarImportExtensions(StarImportExtensionService extension) {
        starImportExtensions.add(extension)
    }

    void unbindStarImportExtensions(StarImportExtensionService extension) {
        starImportExtensions.remove(extension)
    }

    void bindScriptMetaClassExtensions(ScriptMetaClassExtensionService extension) {
        scriptMetaClassExtensions.add(extension)
    }

    void unbindScriptMetaClassExtensions(ScriptMetaClassExtensionService extension) {
        scriptMetaClassExtensions.remove(extension)
    }
}