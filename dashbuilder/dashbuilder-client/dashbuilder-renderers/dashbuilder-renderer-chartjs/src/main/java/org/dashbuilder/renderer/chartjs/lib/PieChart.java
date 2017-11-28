package org.dashbuilder.renderer.chartjs.lib;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.dashbuilder.renderer.chartjs.lib.data.PieChartDataProvider;
import org.dashbuilder.renderer.chartjs.lib.data.Series;


public class PieChart extends ChartWithScale {
	
	private PieChartDataProvider provider;
	
	@Override
	public void draw() {
		reload();
	}
	
	private native void drawPie(JavaScriptObject data)/*-{
        canvas = this.@org.dashbuilder.renderer.chartjs.lib.Chart::getNativeElement()();
        nativeCanvas = this.@org.dashbuilder.renderer.chartjs.lib.Chart::getNativeCanvas()();
        if(nativeCanvas != null) {
            nativeCanvas.destroy();
        }

        options = this.@org.dashbuilder.renderer.chartjs.lib.Chart::constructOptions()();
        nativeCanvas = new $wnd.Chart(canvas.getContext("2d")).Pie(data,options);
        this.@org.dashbuilder.renderer.chartjs.lib.Chart::setNativeCanvas(Lcom/google/gwt/core/client/JavaScriptObject;)(nativeCanvas);
	}-*/;

	@Override
	public void update() {
		if(provider == null)
			throw new NullPointerException("PieChartDataProvider not initialized before invoking update()");
        drawPie(provider.getData());
	}

	@Override
	public void reload() {
		if(provider == null)
			throw new NullPointerException("PieChartDataProvider not initialized before invoking update()");
		
		//TODO: show loading
		provider.reload(new AsyncCallback<JsArray<Series>>() {
			
			@Override
			public void onSuccess(JsArray<Series> result) {
                drawPie(provider.getData());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setDataProvider(PieChartDataProvider provider){
		this.provider = provider;
	}
	
}
