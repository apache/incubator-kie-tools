package org.kie.workbench.common.screens.server.management.client.events;

public class HeaderStartEvent {

    private final Object context;

    public HeaderStartEvent( Object context ) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }
}
