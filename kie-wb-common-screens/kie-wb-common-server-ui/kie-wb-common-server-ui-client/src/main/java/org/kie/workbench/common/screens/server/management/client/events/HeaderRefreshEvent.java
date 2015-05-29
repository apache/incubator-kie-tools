package org.kie.workbench.common.screens.server.management.client.events;

public class HeaderRefreshEvent {

    private final Object context;

    public HeaderRefreshEvent( Object context ) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }

}
