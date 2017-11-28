package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.core.client.JavaScriptObject;

public class SeriesBuilder {
	
	private AreaSeries series = null;
	
	private SeriesBuilder(){
		series = JavaScriptObject.createObject().cast();
	}
	
	public static SeriesBuilder create(){
		return new SeriesBuilder();
	}

    public SeriesBuilder withLabel(String label){
        series.setLabel(label);
        return this;
    }

	public SeriesBuilder withFillColor(String fillColor){
		series.setFillColor(fillColor);
		return this;
	}
	
	public SeriesBuilder withStoreColor(String strokeColor){
		series.setStrokeColor(strokeColor);
		return this;
	}
	
	public SeriesBuilder withPointColor(String pointColor){
		series.setPointColor(pointColor);
		return this;
	}

	public SeriesBuilder withPointStrokeColor(String pointStrokeColor){
		series.setPointStrokeColor(pointStrokeColor);
		return this;
	}
	
	public SeriesBuilder withData(double[] data){
		series.setData(data);
		return this;
	}
	
	public AreaSeries get(){
		return series;
	}
}