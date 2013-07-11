package org.uberfire.workbench.events;

import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Created with IntelliJ IDEA.
 * Date: 7/12/13
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class PerspectiveChange {

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
}
