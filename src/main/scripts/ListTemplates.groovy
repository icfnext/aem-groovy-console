def templates = [] as TreeSet

getPage("/content/we-retail").recurse { page ->
    def template = page?.template?.path

    if (template) {
        templates.add(template)
    }
}

templates.each {
    println it
}