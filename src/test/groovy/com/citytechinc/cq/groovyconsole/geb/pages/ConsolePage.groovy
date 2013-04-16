package com.citytechinc.cq.groovyconsole.geb.pages

import geb.Page

class ConsolePage extends Page {

    static url = "/groovyconsole"

    static at = { title == "Groovy Console" }

    static content = {
        title { $("title").text() }
        runButton { $("#run-script") }
        newButton { $("#new-script") }
        openButton { $("#open-script") }
        saveButton { $("#save-script") }
        editor { $("#editor") }
        script { editor.text() }
        successMessage { $('#message-success .message') }
        errorMessage { $('#message-error .message') }
        result { $("#result") }
        output { $("#output") }
        runningTime { $("#running-time") }
        stacktrace { $("#stacktrace") }
    }
}
