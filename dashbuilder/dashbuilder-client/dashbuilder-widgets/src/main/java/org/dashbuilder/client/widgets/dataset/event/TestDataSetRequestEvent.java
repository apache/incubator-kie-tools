package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.common.client.event.ContextualEvent;

/**
 * <p>CDI event when a test data set operation is requested.</p>
 *
 * @since 0.4.0
 */
public class TestDataSetRequestEvent extends ContextualEvent {

    public TestDataSetRequestEvent(Object context) {
        super(context);
    }

    @Override
    public String toString() {
        return "SaveRequestEvent [Context=" + getContext().toString() + "]";
    }

}
