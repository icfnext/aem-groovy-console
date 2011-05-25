$(document).ready(function() {
    $(function() {
        $("#embedText").dialog({
            bgiframe: true,
            autoOpen: false,
            width: 700,
            height: 450,
            modal: true
        });
    });

    $("#embedLink").click(function(event) {
        $("#embedText").dialog('open');
    });

    $("#toggleLineNumbers").click(function(event) {
        if ($(".syntaxhighlighter").hasClass("nogutter")) {
            $(".syntaxhighlighter").removeClass("nogutter");
        } else {
            $(".syntaxhighlighter").addClass("nogutter");
        }
    });
});