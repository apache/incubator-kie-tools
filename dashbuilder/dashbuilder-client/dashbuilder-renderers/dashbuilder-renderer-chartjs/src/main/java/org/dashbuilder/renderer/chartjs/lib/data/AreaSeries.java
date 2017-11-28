package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;


public class AreaSeries extends JavaScriptObject{
	
	protected AreaSeries() {
	}
	
	public final native String getFillColor() /*-{
		return this.fillColor;
	}-*/;
	
	public final native void setFillColor(String fillColor) /*-{
		this.fillColor = fillColor;
	}-*/;

	public final native String getStrokeColor() /*-{
		return this.strokeColor;
	}-*/;

	public final native void setStrokeColor(String strokeColor) /*-{
		this.strokeColor = strokeColor;
	}-*/;

	public final native String getPointColor() /*-{
		return this.pointColor;
	}-*/;

	public final native void setPointColor(String printColor) /*-{
		this.pointColor = printColor;
	}-*/;

	public final native String getPointStrokeColor() /*-{
		return this.pointStrokeColor;
	}-*/;

	public final native void setPointStrokeColor(String pointStrokeColor) /*-{
		this.pointStrokeColor = pointStrokeColor;
	}-*/;

	public final native String[] getData() /*-{
		return this.data;
	}-*/;

    public final native void setLabel(String label)/*-{
        this.label = label;
    }-*/;

    public final native String getLabel()/*-{
        return this.label;
    }-*/;

	public final void setData(double[] data){
		JsArrayNumber array = JsArrayNumber.createArray().cast();
		for(double str : data)
			array.push(str);
		setData(array);
	}
	
	private final native void setData(JsArrayNumber data) /*-{
		this.data = data;
	}-*/;
}
