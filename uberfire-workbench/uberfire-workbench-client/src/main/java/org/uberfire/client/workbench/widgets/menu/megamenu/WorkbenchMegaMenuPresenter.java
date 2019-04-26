/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.workbench.widgets.menu.megamenu;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.widgets.menu.MenuItemVisibilityHandler;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuPresenter;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuUtils;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuView;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanBeDisabled;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanHide;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.HasChildren;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.Selectable;
import org.uberfire.client.workbench.widgets.menu.megamenu.brand.MegaMenuBrand;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.visitor.WorkbenchMegaMenuContextMenuVisitor;
import org.uberfire.client.workbench.widgets.menu.megamenu.visitor.WorkbenchMegaMenuVisitor;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

public class WorkbenchMegaMenuPresenter extends WorkbenchBaseMenuPresenter {

    public interface View extends WorkbenchBaseMenuView,
                                  UberElement<WorkbenchMegaMenuPresenter>,
                                  IsElement {

        void clear();

        void clearContextMenu();

        void setHomeLinkAction(Command command);

        void setBrandImageAction(Command command);

        String getDefaultMenuText();

        void setBrandImage(String brandImageUrl);

        void setBrandImageTitle(String brandImageLabel);

        void hideBrand();

        void setMenuAccessorText(String menuAccessorLabel);

        void addMenuItemOnRight(ChildMenuItemPresenter itemPresenter);

        void addMenuItemOnLeft(ChildMenuItemPresenter itemPresenter);

        void addMenuItemOnParent(ChildMenuItemPresenter itemPresenter,
                                 HasChildren parentPresenter);

        void addCustomMenuItem(IsElement menu);

        void addCustomMenuItem(IsWidget menu);

        void addGroupMenuItem(GroupMenuItemPresenter itemPresenter);

        void addContextMenuItem(GroupContextMenuItemPresenter itemPresenter);

        void addContextMenuItem(ChildContextMenuItemPresenter itemPresenter);

        void addContextMenuItemOnParent(ChildContextMenuItemPresenter itemPresenter,
                                        HasChildren parentPresenter);

        void setContextMenuActive(boolean active);
    }

    private AuthorizationManager authzManager;
    private PerspectiveManager perspectiveManager;
    private ActivityManager activityManager;
    private User identity;
    private View view;
    private ManagedInstance<MegaMenuBrand> megaMenuBrands;
    private PlaceManager placeManager;
    private AuthorizationManager authorizationManager;
    private SessionInfo sessionInfo;
    private ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters;
    private ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters;
    private ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters;
    private ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters;
    private Workbench workbench;
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    Map<String, Selectable> selectableMenuItemByIdentifier = new HashMap<>();
    Map<String, HasChildren> hasChildrenMenuItemByIdentifier = new HashMap<>();
    Map<String, CanBeDisabled> canBeDisabledMenuItemByIdentifier = new HashMap<>();
    Map<String, CanHide> canHideMenuItemByIdentifier = new HashMap<>();

    public WorkbenchMegaMenuPresenter(final AuthorizationManager authzManager,
                                      final PerspectiveManager perspectiveManager,
                                      final ActivityManager activityManager,
                                      final User identity,
                                      final View view,
                                      final ManagedInstance<MegaMenuBrand> megaMenuBrands,
                                      final PlaceManager placeManager,
                                      final AuthorizationManager authorizationManager,
                                      final SessionInfo sessionInfo,
                                      final ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters,
                                      final ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters,
                                      final ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters,
                                      final ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters,
                                      final Workbench workbench,
                                      final ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager) {
        this.authzManager = authzManager;
        this.perspectiveManager = perspectiveManager;
        this.activityManager = activityManager;
        this.identity = identity;
        this.view = view;
        this.megaMenuBrands = megaMenuBrands;
        this.placeManager = placeManager;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.childMenuItemPresenters = childMenuItemPresenters;
        this.groupMenuItemPresenters = groupMenuItemPresenters;
        this.childContextMenuItemPresenters = childContextMenuItemPresenters;
        this.groupContextMenuItemPresenters = groupContextMenuItemPresenters;
        this.workbench = workbench;
        this.experimentalActivitiesAuthorizationManager = experimentalActivitiesAuthorizationManager;

        setup();
    }

    void setup() {
        view.init(this);
        setupBrand();
        setupHomeLink();
    }

