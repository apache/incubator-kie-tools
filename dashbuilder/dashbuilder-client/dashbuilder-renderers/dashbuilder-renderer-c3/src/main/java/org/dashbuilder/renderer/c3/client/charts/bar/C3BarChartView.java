package org.dashbuilder.renderer.c3.client.charts.bar;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;

public class C3BarChartView 
      extends C3DisplayerView<C3BarChartDisplayer> 
      implements C3BarChartDisplayer.View {

    @Override
    public String getType() {
        return "bar";
    }

}
