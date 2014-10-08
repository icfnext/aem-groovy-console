package com.citytechinc.aem.groovy.console.tags

import com.citytechinc.aem.groovy.console.services.ConfigurationService
import com.citytechinc.aem.groovy.console.services.audit.AuditService
import com.day.cq.wcm.api.WCMMode
import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.scripting.SlingScriptHelper

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_SCRIPT
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME
import static javax.servlet.jsp.PageContext.REQUEST_SCOPE
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME

class DefineObjectsTag extends TagSupport {

    static final def HREF = "href"

    static final def IS_AUTHOR = "isAuthor"

    static final def INITIALIZED = "initialized"

    static final def AUDIT_RECORD = "auditRecord"

    @Override
    int doEndTag() throws JspException {
        def initialized = pageContext.getAttribute(INITIALIZED, REQUEST_SCOPE)

        if (!initialized) {
            pageContext.setAttribute(INITIALIZED, true, REQUEST_SCOPE)

            def sling = pageContext.getAttribute(DEFAULT_SLING_NAME) as SlingScriptHelper

            pageContext.setAttribute(HREF, sling.getService(ConfigurationService).consoleHref, REQUEST_SCOPE)

            def request = pageContext.getAttribute(DEFAULT_REQUEST_NAME) as SlingHttpServletRequest

            pageContext.setAttribute(IS_AUTHOR, WCMMode.fromRequest(request) != WCMMode.DISABLED, REQUEST_SCOPE)

            def script = request.getParameter(PARAMETER_SCRIPT)

            if (script) {
                def auditRecord = sling.getService(AuditService).getAuditRecord(script)

                if (auditRecord) {
                    pageContext.setAttribute(AUDIT_RECORD, new JsonBuilder(auditRecord).toString(), REQUEST_SCOPE)
                }
            }
        }

        EVAL_PAGE
    }
}
