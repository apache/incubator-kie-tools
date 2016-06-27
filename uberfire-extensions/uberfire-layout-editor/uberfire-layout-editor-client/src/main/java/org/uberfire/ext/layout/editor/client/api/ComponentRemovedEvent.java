package org.uberfire.ext.layout.editor.client.api;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public class ComponentRemovedEvent {

    private LayoutComponent layoutComponent;

    public ComponentRemovedEvent( LayoutComponent layoutComponent ) {

        this.layoutComponent = layoutComponent;
    }

    public LayoutComponent getLayoutComponent() {
        return layoutComponent;
    }
}
