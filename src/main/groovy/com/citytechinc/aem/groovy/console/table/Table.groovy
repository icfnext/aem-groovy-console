package com.citytechinc.aem.groovy.console.table

class Table {

    def columns = []

    def rows = []

    void columns(String... columnNames) {
        columns.addAll(columnNames)
    }

    void columns(List<String> columnNames) {
        columns.addAll(columnNames)
    }

    void row(data) {
        rows.add(data)
    }

    void rows(rows) {
        rows.addAll(rows)
    }
}
