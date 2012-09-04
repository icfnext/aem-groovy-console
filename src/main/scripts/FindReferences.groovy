import com.day.cq.wcm.commons.ReferenceSearch

def referenceSearch = new ReferenceSearch()

referenceSearch.setSearchRoot('content/geometrixx')

def map = referenceSearch.search(resourceResolver, 'Geometrixx')

map.each { k, v ->
    println "page : $k"

    v.properties.each {
        println "reference : ${it}"
    }
}