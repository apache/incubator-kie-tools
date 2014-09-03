package org.uberfire.client.workbench.events;

import org.uberfire.workbench.events.UberFireEvent;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * CDI event fired by the framework just after the
 * @author jfuerth
 *
 */
public class PerspectiveChange implements UberFireEvent {

    private final PerspectiveDefinition perspectiveDefinition;
    private final Menus menus;
    private final String identifier;

    public PerspectiveChange( final PerspectiveDefinition perspectiveDefinition,
                              final Menus menus,
                              final String identifier ) {
        this.perspectiveDefinition = perspectiveDefinition;
        this.menus = menus;
        this.identifier = identifier;
    }

    public PerspectiveDefinition getPerspectiveDefinition() {
        return perspectiveDefinition;
    }

    public Menus getMenus() {
        return menus;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "PerspectiveChange [perspectiveDefinition=" + perspectiveDefinition + ", menus=" + menus + ", identifier="
                + identifier + "]";
    }

}
