import com.day.cq.wcm.api.components.ComponentManager

def componentManager = resourceResolver.adaptTo(ComponentManager)

def validResourceTypes = componentManager.components*.resourceType

getPage("/content/geometrixx").recurse { page ->
    def content = page.node

    content?.recurse { node ->
        def resourceType = node.get("sling:resourceType")

        if (resourceType && !validResourceTypes.contains(resourceType)) {
            println "component at path ${node.path} has invalid resource type = $resourceType"
        }
    }
}