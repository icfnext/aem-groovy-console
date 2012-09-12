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
            registerNodeType("/SLING-INF/nodetypes/${type}.cnd")
        }
    }

    def cleanupSpec() {
        session.logout()
    }

    @Synchronized
    def getRepository() {
        if (!repository) {
            println 'starting repo'

            RepositoryUtil.startRepository()

            repository = RepositoryUtil.getRepository()

            addShutdownHook {
                RepositoryUtil.stopRepository()
            }
        }

        println 'got repo'

        repository
    }

    def registerNodeType(cndPath) {
        RepositoryUtil.registerNodeType(session, this.class.getResourceAsStream(cndPath))
    }
}