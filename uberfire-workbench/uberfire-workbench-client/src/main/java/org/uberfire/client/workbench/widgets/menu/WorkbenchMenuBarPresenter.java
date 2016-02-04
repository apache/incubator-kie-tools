/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuVisitor;

/**
 * Presenter for WorkbenchMenuBar that mediates changes to the Workbench MenuBar
 * in response to changes to the selected WorkbenchPart. The menu structure is
 * cloned and items that lack permission are removed. This implementation is
 * specific to GWT. An alternative implementation should be considered for use
 * within Eclipse.
 */
@ApplicationScoped
public class WorkbenchMenuBarPresenter implements WorkbenchMenuBar {

    private boolean useExpandedMode = true;
    private boolean expanded = true;

    private List<Menus> addedMenus;

    @Inject
    protected AuthorizationManager authzManager;

    @Inject
    protected User identity;

    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    private ActivityManager activityManager;

    public interface View extends IsWidget {

        void clear();

        void addMenuItem( String id, String label, String parentId, Command command );

        void addCustomMenuItem( Widget menu );

        void addGroupMenuItem( String id, String label );

        void selectMenuItem( String id );

        void addContextMenuItem( String menuItemId, String id, String label, String parentId, Command command, MenuPosition position );

        void addContextGroupMenuItem( String menuItemId, String id, String label );

        void expand();

        void collapse();

        void addCollapseHandler( Command command );

        void addExpandHandler( Command command );

        void enableMenuItem( String menuItemId, boolean enabled );

        void enableContextMenuItem( String menuItemId, boolean enabled );

    }

    @Inject
    private View view;

    public IsWidget getView() {
        return this.view;
    }

    @PostConstruct
    protected void setup() {
        view.addExpandHandler( new Command() {
            @Override
            public void execute() {
                expanded = true;
            }
        } );
        view.addCollapseHandler( new Command() {
            @Override
            public void execute() {
                expanded = false;
            }
        } );
    }

    @Override
    public void addMenus( final Menus menus ) {
        if ( menus != null && !menus.getItems().isEmpty() ) {

            if ( addedMenus == null ) {
                addedMenus = new ArrayList<Menus>();
            }

            addedMenus.add( menus );

            if ( menusMustBeReordered( menus ) ) {
                reorderMenus();
                view.clear();

                for ( Menus currentMenus : addedMenus ) {
                    visitMenus( currentMenus );
                }
            } else {
                visitMenus( menus );
            }
        }
    }

    private boolean menusMustBeReordered( final Menus menus ) {
        if ( addedMenus.size() < 2 ) {
            return false;
        }

        final Menus previousMenus = addedMenus.get( addedMenus.size() - 2 );
        return previousMenus.getOrder() > menus.getOrder();
    }

    private void reorderMenus() {
        Collections.sort( addedMenus, new Comparator<Menus>() {
            @Override
            public int compare( final Menus o1,
                                final Menus o2 ) {
                return o1.getOrder() - o2.getOrder();
            }
        } );
    }

