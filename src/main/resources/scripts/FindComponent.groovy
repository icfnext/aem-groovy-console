import javax.jcr.Node

page('/content/geometrixx').recurse { page ->
    final def content = page.contentResource.adaptTo(Node.class)

    content.recurse { node ->
        if (node.hasProperty('sling:resourceType')) {
            final def resourceType = node.getProperty('sling:resourceType').string

            if ('geometrixx/components/title' == resourceType) {
                println node.path
            }
        }
    }
}