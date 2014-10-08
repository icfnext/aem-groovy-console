<%@include file="/libs/foundation/global.jsp" %>

<div class="panel panel-default">
    <div class="panel-heading">
        <h4 class="panel-title">
            <a data-toggle="collapse" data-parent="#info" href="#history">History</a>
        </h4>
    </div>
    <div id="history" class="panel-collapse collapse">
        <div class="panel-body">
            <form class="col-xs-3" role="form">
                <label for="date-range">Date Range</label>
                <input type="text" id="date-range" name="date-range" class="form-control">
            </form>
        </div>
        <table class="table table-striped audit">
            <thead>
            <tr>
                <th></th>
                <th>Date</th>
                <th>Script</th>
                <th>Success</th>
                <th>Permalink</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>