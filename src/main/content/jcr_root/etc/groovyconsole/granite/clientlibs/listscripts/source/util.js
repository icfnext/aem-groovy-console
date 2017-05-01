"use strict";
// groovyconsole namespace
window._gc = window._gc || {};
_gc.config = {
	"scripts": { 
		"servlet": "/bin/querybuilder.json"
		,"pagesize": -1
		,"params" : {
			"path" : "/etc/groovyconsole/scripts"
			,"nodename" : "*.groovy"
			,"type" : "nt:file"
		}
	}
}
var FLASH_DURATION = 3000;

function buildQuery(path, type, offset, pagesize, filter) {
    var query = {};
    
    query["path"] = path || "/";
    query["type"] = type || "nt:unstructured";
    query["nodename"] = filter || _gc.config.scripts.params.nodename;
    query["p.offset"] =  offset || 0;
    query["p.limit"] = pagesize || -1;
 
    return query;
}


function getScriptsList(filter, offset, callback) {
	var wildcard = (null == filter)? filter : $.trim(('*' + filter + '*').replace(/\*+/g, '*'));
    var query = buildQuery(_gc.config.scripts.params.path, _gc.config.scripts.params.type, offset, _gc.config.scripts.pagesize, wildcard);

    $.getJSON(_gc.config.scripts.servlet, query, callback);
}

function renderList(data) {
	var content = $('#groovyconsole-scripts-list')
		,editor =  $(content).data('editor')
		,target = $('#g-scripts-list');
	Handlebars.registerHelper('editorURL', function(path) {
		return editor + path;
	});
	var tmpl = Handlebars.compile($(content).html());
	$(target)
		.html(tmpl(data))
		.find('a[href]').click(function() {
			GroovyConsole.loadScript($(this).attr('href'), function() { 
				$(document).trigger('pageload'); 
			});
			return false;
		});
	$(target).listview('refresh');
}

function updateList(data) {
	renderList(data);
	$('#g-scripts').addClass('ui-page-active');
}