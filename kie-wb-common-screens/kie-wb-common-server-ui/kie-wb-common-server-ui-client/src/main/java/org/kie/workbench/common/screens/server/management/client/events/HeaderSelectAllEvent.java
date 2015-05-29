package org.kie.workbench.common.screens.server.management.client.events;

public class HeaderSelectAllEvent {

    private final Object context;

    public HeaderSelectAllEvent( Object context ) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }

}
