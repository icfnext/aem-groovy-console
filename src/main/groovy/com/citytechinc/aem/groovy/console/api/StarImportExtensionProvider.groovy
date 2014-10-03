package com.citytechinc.aem.groovy.console.api

interface StarImportExtensionProvider {

    /**
     * Get the star imports to add to the script compiler.  All imports provided by extension services will be merged
     * prior to script execution.
     *
     * @return set of star imports
     */
    Set<String> getStarImports()
}