package org.uberfire.client.screens.charts;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.charts.ChartRefreshEvent;
import org.uberfire.shared.charts.Column;
import org.uberfire.shared.charts.DataSet;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchScreen(identifier = "Chart")
public class ChartScreen {

    @Inject
    private Event<ChartRefreshEvent> chartRefreshEvent;

    private GenericChart             chart = new GenericChart( "Finished tasks per user" );

    public ChartScreen() {
        init();
    }

    public void init() {
        Timer timer = new Timer() {
            public void run() {
                chartRefreshEvent.fire( new ChartRefreshEvent() );
            }
        };

        timer.scheduleRepeating( 5000 );
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Demo Chart";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return chart;
    }

    public void addDataSet(@Observes DataSet dataSet) {
        for ( Column column : dataSet ) {
            chart.setColumnData( column.getColumnName(),
                                 column.getValue() );
        }
    }
}
