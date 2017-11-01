/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory.MenuItemViewHolder;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
public class InsertMenuBuilder extends BaseMenu implements MenuFactory.CustomMenuBuilder {

    public interface SupportsAppendRow {

        void onAppendRow();
    }

    public interface SupportsInsertRowAbove {

        void onInsertRowAbove();
    }

    public interface SupportsInsertRowBelow {

        void onInsertRowBelow();
    }

    public interface SupportsInsertColumn {

        void onInsertColumn();
    }

    private TranslationService ts;
    private MenuItemFactory menuItemFactory;
    private GuidedDecisionTableModellerView.Presenter modeller;

    MenuItemViewHolder<MenuItemWithIconView> miAppendRow;
    MenuItemViewHolder<MenuItemWithIconView> miInsertRowAbove;
    MenuItemViewHolder<MenuItemWithIconView> miInsertRowBelow;
    MenuItemViewHolder<MenuItemWithIconView> miInsertColumn;

    @Inject
    public InsertMenuBuilder(final TranslationService ts,
                             final MenuItemFactory menuItemFactory) {
        this.ts = ts;
        this.menuItemFactory = menuItemFactory;
    }

    @PostConstruct
    public void setup() {
        miAppendRow = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.InsertMenu_appendRow),
                                                           this::onAppendRow);

        miInsertRowAbove = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.InsertMenu_insertRowAbove),
                                                                this::onInsertRowAbove);
        miInsertRowBelow = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.InsertMenu_insertRowBelow),
                                                                this::onInsertRowBelow);
        miInsertColumn = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.InsertMenu_insertColumn),
                                                              this::onAppendColumn);
    }

    public void setModeller(final GuidedDecisionTableModellerView.Presenter modeller) {
        this.modeller = modeller;
    }

    @Override
    public void push(final MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return MenuFactory.newTopLevelMenu(ts.getTranslation(GuidedDecisionTableErraiConstants.InsertMenu_title))
                .withItems(getEditMenuItems())
                .endMenu()
                .build()
                .getItems()
                .get(0);
    }

    List<MenuItem> getEditMenuItems() {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(miAppendRow.getMenuItem());
        menuItems.add(miInsertRowAbove.getMenuItem());
        menuItems.add(miInsertRowBelow.getMenuItem());
        menuItems.add(miInsertColumn.getMenuItem());
        return menuItems;
    }

    @Override
    public void onDecisionTableSelectedEvent(final @Observes DecisionTableSelectedEvent event) {
        super.onDecisionTableSelectedEvent(event);
    }

    @Override
    public void onDecisionTableSelectionsChangedEvent(final @Observes DecisionTableSelectionsChangedEvent event) {
        super.onDecisionTableSelectionsChangedEvent(event);
    }

    @Override
    public void initialise() {

        if (this.activeDecisionTable == null || !activeDecisionTable.getAccess().isEditable()) {
            enableMenuItemsForAppendingRows(false);
            enableMenuItemsForAppendingColumns(false);
            enableMenuItemsForInsertingRows(false);
            return;
        }

        final List<GridData.SelectedCell> selections = activeDecisionTable.getView().getModel().getSelectedCells();

        if (selections == null || selections.isEmpty()) {
            enableMenuItemsForAppendingRows(true);
            enableMenuItemsForInsertingRows(false);
            enableMenuItemsForAppendingColumns(activeDecisionTable.hasEditableColumns());
            return;
        }
        final Map<Integer, Boolean> rowUsage = new HashMap<>();
        for (GridData.SelectedCell sc : selections) {
            rowUsage.put(sc.getRowIndex(),
                         true);
        }
        enableMenuItemsForAppendingRows(true);
        enableMenuItemsForAppendingColumns(activeDecisionTable.hasEditableColumns());
        enableMenuItemsForInsertingRows(rowUsage.keySet().size() == 1);
    }

    void onAppendRow() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onAppendRow();
        }
    }

    void onInsertRowAbove() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onInsertRowAbove();
        }
    }

    void onInsertRowBelow() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onInsertRowBelow();
        }
    }

    void onAppendColumn() {
        if (modeller != null) {
            modeller.onInsertColumn();
        }
    }

    private void enableMenuItemsForAppendingRows(final boolean enabled) {
        miAppendRow.getMenuItem().setEnabled(enabled);
    }

    private void enableMenuItemsForAppendingColumns(final boolean enabled) {
        miInsertColumn.getMenuItem().setEnabled(enabled);
    }

    private void enableMenuItemsForInsertingRows(final boolean enabled) {
        miInsertRowAbove.getMenuItem().setEnabled(enabled);
        miInsertRowBelow.getMenuItem().setEnabled(enabled);
    }
}
