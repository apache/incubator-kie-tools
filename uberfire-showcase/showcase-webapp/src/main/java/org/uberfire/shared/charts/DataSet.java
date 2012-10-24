package org.uberfire.shared.charts;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Portable
public class DataSet
        implements Iterable<Column> {

    private Set<Column> columns = new HashSet<Column>();

    public void addColumn(Column column) {
        columns.add(column);
    }

    public void setColumns(Set<Column> columns) {
        this.columns = columns;
    }

    public Set<Column> getColumns() {
        return columns;
    }

    @Override
    public Iterator<Column> iterator() {
        return columns.iterator();
    }
}
