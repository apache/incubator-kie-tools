package org.uberfire.client.menu;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * Wraps a menu visitor, filtering out menu items that a given user is not allowed to access. The wrapped visitor only
 * sees the items that the user is allowed to see.
 */
public class AuthFilterMenuVisitor implements MenuVisitor {

    private final AuthorizationManager authzManager;
    private final User user;
    private final MenuVisitor chainedVisitor;

    /**
     * Wraps the given menu visitor, only forwarding calls that represent menu items the given user is allowed to see.
     *
     * @param authzManager The authorization manager that decides what is visible. Not null.
     * @param user The user who will see the menus being visited. Not null.
     * @param chainedVisitor The menu visitor that receives calls for all authorized parts of the menu tree. Not null.
     */
    public AuthFilterMenuVisitor( AuthorizationManager authzManager,
                                  User user,
                                  MenuVisitor chainedVisitor ) {
        this.authzManager = checkNotNull( "authzManager",
                                          authzManager );
        this.user = checkNotNull( "user",
                                  user );
        this.chainedVisitor = checkNotNull( "chainedVisitor",
                                            chainedVisitor );
    }

    @Override
    public boolean visitEnter( Menus menus ) {
        return chainedVisitor.visitEnter( menus );
    }

    @Override
    public void visitLeave( Menus menus ) {
        chainedVisitor.visitLeave( menus );
    }

    @Override
    public boolean visitEnter( MenuGroup menuGroup ) {
        if ( !authzManager.authorize( menuGroup,
                                      user ) ) {
            return false;
        }
        return chainedVisitor.visitEnter( menuGroup );
    }

    @Override
    public void visitLeave( MenuGroup menuGroup ) {
        chainedVisitor.visitLeave( menuGroup );
    }

    @Override
    public void visit( MenuItemPlain menuItemPlain ) {
        if ( authzManager.authorize( menuItemPlain,
                                     user ) ) {
            chainedVisitor.visit( menuItemPlain );
        }
    }

    @Override
    public void visit( MenuItemCommand menuItemCommand ) {
        if ( authzManager.authorize( menuItemCommand,
                                     user ) ) {
            chainedVisitor.visit( menuItemCommand );
        }
    }

    @Override
    public void visit( MenuCustom<?> menuCustom ) {
        if ( authzManager.authorize( menuCustom,
                                     user ) ) {
            chainedVisitor.visit( menuCustom );
        }
    }

    @Override
    public void visit( MenuItemPerspective menuItemPerspective ) {
        if ( authzManager.authorize( menuItemPerspective,
                user ) ) {
            chainedVisitor.visit( menuItemPerspective );
        }
    }
}
