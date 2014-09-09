package com.citytechinc.cq.groovyconsole.services.impl

import groovy.util.logging.Slf4j

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.codehaus.groovy.runtime.InvokerHelper

import com.citytechinc.aem.groovy.extension.api.BindingExtensionService
import com.citytechinc.aem.groovy.extension.api.MetaClassExtensionService
import com.citytechinc.aem.groovy.extension.api.StarImportExtensionService
import com.citytechinc.cq.groovyconsole.services.ExtensionsService

@Service(ExtensionsService)
@Component(immediate = true)
@Slf4j("LOG")
class DefaultExtensionsService implements ExtensionsService {

	@Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = com.citytechinc.aem.groovy.extension.api.BindingExtensionService.class, policy = ReferencePolicy.DYNAMIC)
	List<BindingExtensionService> bindingExtensions

	@Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = com.citytechinc.aem.groovy.extension.api.MetaClassExtensionService.class, policy = ReferencePolicy.DYNAMIC)
	List<MetaClassExtensionService> metaClassExtensions

	@Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, referenceInterface = com.citytechinc.aem.groovy.extension.api.StarImportExtensionService.class, policy = ReferencePolicy.DYNAMIC)
	List<StarImportExtensionService> starImportExtensions

	Map<Class, MetaClassExtensionService[]> metaClassExtensionMap = [:]

	void bindBindingExtensions(BindingExtensionService extension) {
		if (bindingExtensions){
			bindingExtensions.add(extension)
		} else {
			bindingExtensions = [extension]
		}
	}

	void unbindBindingExtensions(BindingExtensionService extension) {
		bindingExtensions?.remove(extension)
	}

	void bindMetaClassExtensions(MetaClassExtensionService extension) {
		if (metaClassExtensions) {
			metaClassExtensions.add(extension)
		} else {
			metaClassExtensions = [extension]
		}

		def extensionMap = extension.getMetaClassExtensions()
		extensionMap?.entrySet().each { entry ->
			def clazz = entry.key
			def closures = entry.value
			if (metaClassExtensionMap[clazz]) {
				metaClassExtensionMap[clazz].add(extension)
			} else {
				metaClassExtensionMap[clazz] = [extension]
			}
			closures.each {clazz.metaClass it}
		}
	}

	void unbindMetaClassExtensions(MetaClassExtensionService extension) {
		metaClassExtensions?.remove(extension)

		def extensionMap = extension.getMetaClassExtensions()
		def metaClassesToUpdate = []
		extensionMap?.entrySet().each { entry ->
			def clazz = entry.key
			if (metaClassExtensionMap[clazz]?.contains(extension)) {
				metaClassExtensionMap[clazz].remove(extension)
				metaClassesToUpdate.add(clazz)
			}
		}

		metaClassesToUpdate.each { clazz ->
			InvokerHelper.metaRegistry.removeMetaClass(clazz)
			metaClassExtensionMap[clazz].collectMany {it.getMetaClassExtensions()[clazz]}?.each {clazz.metaClass it}
		}
	}

	void bindStarImportExtensions(StarImportExtensionService extension) {
		if (starImportExtensions){
			starImportExtensions.add(extension)
		} else {
			starImportExtensions = [extension]
		}
	}

	void unbindStarImportExtensions(StarImportExtensionService extension) {
		starImportExtensions?.remove(extension)
	}

	@Override
	String[] getStarImports() {
		starImportExtensions?.collect { it.getStarImports() }?.flatten()
	}

	@Override
	Map<String, ?> getBindings(SlingHttpServletRequest request) {
		def bindings = [:]

		bindingExtensions?.collectMany{
			it.getBindings(request).entrySet()
		}.each{
			if(bindings[it.key]){
				LOG.warn("Variable '${it.key}' is already bound to value '${bindings[it.key]}'. Cannot assign value '${it.value}'.")
			} else {
				bindings[it.key] = it.value
			}
		}

		bindings
	}
}