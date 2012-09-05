package org.uberfire.client.editors.charts;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.gchart.client.GChart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.charts.ChartPopulateEvent;
import org.uberfire.shared.charts.ChartRefreshEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Dependent
@WorkbenchScreen(identifier = "chart0")
public class FinishedTasksPerUserChartScreen extends GChart {


    @Inject
    private Event<ChartRefreshEvent> chartRefreshEvent;

    private int position = 1;

    private Map<String, Column> columns = new HashMap<String, Column>();

    public FinishedTasksPerUserChartScreen() {
        setChartSize(300, 200);
        setChartTitle("<b><big><big>" +
                "Finished tasks per user" +
                "</big></big><br>&nbsp;</b>");
        addCurve();
        getCurve().getSymbol().setSymbolType(
                SymbolType.VBAR_SOUTH);
        getCurve().getSymbol().setBackgroundColor("#DDF");
        getCurve().getSymbol().setModelWidth(0.5);
        getCurve().getSymbol().setBorderColor("red");
        getCurve().getSymbol().setBorderWidth(1);

        getXAxis().setTickThickness(0);
        getXAxis().setAxisMin(0);

        getYAxis().setAxisMin(0);
        getYAxis().setAxisMax(100);
        getYAxis().setTickCount(11);
        getYAxis().setHasGridlines(true);
        getXAxis().addTick(0, "");

        Button updateButton = new Button("Refresh");
        updateButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                chartRefreshEvent.fire(new ChartRefreshEvent());
            }
        });
        setChartFootnotes(updateButton);
    }

    private void addColumn(String name, double value) {
        columns.put(name, new Column(name, value));
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Demo Chart";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        update();
        return this;
    }

    public void addNotification(@Observes ChartPopulateEvent event) {
        if (columns.containsKey(event.getColumnName())) {
            columns.get(event.getColumnName()).setValue(event.getValue());
        } else {
            addColumn(event.getColumnName(), event.getValue());
        }

        update();
    }

    class Column {

        private final int myPosition;

        Column(String name, double value) {
            myPosition = position++;
            getCurve().addPoint(myPosition, value);
            getCurve().getPoint().setAnnotationText(name);
            getCurve().getPoint().setAnnotationLocation(AnnotationLocation.SOUTH);

            // Hides X axis ticks that get in our labels way
            getXAxis().addTick(myPosition, "");
            // Looks nicer if there is room after the last column
            getXAxis().setAxisMax(position);
        }

        public void setValue(double value) {
            getCurve(0).getPoint(myPosition - 1).setY(value);
        }
    }
}
