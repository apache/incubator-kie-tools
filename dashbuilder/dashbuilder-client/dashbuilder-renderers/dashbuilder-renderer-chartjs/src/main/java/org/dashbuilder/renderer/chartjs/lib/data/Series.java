package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;


public class Series extends JavaScriptObject{
	
	protected Series() {
	}
	
	public final native double getValue() /*-{
		return this.value;
	}-*/;
	
	public final native void setValue(double value) /*-{
		this.value = value;
	}-*/;

	public final native String getColor() /*-{
		return this.color;
	}-*/;

	public final native void setColor(String color) /*-{
		this.color = color;
	}-*/;
}
