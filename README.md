# CQ5 Groovy Console

[CITYTECH, Inc.](http://www.citytechinc.com)

## Overview

The CQ5 Groovy Console provides an interface for running [Groovy](http://groovy.codehaus.org/) scripts in the CQ5 container.  Scripts can be created to manipulate content in the JCR, call OSGi services, or execute arbitrary code using the CQ, Sling, or JCR APIs.  After installing the package in CQ5 (instructions below), see the [console page](http://localhost:4502/etc/groovyconsole.html) for documentation on the available bindings and methods.  Sample scripts are included in the package for reference.

## Requirements

* CQ 5.4 or 5.5 running on localhost:4502

## Installation

[Maven](http://maven.apache.org/) 2.x+ and [cURL](http://curl.haxx.se/) are required to build the project.

1.  Install the console package (NOTE: if cURL is not installed, the package can be uploaded manually via Package Manager)

    a. If you already have the [Groovy](http://groovy.codehaus.org/Download) bundle installed in the Felix container:

        mvn install -P local-author

    b. If you do not have the Groovy bundle installed:

        mvn install -P install-groovy,local-author

    NOTE: if you are running CQ 5.4, add the profile 'cq5.4' to the above Maven commands to resolve the correct dependencies.

        mvn install -P cq5.4,local-author

2.  [Verify](http://localhost:4502/etc/groovyconsole.html) the installation.

Additional build profiles may be added in the project's pom.xml to support deployment to non-localhost CQ5 servers.

## Notes

Sample scripts can be found in the src/main/scripts directory.

Please contact [Mark Daugherty](mailto:mdaugherty@citytechinc.com) with any questions.

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.

## License

Copyright 2012 CITYTECH, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.