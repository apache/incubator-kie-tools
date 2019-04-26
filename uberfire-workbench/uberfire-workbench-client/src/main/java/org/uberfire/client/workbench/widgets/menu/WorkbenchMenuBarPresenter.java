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

import java.util.function.BiConsumer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuPresenter;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuView;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
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
public class WorkbenchMenuBarPresenter extends WorkbenchBaseMenuPresenter implements WorkbenchMenuBar {

    protected AuthorizationManager authzManager;
    protected User identity;
    private boolean useExpandedMode = true;
    private boolean expanded = true;
    private PerspectiveManager perspectiveManager;
    private PlaceManager placeManager;
    private ActivityManager activityManager;
    private View view;
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    WorkbenchMenuBarPresenter(final AuthorizationManager authzManager,
                              final PerspectiveManager perspectiveManager,
                              final PlaceManager placeManager,
                              final ActivityManager activityManager,
                              final User identity,
                              final View view,
                              final ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager) {
        this.authzManager = authzManager;
        this.perspectiveManager = perspectiveManager;
        this.placeManager = placeManager;
        this.activityManager = activityManager;
        this.identity = identity;
        this.view = view;
        this.experimentalActivitiesAuthorizationManager = experimentalActivitiesAuthorizationManager;

        setup();
    }

    protected void setup() {
        view.addExpandHandler(new Command() {
            @Override
            public void execute() {
                expanded = true;
            }
        });
        view.addCollapseHandler(new Command() {
            @Override
            public void execute() {
                expanded = false;
            }
        });
    }

    public IsWidget getView() {
        return this.view;
    }

    @Override
    protected WorkbenchBaseMenuView getBaseView() {
        return view;
    }

    @Override
    protected void visitMenus(final Menus addedMenu) {
        addedMenu.accept(new AuthFilterMenuVisitor(authzManager,
                                                   identity,
                                                   new BaseMenuVisitor() {

                                                       private String parentId = null;

                                                       @Override
                                                       public boolean visitEnter(final MenuGroup menuGroup) {
                                                           parentId = getMenuItemId(menuGroup);
                                                           view.addGroupMenuItem(parentId,
                                                                                 menuGroup.getCaption(),
                                                                                 menuGroup.getPosition());
                                                           return true;
                                                       }

                                                       @Override
                                                       public void visitLeave(MenuGroup menuGroup) {
                                                           parentId = null;
                                                       }

                                                       @Override
                                                       public void visit(final MenuItemPlain menuItemPlain) {
                                                           view.addMenuItem(getMenuItemId(menuItemPlain),
                                                                            menuItemPlain.getCaption(),
                                                                            parentId,
                                                                            null,
                                                                            menuItemPlain.getPosition());
                                                           setupEnableDisableMenuItem(menuItemPlain);
                                                       }

                                                       @Override
                                                       public void visit(final MenuCustom<?> menuCustom) {
                                                           final Object build = menuCustom.build();
                                                           if (build instanceof IsWidget) {
                                                               view.addCustomMenuItem(((IsWidget) build).asWidget(),
                                                                                      menuCustom.getPosition());
                                                           } else {
                                                               view.addMenuItem(getMenuItemId(menuCustom),
                                                                                menuCustom.getCaption(),
                                                                                parentId,
                                                                                null,
                                                                                menuCustom.getPosition());
                                                           }
                                                           setupEnableDisableMenuItem(menuCustom);
                                                       }

                                                       @Override
                                                       public void visit(final MenuItemCommand menuItemCommand) {
                                                           view.addMenuItem(getMenuItemId(menuItemCommand),
                                                                            menuItemCommand.getCaption(),
                                                                            parentId,
                                                                            menuItemCommand.getCommand(),
                                                                            menuItemCommand.getPosition());
                                                           setupEnableDisableMenuItem(menuItemCommand);
                                                       }

                                                       @Override
                                                       public void visit(final MenuItemPerspective menuItemPerspective) {
                                                           final String id = menuItemPerspective.getPlaceRequest().getIdentifier();
                                                           view.addMenuItem(id,
                                                                            menuItemPerspective.getCaption(),
                                                                            parentId,
                                                                            new Command() {
                                                                                @Override
                                                                                public void execute() {
                                                                                    placeManager.goTo(menuItemPerspective.getPlaceRequest());
                                                                                }
                                                                            },
                                                                            menuItemPerspective.getPosition());
                                                           setupEnableDisableMenuItem(menuItemPerspective);
                                                           setupSetVisibleMenuItem(menuItemPerspective);
                                                           final PlaceRequest placeRequest = menuItemPerspective.getPlaceRequest();
                                                           if (perspectiveManager.getCurrentPerspective() != null && placeRequest.equals(perspectiveManager.getCurrentPerspective().getPlace())) {
                                                               view.selectMenuItem(id);
                                                           }
                                                       }

                                                       private void setupEnableDisableMenuItem(final MenuItem menuItem) {
                                                           menuItem.addEnabledStateChangeListener(new EnabledStateChangeListener() {
                                                               @Override
                                                               public void enabledStateChanged(final boolean enabled) {
                                                                   view.enableMenuItem(getMenuItemId(menuItem),
                                                                                       enabled);
                                                               }
                                                           });
                                                       }
                                                   }));

        synchronizeUIWithMenus(addedMenu.getItems());
    }

