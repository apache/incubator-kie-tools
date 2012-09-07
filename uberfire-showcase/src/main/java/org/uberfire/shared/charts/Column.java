package org.uberfire.shared.charts;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Column {
    private String columnName;
    private double value;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
