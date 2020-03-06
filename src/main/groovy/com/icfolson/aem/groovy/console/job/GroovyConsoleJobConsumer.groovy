package com.icfolson.aem.groovy.console.job

import com.google.common.base.Charsets
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.impl.JobScriptContext
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
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
        LOG.info("executing groovy console job with properties : {}", job.propertyNames.collectEntries { propertyName ->
            [propertyName, job.getProperty(propertyName)]
        })

        def resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)

        resourceResolver.withCloseable {
            def outputStream = new ByteArrayOutputStream()

            def scriptContext = new JobScriptContext(
                resourceResolver: resourceResolver,
                outputStream: outputStream,
                printStream: new PrintStream(outputStream, true, Charsets.UTF_8.name()),
                script: job.getProperty(GroovyConsoleConstants.PARAMETER_SCRIPT, String),
                data: job.getProperty(GroovyConsoleConstants.PARAMETER_DATA, String)
            )

            groovyConsoleService.runScript(scriptContext)

            // runScriptResponse.exceptionStackTrace ? JobResult.CANCEL : JobResult.OK
            JobResult.OK
        }
    }
}
