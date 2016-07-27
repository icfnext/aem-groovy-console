package com.icfolson.aem.groovy.console.api

/**
 * Services may implement this interface to supply additional star imports to the compiler configuration for Groovy
 * script execution.
 */
interface StarImportExtensionProvider {

    /**
     * Get the star imports to add to the script compiler.  All imports provided by extension services will be merged
     * prior to script execution.
     *
     * @return set of star imports
     */
    Set<String> getStarImports()
}