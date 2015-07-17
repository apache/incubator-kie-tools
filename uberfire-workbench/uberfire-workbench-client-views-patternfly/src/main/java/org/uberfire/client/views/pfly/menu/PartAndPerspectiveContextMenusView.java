package org.uberfire.client.views.pfly.menu;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter;
import org.uberfire.client.workbench.widgets.menu.PespectiveContextMenusPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.Composite;

@Dependent
public class PartAndPerspectiveContextMenusView
        extends Composite
        implements PartContextMenusPresenter.View, PespectiveContextMenusPresenter.View {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    Bs3NavPillsMenuBar menuBar = new Bs3NavPillsMenuBar();

    public PartAndPerspectiveContextMenusView() {
        initWidget( menuBar );
    }

    @Override
    public void buildMenu( final Menus menus ) {
        menuBar.clear();
        Bs3Menus.constructMenuView( menus, authzManager, identity, menuBar );
    }

    @Override
    public void clear() {
        menuBar.clear();
    }

}
