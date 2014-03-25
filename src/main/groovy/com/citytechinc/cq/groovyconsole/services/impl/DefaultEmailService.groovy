package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.EmailService
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

    static final def SUBJECT = "CQ Groovy Console Script Execution Result"

    static final def TEMPLATE_PATH_SUCCESS = "/email-success.template"

    static final def TEMPLATE_PATH_FAIL = "/email-fail.template"

    static final def FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss"

    @Reference
    ConfigurationService configurationService

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY)
    MailService mailService

    @Override
    void sendEmail(Session session, String script, String output, String runningTime, boolean success) {
        if (configurationService.emailEnabled) {
            def recipients = configurationService.emailRecipients

            if (recipients) {
                if (mailService) {
                    def email = new HtmlEmail()

                    email.charset = CharEncoding.UTF_8

                    recipients.each { name ->
                        email.addTo(name)
                    }

                    email.subject = SUBJECT

                    def binding = [
                        username: session.userID,
                        timestamp: new Date().format(FORMAT_TIMESTAMP),
                        script: script,
                        output: output,
                        runningTime: runningTime
                    ]

                    def templatePath = success ? TEMPLATE_PATH_SUCCESS : TEMPLATE_PATH_FAIL
                    def template = new GStringTemplateEngine().createTemplate(this.class.getResource(templatePath))

                    email.htmlMsg = template.make(binding).toString()

                    LOG.debug "sending email, recipients = {}", recipients

                    Thread.start {
                        mailService.send(email)
                    }
                } else {
                    LOG.warn "email service not available"
                }
            } else {
                LOG.error "email enabled but no recipients configured"
            }
        } else {
            LOG.info "email not enabled"
        }
    }
}
