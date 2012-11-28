package com.citytechinc.cqlibrary.groovyconsole.builders

class PageBuilder extends BuilderSupport {

    def session

    def currentNode

    public PageBuilder(session) {
        this.session = session

        currentNode = session.rootNode
    }

    @Override
    def createNode(name) {
        if (isContentNode(name)) {
            currentNode = currentNode.getOrAddNode(name)
        } else {
            currentNode = currentNode.getOrAddNode(name, 'cq:Page')
        }

        currentNode
    }

    @Override
    def createNode(name, value) {
        if (isContentNode(name)) {
            currentNode = currentNode.getOrAddNode(name, value)
        } else {
            currentNode = currentNode.getOrAddNode(name, 'cq:Page')
        }

        currentNode
    }

    @Override
    def createNode(name, Map attributes) {
        if (isContentNode(name)) {
            currentNode = currentNode.getOrAddNode(name)

            setAttributes(currentNode, attributes)
        } else {
            currentNode = currentNode.getOrAddNode(name, 'cq:Page')

            def contentNode = currentNode.getOrAddNode('jcr:content')

            setAttributes(contentNode, attributes)
        }

        currentNode
    }

    @Override
    def createNode(name, Map attributes, value) {
        if (isContentNode(name)) {
            currentNode = currentNode.getOrAddNode(name, value)

            setAttributes(currentNode, attributes)
        } else {
            currentNode = currentNode.getOrAddNode(name, 'cq:Page')

            def contentNode = currentNode.getOrAddNode('jcr:content')

            setAttributes(contentNode, attributes)
        }

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

    private boolean isContentNode(name) {
        name == 'jcr:content' || currentNode.path.contains('jcr:content')
    }

    private void setAttributes(node, attributes) {
        attributes.each { k, v ->
            node.set(k, v)
        }
    }
}