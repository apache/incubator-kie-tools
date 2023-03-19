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

package org.uberfire.client.workbench.widgets.menu.megamenu.visitor;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuUtils;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.impl.BaseMenuVisitor;

public class WorkbenchMegaMenuVisitor extends BaseMenuVisitor {

    private WorkbenchMegaMenuPresenter presenter;

    private PerspectiveManager perspectiveManager;

    private PlaceManager placeManager;

    private String parentId = null;

    public WorkbenchMegaMenuVisitor(final WorkbenchMegaMenuPresenter presenter,
                                    final PerspectiveManager perspectiveManager,
                                    final PlaceManager placeManager) {
        this.presenter = presenter;
        this.perspectiveManager = perspectiveManager;
        this.placeManager = placeManager;
    }

    @Override
    public boolean visitEnter(final MenuGroup menuGroup) {
        parentId = WorkbenchBaseMenuUtils.getMenuItemId(menuGroup);
        presenter.addGroupMenuItem(parentId,
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
        presenter.addMenuItem(WorkbenchBaseMenuUtils.getMenuItemId(menuItemPlain),
                              menuItemPlain.getCaption(),
                              parentId,
                              null,
                              menuItemPlain.getPosition());
        setupEnableDisableMenuItem(menuItemPlain);
    }

    @Override
    public void visit(final MenuCustom<?> menuCustom) {
        presenter.addMenuItem(WorkbenchBaseMenuUtils.getMenuItemId(menuCustom),
                              menuCustom.getCaption(),
                              parentId,
                              null,
                              menuCustom.getPosition());

        setupEnableDisableMenuItem(menuCustom);
    }

    @Override
    public void visit(final MenuItemCommand menuItemCommand) {
        presenter.addMenuItem(WorkbenchBaseMenuUtils.getMenuItemId(menuItemCommand),
                              menuItemCommand.getCaption(),
                              parentId,
                              menuItemCommand.getCommand(),
                              menuItemCommand.getPosition());
        setupEnableDisableMenuItem(menuItemCommand);
    }

    @Override
    public void visit(final MenuItemPerspective menuItemPerspective) {
        final String id = menuItemPerspective.getPlaceRequest().getIdentifier();
        presenter.addMenuItem(id,
                              menuItemPerspective.getCaption(),
                              parentId,
                              () -> placeManager.goTo(menuItemPerspective.getPlaceRequest()),
                              menuItemPerspective.getPosition());
        setupEnableDisableMenuItem(menuItemPerspective);
        presenter.setupSetVisibleMenuItem(menuItemPerspective);
        final PlaceRequest placeRequest = menuItemPerspective.getPlaceRequest();
        if (perspectiveManager.getCurrentPerspective() != null && placeRequest.equals(perspectiveManager.getCurrentPerspective().getPlace())) {
            presenter.selectMenuItem(id);
        }
    }

    protected String getParentId() {
        return parentId;
    }

    protected void setParentId(String parentId) {
        this.parentId = parentId;
    }

    protected void setupEnableDisableMenuItem(final MenuItem menuItem) {
        menuItem.addEnabledStateChangeListener(enabled -> presenter.enableMenuItem(WorkbenchBaseMenuUtils.getMenuItemId(menuItem),
                                                                                   enabled));
    }
}
