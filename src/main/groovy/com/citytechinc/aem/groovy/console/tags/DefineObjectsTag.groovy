package com.citytechinc.aem.groovy.console.tags

import com.citytechinc.aem.groovy.console.services.ConfigurationService
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

    @Override
    int doEndTag() throws JspException {
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

        EVAL_PAGE
    }
}
