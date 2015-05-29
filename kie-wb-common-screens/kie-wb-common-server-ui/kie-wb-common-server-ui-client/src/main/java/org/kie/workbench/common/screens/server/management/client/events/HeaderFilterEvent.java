package org.kie.workbench.common.screens.server.management.client.events;

public class HeaderFilterEvent {

    private String filter;
    private final Object context;

    public HeaderFilterEvent( final Object context,
                              final String filter ) {
        this.filter = filter;
        this.context = context;
    }

    public Object getContext() {
        return context;
    }

    public String getFilter() {
        return filter;
    }
}
