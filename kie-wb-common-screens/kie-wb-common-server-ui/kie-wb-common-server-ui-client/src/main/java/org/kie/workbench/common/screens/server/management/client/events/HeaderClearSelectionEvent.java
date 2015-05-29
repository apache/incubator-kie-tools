package org.kie.workbench.common.screens.server.management.client.events;

public class HeaderClearSelectionEvent {

    private final Object context;

    public HeaderClearSelectionEvent( final Object context ) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }
}
