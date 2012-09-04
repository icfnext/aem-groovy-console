def start = getPage('/content/geometrixx')

def querySql2 = createSql2Query(start, 'beer')
def queryXPath = createXPathQuery(start, 'beer')

execute(querySql2)
execute(queryXPath)

def execute(query) {
    println "query = ${query.statement}"

    def result = query.execute()

    def rows = result.rows

    println "found ${rows.size} results"

    rows.each { row ->
        println row.path
    }
}

def createSql2Query(page, term) {
    def qomFactory = session.workspace.queryManager.qomFactory
    def valueFactory = session.valueFactory

    def termValue = valueFactory.createValue(term)

    def descendant = qomFactory.descendantNode('selector', page.path)
    def fullText = qomFactory.fullTextSearch('selector', null, qomFactory.literal(termValue))

    def constraint = qomFactory.and(descendant, fullText)

    def query = qomFactory.createQuery(qomFactory.selector('nt:unstructured', 'selector'), constraint, null, null)

    query
}

def createXPathQuery(page, term) {
    def queryManager = session.workspace.queryManager

    def statement = "/jcr:root${page.path}//element(*, cq:Page)[jcr:contains(., \'$term\')]/(rep:excerpt(.)) order by @jcr:score descending"

    def query = queryManager.createQuery(statement, 'xpath')

    query
}