getPage("/content/we-retail").recurse { page ->
    def content = page.node

    if (content && !content.get("hideInNav")) {
        println page.path
    }
}