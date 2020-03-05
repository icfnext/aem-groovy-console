package com.icfolson.aem.groovy.console.job

import com.google.common.base.Charsets
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.impl.JobScriptContext
import groovy.util.logging.Slf4j
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.event.jobs.Job
import org.apache.sling.event.jobs.consumer.JobConsumer
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

@Component(service = JobConsumer, immediate = true, property = [
    "job.topics=groovyconsole/job"
])
@Slf4j("LOG")
class GroovyConsoleJobConsumer implements JobConsumer {

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    @Reference
    private GroovyConsoleService groovyConsoleService

    @Override
    JobResult process(Job job) {
        def resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)

        resourceResolver.withCloseable {
            def outputStream = new ByteArrayOutputStream()

            def scriptContext = new JobScriptContext(
                resourceResolver: resourceResolver,
                outputStream: outputStream,
                printStream: new PrintStream(outputStream, true, Charsets.UTF_8.name()),
                scriptContent: job.getProperty("scriptContent", String),
                data: job.getProperty("data", String)
            )

            def runScriptResponse = groovyConsoleService.runScript(scriptContext)

            runScriptResponse.exceptionStackTrace ? JobResult.CANCEL : JobResult.OK
        }
    }
}
