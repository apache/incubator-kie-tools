package org.uberfire.backend.server.impl.charts;


import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.shared.charts.ChartRefreshEvent;
import org.uberfire.shared.charts.Column;
import org.uberfire.shared.charts.DataSet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Service
@ApplicationScoped
public class ChartContentProducer {

    @Inject
    private Event<DataSet> dataSetEvent;

    public void addNotification(@Observes ChartRefreshEvent event) {
        DataSet columns = new DataSet();

        columns.addColumn(createColumn("Porcelli", Math.random() * 100));
        columns.addColumn(createColumn("Toni", Math.random() * 100));
        columns.addColumn(createColumn("Mark", Math.random() * 100));
        columns.addColumn(createColumn("Salaboy", Math.random() * 100));
        columns.addColumn(createColumn("Michael", Math.random() * 100));

        dataSetEvent.fire(columns);
    }

    private Column createColumn(String columnName, double value) {
        Column column = new Column();
        column.setColumnName(columnName);
        column.setValue(value);
        return column;
    }

}
