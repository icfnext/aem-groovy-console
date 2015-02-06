package com.citytechinc.aem.groovy.console.extension.impl

import com.citytechinc.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.citytechinc.aem.groovy.console.table.Table
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.ReplicationOptions
import com.day.cq.replication.Replicator
import com.day.cq.search.PredicateGroup
import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import org.apache.felix.scr.ScrService
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.osgi.framework.BundleContext

import javax.jcr.Node
import javax.jcr.Session

@Service(ScriptMetaClassExtensionProvider)
@Component(immediate = true)
class DefaultScriptMetaClassExtensionProvider implements ScriptMetaClassExtensionProvider {

    @Reference
    Replicator replicator

    @Reference
    ScrService scrService

    @Reference
    QueryBuilder queryBuilder

    BundleContext bundleContext

    @Override
    Closure getScriptMetaClass(SlingHttpServletRequest request) {
        def resourceResolver = request.resourceResolver
        def session = resourceResolver.adaptTo(Session)
        def pageManager = resourceResolver.adaptTo(PageManager)

        def closure = {
            delegate.getNode = { String path ->
                session.getNode(path)
            }

            delegate.getResource = { String path ->
                resourceResolver.getResource(path)
            }

            delegate.getPage = { String path ->
                pageManager.getPage(path)
            }

            delegate.move = { String src ->
                ["to": { String dst ->
                    session.move(src, dst)
                    session.save()
                }]
            }

            delegate.rename = { Node node ->
                ["to": { String newName ->
                    def parent = node.parent

                    delegate.move node.path to parent.path + "/" + newName

                    if (parent.primaryNodeType.hasOrderableChildNodes()) {
                        def nextSibling = node.nextSibling as Node

                        if (nextSibling) {
                            parent.orderBefore(newName, nextSibling.name)
                        }
                    }

                    session.save()
                }]
            }

            delegate.copy = { String src ->
                ["to": { String dst ->
                    session.workspace.copy(src, dst)
                }]
            }

            delegate.save = {
                session.save()
            }

            delegate.getService = { Class serviceType ->
                def serviceReference = bundleContext.getServiceReference(serviceType)

                bundleContext.getService(serviceReference)
            }

            delegate.getService = { String className ->
                def serviceReference = bundleContext.getServiceReference(className)

                bundleContext.getService(serviceReference)
            }

            delegate.getServices = { Class serviceType, String filter ->
                def serviceReferences = bundleContext.getServiceReferences(serviceType, filter)

                serviceReferences.collect { bundleContext.getService(it) }
            }

            delegate.getServices = { String className, String filter ->
                def serviceReferences = bundleContext.getServiceReferences(className, filter)

                serviceReferences.collect { bundleContext.getService(it) }
            }

            delegate.activate = { String path, ReplicationOptions options = null  ->
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path, options)
            }

            delegate.deactivate = { String path, ReplicationOptions options = null  ->
                replicator.replicate(session, ReplicationActionType.DEACTIVATE, path, options)
            }

            delegate.doWhileDisabled = { String componentClassName, Closure closure ->
                def component = scrService.components.find { it.className == componentClassName }
                def result = null

                if (component) {
                    component.disable()

                    try {
                        result = closure()
                    } finally {
                        component.enable()
                    }
                } else {
                    result = closure()
                }

                result
            }

            delegate.createQuery { Map predicates ->
                queryBuilder.createQuery(PredicateGroup.create(predicates), session)
            }

            delegate.table = { Closure closure ->
                def table = new Table()

                closure.delegate = table
                closure.resolveStrategy = DELEGATE_FIRST
                closure()

                table
            }
        }

        closure
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }
}
