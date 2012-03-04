getPage('/content/geometrixx').recurse { page ->
    if ('/apps/geometrixx/templates/contentpage' == page.template?.path) {
        println(page.path)
    }
}