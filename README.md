# CQ Groovy Console

[CITYTECH, Inc.](http://www.citytechinc.com)

[![Stories in Ready](https://badge.waffle.io/citytechinc/cq-groovy-console.png?label=ready)](http://waffle.io/citytechinc/cq-groovy-console)

## Overview

The CQ Groovy Console provides an interface for running [Groovy](http://groovy.codehaus.org/) scripts in the Adobe CQ5
container.  Scripts can be created to manipulate content in the JCR, call OSGi services, or execute arbitrary code using
the CQ, Sling, or JCR APIs.  After installing the package in CQ5 (instructions below), see the
[console page](http://localhost:4502/groovyconsole) for documentation on the available bindings and methods.  Sample
scripts are included in the package for reference.

## Requirements

* CQ (AEM) 5.6 running on localhost:4502
* Versions 3.x.x are compatible with CQ 5.4 and 5.5 (see GitHub tags).

## Installation

[Maven](http://maven.apache.org/) 2.x+ is required to build the project.

1.  Install the console package.

        mvn install -P local

	or

        mvn install -P local,replicate

    The optional `replicate` profile activates the deployed package to the local publish instance.

2.  [Verify](http://localhost:4502/groovyconsole) the installation.

Additional build profiles may be added in the project's pom.xml to support deployment to non-localhost CQ5 servers.

## Context Path Support

If you are running AEM with a context path, set the Maven property `cq.context.path` during installation.

    mvn install -P local -Dcq.context.path=/context

## Notes

Sample scripts can be found in the src/main/scripts directory.

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.

## Issues

Please contact [Mark Daugherty](mailto:mdaugherty@citytechinc.com) with any questions; issues can be submitted via
[GitHub](https://github.com/Citytechinc/cq-groovy-console/issues).