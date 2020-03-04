package com.icfolson.aem.groovy.console.audit.impl

import com.icfolson.aem.groovy.console.api.impl.RequestScriptContext
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.configuration.impl.DefaultConfigurationService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.sling.jcr.resource.JcrResourceConstants
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class DefaultAuditServiceSpec extends ProsperSpec {

    @Shared
    DefaultAuditService auditService = new DefaultAuditService()

    def setupSpec() {
        pageBuilder.var {
            groovyconsole(JcrResourceConstants.NT_SLING_FOLDER)
        }

        slingContext.registerInjectActivateService(new DefaultConfigurationService())
        slingContext.registerInjectActivateService(auditService)
    }

    def cleanup() {
        // remove all audit nodes
        auditService.getAllAuditRecords(resourceResolver.userID)*.path.each {
            session.getNode(it).remove()
        }

        session.save()
    }

    def "create audit record for script with result and output"() {
        when:
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def scriptContext = new RequestScriptContext(request, response, new ByteArrayOutputStream(), null, script, "data")

        def runScriptResponse = RunScriptResponse.fromResult(scriptContext, result, output, runningTime)

        def auditRecord = auditService.createAuditRecord(runScriptResponse)

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
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def scriptContext = new RequestScriptContext(request, response, new ByteArrayOutputStream(), null, "script content", null)

        def exception = new RuntimeException("")

        def runScriptResponse = RunScriptResponse.fromException(scriptContext, "output", exception)

        def auditRecord = auditService.createAuditRecord(runScriptResponse)

        then:
        assertNodeExists(auditRecord.path)

        and:
        auditRecord.script == "script content"
        auditRecord.output == "output"
        auditRecord.exceptionStackTrace == ExceptionUtils.getStackTrace(exception)
    }

    def "create multiple audit records"() {
        setup:
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def scriptContext = new RequestScriptContext(request, response, new ByteArrayOutputStream(), null, "script content", "data")

        def runScriptResponse = RunScriptResponse.fromResult(scriptContext, "result", "output", "running time")

        def auditRecords = []

        when:
        (1..5).each {
            auditRecords.add(auditService.createAuditRecord(runScriptResponse))
        }

        then:
        assertAuditRecordsCreated(auditRecords)
    }

    def "get audit records for valid date range"() {
        setup:
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def scriptContext = new RequestScriptContext(request, response, new ByteArrayOutputStream(), null, "script content", "data")

        def runScriptResponse = RunScriptResponse.fromResult(scriptContext, "result", "output", "running time")

        auditService.createAuditRecord(runScriptResponse)

        def startDate = getDate(startDateOffset)
        def endDate = getDate(endDateOffset)

        expect:
        auditService.getAuditRecords(resourceResolver.userID, startDate, endDate).size() == size

        where:
        startDateOffset | endDateOffset | size
        -2              | -1            | 0
        -1              | 0             | 1
        0               | 0             | 1
        0               | 1             | 1
        -1              | 1             | 1
        1               | 2             | 0
    }

    private Calendar getDate(Integer offset) {
        def date = (new Date() + offset).toCalendar()

        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MILLISECOND, 0)

        date
    }

    private void assertAuditRecordsCreated(List<AuditRecord> auditRecords) {
        auditRecords.each {
            assertNodeExists(it.path)
        }
    }
}
