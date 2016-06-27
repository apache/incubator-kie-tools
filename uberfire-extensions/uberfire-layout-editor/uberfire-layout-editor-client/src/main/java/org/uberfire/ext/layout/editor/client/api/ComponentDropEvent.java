package org.uberfire.ext.layout.editor.client.api;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public class ComponentDropEvent {

    private LayoutComponent component;

    public ComponentDropEvent( LayoutComponent component ) {

        this.component = component;

    }

    public LayoutComponent getComponent() {
        return component;
    }
}
