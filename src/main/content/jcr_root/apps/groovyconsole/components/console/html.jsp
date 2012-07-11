<%@include file="/libs/foundation/global.jsp" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Groovy Console</title>

        <cq:defineObjects />
        <cq:includeClientLib categories="cq.wcm.edit" />

        <!--
        <script src="/apps/groovyconsole/docroot/js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-resize-plugin.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery.ui.selectmenu.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/ace-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/mode-groovy-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/themes-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/theme-textmate-uncompressed.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/console-main.js" type="text/javascript"></script>

        <script type="text/javascript">
            $(function() {
                initialize('${resource.path}.html');
            });
        </script>
         -->

        <script src="/apps/groovyconsole/docroot/js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/bootstrap.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/jquery-ui-resize-plugin.js" type="text/javascript"></script>
        <script src="/apps/groovyconsole/docroot/js/ace-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/mode-groovy-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/themes-noconflict.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/theme-textmate-uncompressed.js" type="text/javascript" charset="utf-8"></script>
        <script src="/apps/groovyconsole/docroot/js/console.js" type="text/javascript"></script>

        <script type="text/javascript">
            $(function() {
                initialize('${resource.path}.html');
            });
        </script>

        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/bootstrap.min.css" type="text/css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/bootstrap-responsive.min.css" type="text/css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" type="text/css" />

        <!--
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/smoothness/jquery-ui-1.8.18.custom.css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/jquery.ui.selectmenu.css" type="text/css" />
        <link rel="stylesheet" href="/apps/groovyconsole/docroot/css/main.css" type="text/css" type="text/css" />
         -->
    </head>
    <body style="padding-top: 40px;">
        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <a class="brand" href="${currentPage.path}.html">Groovy Console</a>

					<!--
                    <div class="btn-group pull-right">
                        <button id="dropdown-themes" class="btn dropdown-toggle" data-toggle="dropdown">Themes <span class="caret"></span></button>
                        <ul class="dropdown-menu">
                            <li><a href="#">Action</a></li>
                            <li><a href="#">Another action</a></li>
                            <li><a href="#">Something else here</a></li>
                        </ul>
                    </div>
                    -->
                </div>
            </div>
        </div>

        <div class="container">
            <div class="btn-toolbar">
            	<div class="btn-group">
                    <a class="btn btn-success" href="#" id="run-script"><i class="icon-play icon-white"></i> Run Script</a>
                </div>

                <div class="btn-group">
                    <a class="btn" href="#" id="new-script"><i class="icon-pencil"></i> New</a>
                    <a class="btn" href="#" id="open-script"><i class="icon-folder-open"></i> Open</a>
                    <a class="btn" href="#" id="save-script"><i class="icon-hdd"></i> Save</a>
                </div>
            </div>

            <div class="alert alert-error" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">x</a>
				<span class="message"></span>
            </div>

            <pre id="editor" class="pre-scrollable"></pre>

			<div class="accordion" id="accordion">
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#result">Result</a>
					</div>
					<div id="result" class="accordion-body collapse">
						<div class="accordion-inner"></div>
					</div>
				</div>
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#output">Output</a>
					</div>
					<div id="output" class="accordion-body collapse">
						<div class="accordion-inner"></div>
					</div>
				</div>
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#stacktrace">Stack Trace</a>
					</div>
					<div id="stacktrace" class="accordion-body collapse">
						<div class="accordion-inner"></div>
					</div>
				</div>
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#help">Help</a>
					</div>
					<div id="help" class="accordion-body collapse">
						<div class="accordion-inner">
							<h3>Bindings</h3>

				            <ul>
				                <li>session - <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html">javax.jcr.Session</a></li>
				                <li>pageManager - <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/PageManager.html">com.day.cq.wcm.api.PageManager</a></li>
				                <li>resourceResolver - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/resource/ResourceResolver.html">org.apache.sling.api.resource.ResourceResolver</a></li>
				                <li>slingRequest - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/SlingHttpServletRequest.html">org.apache.sling.api.SlingHttpServletRequest</a></li>
				                <li>sling - <a href="http://sling.apache.org/apidocs/sling5/org/apache/sling/api/scripting/SlingScriptHelper.html">org.apache.sling.api.scripting.SlingScriptHelper</a></li>
				                <li>log - <a href="http://www.slf4j.org/api/org/slf4j/Logger.html">org.slf4j.Logger</a></li>
				            </ul>

					        <h3>Methods</h3>

				            <ul>
				                <li>getPage(path) - get the <a href="http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/Page.html">Page</a> for the given path, or null if it does not exist.</li>
				                <li>getNode(path) - get the <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html">Node</a> for the given path.  Throws <a href="http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/RepositoryException.html">javax.jcr.RepositoryException</a> if it does not exist.</li>
				            </ul>
						</div>
					</div>
				</div>
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#about">About</a>
					</div>
					<div id="about" class="accordion-body collapse in">
						<div class="accordion-inner">
					        <ul>
					            <li>Inspired by and heavily sourced from Guillaume Laforge's <a href="http://groovyconsole.appspot.com">Groovy web console</a></li>
					            <li>Programmed with <a href="http://groovy.codehaus.org">Groovy</a>, version 2.0.0</li>
					            <li>Code editing capabilities provided by <a href="http://ace.ajax.org/">Ace</a></li>
					            <li>Project hosted on <a href="https://github.com/Citytechinc/cq5-groovy-console">GitHub</a> for <a href="http://www.citytechinc.com">CITYTECH, Inc.</a></li>
					        </ul>
						</div>
					</div>
				</div>
			</div>
		</div>






        <!--
        <div class="header row">
            <h1><span class="title">Groovy Console</span></h1>

            <div id="toolbar">
                <button id="new-script" >New Script</button>
                <button id="open-script">Open Script</button>
                <button id="save-script">Save Script</button>

                <label for="editor-theme" id="theme-label">Editor Theme:</label>
                <select id="editor-theme" name="editor-theme">
                    <option value="ace/theme/chrome">Chrome</option>
                    <option value="ace/theme/clouds">Clouds</option>
                    <option value="ace/theme/clouds_midnight">Clouds Midnight</option>
                    <option value="ace/theme/cobalt">Cobalt</option>
                    <option value="ace/theme/crimson_editor">Crimson Editor</option>
                    <option value="ace/theme/dawn">Dawn</option>
                    <option value="ace/theme/dreamweaver">Dreamweaver</option>
                    <option value="ace/theme/eclipse">Eclipse</option>
                    <option value="ace/theme/idle_fingers">idleFingers</option>
                    <option value="ace/theme/kr_theme">krTheme</option>
                    <option value="ace/theme/merbivore">Merbivore</option>
                    <option value="ace/theme/merbivore_soft">Merbivore Soft</option>
                    <option value="ace/theme/mono_industrial">Mono Industrial</option>
                    <option value="ace/theme/monokai">Monokai</option>
                    <option value="ace/theme/pastel_on_dark">Pastel on dark</option>
                    <option value="ace/theme/solarized_dark">Solarized Dark</option>
                    <option value="ace/theme/solarized_light">Solarized Light</option>
                    <option value="ace/theme/textmate">TextMate</option>
                    <option value="ace/theme/twilight">Twilight</option>
                    <option value="ace/theme/tomorrow">Tomorrow</option>
                    <option value="ace/theme/tomorrow_night" selected="selected">Tomorrow Night</option>
                    <option value="ace/theme/tomorrow_night_blue">Tomorrow Night Blue</option>
                    <option value="ace/theme/tomorrow_night_bright">Tomorrow Night Bright</option>
                    <option value="ace/theme/tomorrow_night_eighties">Tomorrow Night 80s</option>
                    <option value="ace/theme/vibrant_ink">Vibrant Ink</option>
                </select>

                <div id="loadingDiv"><img src="/apps/groovyconsole/docroot/img/ajax-loader-1.gif"></div>

                <button id="run-script" style="float:right;">Run Script</button>
            </div>
        </div>

        <pre id="editor" class="row"></pre>

        <div id="tabs" class="row tab">
            <ul>
                <li><a href="#tabs-result">Result</a></li>
                <li><a href="#tabs-output">Output</a></li>
                <li><a href="#tabs-stacktrace">Stacktrace</a></li>
                <li><a href="#tabs-about">About</a></li>
            </ul>

            <div id="tabs-result" class="tab">
                <pre id="result-time" class="hidden"></pre>
                <pre id="result" class="border hidden"></pre>
            </div>

            <div id="tabs-output" class="tab">
                <pre id="output" class="border hidden"></pre>
            </div>

            <div id="tabs-stacktrace" class="tab">
                <pre id="stacktrace" class="border hidden"></pre>
            </div>

            <div id="tabs-about" class="tab">
                <cq:include script="about.jsp" />

                <div style="clear:both;"></div>
            </div>
        </div>
         -->
    </body>
</html>