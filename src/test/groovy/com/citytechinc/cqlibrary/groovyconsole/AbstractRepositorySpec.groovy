package com.citytechinc.cqlibrary.groovyconsole

import groovy.transform.Synchronized

import org.apache.sling.commons.testing.jcr.RepositoryUtil

import spock.lang.Shared
import spock.lang.Specification

/**
 * Abstract Spock specification for JCR-based testing.
 */
abstract class AbstractRepositorySpec extends Specification {

    static final def NODE_TYPES = ['sling', 'replication', 'tagging', 'core', 'dam']

    static def repository

    @Shared session

    def setupSpec() {
        session = getRepository().loginAdministrative(null)
    }

    def cleanupSpec() {
        session.logout()
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