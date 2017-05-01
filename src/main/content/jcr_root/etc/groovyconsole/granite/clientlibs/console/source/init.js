$(document).unbind('mobileinit');
if (_g) {
	$(document).bind('mobileinit', function() {
		_g.$.mobile.pushStateEnabled = false;
	        _g.$.mobile.page.prototype.options.backBtnText		= "Back";
	        _g.$.mobile.page.prototype.options.addBackBtn		= false;
	        _g.$.mobile.page.prototype.options.backBtnTheme	= null;
	        _g.$.mobile.page.prototype.options.headerTheme		= "d";
	        _g.$.mobile.page.prototype.options.footerTheme		= "a";
	        _g.$.mobile.page.prototype.options.contentTheme	= "d";
	        _g.$.mobile.page.prototype.options.theme	                = "d";
	});
}

$('#g-editor').on('DOMSubtreeModified','.live', function() { 
	$('#g-editor').trigger('pageshow.contentHeight'); 
});
$('.accordion-heading a[href]').on('click', function() { 
	setTimeout(function() {
		$('#g-editor').trigger('pageshow.contentHeight'); 
	}, 500); 
});