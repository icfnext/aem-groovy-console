package com.citytechinc.aem.groovy.console.notification.impl

import com.citytechinc.aem.groovy.console.configuration.ConfigurationService
import com.citytechinc.aem.groovy.console.notification.NotificationService
import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import com.day.cq.mailer.MailService
import groovy.text.GStringTemplateEngine
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.CharEncoding
import org.apache.commons.mail.HtmlEmail
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.Service

import javax.jcr.Session

@Service(NotificationService)
@Component
@Slf4j("LOG")
class EmailNotificationService implements NotificationService {

    static final def SUBJECT = "Groovy Console Script Execution Result"

    static final def TEMPLATE_PATH_SUCCESS = "/email-success.template"

    static final def TEMPLATE_PATH_FAIL = "/email-fail.template"

    static final def FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss"

    @Reference
    ConfigurationService configurationService

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY)
    MailService mailService

    @Override
    void notify(Session session, RunScriptResponse response) {
        if (configurationService.emailEnabled && mailService) {
            def recipients = configurationService.emailRecipients

            if (recipients) {
                def binding = createBinding(session, response)
                def templatePath = response.exceptionStackTrace ? TEMPLATE_PATH_FAIL : TEMPLATE_PATH_SUCCESS

                def email = createEmail(recipients, binding, templatePath)

                LOG.debug("sending email, recipients = {}", recipients)

                Thread.start {
                    mailService.send(email)
                }
            } else {
                LOG.error("email enabled but no recipients configured")
            }
        } else {
            LOG.debug("email disabled or mail service unavailable")
        }
    }

    private def createEmail(Set<String> recipients, Map binding, String templatePath) {
        def email = new HtmlEmail()

        recipients.each { name ->
            email.addTo(name)
        }

        def template = new GStringTemplateEngine().createTemplate(this.class.getResource(templatePath))

        email.with {
            charset = CharEncoding.UTF_8
            subject = SUBJECT
            htmlMsg = template.make(binding).toString()
        }

        email
    }

    private static def createBinding(Session session, RunScriptResponse response) {
        def binding = [
            username: session.userID,
            timestamp: new Date().format(FORMAT_TIMESTAMP),
            script: response.script
        ]

        if (response.exceptionStackTrace) {
            binding.stackTrace = response.exceptionStackTrace
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
