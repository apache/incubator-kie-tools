package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.common.client.event.ContextualEvent;

/**
 * <p>CDI event when a cancel operation is requested.</p>
 *
 * @since 0.4.0
 */
public class CancelRequestEvent extends ContextualEvent {

    public CancelRequestEvent(Object context) {
        super(context);
    }

    @Override
    public String toString() {
        return "CancelRequestEvent [Context=" + getContext().toString() + "]";
    }

}
