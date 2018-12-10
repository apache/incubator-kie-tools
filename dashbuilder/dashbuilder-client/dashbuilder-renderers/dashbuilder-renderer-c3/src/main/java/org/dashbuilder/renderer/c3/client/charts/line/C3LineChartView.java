package org.dashbuilder.renderer.c3.client.charts.line;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;

public class C3LineChartView 
       extends C3DisplayerView<C3LineChartDisplayer>
       implements C3LineChartDisplayer.View {

    boolean smooth = false;
    
    @Override
    public String getType() {
        return smooth ? "spline" : "line";
    }

    @Override
    public void setSmooth(boolean smooth) {
        this.smooth =  smooth;
    }

}
