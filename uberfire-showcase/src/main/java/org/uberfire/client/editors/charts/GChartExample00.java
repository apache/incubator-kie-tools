package org.uberfire.client.editors.charts;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gchart.client.GChart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "chart0")
public class GChartExample00 extends GChart {

    public GChartExample00() {
        setChartTitle("<b>x<sup>2</sup> vs x</b>");
        setChartSize(150, 150);
        addCurve();
        for (int i = 0; i < 10; i++)
            getCurve().addPoint(i, i * i);
        getCurve().setLegendLabel("x<sup>2</sup>");
        getXAxis().setAxisLabel("x");
        getYAxis().setAxisLabel("x<sup>2</sup>");
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Demo Chart";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        VerticalPanel widgets = new VerticalPanel();
        widgets.add(this);
        widgets.add(new Label("hello salaboy"));
        update();
        return widgets;
    }
}
