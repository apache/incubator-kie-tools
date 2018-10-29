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

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuUtils;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.impl.BaseMenuVisitor;

public class WorkbenchMegaMenuContextMenuVisitor extends BaseMenuVisitor {

    private WorkbenchMegaMenuPresenter presenter;

    private PlaceManager placeManager;

    private String perspectiveId;

    private String parentId = null;

    public WorkbenchMegaMenuContextMenuVisitor(final WorkbenchMegaMenuPresenter presenter,
                                               final PlaceManager placeManager,
                                               final String perspectiveId) {
        this.presenter = presenter;
        this.placeManager = placeManager;
        this.perspectiveId = perspectiveId;
    }

    @Override
    public boolean visitEnter(final MenuGroup menuGroup) {
        parentId = WorkbenchBaseMenuUtils.getMenuItemId(menuGroup);
        presenter.addContextGroupMenuItem(perspectiveId,
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
        presenter.addContextMenuItem(perspectiveId,
                                     WorkbenchBaseMenuUtils.getMenuItemId(menuItemPlain),
                                     menuItemPlain.getCaption(),
                                     parentId,
                                     null,
                                     menuItemPlain.getPosition());
        setupEnableDisableContextMenuItem(menuItemPlain);
    }

    @Override
    public void visit(final MenuCustom<?> menuCustom) {
        presenter.addContextMenuItem(perspectiveId,
                                     WorkbenchBaseMenuUtils.getMenuItemId(menuCustom),
                                     menuCustom.getCaption(),
                                     parentId,
                                     null,
                                     menuCustom.getPosition());
        setupEnableDisableContextMenuItem(menuCustom);
    }

    @Override
    public void visit(final MenuItemCommand menuItemCommand) {
        presenter.addContextMenuItem(perspectiveId,
                                     WorkbenchBaseMenuUtils.getMenuItemId(menuItemCommand),
                                     menuItemCommand.getCaption(),
                                     parentId,
                                     menuItemCommand.getCommand(),
                                     menuItemCommand.getPosition());
        setupEnableDisableContextMenuItem(menuItemCommand);
    }

    @Override
    public void visit(final MenuItemPerspective menuItemPerspective) {
        presenter.addContextMenuItem(perspectiveId,
                                     menuItemPerspective.getPlaceRequest().getIdentifier(),
                                     menuItemPerspective.getCaption(),
                                     parentId,
                                     () -> placeManager.goTo(menuItemPerspective.getPlaceRequest()),
                                     menuItemPerspective.getPosition());
        setupEnableDisableContextMenuItem(menuItemPerspective);
        presenter.setupSetVisibleMenuItem(menuItemPerspective);
    }

    private void setupEnableDisableContextMenuItem(final MenuItem menuItem) {
        menuItem.addEnabledStateChangeListener(enabled -> presenter.enableContextMenuItem(WorkbenchBaseMenuUtils.getMenuItemId(menuItem),
                                                                                          enabled));
    }
}
