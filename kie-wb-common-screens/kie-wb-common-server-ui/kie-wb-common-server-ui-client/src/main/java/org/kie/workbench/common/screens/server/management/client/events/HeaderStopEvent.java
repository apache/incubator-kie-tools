package org.kie.workbench.common.screens.server.management.client.events;

public class HeaderStopEvent {

    private final Object context;

    public HeaderStopEvent( Object context ) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }
}
