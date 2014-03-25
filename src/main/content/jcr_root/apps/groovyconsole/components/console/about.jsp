<%@include file="/libs/foundation/global.jsp" %>

<div class="accordion-group">
    <div class="accordion-heading">
        <a class="accordion-toggle" href="#about" data-toggle="collapse" data-target="#about" data-parent="#accordion">About</a>
    </div>
    <div id="about" class="accordion-body collapse in">
        <div class="accordion-inner">
            <ul>
                <li>Inspired by Guillaume Laforge's <a href="http://groovyconsole.appspot.com" target="_blank">Groovy Web Console</a>.</li>
                <li>Implemented with <a href="http://groovy.codehaus.org" target="_blank">Groovy</a> version <%= groovy.lang.GroovySystem.getVersion() %> and <a href="http://getbootstrap.com/" target="_blank">Bootstrap</a>.</li>
                <li>Code editing capabilities provided by <a href="http://ace.c9.io/" target="_blank">Ace</a>.</li>
                <li>Project hosted on <a href="https://github.com/Citytechinc/cq-groovy-console" target="_blank">GitHub</a> for <a href="http://www.citytechinc.com" target="_blank">CITYTECH, Inc.</a></li>
            </ul>
        </div>
    </div>
</div>