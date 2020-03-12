package com.icfolson.aem.groovy.console.job.consumer

import com.google.common.base.Charsets
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.context.impl.ScheduledJobScriptContext
import groovy.util.logging.Slf4j
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.event.jobs.Job
import org.apache.sling.event.jobs.consumer.JobConsumer
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATA
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.MEDIA_TYPE
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCRIPT

@Component(service = JobConsumer, immediate = true, property = [
    "job.topics=groovyconsole/job"
])
@Slf4j("LOG")
class GroovyConsoleScheduledJobConsumer implements JobConsumer {

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    @Reference
    private GroovyConsoleService groovyConsoleService

    @Override
    JobResult process(Job job) {
        LOG.debug("executing groovy console job with properties : {}", job.propertyNames.collectEntries { propertyName ->
            [propertyName, job.getProperty(propertyName)]
        })

        def resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)

        resourceResolver.withCloseable {
            def outputStream = new ByteArrayOutputStream()

            def scriptContext = new ScheduledJobScriptContext(
                resourceResolver: resourceResolver,
                outputStream: outputStream,
                printStream: new PrintStream(outputStream, true, Charsets.UTF_8.name()),
                script: job.getProperty(SCRIPT, String),
                data: job.getProperty(DATA, String),
                jobId: job.id,
                mediaType: job.getProperty(MEDIA_TYPE)
            )

            groovyConsoleService.runScript(scriptContext)

            JobResult.OK
        }
    }
}
