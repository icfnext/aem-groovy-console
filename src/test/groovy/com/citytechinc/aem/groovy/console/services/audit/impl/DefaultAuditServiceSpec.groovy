package com.citytechinc.aem.groovy.console.services.audit.impl

import com.citytechinc.aem.groovy.console.services.audit.AuditRecord
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.sling.jcr.api.SlingRepository
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class DefaultAuditServiceSpec extends ProsperSpec {

    @Shared
    DefaultAuditService auditService

    def setupSpec() {
        pageBuilder.etc {
            groovyconsole()
        }

        auditService = new DefaultAuditService()

        auditService.with {
            repository = [loginAdministrative: { this.session }] as SlingRepository
            activate()
        }
    }

    def cleanup() {
        // remove all audit nodes
        auditService.allAuditRecords*.path.each {
            session.getNode(it).remove()
        }

        session.save()
    }

    def "create audit record for script with result and output"() {
        when:
        def auditRecord = auditService.createAuditRecord(script, result, output)

        then:
        assertNodeExists(auditRecord.path)

        and:
        auditRecord.script == script
        auditRecord.result == result
        auditRecord.output == output

        where:
        script | result | output
        ""     | ""     | ""
    }

    def "create audit record for script with exception"() {
        when:
        def exception = new RuntimeException("")
        def auditRecord = auditService.createAuditRecord("", exception)

        then:
        assertNodeExists(auditRecord.path)

        and:
        auditRecord.script == ""
        auditRecord.exceptionStackTrace == ExceptionUtils.getStackTrace(exception)
    }

    def "create multiple audit records"() {
        setup:
        def auditRecords = []

        when:
        (1..5).each {
            auditRecords.add(auditService.createAuditRecord("", "", ""))
        }

        then:
        assertAuditRecordsCreated(auditRecords)
    }

    def "get audit records for valid date range"() {
        setup:
        auditService.createAuditRecord("", "", "")

        def date = new Date()
        def startDate = (date + startDateOffset).toCalendar()
        def endDate = (date + endDateOffset).toCalendar()

        expect:
        auditService.getAuditRecords(startDate, endDate).size() == size

        where:
        startDateOffset | endDateOffset | size
        -2              | -1            | 0
        -1              | 0             | 1
        0               | 0             | 1
        0               | 1             | 1
        -1              | 1             | 1
        1               | 2             | 0
    }

    private void assertAuditRecordsCreated(List<AuditRecord> auditRecords) {
        auditRecords.each {
            assertNodeExists(it.path)
        }
    }
}
