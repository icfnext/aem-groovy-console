package com.citytechinc.cq.groovyconsole

import groovy.transform.Synchronized

import org.apache.sling.commons.testing.jcr.RepositoryUtil

import spock.lang.Shared
import spock.lang.Specification

import com.citytechinc.cq.groovyconsole.builders.NodeBuilder
import com.citytechinc.cq.groovyconsole.builders.PageBuilder
import com.citytechinc.cq.groovyconsole.metaclass.GroovyConsoleMetaClassRegistry

/**
 * Abstract Spock specification for JCR-based testing.
 */
abstract class AbstractRepositorySpec extends Specification {

    static final def NODE_TYPES = ['sling', 'replication', 'tagging', 'core', 'dam', 'vlt']

    static final def SYSTEM_NODE_NAMES = ['jcr:system', 'rep:policy']

    static def repository

    @Shared session

    @Shared nodeBuilder

    @Shared pageBuilder

    def setupSpec() {
        session = getRepository().loginAdministrative(null)

        nodeBuilder = new NodeBuilder(session)
        pageBuilder = new PageBuilder(session)

        GroovyConsoleMetaClassRegistry.registerNodeMetaClass()
    }

    def cleanupSpec() {
        session.logout()
    }

    def cleanup() {
        session.rootNode.nodes.findAll { !SYSTEM_NODE_NAMES.contains(it.name) }*.remove()
        session.save()
    }

    @Synchronized
    def getRepository() {
        if (!repository) {
            RepositoryUtil.startRepository()

            repository = RepositoryUtil.getRepository()

            registerNodeTypes()

            addShutdownHook {
                RepositoryUtil.stopRepository()
            }
        }

        repository
    }

    private def registerNodeTypes() {
        session = getRepository().loginAdministrative(null)

        NODE_TYPES.each { type ->
            this.class.getResourceAsStream("/SLING-INF/nodetypes/${type}.cnd").withStream { stream ->
                RepositoryUtil.registerNodeType(session, stream)
            }
        }

        session.logout()
    }
}