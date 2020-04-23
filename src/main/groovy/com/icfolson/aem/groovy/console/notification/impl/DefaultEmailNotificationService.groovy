package com.icfolson.aem.groovy.console.notification.impl

import com.day.cq.mailer.MailService
import com.google.common.base.Charsets
import com.google.common.net.MediaType
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.notification.EmailNotificationService
import com.icfolson.aem.groovy.console.notification.NotificationService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import groovy.text.GStringTemplateEngine
import groovy.util.logging.Slf4j
import org.apache.commons.mail.Email
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.mail.MultiPartEmail
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality

import javax.mail.util.ByteArrayDataSource

@Component(service = [EmailNotificationService, NotificationService], immediate = true)
@Slf4j("LOG")
class DefaultEmailNotificationService implements EmailNotificationService {

    static final String SUBJECT = "Groovy Console Script Execution Result"

    static final String TEMPLATE_PATH_SUCCESS = "/email-success.template"

    static final String TEMPLATE_PATH_FAIL = "/email-fail.template"

    static final String FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss"

    @Reference
    private ConfigurationService configurationService

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private MailService mailService

    @Override
    void notify(RunScriptResponse response) {
        notify(response, configurationService.emailRecipients, false)
    }

    @Override
    void notify(RunScriptResponse response, Set<String> recipients, boolean attachOutput) {
        notify(response, recipients, null, null, attachOutput)
    }

    @Override
    void notify(RunScriptResponse response, Set<String> recipients, String successTemplate,
        String failureTemplate, boolean attachOutput) {
        if (configurationService.emailEnabled && mailService) {
            if (recipients) {
                def email = createEmail(response, recipients, successTemplate, failureTemplate, attachOutput)

                LOG.debug("sending email, recipients : {}", recipients)

                mailService.send(email)
            } else {
                LOG.error("email enabled but no recipients configured")
            }
        } else {
            LOG.debug("email disabled or mail service unavailable")
        }
    }

    private Email createEmail(RunScriptResponse response, Set<String> recipients, String successTemplate,
        String failureTemplate, boolean attachOutput) {
        def email = attachOutput ? new MultiPartEmail() : new HtmlEmail()

        recipients.each { name ->
            email.addTo(name)
        }

        email.subject = SUBJECT
        email.charset = Charsets.UTF_8.name()

        def message = getMessage(response, successTemplate, failureTemplate)

        if (attachOutput) {
            (email as MultiPartEmail).addPart(message, MediaType.HTML_UTF_8.toString())

            def dataSource = new ByteArrayDataSource(response.output.getBytes(Charsets.UTF_8.name()),
                MediaType.parse(response.mediaType).toString())

            // attach output file
            (email as MultiPartEmail).attach(dataSource, response.outputFileName, null)
        } else {
            (email as HtmlEmail).htmlMsg = message
        }

        email
    }

    private String getMessage(RunScriptResponse response, String successTemplate, String failureTemplate) {
        def template

        if (response.exceptionStackTrace) {
            template = failureTemplate ?: this.class.getResource(TEMPLATE_PATH_FAIL).text
        } else {
            template = successTemplate ?: this.class.getResource(TEMPLATE_PATH_SUCCESS).text
        }

        new GStringTemplateEngine()
            .createTemplate(template)
            .make(createBinding(response))
            .toString()
    }

    private Map<String, String> createBinding(RunScriptResponse response) {
        def binding = [
            username: response.userId,
            timestamp: new Date().format(FORMAT_TIMESTAMP),
            script: response.script
        ]

        if (response.exceptionStackTrace) {
            binding.putAll([
                stackTrace: response.exceptionStackTrace,
                output: response.output
            ])
        } else {
            binding.putAll([
                result: response.result,
                output: response.output,
                runningTime: response.runningTime
            ])
        }

        binding
    }
}
