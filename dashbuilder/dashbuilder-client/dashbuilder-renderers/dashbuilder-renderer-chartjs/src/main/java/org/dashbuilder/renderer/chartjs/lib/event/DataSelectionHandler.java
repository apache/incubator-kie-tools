package org.dashbuilder.renderer.chartjs.lib.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for handling {@link org.dashbuilder.renderer.chartjs.lib.event.AnimationCompleteEvent}
 */
public interface DataSelectionHandler extends EventHandler{

	/**
	 * Method will be invoked when animation is complete
	 * @param event : object contains reference to the instance of chart
	 */
	public void onDataSelected(DataSelectionEvent event);
	
}
