package com.citytechinc.cqlibrary.groovyconsole.builders

class NodeBuilder extends BuilderSupport {

    def session

    def currentNode

    public NodeBuilder(session) {
        this.session = session

        currentNode = session.rootNode
    }

    @Override
    def createNode(name) {
        currentNode = currentNode.getOrAddNode(name)

        currentNode
    }

    @Override
    def createNode(name, value) {
        currentNode = currentNode.getOrAddNode(name, value)

        currentNode
    }

    @Override
    def createNode(name, Map attributes) {
        currentNode = currentNode.getOrAddNode(name)

        setAttributes(currentNode, attributes)

        currentNode
    }

    @Override
    def createNode(name, Map attributes, value) {
        currentNode = createNode(name, value)

        setAttributes(currentNode, attributes)

        currentNode
    }

    @Override
    void setParent(parent, child) {

    }

    @Override
    void nodeCompleted(parent, node) {
        session.save()

        currentNode = currentNode.parent
    }

    private void setAttributes(node, attributes) {
        attributes.each { k, v ->
            node.set(k, v)
        }
    }
}