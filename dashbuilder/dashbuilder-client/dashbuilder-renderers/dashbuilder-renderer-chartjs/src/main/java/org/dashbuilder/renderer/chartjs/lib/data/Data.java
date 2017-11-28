package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Data storage used for Pie, Radar charts
 */
public class Data extends JavaScriptObject{

    protected Data() {
    }

    public final native void setValue(double value)/*-{
        this.value = value;
    }-*/;
    public final native double setValue()/*-{
        return this.value;
    }-*/;

    public final native void setColor(String color)/*-{
        this.color = color;
    }-*/;
    public final native String setColor()/*-{
        return this.color;
    }-*/;

    public final native void setHighlighColor(String color)/*-{
        this.highlightColor = color;
    }-*/;
    public final native String setHighlighColor()/*-{
        return this.highlightColor;
    }-*/;

    public final native void setLabel(String label)/*-{
        this.label = label;
    }-*/;
    public final native String setLabel()/*-{
        return this.label;
    }-*/;
}
