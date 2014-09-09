package com.citytechinc.cq.groovyconsole.services

import org.apache.sling.api.SlingHttpServletRequest

interface ExtensionsService {

	String[] getStarImports()

	Map<String, ?> getBindings(SlingHttpServletRequest request)
}