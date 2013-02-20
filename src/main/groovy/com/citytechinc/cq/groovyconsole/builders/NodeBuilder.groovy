package com.citytechinc.cq.groovyconsole.builders

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
    def createNode(name, primaryNodeTypeName) {
        currentNode = currentNode.getOrAddNode(name, primaryNodeTypeName)

        currentNode
    }

    @Override
    def createNode(name, Map properties) {
        currentNode = currentNode.getOrAddNode(name)

        setProperties(currentNode, properties)

        currentNode
    }

    @Override
    def createNode(name, Map properties, primaryNodeTypeName) {
        currentNode = createNode(name, primaryNodeTypeName)

        setProperties(currentNode, properties)

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

    private void setProperties(node, properties) {
        properties.each { k, v ->
            node.set(k, v)
        }
    }
}