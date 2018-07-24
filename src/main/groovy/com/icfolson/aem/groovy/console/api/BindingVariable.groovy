package com.icfolson.aem.groovy.console.api

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class BindingVariable {

    Object value

    Class type

    String link

    BindingVariable(Object value) {
        this.value = value
        this.type = value.class
    }

    BindingVariable(Object value, Class type) {
        this.value = value
        this.type = type
    }

    BindingVariable(Object value, Class type, String link) {
        this.value = value
        this.type = type
        this.link = link
    }
}
