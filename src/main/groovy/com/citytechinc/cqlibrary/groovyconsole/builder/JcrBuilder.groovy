package com.citytechinc.cqlibrary.groovyconsole.builder

import javax.jcr.Node
import javax.jcr.Session

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JcrBuilder extends BuilderSupport {

    private static final Logger LOG = LoggerFactory.getLogger(JcrBuilder.class)

    def session

    def currentNode

    public JcrBuilder(session) {
        this.session = session
    }

    @Override
    def createNode(name) {
        checkCurrentNode()

        if (currentNode.hasNode(name)) {
            currentNode = currentNode.getNode(name)
        } else {
            if (isPage() && name != 'jcr:content') {
                currentNode = currentNode.addNode(name, 'cq:Page')

                LOG.info "createNode() added page = $name"
            } else {
                currentNode = currentNode.addNode(name)

                LOG.info "createNode() added node = $name"
            }
        }

        currentNode
    }

    @Override
    def createNode(name, value) {
        checkCurrentNode()

        if (currentNode.hasNode(name)) {
            currentNode = currentNode.getNode(name)
        } else {
            currentNode = currentNode.addNode(name, value)
        }

        LOG.info "createNode() added node = $name, type = $value"

        currentNode
    }

    @Override
    def createNode(name, Map attributes) {
        checkCurrentNode()

        def contentNode

        if (currentNode.hasNode(name)) {
            currentNode = currentNode.getNode(name)
            contentNode = currentNode
        } else {
            if (isPage() && name != 'jcr:content') {
                currentNode = currentNode.addNode(name, 'cq:Page')
                contentNode = currentNode.getNodeSafe('jcr:content')

                LOG.info "createNode() added page = $name"
            } else {
                currentNode = currentNode.addNode(name)
                contentNode = currentNode

                LOG.info "createNode() added node = $name"
            }
        }

        setAttributes(contentNode, attributes)

        currentNode
    }

    @Override
    def createNode(name, Map attributes, value) {
        checkCurrentNode()

        currentNode = createNode(name, value)

        setAttributes(currentNode, attributes)

        currentNode
    }

    private void setAttributes(node, attributes) {
        attributes.each { k, v ->
            if (v instanceof Calendar) {
                node.setProperty(k, (Calendar) v)
            } else {
                if (k == 'title') {
                    node.setProperty('jcr:title', v)
                } else {
                    node.setProperty(k, v)
                }
            }
        }
    }

    @Override
    def void setParent(parent, child) {

    }

    @Override
    void nodeCompleted(parent, node) {
        session.save()

        currentNode = currentNode.parent
    }

    private boolean isPage() {
        // check if current node is descendant of jcr:content
        def parentNode = currentNode

        boolean page = true

        while (parentNode.depth > 1) {
            if (parentNode.name == 'jcr:content') {
                page = false

                break
            } else {
                parentNode = parentNode.parent
            }
        }

        page
    }

    private checkCurrentNode() {
        if (currentNode == null) {
            currentNode = session.getRootNode()
        }
    }
}