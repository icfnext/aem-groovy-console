def templates = [] as TreeSet

page('/content/geometrixx').recurse { page ->
    if (page.template) {
        templates.add(page.template.path)
    }
}

templates.each {
    println it
}