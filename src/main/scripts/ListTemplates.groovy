def templates = [] as TreeSet

getPage("/content/geometrixx").recurse { page ->
    def template = page?.template?.path

    if (template) {
        templates.add(template)
    }
}

templates.each {
    println it
}