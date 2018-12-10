package org.dashbuilder.renderer.c3.client.charts.pie;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;

public class C3PieChartView 
       extends C3DisplayerView<C3PieChartDisplayer>
       implements C3PieChartDisplayer.View {

    private boolean showAsDonut;

    @Override
    public String getType() {
        return showAsDonut ? "donut" : "pie";
    }

    @Override
    public void setShowAsDonut(boolean showAsDonut) {
        this.showAsDonut = showAsDonut;
        
    }

}
