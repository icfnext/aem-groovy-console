<%@include file="/libs/foundation/global.jsp" %>

<div class="btn-toolbar">
    <div class="btn-group">
        <a class="btn btn-success" href="#" id="run-script">
            <i class="icon-play icon-white"></i> <span id="run-script-text">Run Script</span>
        </a>
    </div>

    <div class="btn-group">
        <a class="btn" href="#" id="new-script"><i class="icon-pencil"></i> New</a>
        <a class="btn" href="#" id="open-script"><i class="icon-folder-open"></i> Open</a>
        <a class="btn" href="#" id="save-script"><i class="icon-hdd"></i> Save</a>
    </div>

    <div id="loader" class="pull-right" style="display: none;">
        <img src="/apps/groovyconsole/docroot/img/ajax-loader.gif">
    </div>
</div>