def templates = [] as TreeSet

getPage('/content/geometrixx').recurse { page ->
    if (page.template) {
        templates.add(page.template.path)
    }
}

templates.each {
    println it
}