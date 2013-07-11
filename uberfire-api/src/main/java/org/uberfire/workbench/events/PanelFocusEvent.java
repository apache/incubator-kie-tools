package org.uberfire.workbench.events;

import org.uberfire.workbench.model.PanelDefinition;

public class PanelFocusEvent {

    private final PanelDefinition panel;

    public PanelFocusEvent( PanelDefinition panel ) {
        this.panel = panel;
    }

    public PanelDefinition getPanel() {
        return panel;
    }
}
