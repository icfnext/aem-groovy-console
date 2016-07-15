package com.citytechinc.aem.groovy.console.audit.impl

import com.citytechinc.aem.groovy.console.audit.AuditRecord
import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.apache.commons.lang3.exception.ExceptionUtils
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class DefaultAuditServiceSpec extends ProsperSpec {

    @Shared
    DefaultAuditService auditService = new DefaultAuditService()

    def setupSpec() {
        pageBuilder.etc {
            groovyconsole()
        }

        slingContext.registerInjectActivateService(auditService)
    }

    def cleanup() {
        // remove all audit nodes
        auditService.getAllAuditRecords(session)*.path.each {
            session.getNode(it).remove()
        }

        session.save()
    }

    def "create audit record for script with result and output"() {
        when:
        def response = RunScriptResponse.fromResult(script, result, output, runningTime)
        def auditRecord = auditService.createAuditRecord(session, response)

        then:
        assertNodeExists(auditRecord.path)

        and:
        auditRecord.script == script
        auditRecord.result == result
        auditRecord.output == output

        where:
        script           | result   | output   | runningTime
        "script content" | "result" | "output" | "running time"
    }

    def "create audit record for script with exception"() {
        when:
        def exception = new RuntimeException("")
        def response = RunScriptResponse.fromException("script content", exception)
        def auditRecord = auditService.createAuditRecord(session, response)

        then:
        assertNodeExists(auditRecord.path)

        and:
        auditRecord.script == "script content"
        auditRecord.exceptionStackTrace == ExceptionUtils.getStackTrace(exception)
    }

    def "create multiple audit records"() {
        setup:
        def response = RunScriptResponse.fromResult("script content", "result", "output", "running time")
        def auditRecords = []

        when:
        (1..5).each {
            auditRecords.add(auditService.createAuditRecord(session, response))
        }

        then:
        assertAuditRecordsCreated(auditRecords)
    }

    def "get audit records for valid date range"() {
        setup:
        def response = RunScriptResponse.fromResult("script content", "result", "output", "running time")

        auditService.createAuditRecord(session, response)

        def date = new Date()
        def startDate = (date + startDateOffset).toCalendar()
        def endDate = (date + endDateOffset).toCalendar()

        expect:
        auditService.getAuditRecords(session, startDate, endDate).size() == size

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
