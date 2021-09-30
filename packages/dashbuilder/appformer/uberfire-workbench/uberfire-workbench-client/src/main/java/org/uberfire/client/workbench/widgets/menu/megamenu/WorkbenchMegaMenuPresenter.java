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

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;

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

    private PerspectiveManager perspectiveManager;
    private ActivityManager activityManager;
    private View view;
    private ManagedInstance<MegaMenuBrand> megaMenuBrands;
    private PlaceManager placeManager;
    private SessionInfo sessionInfo;
    private ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters;
    private ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters;
    private ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters;
    private ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters;
    private Workbench workbench;

    Map<String, Selectable> selectableMenuItemByIdentifier = new HashMap<>();
    Map<String, HasChildren> hasChildrenMenuItemByIdentifier = new HashMap<>();
    Map<String, CanBeDisabled> canBeDisabledMenuItemByIdentifier = new HashMap<>();
    Map<String, CanHide> canHideMenuItemByIdentifier = new HashMap<>();

    public WorkbenchMegaMenuPresenter(PerspectiveManager perspectiveManager,
                                      final ActivityManager activityManager,
                                      final View view,
                                      final ManagedInstance<MegaMenuBrand> megaMenuBrands,
                                      final PlaceManager placeManager,
                                      final ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters,
                                      final ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters,
                                      final ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters,
                                      final ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters,
                                      final Workbench workbench) {
        this.perspectiveManager = perspectiveManager;
        this.activityManager = activityManager;
        this.view = view;
        this.megaMenuBrands = megaMenuBrands;
        this.placeManager = placeManager;
        this.childMenuItemPresenters = childMenuItemPresenters;
        this.groupMenuItemPresenters = groupMenuItemPresenters;
        this.childContextMenuItemPresenters = childContextMenuItemPresenters;
        this.groupContextMenuItemPresenters = groupContextMenuItemPresenters;
        this.workbench = workbench;

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
        addedMenu.accept(new WorkbenchMegaMenuVisitor(this, perspectiveManager, placeManager) {

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
        });

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
        final var childMenuItemPresenter = childMenuItemPresenters.get();
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
        final var groupMenuItemPresenter = groupMenuItemPresenters.get();
        groupMenuItemPresenter.setup(label);

        hasChildrenMenuItemByIdentifier.put(id,
                groupMenuItemPresenter);

        view.addGroupMenuItem(groupMenuItemPresenter);
    }

    public void selectMenuItem(final String id) {
        final var itemPresenter = selectableMenuItemByIdentifier.get(id);
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
        final var groupContextMenuItemPresenter = groupContextMenuItemPresenters.get();
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
                menus.accept(new WorkbenchMegaMenuContextMenuVisitor(this,
                        placeManager,
                        perspectiveId));

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
        final var homePerspectiveActivity = workbench.getHomePerspectiveActivity();
        if (homePerspectiveActivity != null) {
            final var homePerspectiveIdentifier = homePerspectiveActivity.getIdentifier();
            placeManager.goTo(homePerspectiveIdentifier);
        }
    }

    public void setupSetVisibleMenuItem(MenuItemPerspective menuItemPerspective) {
        var perspectiveId = menuItemPerspective.getPlaceRequest().getIdentifier();

        changeMenuItemVisibility(perspectiveId, true);

        registerVisibilityChangeHandler(new MenuItemVisibilityHandler(perspectiveId, this::changeMenuItemVisibility));
    }

    private void changeMenuItemVisibility(String id, boolean visible) {
        var canHide = canHideMenuItemByIdentifier.get(id);
        if (canHide != null) {
            if (visible) {
                canHide.show();
            } else {
                canHide.hide();
            }
        }
    }
}