    private void visitMenus( final Menus addedMenu ) {
        addedMenu.accept( new AuthFilterMenuVisitor( authzManager, identity, new BaseMenuVisitor() {

            private String parentId = null;

            @Override
            public boolean visitEnter( final MenuGroup menuGroup ) {
                parentId = getMenuItemId( menuGroup );
                view.addGroupMenuItem( parentId, menuGroup.getCaption() );
                return true;
            }

            @Override
            public void visitLeave( MenuGroup menuGroup ) {
                parentId = null;
            }

            @Override
            public void visit( final MenuItemPlain menuItemPlain ) {
                view.addMenuItem( getMenuItemId( menuItemPlain ), menuItemPlain.getCaption(), parentId, null );
                setupEnableDisableMenuItem( menuItemPlain );
            }

            @Override
            public void visit( final MenuCustom<?> menuCustom ) {
                final Object build = menuCustom.build();
                if ( build instanceof IsWidget ) {
                    view.addCustomMenuItem( ( (IsWidget) build ).asWidget() );
                } else {
                    view.addMenuItem( getMenuItemId( menuCustom ), menuCustom.getCaption(), parentId, null );
                }
                setupEnableDisableMenuItem( menuCustom );
            }

            @Override
            public void visit( final MenuItemCommand menuItemCommand ) {
                view.addMenuItem( getMenuItemId( menuItemCommand ), menuItemCommand.getCaption(), parentId, menuItemCommand.getCommand() );
                setupEnableDisableMenuItem( menuItemCommand );
            }

            @Override
            public void visit( final MenuItemPerspective menuItemPerspective ) {
                final String id = menuItemPerspective.getPlaceRequest().getIdentifier();
                view.addMenuItem( id, menuItemPerspective.getCaption(), parentId, new Command() {
                    @Override
                    public void execute() {
                        IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance().goTo( menuItemPerspective.getPlaceRequest() );
                    }
                } );
                setupEnableDisableMenuItem( menuItemPerspective );
                final Menus perspectiveMenus = getPerspectiveMenus( id );
                if ( perspectiveMenus != null ) {
                    addPerspectiveMenus( id, perspectiveMenus );
                }
                final PlaceRequest placeRequest = menuItemPerspective.getPlaceRequest();
                if ( perspectiveManager.getCurrentPerspective() != null && placeRequest.equals( perspectiveManager.getCurrentPerspective().getPlace() ) ) {
                    view.selectMenuItem( id );
                }
            }

            private void setupEnableDisableMenuItem( final MenuItem menuItem ) {
                menuItem.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        view.enableMenuItem( getMenuItemId( menuItem ),
                                             enabled );
                    }
                } );
            }

        }

        ) );
    }

    private String getMenuItemId( final MenuItem menuItem ) {
        return menuItem.getSignatureId() == null ? menuItem.getCaption() : menuItem.getSignatureId();
    }

    private void addPerspectiveMenus( final String perspectiveId,
                                      final Menus menus ) {
        menus.accept( new AuthFilterMenuVisitor( authzManager, identity, new BaseMenuVisitor() {

            private String parentId = null;

            @Override
            public boolean visitEnter( final MenuGroup menuGroup ) {
                parentId = getMenuItemId( menuGroup );
                view.addContextGroupMenuItem( perspectiveId, parentId, menuGroup.getCaption() );
                return true;
            }

            @Override
            public void visitLeave( MenuGroup menuGroup ) {
                parentId = null;
            }

            @Override
            public void visit( final MenuItemPlain menuItemPlain ) {
                view.addContextMenuItem( perspectiveId, getMenuItemId( menuItemPlain ), menuItemPlain.getCaption(), parentId, null, menuItemPlain.getPosition() );
                setupEnableDisableContextMenuItem( menuItemPlain );
            }

            @Override
            public void visit( final MenuCustom<?> menuCustom ) {
                view.addContextMenuItem( perspectiveId, getMenuItemId( menuCustom ), menuCustom.getCaption(), parentId, null, menuCustom.getPosition() );
                setupEnableDisableContextMenuItem( menuCustom );
            }

            @Override
            public void visit( final MenuItemCommand menuItemCommand ) {
                view.addContextMenuItem( perspectiveId, getMenuItemId( menuItemCommand ), menuItemCommand.getCaption(), parentId, menuItemCommand.getCommand(), menuItemCommand.getPosition() );
                setupEnableDisableContextMenuItem( menuItemCommand );
            }

            @Override
            public void visit( final MenuItemPerspective menuItemPerspective ) {
                view.addContextMenuItem( perspectiveId, menuItemPerspective.getPlaceRequest().getIdentifier(), menuItemPerspective.getCaption(), parentId, new Command() {
                    @Override
                    public void execute() {
                        IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance().goTo( menuItemPerspective.getPlaceRequest() );
                    }
                }, menuItemPerspective.getPosition() );
                setupEnableDisableContextMenuItem( menuItemPerspective );
            }

            private void setupEnableDisableContextMenuItem( final MenuItem menuItem ) {
                menuItem.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        view.enableContextMenuItem( getMenuItemId( menuItem ),
                                enabled );
                    }
                } );
            }

        } ) );
    }

    private Menus getPerspectiveMenus( final String perspectiveId ) {
        final Set<PerspectiveActivity> activities = activityManager.getActivities( PerspectiveActivity.class );

        for ( PerspectiveActivity activity : activities ) {
            if ( activity.getIdentifier().equals( perspectiveId ) ) {
                return activity.getMenus();
            }
        }

        return null;
    }

    protected void onPerspectiveChange( @Observes final PerspectiveChange perspectiveChange ) {
        view.selectMenuItem( perspectiveChange.getPlaceRequest().getIdentifier() );
    }

    protected void onPlaceMinimized( @Observes final PlaceMinimizedEvent event ) {
        if ( isUseExpandedMode() ) {
            view.expand();
        }
    }

    protected void onPlaceMaximized( @Observes final PlaceMaximizedEvent event ) {
        view.collapse();
    }

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public void expand() {
        useExpandedMode = true;
        view.expand();
    }

    @Override
    public boolean isUseExpandedMode() {
        return useExpandedMode;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void collapse() {
        useExpandedMode = false;
        view.collapse();
    }

    @Override
    public void addCollapseHandler( final Command command ) {
        view.addCollapseHandler( command );
    }

    @Override
    public void addExpandHandler( final Command command ) {
        view.addExpandHandler( command );
    }

    List<Menus> getAddedMenus() {
        return this.addedMenus;
    }
}
