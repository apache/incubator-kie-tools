package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class AreaChartData extends JavaScriptObject{

	protected AreaChartData(){
	}
	
	public final void setLabels(String[] labels){
		JsArrayString array = JsArrayString.createArray().cast();
		for(String str : labels)
			array.push(str);
		setLabels(array);
	}
	
	private final native void setLabels(JsArrayString labels) /*-{
		this.labels = labels;
	}-*/;

	public final native JsArray<AreaSeries> getSeries() /*-{
		return this.datasets;
	}-*/;

	public final native void setSeries(JsArray<AreaSeries> series) /*-{
		this.datasets = series;
	}-*/;

}
