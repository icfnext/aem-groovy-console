package com.citytechinc.aem.groovy.console.table

class Table {

    List<String> columns = []

    List<List<String>> rows = []

    void columns(String... columnNames) {
        assert !columns, "columns are already defined"

        columns.addAll(columnNames)
    }

    void columns(List<String> columnNames) {
        assert !columns, "columns are already defined"

        columns.addAll(columnNames)
    }

    void row(String... row) {
        assert columns, "columns must be defined before adding a row"
        assert row.length == columns.size(), "row data size does not match number of columns"

        rows.add(row as List)
    }

    void row(List<String> row) {
        assert columns, "columns must be defined before adding a row"
        assert row.size() == columns.size(), "row data size does not match number of columns"

        rows.add(row)
    }

    void rows(List<List<String>> rows) {
        assert columns, "columns must be defined before adding rows"

        rows.each { row ->
            assert row.size() == columns.size(), "one or more row data sizes does not match number of columns"
        }

        this.rows.addAll(rows)
    }
}
