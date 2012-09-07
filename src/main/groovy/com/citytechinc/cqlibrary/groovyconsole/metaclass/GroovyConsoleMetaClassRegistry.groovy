package com.citytechinc.cqlibrary.groovyconsole.metaclass

import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Deactivate

import javax.jcr.Node
import javax.jcr.PropertyType
import javax.jcr.Value

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.LoggerFactory

import com.day.cq.wcm.api.Page

@Component(immediate = true, label = "Groovy Console MetaClass Registry")
@Property(name = "service.description", value = "Groovy Console MetaClass Registry")
class GroovyConsoleMetaClassRegistry {

    static final def LOG = LoggerFactory.getLogger(GroovyConsoleMetaClassRegistry)

    @Activate
    void activate() {
        LOG.info("activate() activating metaclass registry")

        GroovyConsoleMetaClassRegistry.registerMetaClasses()
    }

    @Deactivate
    void deactivate() {
        LOG.info("deactivate() deactivating metaclass registry")

        GroovyConsoleMetaClassRegistry.removeMetaClasses()
    }

    static void registerMetaClasses() {
        removeMetaClasses()
        registerNodeMetaClass()
        registerPageMetaClass()
    }

    static void removeMetaClasses() {
        def registry = InvokerHelper.metaRegistry

        registry.removeMetaClass(Node)
        registry.removeMetaClass(Page)
    }

    static void registerNodeMetaClass() {
        Node.metaClass {
            iterator {
                delegate.nodes
            }

            recurse { c ->
                c(delegate)

                delegate.nodes.each { node ->
                    node.recurse(c)
                }
            }

            recurse { String primaryNodeTypeName, c ->
                if (delegate.primaryNodeType.name == primaryNodeTypeName) {
                    c(delegate)
                }

                delegate.nodes.findAll { it.primaryNodeType.name == primaryNodeTypeName }.each { node ->
                    node.recurse(primaryNodeTypeName, c)
                }
            }

            recurse { Collection<String> primaryNodeTypeNames, c ->
                if (primaryNodeTypeNames.contains(delegate.primaryNodeType.name)) {
                    c(delegate)
                }

                delegate.nodes.findAll { primaryNodeTypeNames.contains(it.primaryNodeType.name) }.each { node ->
                    node.recurse(primaryNodeTypeNames, c)
                }
            }

            get { String propertyName ->
                def result = null

                if (delegate.hasProperty(propertyName)) {
                    def property = delegate.getProperty(propertyName)

                    if (property.multiple) {
                        result = property.values.collect { getResult(it) }
                    } else {
                        result = getResult(property.value)
                    }
                }

                result
            }

            set { String propertyName, value ->
                if (value) {
                    def valueFactory = delegate.session.valueFactory

                    if (value instanceof Object[]) {
                        def values = value.collect { valueFactory.createValue(it) }.toArray(new Value[0])

                        delegate.setProperty(propertyName, values)
                    } else {
                        def jcrValue = valueFactory.createValue(value)

                        delegate.setProperty(propertyName, jcrValue)
                    }
                } else {
                    if (delegate.hasProperty(propertyName)) {
                        delegate.getProperty(propertyName).remove()
                    }
                }
            }

            getOrAddNode { String name ->
                def node = delegate

                name.split("/").each { path ->
                    if (node.hasNode(path)) {
                        node = node.getNode(path)
                    } else {
                        node = node.addNode(path)
                    }
                }

                node
            }

            getOrAddNode { String name, String primaryNodeTypeName ->
                delegate.hasNode(name) ? delegate.getNode(name) : delegate.addNode(name, primaryNodeTypeName)
            }

            removeNode { String name ->
                boolean removed = false

                if (delegate.hasNode(name)) {
                    delegate.getNode(name).remove()
                    remove = true
                }

                removed
            }
        }
    }

    static void registerPageMetaClass() {
        Page.metaClass {
            iterator {
                delegate.listChildren()
            }

            recurse { Closure c ->
                c(delegate)

                delegate.listChildren().each { child ->
                    child.recurse(c)
                }
            }

            getNode {
                delegate.contentResource?.adaptTo(Node)
            }

            get { String name ->
                def node = delegate.contentResource?.adaptTo(Node)

                node ? node.get(name) : null
            }

            set { String name, value ->
                def node = delegate.contentResource?.adaptTo(Node)

                if (node) {
                    node.set(name, value)
                }
            }
        }
    }

    private static def getResult(value) {
        def result = null

        switch(value.type) {
            case PropertyType.BINARY:
                result = value.binary
                break
            case PropertyType.BOOLEAN:
                result = value.boolean
                break
            case PropertyType.DATE:
                result = value.date
                break
            case PropertyType.DECIMAL:
                result = value.decimal
                break
            case PropertyType.DOUBLE:
                result = value.double
                break
            case PropertyType.LONG:
                result = value.long
                break
            case PropertyType.STRING:
                result = value.string
        }

        result
    }
}
