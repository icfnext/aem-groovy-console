import groovy.transform.Field

@Field packagesPath = "/etc/packages"
@Field packageName = "geometrixx"
@Field definitionPath = "$packagesPath/${packageName}.zip/jcr:content/vlt:definition"

def definitionNode = getOrAddDefinitionNode()
def filterNode = getOrAddFilterNode(definitionNode)

["/content/geometrixx", "/content/dam/geometrixx", "/etc/designs/geometrixx"].eachWithIndex { path, i ->
    def f = filterNode.addNode("filter$i")

    f.set("mode", "replace")
    f.set("root", path)
    f.set("rules", new String[0])
}

save()

def getOrAddDefinitionNode() {
    def definitionNode

    if (session.nodeExists(definitionPath)) {
        definitionNode = getNode(definitionPath)
    } else {
        def fileNode = getNode(packagesPath).addNode("${packageName}.zip", "nt:file")

        def contentNode = fileNode.addNode("jcr:content", "nt:resource")

        contentNode.addMixin("vlt:Package")
        contentNode.set("jcr:mimeType", "application/zip")

        def stream = new ByteArrayInputStream("".bytes)
        def binary = session.valueFactory.createBinary(stream)

        contentNode.set("jcr:data", binary)

        definitionNode = contentNode.addNode("vlt:definition", "vlt:PackageDefinition")

        definitionNode.set("sling:resourceType", "cq/packaging/components/pack/definition")
        definitionNode.set("name", packageName)
        definitionNode.set("path", "$packagesPath/$packageName")
    }

    definitionNode
}

def getOrAddFilterNode(definitionNode) {
    def filterNode

    if (definitionNode.hasNode("filter")) {
        filterNode = definitionNode.getNode("filter")

        filterNode.nodes.each {
            it.remove()
        }
    } else {
        filterNode = definitionNode.addNode("filter")

        filterNode.set("sling:resourceType", "cq/packaging/components/pack/definition/filterlist")
    }

    filterNode
}