package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.common.client.event.ContextualEvent;

/**
 * <p>CDI event when a save operation is requested.</p>
 *
 * @since 0.4.0
 */
public class SaveRequestEvent extends ContextualEvent {

    public SaveRequestEvent(Object context) {
        super(context);
    }

    @Override
    public String toString() {
        return "SaveRequestEvent [Context=" + getContext().toString() + "]";
    }

}
