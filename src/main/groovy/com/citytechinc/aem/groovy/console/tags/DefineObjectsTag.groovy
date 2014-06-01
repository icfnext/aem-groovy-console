package com.citytechinc.aem.groovy.console.tags

import com.citytechinc.aem.groovy.console.services.ConfigurationService
import com.day.cq.wcm.api.WCMMode
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.scripting.SlingScriptHelper

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME

class DefineObjectsTag extends TagSupport {

    static final def GROOVY_CONSOLE_HREF = "groovyConsoleHref"

    static final def IS_AUTHOR = "isAuthor"

    @Override
    int doEndTag() throws JspException {
        def sling = pageContext.getAttribute(DEFAULT_SLING_NAME) as SlingScriptHelper

        def configurationService = sling.getService(ConfigurationService)

        pageContext.setAttribute(GROOVY_CONSOLE_HREF, configurationService.vanityPathEnabled ? "/groovyconsole" : "/etc/groovyconsole.html")

        def request = pageContext.getAttribute(DEFAULT_REQUEST_NAME) as SlingHttpServletRequest

        pageContext.setAttribute(IS_AUTHOR, WCMMode.fromRequest(request) != WCMMode.DISABLED)

        EVAL_PAGE
    }
}