    private String getMenuItemId(final MenuItem menuItem) {
        return menuItem.getIdentifier() == null ? menuItem.getCaption() : menuItem.getIdentifier();
    }

    protected void addPerspectiveMenus(final PerspectiveActivity perspective) {
        final String perspectiveId = perspective.getIdentifier();
        perspective.getMenus(menus -> {
            view.clearContextMenu();
            if (menus != null) {
                menus.accept(new AuthFilterMenuVisitor(authzManager,
                                                       identity,
                                                       new BaseMenuVisitor() {

                                                           private String parentId = null;

                                                           @Override
                                                           public boolean visitEnter(final MenuGroup menuGroup) {
                                                               parentId = getMenuItemId(menuGroup);
                                                               view.addContextGroupMenuItem(perspectiveId,
                                                                                            parentId,
                                                                                            menuGroup.getCaption(),
                                                                                            menuGroup.getPosition());
                                                               return true;
                                                           }

                                                           @Override
                                                           public void visitLeave(MenuGroup menuGroup) {
                                                               parentId = null;
                                                           }

                                                           @Override
                                                           public void visit(final MenuItemPlain menuItemPlain) {
                                                               view.addContextMenuItem(perspectiveId,
                                                                                       getMenuItemId(menuItemPlain),
                                                                                       menuItemPlain.getCaption(),
                                                                                       parentId,
                                                                                       null,
                                                                                       menuItemPlain.getPosition());
                                                               setupEnableDisableContextMenuItem(menuItemPlain);
                                                           }

                                                           @Override
                                                           public void visit(final MenuCustom<?> menuCustom) {
                                                               view.addContextMenuItem(perspectiveId,
                                                                                       getMenuItemId(menuCustom),
                                                                                       menuCustom.getCaption(),
                                                                                       parentId,
                                                                                       null,
                                                                                       menuCustom.getPosition());
                                                               setupEnableDisableContextMenuItem(menuCustom);
                                                           }

                                                           @Override
                                                           public void visit(final MenuItemCommand menuItemCommand) {
                                                               view.addContextMenuItem(perspectiveId,
                                                                                       getMenuItemId(menuItemCommand),
                                                                                       menuItemCommand.getCaption(),
                                                                                       parentId,
                                                                                       menuItemCommand.getCommand(),
                                                                                       menuItemCommand.getPosition());
                                                               setupEnableDisableContextMenuItem(menuItemCommand);
                                                           }

                                                           @Override
                                                           public void visit(final MenuItemPerspective menuItemPerspective) {
                                                               view.addContextMenuItem(perspectiveId,
                                                                                       menuItemPerspective.getPlaceRequest().getIdentifier(),
                                                                                       menuItemPerspective.getCaption(),
                                                                                       parentId,
                                                                                       new Command() {
                                                                                           @Override
                                                                                           public void execute() {
                                                                                               placeManager.goTo(menuItemPerspective.getPlaceRequest());
                                                                                           }
                                                                                       },
                                                                                       menuItemPerspective.getPosition());
                                                               setupEnableDisableContextMenuItem(menuItemPerspective);
                                                               setupSetVisibleContextMenuItem(menuItemPerspective);
                                                           }

                                                           private void setupEnableDisableContextMenuItem(final MenuItem menuItem) {
                                                               menuItem.addEnabledStateChangeListener(new EnabledStateChangeListener() {
                                                                   @Override
                                                                   public void enabledStateChanged(final boolean enabled) {
                                                                       view.enableContextMenuItem(getMenuItemId(menuItem),
                                                                                                  enabled);
                                                                   }
                                                               });
                                                           }
                                                       }));

                synchronizeUIWithMenus(menus.getItems());
            }
        });
    }

