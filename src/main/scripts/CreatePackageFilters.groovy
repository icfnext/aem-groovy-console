def paths = ["/content/geometrixx", "/content/dam/geometrixx", "/etc/designs/geometrixx"]

def packagePath = "/etc/packages/my_packages/geometrixx.zip"
def definitionPath = "$packagePath/jcr:content/vlt:definition"

def definition = getNode(definitionPath)

def filter

if (definition.hasNode("filter")) {
    filter = definition.getNode("filter")

    filter.nodes.each {
        it.remove()
    }
} else {
    filter = definition.addNode("filter")

    filter.set("sling:resourceType", "cq/packaging/components/pack/definition/filterlist")
}

paths.eachWithIndex { path, i ->
    def f = filter.addNode("filter$i")

    f.set("mode", "replace")
    f.set("root", path)
    f.set("rules", new String[0])
}

save()