def start = getPage('/content/geometrixx')

def query = createXPathQuery(start, 'beer')

println "query = ${query.statement}"

def result = query.execute()

def rows = result.rows

println "found ${rows.size} result(s)"

rows.each { row ->
    println row.path
}

def createXPathQuery(page, term) {
    def queryManager = session.workspace.queryManager

    def statement = "/jcr:root${page.path}//element(*, cq:Page)[jcr:contains(., \'$term\')]/(rep:excerpt(.)) order by @jcr:score descending"

    def query = queryManager.createQuery(statement, 'xpath')

    query
}