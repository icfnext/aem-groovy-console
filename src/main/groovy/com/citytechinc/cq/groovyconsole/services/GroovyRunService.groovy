package com.citytechinc.cq.groovyconsole.services

import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver

/**
 * @author Daniel Madejek
 */
public interface GroovyRunService {

    JsonBuilder runGroovyScript(String scriptContent,SlingHttpServletRequest request);

}