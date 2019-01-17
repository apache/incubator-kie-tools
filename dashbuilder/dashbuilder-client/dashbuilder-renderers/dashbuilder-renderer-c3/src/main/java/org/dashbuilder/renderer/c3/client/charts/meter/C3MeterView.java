package org.dashbuilder.renderer.c3.client.charts.meter;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;

public class C3MeterView 
      extends C3DisplayerView<C3MeterChartDisplayer> 
      implements C3MeterChartDisplayer.View {

    private static final String RED = "#FF0000";
    private static final String ORANGE = "#F97600";
    private static final String GREEN = "#60B044";
    String[] colors = {
            GREEN,
            ORANGE,
            RED
    };
    
    @Override
    public String getType() {
        return "gauge";
    }

    @Override
    public String[] getColorPattern() {
        return colors;
    }

}
