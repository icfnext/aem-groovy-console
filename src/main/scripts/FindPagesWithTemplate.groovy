getPage("/content/we-retail").recurse { page ->
    def content = page.node

    if (content && "/conf/we-retail/settings/wcm/templates/section-page" == content.get("cq:template")) {
        println page.path
    }
}