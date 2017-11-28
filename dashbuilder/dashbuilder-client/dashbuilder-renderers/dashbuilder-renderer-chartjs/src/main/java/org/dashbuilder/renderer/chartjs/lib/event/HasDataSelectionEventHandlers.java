package org.dashbuilder.renderer.chartjs.lib.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Interface describe methods required for adding new handlers of {@link org.dashbuilder.renderer.chartjs.lib.event.AnimationCompleteEvent}
 */
public interface HasDataSelectionEventHandlers extends HasHandlers{

	/**
	 * Add {@link org.dashbuilder.renderer.chartjs.lib.event.AnimationCompleteEvent} handler to widget.
	 * @param handler
	 */
	public HandlerRegistration addDataSelectionHandler(DataSelectionHandler handler);
}
