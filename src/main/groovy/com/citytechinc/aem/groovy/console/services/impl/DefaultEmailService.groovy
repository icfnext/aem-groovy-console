package com.citytechinc.aem.groovy.console.services.impl

import com.citytechinc.aem.groovy.console.services.ConfigurationService
import com.citytechinc.aem.groovy.console.services.EmailService
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

@Service(EmailService)
@Component
@Slf4j("LOG")
class DefaultEmailService implements EmailService {

    static final def SUBJECT = "Groovy Console Script Execution Result"

    static final def TEMPLATE_PATH_SUCCESS = "/email-success.template"

    static final def TEMPLATE_PATH_FAIL = "/email-fail.template"

    static final def FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss"

    @Reference
    ConfigurationService configurationService

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY)
    MailService mailService

    @Override
    void sendEmail(Session session, String script, String result, String output, String runningTime) {
        sendEmailInternal(createBinding(session, script, result, output, runningTime), TEMPLATE_PATH_SUCCESS)
    }

    @Override
    void sendEmail(Session session, String script, String stackTrace) {
        sendEmailInternal(createBinding(session, script, stackTrace), TEMPLATE_PATH_FAIL)
    }

    private def sendEmailInternal(Map binding, String templatePath) {
        if (configurationService.emailEnabled && mailService) {
            def recipients = configurationService.emailRecipients

            if (recipients) {
                def email = createEmail(recipients, binding, templatePath)

                LOG.debug "sending email, recipients = {}", recipients

                Thread.start {
                    mailService.send(email)
                }
            } else {
                LOG.error "email enabled but no recipients configured"
            }
        } else {
            LOG.info "email disabled or mail service unavailable"
        }
    }

    private def createEmail(Set<String> recipients, Map binding, String templatePath) {
        def email = new HtmlEmail()

        email.charset = CharEncoding.UTF_8

        recipients.each { name ->
            email.addTo(name)
        }

        email.subject = SUBJECT

        def template = new GStringTemplateEngine().createTemplate(this.class.getResource(templatePath))

        email.htmlMsg = template.make(binding).toString()

        email
    }

    private static def createBinding(Session session, String script, String output, String result, String runningTime) {
        def binding = createBinding(session, script)

        binding.putAll([
            result     : result,
            output     : output,
            runningTime: runningTime
        ])

        binding
    }

    private static def createBinding(Session session, String script, String stackTrace) {
        def binding = createBinding(session, script)

        binding.stackTrace = stackTrace

        binding
    }

    private static def createBinding(Session session, String script) {
        [username : session.userID,
         timestamp: new Date().format(FORMAT_TIMESTAMP),
         script   : script]
    }
}
