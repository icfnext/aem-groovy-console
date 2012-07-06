package com.citytechinc.cqlibrary.groovyconsole.metaclass

import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Deactivate

import javax.jcr.Node
import javax.jcr.PropertyType
import javax.jcr.Value

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.day.cq.wcm.api.Page

@Component(immediate = true, label = "Groovy Console MetaClass Registry")
@Property(name = "service.description", value = "Groovy Console MetaClass Registry")
class MetaClassRegistry {

    static final def LOG = LoggerFactory.getLogger(MetaClassRegistry)

    @Activate
    void activate() {
        LOG.info("activate() activating metaclass registry")

        MetaClassRegistry.registerMetaClasses()
    }

    @Deactivate
    void deactivate() {
        LOG.info("deactivate() deactivating metaclass registry")

        MetaClassRegistry.removeMetaClasses()
    }

    static void removeMetaClasses() {
        def registry = InvokerHelper.metaRegistry

        registry.removeMetaClass(Node)
        registry.removeMetaClass(Page)
    }

    static void registerMetaClasses() {
        removeMetaClasses()

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

            get { String name ->
                def result = null

                if (delegate.hasProperty(name)) {
                    def property = delegate.getProperty(name)

                    if (property.multiple) {
                        result = property.values.collect { getResult(it) }
                    } else {
                        result = getResult(property.value)
                    }
                }

                result
            }

            set { String name, value ->
                if (value) {
                    def valueFactory = delegate.session.valueFactory

                    if (value instanceof Object[]) {
                        def values = value.collect { valueFactory.createValue(it) }.toArray(new Value[0])

                        delegate.setProperty(name, values)
                    } else {
                        def jcrValue = valueFactory.createValue(value)

                        delegate.setProperty(name, jcrValue)
                    }
                } else {
                    if (delegate.hasProperty(name)) {
                        delegate.getProperty(name).remove()
                    }
                }
            }

            getNodeSafe { relativePath ->
                def node = delegate

                relativePath.split("/").each { path ->
                    if (node.hasNode(path)) {
                        node = node.getNode(path)
                    } else {
                        node = node.addNode(path)
                    }
                }

                node
            }

            getNodeSafe { name, nodeTypeName ->
                delegate.hasNode(name) ? delegate.getNode(name) : delegate.addNode(name, nodeTypeName)
            }

            removeNode { name ->
                if (delegate.hasNode(name)) {
                    delegate.getNode(name).remove()
                }
            }
        }

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
