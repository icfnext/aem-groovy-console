package com.citytechinc.cqlibrary.groovyconsole

import com.citytechinc.cqlibrary.groovyconsole.builder.JcrBuilder
import com.citytechinc.cqlibrary.groovyconsole.metaclass.GroovyConsoleMetaClassRegistry

import javax.jcr.Node

import org.apache.sling.commons.testing.jcr.RepositoryUtil

import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractRepositorySpec extends Specification {

    static class ShutdownThread extends Thread {

        @Override
        void run() {
            RepositoryUtil.stopRepository();
        }
    };

    static def repository

    @Shared session

    @Shared jcrBuilder

    def setupSpec() {
        session = getRepository().loginAdministrative(null)

        jcrBuilder = new JcrBuilder(session)

        registerNodeType("/SLING-INF/nodetypes/sling.cnd")
        registerNodeType("/SLING-INF/nodetypes/replication.cnd")
        registerNodeType("/SLING-INF/nodetypes/tagging.cnd")
        registerNodeType("/SLING-INF/nodetypes/core.cnd")
        registerNodeType("/SLING-INF/nodetypes/dam.cnd")
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