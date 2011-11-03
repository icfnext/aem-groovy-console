# CQ5 Groovy Console

[CITYTECH, Inc.](http://www.citytechinc.com)

Requirements:

* CQ 5.4 running on localhost:4502
* Maven 2.x+
* [cURL](http://curl.haxx.se/) for automated deployment (optional)

Installation:

1.  Install the console package (NOTE: if cURL is not installed, the package can be uploaded manually via Package Manager)

    a. If you already have the Groovy bundle installed in Felix:

        `mvn install -P install-console,local-author`

    b. If you do not have the Groovy bundle installed:

        `mvn install -P install-groovy,install-console,local-author`

2.  [Test the installation](http://localhost:4502/etc/groovyconsole.html)

Additional build profiles may be created in the project's pom.xml to support deployment to non-local CQ5 servers.

Sample code:

    page('/content/geometrixx').recurse { page ->
        println page.title + ' - ' + page.path
    }

Additional sample scripts can be found in src/main/resources/scripts.

Please contact [Mark Daugherty](mailto:mdaugherty@citytechinc.com) with any questions.