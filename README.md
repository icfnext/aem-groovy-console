# CQ5 Groovy Console

[CITYTECH, Inc.](http://www.citytechinc.com)

See below for installation instructions on a local CQ5 author instance (must be running).

Additional build profiles may be created in the project's pom.xml to support deployment to non-local CQ5 servers.

Install the Groovy jar:

```bash
mvn install -P install-groovy,local-author
```

Install the console package:

```bash
mvn install -P install-console,local-author
```

[Test](http://localhost:4502/etc/groovyconsole.html)

Sample code:

    page('/content/geometrixx').recurse { page ->
        println page.title + ' - ' + page.path
    }

Please contact [Mark Daugherty](mailto:mdaugherty@citytechinc.com) with any questions.