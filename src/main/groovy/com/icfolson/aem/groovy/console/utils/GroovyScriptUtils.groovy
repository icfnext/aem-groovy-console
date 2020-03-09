package com.icfolson.aem.groovy.console.utils

final class GroovyScriptUtils {

    /**
     * Get the script preview for display in data tables.
     *
     * @param script Groovy script
     * @return first line of script
     */
    static final String getScriptPreview(String script) {
        def lines = script.readLines()

        lines.first() + (lines.size() > 1 ? " [...]" : "")
    }

    private GroovyScriptUtils() {

    }
}
