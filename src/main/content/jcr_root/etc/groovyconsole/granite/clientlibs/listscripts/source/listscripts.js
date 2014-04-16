"use strict";
$(function() {
    
    // template may already be loaded
	if ($("#groovyconsole-scripts-list").length <= 0){
	    return;
	}

	$('#script-search').keyup(function(e) {
		getScriptsList($(this).val(), null, updateList);
	});

	getScriptsList(null, null, updateList);
});