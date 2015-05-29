package org.kie.workbench.common.screens.server.management.client.events;

public class DependencyPathSelectedEvent {

    private final Object context;
    private final String path;

    public DependencyPathSelectedEvent( final Object context,
                                        final String path ) {
        this.context = context;
        this.path = path;
    }

    public Object getContext() {
        return context;
    }

    public String getPath() {
        return path;
    }
}
