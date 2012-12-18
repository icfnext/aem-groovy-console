package com.citytechinc.cqlibrary.groovyconsole.builders

import com.citytechinc.cqlibrary.groovyconsole.AbstractRepositorySpec

import spock.lang.Shared

class PageBuilderSpec extends AbstractRepositorySpec {

    def 'build page'() {
        setup:
        pageBuilder.foo()

        expect:
        session.nodeExists('/foo')
        session.nodeExists('/foo/jcr:content')
        session.getNode('/foo').primaryNodeType.name == 'cq:Page'
    }

    def 'build page with content'() {
        setup:
        def pageProperties = ['sling:resourceType': 'foundation/components/page']
        def parProperties = ['sling:resourceType': 'foundation/components/parsys']

        pageBuilder.content {
            citytechinc('CITYTECH, Inc.') {
                'jcr:content'(pageProperties) {
                    mainpar(parProperties)
                }
            }
        }

        expect:
        def pageNode = session.getNode('/content/citytechinc')
        def parNode = session.getNode('/content/citytechinc/jcr:content/mainpar')

        pageHasExpectedProperties(pageNode, 'CITYTECH, Inc.', pageProperties)
        nodeHasExpectedProperties(parNode, parProperties)
    }

    def 'build pages with content'() {
        setup:
        def page1Properties = ['sling:resourceType': 'foundation/components/page']
        def page2Properties = ['sling:resourceType': 'foundation/components/page']

        pageBuilder.content {
            citytechinc('CITYTECH, Inc.') {
                'jcr:content'(page1Properties)
            }
            ctmsp('CTMSP') {
                'jcr:content'(page2Properties)
            }
        }

        expect:
        def page1Node = session.getNode('/content/citytechinc')
        def page2Node = session.getNode('/content/ctmsp')

        pageHasExpectedProperties(page1Node, 'CITYTECH, Inc.', page1Properties)
        pageHasExpectedProperties(page2Node, 'CTMSP', page2Properties)
    }

    void pageHasExpectedProperties(node, title, properties) {
        assert node.primaryNodeType.name == 'cq:Page'
        assert node.hasNode('jcr:content')

        def contentNode = node.getNode('jcr:content')

        assert contentNode.get('jcr:title') == title

        properties.each { k, v ->
            assert contentNode.get(k) == v
        }
    }

    void nodeHasExpectedProperties(node, properties) {
        properties.each { k, v ->
            assert node.get(k) == v
        }
    }
}
