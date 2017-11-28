package org.dashbuilder.renderer.chartjs.lib.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for handling {@link AnimationCompleteEvent}
 */
public interface AnimationCompleteHandler extends EventHandler{

	/**
	 * Method will be invoked when animation is complete
	 * @param event : object contains reference to the instance of chart
	 */
	public void onAnimationComplete(AnimationCompleteEvent event);
	
}
