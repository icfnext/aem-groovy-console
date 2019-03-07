package com.icfolson.aem.groovy.console.api

import org.codehaus.groovy.control.customizers.CompilationCustomizer

/**
 * Services may implement this interface to customize the compiler configuration for Groovy script execution.
 */
interface CompilationCustomizerExtensionProvider {

    /**
     * Get a list of compilation customizers for Groovy script execution.
     *
     * @return list of compilation customizers
     */
    List<CompilationCustomizer> getCompilationCustomizers()
}