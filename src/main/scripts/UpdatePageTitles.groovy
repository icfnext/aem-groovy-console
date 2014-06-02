import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.NameConstants

getPage('/content/geometrixx').recurse { page ->
    def content = page.node

    if (content) {
        def title = content.get(JcrConstants.JCR_TITLE)

        content.set(NameConstants.PN_PAGE_TITLE, "$title | Geometrixx Inc.")
    }
}

save()