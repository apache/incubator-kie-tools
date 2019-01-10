package org.dashbuilder.renderer.c3.client.charts.area;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class C3AreaChartView 
       extends C3DisplayerView<C3AreaChartDisplayer> 
       implements C3AreaChartDisplayer.View {

    @Override
    public void updateChart(C3ChartConf conf) {
        super.updateChart(conf);
        fixAreaOpacity();
    }

    @Override
    public String getType() {
        return "area";
    }
    
    @Override
    public void fixAreaOpacity() {
        // This is a workaround for: https://github.com/c3js/c3/issues/2551
        if (chart != null) {
            NodeList<Element> paths = chart.getElement().getElementsByTagName("path");
            int n = paths.getLength();
            for (int i = 0; i < n; i++) {
                Element child = paths.getItem(i);
                String className = child.getAttribute("class");
                if (className != null && className.contains("c3-area-")) {
                    child.getStyle().setOpacity(0.2);
                }
            }
        }
    }

}