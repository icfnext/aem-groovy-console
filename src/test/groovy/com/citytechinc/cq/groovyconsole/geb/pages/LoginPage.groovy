package com.citytechinc.cq.groovyconsole.geb.pages

import geb.Page

class LoginPage extends Page {

    static url = "libs/granite/core/content/login.html"

    static content = {
        username { $("#username") }
        password { $("#password") }
        signIn { $("#login button") }
    }
}
