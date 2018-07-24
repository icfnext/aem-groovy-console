package com.icfolson.aem.groovy.console.extension.impl

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import com.icfolson.aem.groovy.console.extension.ExtensionService
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest

import java.util.concurrent.CopyOnWriteArrayList

@Service(ExtensionService)
@Component(immediate = true)
@Slf4j("LOG")
class DefaultExtensionService implements ExtensionService {

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = BindingExtensionProvider,
        policy = ReferencePolicy.DYNAMIC)
    private List<BindingExtensionProvider> bindingExtensionProviders = new CopyOnWriteArrayList<>()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = StarImportExtensionProvider,
        policy = ReferencePolicy.DYNAMIC)
    private List<StarImportExtensionProvider> starImportExtensionProviders = new CopyOnWriteArrayList<>()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        referenceInterface = ScriptMetaClassExtensionProvider, policy = ReferencePolicy.DYNAMIC)
    private List<ScriptMetaClassExtensionProvider> scriptMetaClassExtensionProviders = new CopyOnWriteArrayList<>()

    @Override
    Set<String> getStarImports() {
        starImportExtensionProviders.collectMany { it.starImports } as Set
    }

    @Override
    Map<String, BindingVariable> getBindingVariables(SlingHttpServletRequest request) {
        def bindingVariables = [:]

        bindingExtensionProviders.each { extension ->
            extension.getBindingVariables(request).each { name, variable ->
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
    List<Closure> getScriptMetaClasses(SlingHttpServletRequest request) {
        scriptMetaClassExtensionProviders*.getScriptMetaClass(request)
    }

    @Synchronized
    void bindBindingExtensionProvider(BindingExtensionProvider extension) {
        bindingExtensionProviders.add(extension)

        LOG.info("added binding extension = {}", extension.class.name)
    }

    @Synchronized
    void unbindBindingExtensionProvider(BindingExtensionProvider extension) {
        bindingExtensionProviders.remove(extension)

        LOG.info("removed binding extension = {}", extension.class.name)
    }

    @Synchronized
    void bindStarImportExtensionProvider(StarImportExtensionProvider extension) {
        starImportExtensionProviders.add(extension)

        LOG.info("added star import extension = {} with imports = {}", extension.class.name, extension.starImports)
    }

    @Synchronized
    void unbindStarImportExtensionProvider(StarImportExtensionProvider extension) {
        starImportExtensionProviders.remove(extension)

        LOG.info("removed star import extension = {} with imports = {}", extension.class.name, extension.starImports)
    }

    @Synchronized
    void bindScriptMetaClassExtensionProvider(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensionProviders.add(extension)

        LOG.info("added script metaclass extension = {}", extension.class.name)
    }

    @Synchronized
    void unbindScriptMetaClassExtensionProvider(ScriptMetaClassExtensionProvider extension) {
        scriptMetaClassExtensionProviders.remove(extension)

        LOG.info("removed script metaclass extension = {}", extension.class.name)
    }
}