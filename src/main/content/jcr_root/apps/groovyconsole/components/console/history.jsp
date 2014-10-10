<%@include file="/libs/foundation/global.jsp" %>

<div class="panel panel-default">
    <div class="panel-heading">
        <h4 class="panel-title">
            <a data-toggle="collapse" data-parent="#info" href="#history">History</a>
        </h4>
    </div>
    <div id="history" class="panel-collapse collapse">
        <div class="panel-body">
            <div class="row panel-row">
                <div class="alert alert-success" role="alert" style="display: none;"></div>
                <div class="alert alert-danger" role="alert" style="display: none;"></div>
            </div>

            <form class="col-xs-3" role="form">
                <label for="date-range">Date Range</label>
                <input type="text" id="date-range" name="date-range" class="form-control">
            </form>

            <div class="delete-all pull-right">
                <button type="button" class="btn btn-warning">
                    <span class="glyphicon glyphicon-trash" title="Delete All"></span> Delete All
                </button>
            </div>
        </div>
        <table class="table table-striped audit">
            <thead>
            <tr>
                <th></th>
                <th>Date</th>
                <th>Script</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>