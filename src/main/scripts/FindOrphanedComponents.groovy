import com.day.cq.wcm.api.components.ComponentManager

def componentManager = resourceResolver.adaptTo(ComponentManager)

def validResourceTypes = componentManager.components*.resourceType

def data = []

getPage("/content/we-retail").recurse { page ->
    def content = page.node

    content?.recurse { node ->
        def resourceType = node.get("sling:resourceType")

        if (resourceType && !validResourceTypes.contains(resourceType)) {
            data.add([node.path, resourceType])
        }
    }
}

table {
    columns("Component Path", "Invalid Resource Type")
    rows(data)
}