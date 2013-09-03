def predicates = [path: "/content", type:"cq:Page", fulltext: "geometrixx", orderby: "@jcr:score,@jcr:created", "orderby.index": "true", "orderby.sort": "desc"]
def query = createQuery(predicates)
query.setHitsPerPage(10)

def result = query.getResult()
println "${result.getTotalMatches()} hits. Execution time: ${result.getExecutionTime()}s \n--"
result.getHits().each { hit ->
	println "page : ${hit.getNode()}" 
}