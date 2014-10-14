import com.day.cq.wcm.commons.ReferenceSearch

def referenceSearch = new ReferenceSearch()

referenceSearch.setSearchRoot("content/geometrixx")

def map = referenceSearch.search(resourceResolver, "Geometrixx")
def data = []

map.each { path, info ->
    info.properties.each { ref ->
        data.add([path, ref])
    }
}

table {
    columns("Page", "Reference")
    rows(data)
}