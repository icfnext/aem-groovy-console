package com.citytechinc.cqlibrary.groovyconsole

import com.citytechinc.cqlibrary.groovyconsole.metaclass.GroovyConsoleMetaClassRegistry

import javax.jcr.Node

import org.apache.sling.commons.testing.jcr.RepositoryUtil

import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractRepositorySpec extends Specification {

    static final def NODE_TYPES = ['sling', 'replication', 'tagging', 'core', 'dam']

    static class ShutdownThread extends Thread {

        @Override
        void run() {
            RepositoryUtil.stopRepository();
        }
    };

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

    def getRepository() {
        synchronized (AbstractRepositorySpec) {
            if (!repository) {
                RepositoryUtil.startRepository()

                repository = RepositoryUtil.getRepository()

                Runtime.getRuntime().addShutdownHook(new ShutdownThread())
            }
        }

        repository
    }

    def registerNodeType(cndPath) {
        RepositoryUtil.registerNodeType(session, AbstractRepositorySpec.class.getResourceAsStream(cndPath))
    }
}