package org.dashbuilder.renderer.chartjs.lib.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Class contains resources required for chart library
 */
public interface Resources extends ClientBundle{
	
	/**
	 * Contains text representation of native chart.js code
	 */
	@Source("js/chart.min.js")
	TextResource chartJsSource();
	
	/**
	 * Default style required for chart styling
	 */
	@Source("js/chart.css")
	ChartStyle chartStyle();
}
