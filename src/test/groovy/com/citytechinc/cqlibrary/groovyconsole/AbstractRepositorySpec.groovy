package com.citytechinc.cqlibrary.groovyconsole

import groovy.transform.Synchronized

import org.apache.sling.commons.testing.jcr.RepositoryUtil

import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractRepositorySpec extends Specification {

    static final def NODE_TYPES = ['sling', 'replication', 'tagging', 'core', 'dam']

    static def repository

    @Shared session

    def setupSpec() {
        session = getRepository().loginAdministrative(null)

        NODE_TYPES.each { type ->
            registerNodeType(type)
        }
    }

    def cleanupSpec() {
        session.logout()
    }

    @Synchronized
    def getRepository() {
        if (!repository) {
            RepositoryUtil.startRepository()

            repository = RepositoryUtil.getRepository()

            addShutdownHook {
                RepositoryUtil.stopRepository()
            }
        }

        repository
    }

    def registerNodeType(type) {
        this.class.getResourceAsStream("/SLING-INF/nodetypes/${type}.cnd").withStream { stream ->
            RepositoryUtil.registerNodeType(session, stream)
        }
    }
}