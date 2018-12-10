package org.dashbuilder.renderer.c3.client.charts.bubble;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;

public class C3BubbleChartView 
       extends C3DisplayerView<C3BubbleChartDisplayer> 
       implements C3BubbleChartDisplayer.View {

    @Override
    public String getType() {
        return "scatter";
    }

}
