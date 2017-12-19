def predicates = [
    "path": "/content/we-retail",
    "type": "cq:Page",
    "fulltext": "Bike",
    "orderby": "@jcr:score,@jcr:created",
    "orderby.index": "true",
    "orderby.sort": "desc"
]

def query = createQuery(predicates)

query.hitsPerPage = 10

def result = query.result

println "${result.totalMatches} hits, execution time = ${result.executionTime}s\n--"

result.hits.each { hit ->
    println "hit path = ${hit.node.path}"
}