## Overview

The CQ Groovy Console provides an interface for running [Groovy](http://groovy.codehaus.org/) scripts in the Adobe CQ5
container.  Scripts can be created to manipulate content in the JCR, call OSGi services, or execute arbitrary code using
the CQ, Sling, or JCR APIs.  After installing the package in CQ5 (instructions below), see the
[console page](http://localhost:4502/etc/groovyconsole.html) for documentation on the available bindings and methods.
Sample scripts are included in the package for reference.

## Requirements

* CQ (AEM) 5.6 running on localhost:4502
* Versions 3.x.x are compatible with CQ 5.4 and 5.5 (see GitHub tags).

## Installation

[Maven](http://maven.apache.org/) 2.x+ is required to build the project.

1.  Install the console package.

        mvn install -P local-author

2.  [Verify](http://localhost:4502/groovyconsole) the installation.

Additional build profiles may be added in the project's pom.xml to support deployment to non-localhost CQ5 servers.

## Notes

Sample scripts can be found in the src/main/scripts directory.

Please contact [Mark Daugherty](mailto:mdaugherty@citytechinc.com) with any questions.