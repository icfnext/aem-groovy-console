package com.citytechinc.cq.groovyconsole.builders

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
            currentNode = getOrAddPage(name: name)
        }

        currentNode
    }

    @Override
    def createNode(name, value) {
        if (isContentNode(name)) {
            currentNode = currentNode.getOrAddNode(name, value)
        } else {
            currentNode = getOrAddPage(name: name, title: value)
        }

        currentNode
    }

    @Override
    def createNode(name, Map attributes) {
        if (isContentNode(name)) {
            currentNode = currentNode.getOrAddNode(name)

            setAttributes(currentNode, attributes)
        } else {
            currentNode = getOrAddPage(name: name, attributes: attributes)
        }

        currentNode
    }

    @Override
    def createNode(name, Map attributes, value) {
        if (isContentNode(name)) {
            currentNode = currentNode.getOrAddNode(name, value)

            setAttributes(currentNode, attributes)
        } else {
            currentNode = getOrAddPage(name: name, title: value, attributes: attributes)
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

    private def getOrAddPage(map) {
        def pageNode = currentNode.getOrAddNode(map.name, 'cq:Page')
        def contentNode = pageNode.getOrAddNode('jcr:content')

        if (map.title) {
            contentNode.set('jcr:title', map.title)
        }

        if (map.attributes) {
            setAttributes(contentNode, map.attributes)
        }

        pageNode
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