    public IsElement getView() {
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
                                                   new WorkbenchMegaMenuVisitor(this,
                                                                                perspectiveManager,
                                                                                placeManager) {

                                                       @Override
                                                       public void visit(final MenuCustom<?> menuCustom) {
                                                           final Object build = menuCustom.build();
                                                           if (build instanceof IsElement) {
                                                               addCustomMenuItem((IsElement) build,
                                                                                 menuCustom.getPosition());
                                                           } else if (build instanceof IsWidget) {
                                                               addCustomMenuItem(((IsWidget) build).asWidget(),
                                                                                 menuCustom.getPosition());
                                                           } else {
                                                               addMenuItem(WorkbenchBaseMenuUtils.getMenuItemId(menuCustom),
                                                                           menuCustom.getCaption(),
                                                                           getParentId(),
                                                                           null,
                                                                           menuCustom.getPosition());
                                                           }
                                                           setupEnableDisableMenuItem(menuCustom);
                                                       }
                                                   }));

        synchronizeUIWithMenus(addedMenu.getItems());
    }

    public void clear() {
        view.clear();
    }

    public void addMenuItem(final String id,
                            final String label,
                            final String parentId,
                            final Command command,
                            final MenuPosition position) {
        final ChildMenuItemPresenter childMenuItemPresenter = childMenuItemPresenters.get();
        childMenuItemPresenter.setup(label,
                                     command);
        selectableMenuItemByIdentifier.put(id, childMenuItemPresenter);
        canBeDisabledMenuItemByIdentifier.put(id, childMenuItemPresenter);
        canHideMenuItemByIdentifier.put(id, childMenuItemPresenter);

        if (parentId == null || parentId.isEmpty()) {
            if (MenuPosition.RIGHT.equals(position)) {
                view.addMenuItemOnRight(childMenuItemPresenter);
            } else {
                view.addMenuItemOnLeft(childMenuItemPresenter);
            }
        } else {
            view.addMenuItemOnParent(childMenuItemPresenter,
                                     hasChildrenMenuItemByIdentifier.get(parentId));
        }
    }

    public void addCustomMenuItem(final IsElement menu,
                                  final MenuPosition position) {
        view.addCustomMenuItem(menu);
    }

    public void addCustomMenuItem(IsWidget menu,
                                  MenuPosition position) {
        view.addCustomMenuItem(menu);
    }

    public void addGroupMenuItem(final String id,
                                 final String label,
                                 final MenuPosition position) {
        final GroupMenuItemPresenter groupMenuItemPresenter = groupMenuItemPresenters.get();
        groupMenuItemPresenter.setup(label);

        hasChildrenMenuItemByIdentifier.put(id,
                                            groupMenuItemPresenter);

        view.addGroupMenuItem(groupMenuItemPresenter);
    }

    public void selectMenuItem(final String id) {
        final Selectable itemPresenter = selectableMenuItemByIdentifier.get(id);
        if (itemPresenter != null) {
            itemPresenter.select();
        }
    }

    public void addContextMenuItem(final String menuItemId,
                                   final String id,
                                   final String label,
                                   final String parentId,
                                   final Command command,
                                   final MenuPosition position) {
        final ChildContextMenuItemPresenter childContextMenuItemPresenter = childContextMenuItemPresenters.get();
        childContextMenuItemPresenter.setup(label,
                                            command);
        selectableMenuItemByIdentifier.put(id,
                                           childContextMenuItemPresenter);
        canBeDisabledMenuItemByIdentifier.put(id,
                                              childContextMenuItemPresenter);
        if (parentId == null || parentId.isEmpty()) {
            if (MenuPosition.RIGHT.equals(position)) {
                childContextMenuItemPresenter.pullRight();
            }
            view.addContextMenuItem(childContextMenuItemPresenter);
        } else {
            view.addContextMenuItemOnParent(childContextMenuItemPresenter,
                                            hasChildrenMenuItemByIdentifier.get(parentId));
        }
        view.setContextMenuActive(true);
    }

    public void addContextGroupMenuItem(final String menuItemId,
                                        final String id,
                                        final String label,
                                        final MenuPosition position) {
        final GroupContextMenuItemPresenter groupContextMenuItemPresenter = groupContextMenuItemPresenters.get();
        groupContextMenuItemPresenter.setup(label);

        hasChildrenMenuItemByIdentifier.put(id,
                                            groupContextMenuItemPresenter);
        canBeDisabledMenuItemByIdentifier.put(id,
                                              groupContextMenuItemPresenter);

        if (MenuPosition.RIGHT.equals(position)) {
            groupContextMenuItemPresenter.pullRight();
        }

        view.addContextMenuItem(groupContextMenuItemPresenter);
        view.setContextMenuActive(true);
    }

    public void clearContextMenu() {
        view.clearContextMenu();
    }

    public void enableMenuItem(final String menuItemId,
                               final boolean enabled) {
        final CanBeDisabled menuItem = canBeDisabledMenuItemByIdentifier.get(menuItemId);
        if (menuItem != null) {
            if (enabled) {
                menuItem.enable();
            } else {
                menuItem.disable();
            }
        }
    }

    public void enableContextMenuItem(final String menuItemId,
                                      final boolean enabled) {
        enableMenuItem(menuItemId,
                       enabled);
    }

    protected void addPerspectiveMenus(final PerspectiveActivity perspective) {
        perspective.getMenus(menus -> {
            final String perspectiveId = perspective.getIdentifier();
            view.clearContextMenu();
            if (menus != null) {
                menus.accept(new AuthFilterMenuVisitor(authzManager,
                                                       identity,
                                                       new WorkbenchMegaMenuContextMenuVisitor(this,
                                                                                               placeManager,
                                                                                               perspectiveId)));

                synchronizeUIWithMenus(menus.getItems());
            }
        });
    }

    public void onPerspectiveChange(final PerspectiveChange perspectiveChange) {
        final Activity activity = activityManager.getActivity(perspectiveChange.getPlaceRequest());
        if (activity != null && activity.isType(ActivityResourceType.PERSPECTIVE.name())) {
            addPerspectiveMenus((PerspectiveActivity) activity);
        }
        selectMenuItem(perspectiveChange.getPlaceRequest().getIdentifier());
    }

    private void setupBrand() {
        final String defaultMenuText = view.getDefaultMenuText();

        if (megaMenuBrands.isAmbiguous()) {
            throw new RuntimeException("Multiple implementations of MegaMenuBrand were provided.");
        }

        if (!megaMenuBrands.isUnsatisfied()) {
            final MegaMenuBrand megaMenuBrand = megaMenuBrands.get();

            final String brandImageUrl = megaMenuBrand.brandImageUrl();
            if (brandImageUrl != null && !brandImageUrl.isEmpty()) {
                view.setBrandImage(brandImageUrl);

                final String brandImageLabel = megaMenuBrand.brandImageLabel();
                if (brandImageLabel != null && !brandImageLabel.isEmpty()) {
                    view.setBrandImageTitle(brandImageLabel);
                }
            } else {
                view.hideBrand();
            }

            final String menuAccessorLabel = megaMenuBrand.menuAccessorLabel();
            if (menuAccessorLabel != null && !menuAccessorLabel.isEmpty()) {
                view.setMenuAccessorText(menuAccessorLabel);
            } else {
                view.setMenuAccessorText(defaultMenuText);
            }
        } else {
            view.hideBrand();
            view.setMenuAccessorText(defaultMenuText);
        }
    }

    void setupHomeLink() {
        view.setHomeLinkAction(() -> {
            goToHomePerspective();
        });
        view.setBrandImageAction(() -> {
            goToHomePerspective();
        });
    }

    private void goToHomePerspective() {
        final PerspectiveActivity homePerspectiveActivity = workbench.getHomePerspectiveActivity();
        if (homePerspectiveActivity != null) {
            final String homePerspectiveIdentifier = homePerspectiveActivity.getIdentifier();
            if (hasAccessToPerspective(homePerspectiveIdentifier)) {
                placeManager.goTo(homePerspectiveIdentifier);
            }
        }
    }

    boolean hasAccessToPerspective(final String perspectiveId) {
        ResourceRef resourceRef = new ResourceRef(perspectiveId,
                                                  ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              sessionInfo.getIdentity());
    }

    public void setupSetVisibleMenuItem(MenuItemPerspective menuItemPerspective) {
        String perspectiveId = menuItemPerspective.getPlaceRequest().getIdentifier();
        boolean visible = experimentalActivitiesAuthorizationManager.authorizeActivityId(perspectiveId);

        changeMenuItemVisibility(perspectiveId, visible);

        registerVisibilityChangeHandler(new MenuItemVisibilityHandler(perspectiveId, this::changeMenuItemVisibility));
    }

    private void changeMenuItemVisibility(String id, boolean visible) {
        CanHide canHide = canHideMenuItemByIdentifier.get(id);
        if (canHide != null) {
            if (visible) {
                canHide.show();
            } else {
                canHide.hide();
            }
        }
    }
}
