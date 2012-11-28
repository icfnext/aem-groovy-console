package com.citytechinc.cqlibrary.groovyconsole.builders

import com.citytechinc.cqlibrary.groovyconsole.AbstractRepositorySpec

import spock.lang.Shared

class NodeBuilderSpec extends AbstractRepositorySpec {

    def 'build node'() {
        setup:
        nodeBuilder.foo()

        expect:
        session.nodeExists('/foo')
        session.getNode('/foo').primaryNodeType.name == 'nt:unstructured'
    }

    def 'build node with type'() {
        setup:
        nodeBuilder.foo('sling:Folder')

        expect:
        session.nodeExists('/foo')
        session.getNode('/foo').primaryNodeType.name == 'sling:Folder'
    }

    def 'build node with properties'() {
        setup:
        def properties = ['jcr:title': 'Foo', 'sling:resourceType': 'foo/bar']

        nodeBuilder.foo(properties)

        expect:
        def foo = session.getNode('/foo')

        nodeHasExpectedProperties(foo, properties)
    }

    def 'build node with type and properties'() {
        setup:
        def properties = ['jcr:title': 'Foo']

        nodeBuilder.foo('sling:Folder', properties)

        expect:
        def foo = session.getNode('/foo')

        nodeHasExpectedProperties(foo, properties)
    }

    def 'build node hierarchy'() {
        setup:
        nodeBuilder.foo {
            bar()
        }

        expect:
        session.nodeExists('/foo/bar')
    }

    def 'build node hierarchy with type'() {
        setup:
        nodeBuilder.foo('sling:Folder') {
            bar('sling:Folder')
        }

        expect:
        session.getNode('/foo').primaryNodeType.name == 'sling:Folder'
        session.getNode('/foo/bar').primaryNodeType.name == 'sling:Folder'
    }

    def 'build node hierarchy with type and properties'() {
        setup:
        def fooProperties = ['jcr:title': 'Foo']
        def barProperties = ['jcr:title': 'Bar']

        nodeBuilder.foo('sling:Folder', fooProperties) {
            bar('sling:Folder', barProperties)
        }

        expect:
        def foo = session.getNode('/foo')
        def bar = session.getNode('/foo/bar')

        nodeHasExpectedProperties(foo, fooProperties)
        nodeHasExpectedProperties(bar, barProperties)
    }

    void nodeHasExpectedProperties(node, properties) {
        properties.each { k, v ->
            assert node.get(k) == v
        }
    }
}
