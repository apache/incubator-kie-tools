/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.views.pfly.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.shared.event.HiddenEvent;
import org.gwtbootstrap3.client.shared.event.HiddenHandler;
import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.client.shared.event.ShownEvent;
import org.gwtbootstrap3.client.shared.event.ShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Collapse;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.NavbarCollapse;
import org.gwtbootstrap3.client.ui.NavbarHeader;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.NavbarType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;

/**
 * The Menu Bar widget
 */
@ApplicationScoped
public class WorkbenchMenuBarView extends Composite implements WorkbenchMenuBarPresenter.View {

    public interface WorkbenchMenuNavBarView {

        void clear();

        void addMenuItem( String id, String label, String parentId, Command command );

        void addGroupMenuItem( String id, String label );

        void selectMenuItem( String id );

        void addContextMenuItem( String menuItemId, String id, String label, String parentId, Command command );

        void addContextGroupMenuItem( String menuItemId, String id, String label );

    }

    @Inject
    private Instance<MainBrand> menuBarBrand;

    private final Navbar navBar = GWT.create( Navbar.class );

    private final NavbarHeader navbarHeader = GWT.create( NavbarHeader.class );

    private final NavbarCollapse navbarCollapse = GWT.create( NavbarCollapse.class );

    @Inject
    private WorkbenchMenuCompactNavBarView workbenchMenuCompactNavBarView;

    @Inject
    private WorkbenchMenuStandardNavBarView workbenchMenuStandardNavBarView;

    private Collapse navBarCollapse = GWT.create( Collapse.class );

    @Inject
    private UtilityMenuBarView utilityMenuBarView;

    @PostConstruct
    protected void setup() {
        navBar.setType( NavbarType.INVERSE );
        navBar.addStyleName( "navbar-pf" );

        try {
            final NavbarBrand brand = GWT.create( NavbarBrand.class );
            brand.add( menuBarBrand.get() );
            navbarHeader.add( brand );
        } catch ( IOCResolutionException e ) {
            // app didn't provide a branded header bean
        }
        navBar.add( navbarHeader );

        setupNavBarCollapse();

        navbarCollapse.add( workbenchMenuCompactNavBarView );
        navbarCollapse.add( navBarCollapse );
        navbarCollapse.add( utilityMenuBarView );

        navBar.add( navbarCollapse );

        setupToggle();

        initWidget( navBar );

        expand();
    }

    protected void setupToggle() {
        final Button btnToggle = GWT.create( Button.class );
        btnToggle.removeStyleName( "btn-default" );
        btnToggle.addStyleName( Styles.NAVBAR_TOGGLE );
        btnToggle.setDataToggle( Toggle.COLLAPSE );
        btnToggle.setDataTargetWidget( navbarCollapse );
        final Icon icon = GWT.create( Icon.class );
        icon.setType( IconType.BARS );
        icon.addStyleName( "fa-inverse" );
        btnToggle.add( icon );
        navbarHeader.add( btnToggle );
    }

    protected void setupNavBarCollapse() {
        workbenchMenuCompactNavBarView.addStyleName( "hidden" );
        navBarCollapse.addShowHandler( new ShowHandler() {
            @Override
            public void onShow( ShowEvent showEvent ) {
                workbenchMenuCompactNavBarView.removeStyleName( "show" );
                workbenchMenuCompactNavBarView.addStyleName( "hidden" );
                navbarHeader.removeStyleName( Styles.PULL_LEFT );
                workbenchMenuStandardNavBarView.removeStyleName( "hidden" );
                workbenchMenuStandardNavBarView.addStyleName( "show" );
            }
        } );
        navBarCollapse.addShownHandler( new ShownHandler() {
            @Override
            public void onShown( ShownEvent event ) {
                navBar.removeStyleName( "uf-navbar-compact" );
            }
        } );

        navBarCollapse.addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( HiddenEvent event ) {
                workbenchMenuStandardNavBarView.removeStyleName( "show" );
                workbenchMenuStandardNavBarView.addStyleName( "hidden" );

                navbarHeader.addStyleName( Styles.PULL_LEFT );
                workbenchMenuCompactNavBarView.removeStyleName( "hidden" );
                workbenchMenuCompactNavBarView.addStyleName( "show" );
                navBar.addStyleName( "uf-navbar-compact" );
                navBarCollapse.removeStyleName( Styles.IN );
            }
        } );
        navBarCollapse.addStyleName( Styles.IN );
        navBarCollapse.add( workbenchMenuStandardNavBarView );
    }

    @Override
    public void addMenuItem( final String id, final String label, final String parentId, final Command command ) {
        workbenchMenuStandardNavBarView.addMenuItem( id, label, parentId, command );
        workbenchMenuCompactNavBarView.addMenuItem( id, label, parentId, command );
    }

    @Override
    public void addGroupMenuItem( final String id, final String label ) {
        workbenchMenuStandardNavBarView.addGroupMenuItem( id, label );
        workbenchMenuCompactNavBarView.addGroupMenuItem( id, label );
    }

    @Override
    public void addContextMenuItem( final String menuItemId, final String id, final String label, final String parentId, final Command command ) {
        workbenchMenuStandardNavBarView.addContextMenuItem( menuItemId, id, label, parentId, command );
        workbenchMenuCompactNavBarView.addContextMenuItem( menuItemId, id, label, parentId, command );
    }

    @Override
    public void addContextGroupMenuItem( final String menuItemId, final String id, final String label ) {
        workbenchMenuStandardNavBarView.addContextGroupMenuItem( menuItemId, id, label );
        workbenchMenuCompactNavBarView.addContextGroupMenuItem( menuItemId, id, label );
    }

    @Override
    public void clear() {
        workbenchMenuStandardNavBarView.clear();
        workbenchMenuCompactNavBarView.clear();
        utilityMenuBarView.clear();
    }

    @Override
    public void expand() {
        if ( navBarCollapse.isHidden() ) {
            navBarCollapse.show();
        }
    }

    @Override
    public void collapse() {
        if ( navBarCollapse.isShown() ) {
            navBarCollapse.hide();
        }
    }

    @Override
    public void selectMenuItem( final String id ) {
        workbenchMenuCompactNavBarView.selectMenuItem( id );
        workbenchMenuStandardNavBarView.selectMenuItem( id );
    }

    @Override
    public void addCollapseHandler( final Command command ) {
        navBarCollapse.addHideHandler( new HideHandler() {
            @Override
            public void onHide( final HideEvent hideEvent ) {
                command.execute();
            }
        } );
    }

    @Override
    public void addExpandHandler( final Command command ) {
        navBarCollapse.addShowHandler( new ShowHandler() {
            @Override
            public void onShow( final ShowEvent showEvent ) {
                command.execute();
            }
        } );
    }

}