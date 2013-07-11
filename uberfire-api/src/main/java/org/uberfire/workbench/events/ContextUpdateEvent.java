package org.uberfire.workbench.events;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.workbench.model.PanelDefinition;

public class ContextUpdateEvent {

    private final PanelDefinition panel;
    private final Map<String, Object> contextData = new HashMap<String, Object>();

    public ContextUpdateEvent( final PanelDefinition panel,
                               final Map<String, Object> contextData ) {
        this.panel = panel;
        this.contextData.putAll( contextData );
    }

    public Map<String, Object> getData() {
        return contextData;
    }

    public PanelDefinition getPanel() {
        return panel;
    }
}
