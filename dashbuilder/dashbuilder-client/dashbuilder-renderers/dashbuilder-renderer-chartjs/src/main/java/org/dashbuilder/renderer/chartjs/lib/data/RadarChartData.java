package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;

public class RadarChartData extends JavaScriptObject{

    protected RadarChartData() {
    }

    public final native void setData(Data[] data)/*-{
        this.data = data;
    }-*/;

    public final native void getData()/*-{
        return this.data;
    }-*/;
}
