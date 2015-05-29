package org.kie.workbench.common.screens.server.management.client.events;

public class HeaderDeleteEvent {

    private final Object context;

    public HeaderDeleteEvent( final Object context ) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }
}
