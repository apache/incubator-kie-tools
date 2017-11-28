package org.dashbuilder.common.client.event;

import org.uberfire.workbench.events.UberFireEvent;

/**
 * <p>Base class for any event that is fired for a given context.</p>
 * 
 * @since 0.4.0
 */
public abstract class ContextualEvent implements UberFireEvent {
    private final Object context;

    public ContextualEvent(Object context) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }
}