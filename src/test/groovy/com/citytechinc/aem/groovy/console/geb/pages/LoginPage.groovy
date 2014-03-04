package com.citytechinc.aem.groovy.console.geb.pages

import geb.Page

class LoginPage extends Page {

    static url = "libs/granite/core/content/login.html"

    static content = {
        username { $("#username") }
        password { $("#password") }
        signIn { $("#login button") }
    }
}
