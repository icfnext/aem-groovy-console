getPage("/content/geometrixx").recurse { page ->
    def content = page.node

    if (content && !content.get("hideInNav")) {
        println page.path
    }
}