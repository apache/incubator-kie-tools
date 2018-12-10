package org.dashbuilder.renderer.c3.client.charts.area;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;

public class C3AreaChartView 
       extends C3DisplayerView<C3AreaChartDisplayer> 
       implements C3AreaChartDisplayer.View{

    @Override
    public String getType() {
        return "area";
    }

}
