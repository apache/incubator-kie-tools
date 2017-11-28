package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Set of data which used for constructing Line/Bar charts
 */
public class Dataset extends JavaScriptObject{

    protected Dataset() {
    }

    public final native void setLabel(String label)/*-{
        this.label = label;
    }-*/;

    public final native String getLabel()/*-{
        return this.label;
    }-*/;

    public final native void setFillColor(String fillColor)/*-{
        this.fillColor = fillColor;
    }-*/;

    public final native String getFillColor()/*-{
        return this.fillColor;
    }-*/;

    public final native void setStrokeColor(String strokeColor)/*-{
        this.strokeColor = strokeColor;
    }-*/;
    public final native String getStrokeColor()/*-{
        return  this.strokeColor;
    }-*/;

    public final native void setPointColor(String pointColor)/*-{
        this.pointColor = pointColor;
    }-*/;
    public final native String getPointColor()/*-{
        return this.pointColor;
    }-*/;

    public final native void setPointStrokeColor(String pointStrokeColor)/*-{
        this.pointStrokeColor = pointStrokeColor;
    }-*/;
    public final native String getPointStrokeColor()/*-{
        return this.pointStrokeColor;
    }-*/;

    public final native void setPointHighlightFill(String pointHighlightFill)/*-{
        this.pointHighlightFill = pointHighlightFill;
    }-*/;
    public final native String getPointHighlightFill()/*-{
        return this.pointHighlightFill;
    }-*/;

    public final native void setPointHighlightStroke(String pointHighlightStroke)/*-{
        this.pointHighlightStroke = pointHighlightStroke;
    }-*/;
    public final native String getPointHighlightStroke()/*-{
        return this.pointHighlightStroke;
    }-*/;

    public final native void setData(double[] data)/*-{
        this.data = data;
    }-*/;
    public final native double[] getData()/*-{
        return this.data;
    }-*/;

}
