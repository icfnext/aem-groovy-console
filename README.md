# CQ5 Groovy Console

[CITYTECH, Inc.](http://www.citytechinc.com)

## Requirements

* CQ 5.5 running on localhost:4502
* Maven 2.x+
* [cURL](http://curl.haxx.se/) for automated deployment (optional)

## Installation

1.  Install the console package (NOTE: if cURL is not installed, the package can be uploaded manually via Package Manager)

    a. If you already have the Groovy bundle installed in the Felix container:

        mvn install -P local-author

    b. If you do not have the Groovy bundle installed:

        mvn install -P install-groovy,local-author

2.  [Test the installation](http://localhost:4502/etc/groovyconsole.html)

Additional build profiles may be added in the project's pom.xml to support deployment to non-localhost CQ5 servers.

Sample code:

    getPage('/content/geometrixx').recurse { page ->
        println "${page.title} - ${page.path}"
    }

Additional sample scripts can be found in src/main/scripts.

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