package org.dashbuilder.renderer.chartjs.lib;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartData;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartDataProvider;


public class BarChart extends ChartWithScale {
	
	private AreaChartDataProvider provider;
	
	@Override
	public void draw() {
		reload();
		
	}
	
	private native void drawBar(JavaScriptObject data)/*-{
        canvas = this.@org.dashbuilder.renderer.chartjs.lib.Chart::getNativeElement()();
        nativeCanvas = this.@org.dashbuilder.renderer.chartjs.lib.Chart::getNativeCanvas()();
        if(nativeCanvas != null) {
            nativeCanvas.destroy();
        }

        var options = this.@org.dashbuilder.renderer.chartjs.lib.Chart::constructOptions()();
        nativeCanvas = new $wnd.Chart(canvas.getContext("2d")).Bar(data, options);
        this.@org.dashbuilder.renderer.chartjs.lib.Chart::setNativeCanvas(Lcom/google/gwt/core/client/JavaScriptObject;)(nativeCanvas);
	}-*/;

	@Override
	public void update() {
		if(provider == null)
			throw new NullPointerException("PieCharDataProvider is not specified before invoking update()");
        drawBar(provider.getData());
	}

	@Override
	public void reload() {
		if(provider == null)
			throw new NullPointerException("PieCharDataProvider is not specified before invoking reload()");

		//TODO : show loading
		provider.reload(new AsyncCallback<AreaChartData>() {
			
			@Override
			public void onSuccess(AreaChartData result) {
                drawBar(result);
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
