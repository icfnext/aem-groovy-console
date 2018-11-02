package com.icfolson.aem.groovy.console.api

import groovy.transform.ToString

/**
 * Groovy script binding variable with optional link to documentation.
 */
@ToString(includePackage = false, includeNames = true)
class BindingVariable {

    /** Binding value. */
    Object value

    /** Binding variable type. */
    Class type

    /** Optional link to documentation (Javadoc/Groovydoc). */
    String link

    /**
     * Create a new binding variable with the given value.  Type of variable will be derived from it's class.
     *
     * @param value binding value
     */
    BindingVariable(Object value) {
        this.value = value
        this.type = value.class
    }

    /**
     * Create a new binding variable with the given value and type.
     *
     * @param value binding value
     * @param type binding variable type
     */
    BindingVariable(Object value, Class type) {
        this.value = value
        this.type = type
    }

    /**
     * Create a new binding variable with the given value, type, and documentation link.
     *
     * @param value binding value
     * @param type binding variable type
     * @param link link to documentation URL
     */
    BindingVariable(Object value, Class type, String link) {
        this.value = value
        this.type = type
        this.link = link
    }
}
