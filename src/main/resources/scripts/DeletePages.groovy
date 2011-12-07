def paths = []

getPage('/content/geometrixx').recurse { page ->
    if (page.template.path == '/apps/name/template') {
        paths.add(page.path)
    }
}

paths.each { path ->
    println 'deleting page = ' + path

    session.getNode(path).remove()
    session.save()
}