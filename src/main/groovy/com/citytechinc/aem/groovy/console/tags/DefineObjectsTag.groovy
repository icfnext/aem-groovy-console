package com.citytechinc.aem.groovy.console.tags

import com.citytechinc.aem.groovy.console.services.ConfigurationService
import com.citytechinc.aem.groovy.console.services.audit.AuditService
import com.day.cq.wcm.api.WCMMode
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.scripting.SlingScriptHelper

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME
import static javax.servlet.jsp.PageContext.REQUEST_SCOPE
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME

class DefineObjectsTag extends TagSupport {

    static final def HREF = "href"

    static final def IS_AUTHOR = "isAuthor"

    static final def INITIALIZED = "initialized"

    static final def SCRIPT = "script"

    static final def RESULT = "result"

    static final def OUTPUT = "output"

    static final def STACK_TRACE = "stackTrace"

    static final def RUNNING_TIME = "runningTime"

    @Override
    int doEndTag() throws JspException {
        def initialized = pageContext.getAttribute(INITIALIZED, REQUEST_SCOPE)

        if (!initialized) {
            pageContext.setAttribute(INITIALIZED, true, REQUEST_SCOPE)

            def sling = pageContext.getAttribute(DEFAULT_SLING_NAME) as SlingScriptHelper

            def href

            if (sling.getService(ConfigurationService).vanityPathEnabled) {
                href = "/groovyconsole"
            } else {
                href = "/etc/groovyconsole.html"
            }

            pageContext.setAttribute(HREF, href, REQUEST_SCOPE)

            def request = pageContext.getAttribute(DEFAULT_REQUEST_NAME) as SlingHttpServletRequest

            pageContext.setAttribute(IS_AUTHOR, WCMMode.fromRequest(request) != WCMMode.DISABLED, REQUEST_SCOPE)

            def suffix = request.requestPathInfo.suffix
            def auditRecord = null

            if (suffix) {
                def auditService = sling.getService(AuditService)

                auditRecord = auditService.getAuditRecord(suffix.substring(1))
            }

            pageContext.setAttribute(SCRIPT, auditRecord?.script ?: "", REQUEST_SCOPE)
            pageContext.setAttribute(RESULT, auditRecord?.result ?: "", REQUEST_SCOPE)
            pageContext.setAttribute(OUTPUT, auditRecord?.output ?: "", REQUEST_SCOPE)
            pageContext.setAttribute(STACK_TRACE, auditRecord?.exceptionStackTrace ?: "", REQUEST_SCOPE)
            pageContext.setAttribute(RUNNING_TIME, auditRecord?.runningTime ?: "", REQUEST_SCOPE)
        }

        EVAL_PAGE
    }
}
