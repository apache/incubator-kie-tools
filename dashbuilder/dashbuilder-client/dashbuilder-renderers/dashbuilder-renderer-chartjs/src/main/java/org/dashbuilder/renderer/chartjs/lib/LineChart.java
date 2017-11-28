package org.dashbuilder.renderer.chartjs.lib;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartData;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartDataProvider;




public class LineChart extends ChartWithScale {

	private AreaChartDataProvider provider = null;


	@Override
	public void draw() {
		reload();
	}
	
	private native void drawLine(JavaScriptObject data)/*-{
        canvas = this.@org.dashbuilder.renderer.chartjs.lib.LineChart::getNativeElement()();
        nativeCanvas = this.@org.dashbuilder.renderer.chartjs.lib.LineChart::getNativeCanvas()();
        if(nativeCanvas != null) {
            nativeCanvas.destroy();
        }

        var options = this.@org.dashbuilder.renderer.chartjs.lib.LineChart::constructOptions()();
        nativeCanvas = new $wnd.Chart(canvas.getContext("2d")).Line(data,options);
        this.@org.dashbuilder.renderer.chartjs.lib.LineChart::setNativeCanvas(Lcom/google/gwt/core/client/JavaScriptObject;)(nativeCanvas);
	}-*/;

	@Override
	public void update() {
		if(provider == null)
			throw new NullPointerException("Data provider is not specified before calling update()");
        drawLine(provider.getData());
	}

	@Override
	public void reload() {
		if(provider == null)
			throw new NullPointerException("Data provider is not specified before calling reload()");
		//TODO: show some king of loading to user
		provider.reload(new AsyncCallback<AreaChartData>() {
			
			@Override
			public void onSuccess(AreaChartData result) {
                drawLine(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				//TODO: show loading error to user 
			}
		});
	}
	
	public void setDataProvider(AreaChartDataProvider provider){
		this.provider = provider;
	}
}
