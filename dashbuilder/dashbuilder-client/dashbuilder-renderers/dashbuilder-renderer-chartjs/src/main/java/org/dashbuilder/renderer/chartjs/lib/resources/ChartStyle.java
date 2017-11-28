package org.dashbuilder.renderer.chartjs.lib.resources;

import com.google.gwt.resources.client.CssResource;

public interface ChartStyle extends CssResource{

	/**
	 * Style for chart area (applied to div wrapper of canvas element)
	 * @return
	 */
	String chart();
	
	/**
	 * Style of default series
	 * @return
	 */
	String defaultSeries();
	
	String series1();
	
}
