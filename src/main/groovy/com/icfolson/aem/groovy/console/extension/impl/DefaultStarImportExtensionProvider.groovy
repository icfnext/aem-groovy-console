package com.icfolson.aem.groovy.console.extension.impl

import com.google.common.collect.ImmutableSet
import com.icfolson.aem.groovy.console.api.StarImport
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import org.osgi.service.component.annotations.Component

@Component(service = StarImportExtensionProvider, immediate = true)
class DefaultStarImportExtensionProvider implements StarImportExtensionProvider {

    private static final String AEM_JAVADOC_PREFIX = "https://helpx.adobe" +
        ".com/experience-manager/6-5/sites/developing/using/reference-materials/javadoc"

    private static final String JCR_JAVADOC_PREFIX = "https://docs.adobe.com/docs/en/spec/javax.jcr/javadocs/jcr-2.0"

    private static final String SLING_JAVADOC_PREFIX = "http://sling.apache.org/apidocs/sling9"

    private static final String JAVADOC_SUFFIX = "package-summary.html"

    private static final Set<StarImport> IMPORTS = ImmutableSet.of(
        new StarImport("com.day.cq.dam.api", "$AEM_JAVADOC_PREFIX/com/day/cq/dam/api/$JAVADOC_SUFFIX"),
        new StarImport("com.day.cq.search", "$AEM_JAVADOC_PREFIX/com/day/cq/search/$JAVADOC_SUFFIX"),
        new StarImport("com.day.cq.tagging", "$AEM_JAVADOC_PREFIX/com/day/cq/tagging/$JAVADOC_SUFFIX"),
        new StarImport("com.day.cq.wcm.api", "$AEM_JAVADOC_PREFIX/com/day/cq/wcm/api/$JAVADOC_SUFFIX"),
        new StarImport("com.day.cq.replication", "$AEM_JAVADOC_PREFIX/com/day/cq/replication/$JAVADOC_SUFFIX"),
        new StarImport("javax.jcr", "$JCR_JAVADOC_PREFIX/javax/jcr/$JAVADOC_SUFFIX"),
        new StarImport("org.apache.sling.api", "$SLING_JAVADOC_PREFIX/org/apache/sling/api/$JAVADOC_SUFFIX"),
        new StarImport("org.apache.sling.api.resource",
            "$SLING_JAVADOC_PREFIX/org/apache/sling/api/resource/$JAVADOC_SUFFIX")
    )

    @Override
    Set<StarImport> getStarImports() {
        IMPORTS
    }
}