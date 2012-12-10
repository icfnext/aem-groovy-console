package com.citytechinc.groovyconsole.builders

import com.citytechinc.groovyconsole.AbstractRepositorySpec

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
        nodeHasExpectedProperties('/foo', properties)
    }

	def 'build node with non-string properties'() {
		setup:
		def properties = ['date': Calendar.instance, 'number': 1L, 'array': ['one', 'two', 'three'].toArray(new String[0])]

		nodeBuilder.foo(properties)

		expect:
		nodeHasExpectedProperties('/foo', properties)
	}

    def 'build node with type and properties'() {
        setup:
        def properties = ['jcr:title': 'Foo']

        nodeBuilder.foo('sling:Folder', properties)

        expect:
		nodeHasExpectedTypeAndProperties('/foo', 'sling:Folder', properties)
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
		nodeHasExpectedTypeAndProperties('/foo', 'sling:Folder', [:])
		nodeHasExpectedTypeAndProperties('/foo/bar', 'sling:Folder', [:])
    }

    def 'build node hierarchy with type and properties'() {
        setup:
        def fooProperties = ['jcr:title': 'Foo']
        def barProperties = ['jcr:title': 'Bar']

        nodeBuilder.foo('sling:Folder', fooProperties) {
            bar('sling:Folder', barProperties)
        }

        expect:
        nodeHasExpectedTypeAndProperties('/foo', 'sling:Folder', fooProperties)
        nodeHasExpectedTypeAndProperties('/foo/bar', 'sling:Folder', barProperties)
    }

	void nodeHasExpectedTypeAndProperties(path, type, properties) {
		def node = session.getNode(path)

		assert node.primaryNodeType.name == type

		properties.each { k, v ->
			assert node.get(k) == v
		}
	}

    void nodeHasExpectedProperties(path, properties) {
		def node = session.getNode(path)

        properties.each { k, v ->
            assert node.get(k) == v
        }
    }
}
