package org.uberfire.client.editors.charts;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gchart.client.GChart;

import java.util.HashMap;
import java.util.Map;

public class GenericChart implements IsWidget {

    private int position = 1;

    private final GChart chart = new GChart();

    private Map<String, InternalColumn> columns = new HashMap<String, InternalColumn>();

    public GenericChart(String title) {
        chart.setChartSize(300, 200);
        chart.setChartTitle("<b><big><big>" +
                title +
                "</big></big><br>&nbsp;</b>");
        chart.addCurve();
        chart.getCurve().getSymbol().setSymbolType(
                GChart.SymbolType.VBAR_SOUTH);
        chart.getCurve().getSymbol().setBackgroundColor("#DDF");
        chart.getCurve().getSymbol().setModelWidth(0.5);
        chart.getCurve().getSymbol().setBorderColor("red");
        chart.getCurve().getSymbol().setBorderWidth(1);

        chart.getXAxis().setTickThickness(0);
        chart.getXAxis().setAxisMin(0);

        chart.getYAxis().setAxisMin(0);
        chart.getYAxis().setAxisMax(100);
        chart.getYAxis().setTickCount(11);
        chart.getYAxis().setHasGridlines(true);
        chart.getXAxis().addTick(0, "");
    }

    public void setColumnData(String columnName, double value) {
        if (columns.containsKey(columnName)) {
            columns.get(columnName).setValue(value);
        } else {
            addColumn(columnName, value);
        }
    }

    private void addColumn(String name, double value) {
        columns.put(name, new InternalColumn(name, value));
    }

    @Override
    public Widget asWidget() {
        return chart;
    }

    class InternalColumn {

        private final int myPosition;

        InternalColumn(String name, double value) {
            myPosition = position++;
            chart.getCurve().addPoint(myPosition, value);
            chart.getCurve().getPoint().setAnnotationText(name);
            chart.getCurve().getPoint().setAnnotationLocation(GChart.AnnotationLocation.SOUTH);

            // Hides X axis ticks that get in our labels way
            chart.getXAxis().addTick(myPosition, "");
            // Looks nicer if there is room after the last column
            chart.getXAxis().setAxisMax(position);
        }

        public void setValue(double value) {
            chart.getCurve(0).getPoint(myPosition - 1).setY(value);
        }
    }
}
