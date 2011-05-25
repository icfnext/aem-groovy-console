# CQ5 Groovy Console

[CITYTECH, Inc.](http://www.citytechinc.com)

Requirements:

* CQ 5.4 running on localhost:4502
* Maven 2.x+
* [cURL](http://curl.haxx.se/) for automated deployment (optional)

See below for installation instructions on a local CQ5 author instance (must be running).

Additional build profiles may be created in the project's pom.xml to support deployment to non-local CQ5 servers.

1.  Install the Groovy jar:

    mvn install -P install-groovy,local-author

2.  Install the console package (NOTE: if cURL is not installed, the package can be uploaded manually via Package Manager):

    mvn install -P install-console,local-author

3.  [Test the installation](http://localhost:4502/etc/groovyconsole.html)

Sample code:

    page('/content/geometrixx').recurse { page ->
        println page.title + ' - ' + page.path
    }

Additional sample scripts can be found in src/main/resources/scripts.

Please contact [Mark Daugherty](mailto:mdaugherty@citytechinc.com) with any questions.