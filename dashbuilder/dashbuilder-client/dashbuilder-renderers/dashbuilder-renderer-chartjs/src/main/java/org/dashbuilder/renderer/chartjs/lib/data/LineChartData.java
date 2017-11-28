package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;

public class LineChartData extends JavaScriptObject{

    protected LineChartData() {
    }

    public final native void setLabels(String[] label)/*-{
        this.labels = labels;
    }-*/;

    public final native void addLabel(String label)/*-{
        labels.push(label);
    }-*/;

    public final native String[] getLabels()/*-{
        return this.labels;
    }-*/;

    public final native void setDataset(Dataset[] dataset)/*-{
        this.datasets = dataset;
    }-*/;

    public final native Dataset[] getDataset()/*-{
        return this.datasets;
    }-*/;

}
