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

        def scriptPreview = new StringBuilder()

        if (lines.first().length() > 50) {
            scriptPreview.append(lines.first().take(50))
            scriptPreview.append(" [...]")
        } else {
            scriptPreview.append(lines.first())

            if (lines.size() > 1) {
                scriptPreview.append(" [...]")
            }
        }

        scriptPreview.toString()
    }

    private GroovyScriptUtils() {

    }
}
