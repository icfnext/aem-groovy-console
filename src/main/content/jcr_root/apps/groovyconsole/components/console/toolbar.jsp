<%@include file="/apps/groovyconsole/components/global.jsp" %>

<div class="btn-toolbar" role="toolbar">
    <div class="btn-group">
        <button type="button" class="btn btn-success" id="run-script">
            <span class="glyphicon glyphicon-play"></span> <span id="run-script-text">Run Script</span>
        </button>
    </div>

    <div class="btn-group">
        <button type="button" class="btn btn-default" id="new-script">
            <span class="glyphicon glyphicon-pencil"></span> New</span>
        </button>

        <c:if test="${isAuthor}">
            <button type="button" class="btn btn-default" id="open-script">
                <span class="glyphicon glyphicon-folder-open"></span> Open</span>
            </button>
            <button type="button" class="btn btn-default" id="save-script">
                <span class="glyphicon glyphicon-hdd"></span> Save</span>
            </button>
        </c:if>
    </div>

    <div id="loader">
        <img src="/etc/groovyconsole/clientlibs/img/ajax-loader.gif">
    </div>

    <div id="btn-group-services" class="btn-group navbar-right">
        <input id="services-list" type="text" placeholder="Service or Adapter Name">
    </div>
</div>