final def page = page('/content/geometrixx')

final def query = buildQuery(page, 'beer')

println query.statement

final long start = System.currentTimeMillis();

final def result = query.execute()

println 'found ' + result.nodes.size() + ' nodes in ' + (System.currentTimeMillis() - start) + 'ms'

result.nodes.each { node ->
    println 'result = ' + node.path
}

def buildQuery(page, term) {
    def queryManager = session.workspace.queryManager

    def statement = '/jcr:root' + page.path + '//element(*, cq:Page)[jcr:contains(., \'' + term + '\')] order by @jcr:score descending'

    queryManager.createQuery(statement, 'xpath')
}