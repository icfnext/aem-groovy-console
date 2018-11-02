package com.icfolson.aem.groovy.console.api

import groovy.transform.Sortable
import groovy.transform.ToString

/**
 * Star import with optional link to documentation.
 */
@ToString(includePackage = false, includeNames = true)
@Sortable(includes = "packageName")
class StarImport {

    /** Star import package name. */
    String packageName

    /** Optional link to documentation (Javadoc/Groovydoc). */
    String link

    /**
     * Create a new star import for the given package name.
     *
     * @param packageName package name
     */
    StarImport(String packageName) {
        this.packageName = packageName
    }

    /**
     * Create a new star import for the given package name and documentation link.
     *
     * @param packageName package name
     * @param link link to documentation URL
     */
    StarImport(String packageName, String link) {
        this.packageName = packageName
        this.link = link
    }
}
