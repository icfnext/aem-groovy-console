getPage('/content/geometrixx').recurse { page ->
    page.node.recurse { node ->
        if ('geometrixx/components/title' == node['sling:resourceType']) {
            println node.path
        }
    }
}