    public void onPerspectiveChange(final PerspectiveChange perspectiveChange) {
        final Activity activity = activityManager.getActivity(perspectiveChange.getPlaceRequest());
        if (activity != null && activity.isType(ActivityResourceType.PERSPECTIVE.name())) {
            addPerspectiveMenus((PerspectiveActivity) activity);
        }
        view.selectMenuItem(perspectiveChange.getPlaceRequest().getIdentifier());
    }

    protected void onPlaceMinimized(final PlaceMinimizedEvent event) {
        if (isUseExpandedMode()) {
            view.expand();
        }
    }

    protected void onPlaceMaximized(final PlaceMaximizedEvent event) {
        view.collapse();
    }

    private void setupSetVisibleMenuItem(MenuItemPerspective menuItemPerspective) {
        doSetMenuItemVisible(menuItemPerspective, view::setMenuItemVisible);
    }

    private void setupSetVisibleContextMenuItem(MenuItemPerspective menuItemPerspective) {
        doSetMenuItemVisible(menuItemPerspective, view::setContextMenuItemVisible);
    }

    protected void doSetMenuItemVisible(MenuItemPerspective menuItemPerspective, BiConsumer<String, Boolean> callback) {
        String perspectiveId = menuItemPerspective.getPlaceRequest().getIdentifier();
        boolean visible = experimentalActivitiesAuthorizationManager.authorizeActivityId(perspectiveId);

        callback.accept(perspectiveId, visible);

        registerVisibilityChangeHandler(new MenuItemVisibilityHandler(perspectiveId, callback));
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
    public void addCollapseHandler(final Command command) {
        view.addCollapseHandler(command);
    }

    @Override
    public void addExpandHandler(final Command command) {
        view.addExpandHandler(command);
    }

    public interface View extends WorkbenchBaseMenuView,
                                  IsWidget {

        void clear();

        void addMenuItem(String id,
                         String label,
                         String parentId,
                         Command command,
                         MenuPosition position);

        void addCustomMenuItem(Widget menu,
                               MenuPosition position);

        void addGroupMenuItem(String id,
                              String label,
                              MenuPosition position);

        void selectMenuItem(String id);

        void addContextMenuItem(String menuItemId,
                                String id,
                                String label,
                                String parentId,
                                Command command,
                                MenuPosition position);

        void addContextGroupMenuItem(String menuItemId,
                                     String id,
                                     String label,
                                     MenuPosition position);

        void clearContextMenu();

        void expand();

        void collapse();

        void addCollapseHandler(Command command);

        void addExpandHandler(Command command);

        void enableMenuItem(String menuItemId,
                            boolean enabled);

        void enableContextMenuItem(String menuItemId,
                                   boolean enabled);

        void setAllMenuItemsVisible(String perspectiveId, boolean visible);

        void setMenuItemVisible(String perspectiveId, boolean visible);

        void setContextMenuItemVisible(String perspectiveId, boolean visible);
    }
}
