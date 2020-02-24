package com.icfolson.aem.groovy.console.extension.impl

import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.ReplicationOptions
import com.day.cq.replication.Replicator
import com.day.cq.search.PredicateGroup
import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.api.ScriptMetaClassExtensionProvider
import com.icfolson.aem.groovy.console.table.Table
import org.apache.sling.models.factory.ModelFactory
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.jcr.Node
import javax.jcr.Session

@Component(service = ScriptMetaClassExtensionProvider, immediate = true)
class DefaultScriptMetaClassExtensionProvider implements ScriptMetaClassExtensionProvider {

    @Reference
    private Replicator replicator

    @Reference
    private QueryBuilder queryBuilder

    private BundleContext bundleContext

    @Override
    Closure getScriptMetaClass(ScriptContext scriptContext) {
        def resourceResolver = scriptContext.resourceResolver
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

            delegate.getModel = { String path, Class type ->
                def modelFactoryReference = bundleContext.getServiceReference(ModelFactory)
                def modelFactory = bundleContext.getService(modelFactoryReference)

                def resource = resourceResolver.resolve(path)

                modelFactory.createModel(resource, type)
            }

            delegate.getService = { Class serviceType ->
                def serviceReference = bundleContext.getServiceReference(serviceType.name)

                bundleContext.getService(serviceReference)
            }

            delegate.getService = { String className ->
                def serviceReference = bundleContext.getServiceReference(className)

                bundleContext.getService(serviceReference)
            }

            delegate.getServices = { Class serviceType, String filter ->
                def serviceReferences = bundleContext.getServiceReferences(serviceType.name, filter)

                serviceReferences.collect { bundleContext.getService(it) }
            }

            delegate.getServices = { String className, String filter ->
                def serviceReferences = bundleContext.getServiceReferences(className, filter)

                serviceReferences.collect { bundleContext.getService(it) }
            }

            delegate.activate = { String path, ReplicationOptions options = null ->
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path, options)
            }

            delegate.deactivate = { String path, ReplicationOptions options = null ->
                replicator.replicate(session, ReplicationActionType.DEACTIVATE, path, options)
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
