package org.uberfire.client.editors.charts;

public class ChartPopulateEvent {

    private final String columnName;
    private final double value;

    public ChartPopulateEvent(String columnName, double value) {
        this.columnName = columnName;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public double getValue() {
        return value;
    }
}
