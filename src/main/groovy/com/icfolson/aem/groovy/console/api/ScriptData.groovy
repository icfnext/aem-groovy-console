package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.resource.ResourceResolver

/**
 * Script data for saving scripts.
 */
interface ScriptData {

    /**
     * Resource resolver for saving scripts.
     *
     * @return resource resolver
     */
    ResourceResolver getResourceResolver()

    /**
     * File name to be saved.
     *
     * @return file name
     */
    String getFileName()

    /**
     * Script content to be saved.
     *
     * @return script content
     */
    String getScript()
}