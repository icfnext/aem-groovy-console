getPage('/content/geometrixx').recurse { page ->
    page.node.recurse { node ->
        if ('geometrixx/components/title' == node.get('sling:resourceType')) {
            println node.path
        }
    }
}