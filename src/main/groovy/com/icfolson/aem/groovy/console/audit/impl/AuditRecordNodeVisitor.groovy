package com.icfolson.aem.groovy.console.audit.impl

import com.icfolson.aem.groovy.console.audit.AuditRecord

import javax.jcr.Node
import javax.jcr.Property
import javax.jcr.RepositoryException
import javax.jcr.util.TraversingItemVisitor

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_RECORD_NODE_PREFIX

class AuditRecordNodeVisitor extends TraversingItemVisitor {

    final List<AuditRecord> auditRecords = []

    AuditRecordNodeVisitor() {
        super(true)
    }

    @Override
    protected void entering(Property property, int level) throws RepositoryException {

    }

    @Override
    protected void entering(Node node, int level) throws RepositoryException {
        if (node.name.startsWith(AUDIT_RECORD_NODE_PREFIX)) {
            auditRecords.add(new AuditRecord(node))
        }
    }

    @Override
    protected void leaving(Property property, int level) throws RepositoryException {

    }

    @Override
    protected void leaving(Node node, int level) throws RepositoryException {

    }
}
