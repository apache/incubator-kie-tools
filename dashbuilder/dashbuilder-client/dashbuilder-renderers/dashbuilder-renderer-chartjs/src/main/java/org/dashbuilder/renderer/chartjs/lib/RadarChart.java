package org.dashbuilder.renderer.chartjs.lib;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartData;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartDataProvider;


public class RadarChart extends ChartWithScale {
	
	private AreaChartDataProvider provider;
	private boolean scaleShowLabels = false;
	
	@Override
	public void draw() {
		reload();
	}
	
	public void setScaleShowLabels(boolean scaleShowLabels){
		this.scaleShowLabels = scaleShowLabels;
	}
	
	private native void drawRadar(JavaScriptObject data)/*-{
        canvas = this.@org.dashbuilder.renderer.chartjs.lib.Chart::getNativeElement()();
        nativeCanvas = this.@org.dashbuilder.renderer.chartjs.lib.Chart::getNativeCanvas()();
        if(nativeCanvas != null) {
            nativeCanvas.destroy();
        }

        var options = this.@org.dashbuilder.renderer.chartjs.lib.Chart::constructOptions()();
        if(options == null)
            options = {scaleShowLabels : true, pointLabelFontSize : 10};
        nativeCanvas = new $wnd.Chart(canvas.getContext("2d")).Radar(data,options);
        this.@org.dashbuilder.renderer.chartjs.lib.Chart::setNativeCanvas(Lcom/google/gwt/core/client/JavaScriptObject;)(nativeCanvas);
	}-*/;

	@Override
	public void update() {
		if(provider == null)
			throw new NullPointerException("PieCharDataProvider was not initialized before invoking update()");

        drawRadar(provider.getData());
	}

	@Override
	public void reload() {
		if(provider == null)
			throw new NullPointerException("PieCharDataProvider was not initialized before invoking reload()");
		
		//TODO: show loading..
		
		provider.reload(new AsyncCallback<AreaChartData>() {
			
			@Override
			public void onSuccess(AreaChartData result) {
                drawRadar(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setDataProvider(AreaChartDataProvider provider){
		this.provider = provider;
	}
	
}
