package org.uberfire.shared.charts;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ChartPopulateEvent {

    private String columnName;
    private double value;

    public ChartPopulateEvent() {
    }

    public ChartPopulateEvent(String columnName, double value) {
        this.columnName = columnName;
        this.value = value;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public double getValue() {
        return value;
    }
}
