$(document).ready(function() {
    $("#form").submit(function( event ) {
        event.preventDefault();

        var f = $(this);
        var url = f.attr("action");
        var content = JSON.stringify(form_to_json(f));

        $.ajax({
            url: url,
            type : "POST",
            contentType: 'application/json',
            dataType : 'json',
            data : content,
            success : function(result) {
                var tbl = "<table class=\"table table-striped table-bordered\">";
                tbl += "<tr><th>Rate</th><th>Count</th></tr>";
                $.each(result, function() {
                    var row = "";
                    $.each(this, function(k , v) {
                        row += "<td>" + v + "</td>";
                    });
                    tbl += "<tr>" + row + "</tr>";
                });
                $("#main").empty().append(tbl).append("<p class=\"total\">Total: " + result.length + "</p>");
                console.log(result);
            },
            error: function(xhr, resp, text) {
                $("#error").empty().append(xhr.responseText);
            }
        });
    });
});

function form_to_json (selector) {
    var ary = $(selector).serializeArray();
    var obj = {};
    for (var a = 0; a < ary.length; a++) obj[ary[a].name] = ary[a].value;
    return obj;
}
