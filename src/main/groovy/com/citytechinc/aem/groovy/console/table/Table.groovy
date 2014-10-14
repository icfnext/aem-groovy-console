package com.citytechinc.aem.groovy.console.table

class Table {

    static class Options {

        private boolean page = false

        private boolean sort = true

        boolean alternatingRowStyle = true

        int pageSize = 10

        int sortColumn = -1

        boolean showRowNumber = true

        String getPage() {
            getEnabled(page)
        }

        String getSort() {
            getEnabled(sort)
        }

        private static String getEnabled(boolean value) {
            value ? "enable" : "disable"
        }
    }

    List<String> columns = []

    List<List<String>> rows = []

    def options = new Options()

    void columns(String... columnNames) {
        columns.addAll(columnNames)
    }

    void columns(List<String> columnNames) {
        columns.addAll(columnNames)
    }

    void row(String... data) {
        rows.add(data as List)
    }

    void row(List<String> data) {
        rows.add(data)
    }

    void rows(List<List<String>> rows) {
        this.rows.addAll(rows)
    }

    void alternatingRowStyle(boolean alternatingRowStyle) {
        this.options.alternatingRowStyle = alternatingRowStyle
    }

    void page(boolean page) {
        this.options.page = page
    }

    void pageSize(int pageSize) {
        this.options.pageSize = pageSize
    }

    void showRowNumber(boolean showRowNumber) {
        this.options.showRowNumber = showRowNumber
    }

    void sort(boolean sort) {
        this.options.sort = sort
    }

    void sortColumn(int sortColumn) {
        this.options.sortColumn = sortColumn
    }
}
