getPage("/content/geometrixx").recurse { page ->
    def content = page.node

    if (content && "/apps/geometrixx/templates/contentpage" == content.get("cq:template")) {
        println page.path
    }
}