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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMenusEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory.MenuItemViewHolder;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
public class EditMenuBuilder extends BaseMenu implements MenuFactory.CustomMenuBuilder {

    public interface SupportsEditMenu {

        void onCut();

        void onCopy();

        void onPaste();

        void onDeleteSelectedCells();

        void onDeleteSelectedColumns();

        void onDeleteSelectedRows();

        void onOtherwiseCell();
    }

    private Clipboard clipboard;
    private TranslationService ts;
    private MenuItemFactory menuItemFactory;
    private DecisionTablePopoverUtils popoverUtils;

    MenuItemViewHolder<MenuItemWithIconView> miCut;
    MenuItemViewHolder<MenuItemWithIconView> miCopy;
    MenuItemViewHolder<MenuItemWithIconView> miPaste;
    MenuItemViewHolder<MenuItemWithIconView> miDeleteSelectedCells;
    MenuItemViewHolder<MenuItemWithIconView> miDeleteSelectedColumns;
    MenuItemViewHolder<MenuItemWithIconView> miDeleteSelectedRows;
    MenuItemViewHolder<MenuItemWithIconView> miOtherwiseCell;

    @Inject
    public EditMenuBuilder(final Clipboard clipboard,
                           final TranslationService ts,
                           final MenuItemFactory menuItemFactory,
                           final DecisionTablePopoverUtils popoverUtils) {
        this.clipboard = clipboard;
        this.ts = ts;
        this.menuItemFactory = menuItemFactory;
        this.popoverUtils = popoverUtils;
    }

    @PostConstruct
    public void setup() {
        miCut = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_cut),
                                                     this::onCut);
        miCopy = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_copy),
                                                      this::onCopy);
        miPaste = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_paste),
                                                       this::onPaste);
        miDeleteSelectedCells = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_deleteCells),
                                                                     this::onDeleteSelectedCells);
        miDeleteSelectedColumns = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_deleteColumns),
                                                                       this::onDeleteSelectedColumns);
        miDeleteSelectedRows = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_deleteRows),
                                                                    this::onDeleteSelectedRows);
        miOtherwiseCell = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_otherwise),
                                                               this::onOtherwiseCell);

        setupOtherwisePopover();
    }

    private void setupOtherwisePopover() {
        miOtherwiseCell.getMenuItemView().getElement().setAttribute("data-toggle",
                                                                    "popover");
        popoverUtils.setupPopover(miOtherwiseCell.getMenuItemView().getElement(),
                                  ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_otherwiseDescription));
    }

    @Override
    public void push(final MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return MenuFactory.newTopLevelMenu(ts.getTranslation(GuidedDecisionTableErraiConstants.EditMenu_title))
                .withItems(getEditMenuItems())
                .endMenu()
                .build()
                .getItems()
                .get(0);
    }

    List<MenuItem> getEditMenuItems() {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(miCut.getMenuItem());
        menuItems.add(miCopy.getMenuItem());
        menuItems.add(miPaste.getMenuItem());
        menuItems.add(miDeleteSelectedCells.getMenuItem());
        menuItems.add(miDeleteSelectedColumns.getMenuItem());
        menuItems.add(miDeleteSelectedRows.getMenuItem());
        menuItems.add(miOtherwiseCell.getMenuItem());
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
    public void onRefreshMenusEvent(final @Observes RefreshMenusEvent event) {
        super.onRefreshMenusEvent(event);
    }

    @Override
    public void initialise() {
        if (activeDecisionTable == null || !activeDecisionTable.getAccess().isEditable()) {
            disableMenuItems();
            return;
        }
        final List<GridData.SelectedCell> selections = activeDecisionTable.getView().getModel().getSelectedCells();
        if (selections == null || selections.isEmpty()) {
            disableMenuItems();
            return;
        }
        enableMenuItems(selections);
        setupOtherwiseCellEntry(selections);
    }

    void onCut() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onCut();
        }
    }

    void onCopy() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onCopy();
        }
    }

    void onPaste() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onPaste();
        }
    }

    void onDeleteSelectedCells() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onDeleteSelectedCells();
        }
    }

    void onDeleteSelectedColumns() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onDeleteSelectedColumns();
        }
    }

    void onDeleteSelectedRows() {
        if (activeDecisionTable != null) {
            activeDecisionTable.onDeleteSelectedRows();
        }
    }

    void onOtherwiseCell() {
        if (activeDecisionTable != null) {
            miOtherwiseCell.getMenuItemView().setIconType(IconType.CHECK);
            activeDecisionTable.onOtherwiseCell();
        }
    }

    private void disableMenuItems() {
        miCut.getMenuItem().setEnabled(false);
        miCopy.getMenuItem().setEnabled(false);
        miPaste.getMenuItem().setEnabled(false);
        miDeleteSelectedCells.getMenuItem().setEnabled(false);
        miDeleteSelectedColumns.getMenuItem().setEnabled(false);
        miDeleteSelectedRows.getMenuItem().setEnabled(false);
        miOtherwiseCell.getMenuItem().setEnabled(false);
        popoverUtils.enableOtherwisePopover(miOtherwiseCell.getMenuItemView().getElement(),
                                            false);
    }

    private void enableMenuItems(final List<GridData.SelectedCell> selections) {
        final boolean enabled = selections.size() > 0;
        final boolean isOtherwiseEnabled = isOtherwiseEnabled(selections);
        final boolean isColumnDeletable = isColumnDeletable(selections);

        miCut.getMenuItem().setEnabled(enabled);
        miCopy.getMenuItem().setEnabled(enabled);
        miPaste.getMenuItem().setEnabled(clipboard.hasData());
        miDeleteSelectedCells.getMenuItem().setEnabled(enabled);
        miDeleteSelectedColumns.getMenuItem().setEnabled(isColumnDeletable);
        miDeleteSelectedRows.getMenuItem().setEnabled(enabled);
        miOtherwiseCell.getMenuItem().setEnabled(isOtherwiseEnabled);
        popoverUtils.enableOtherwisePopover(miOtherwiseCell.getMenuItemView().getElement(),
                                            isOtherwiseEnabled);
    }

    private boolean isColumnDeletable(final List<GridData.SelectedCell> selections) {

        final boolean enabled = selections.size() > 0;
        final boolean isNotOnlyMandatoryColumnSelected = !isOnlyMandatoryColumnSelected(selections);
        final boolean guidedDecisionTableHasEditableColumns = activeDecisionTable.hasEditableColumns();

        return enabled && isNotOnlyMandatoryColumnSelected && guidedDecisionTableHasEditableColumns;
    }

    private void setupOtherwiseCellEntry(final List<GridData.SelectedCell> selections) {
        if (selections.size() != 1) {
            miOtherwiseCell.getMenuItemView().setIconType(null);
            return;
        }
        final GridData.SelectedCell selection = selections.get(0);
        final int rowIndex = selection.getRowIndex();
        final int columnIndex = findUiColumnIndex(selection.getColumnIndex());
        final boolean isOtherwiseCell = activeDecisionTable.getModel().getData().get(rowIndex).get(columnIndex).isOtherwise();
        miOtherwiseCell.getMenuItemView().setIconType(isOtherwiseCell ? IconType.CHECK : null);
    }

    //Check whether the "otherwise" menu item can be enabled
    private boolean isOtherwiseEnabled(final List<GridData.SelectedCell> selections) {
        if (selections.size() != 1) {
            return false;
        }
        boolean isOtherwiseEnabled = true;
        final GridData.SelectedCell selection = selections.get(0);
        final int columnIndex = findUiColumnIndex(selection.getColumnIndex());
        final BaseColumn column = activeDecisionTable.getModel().getExpandedColumns().get(columnIndex);
        isOtherwiseEnabled = isOtherwiseEnabled && ColumnUtilities.canAcceptOtherwiseValues(column);
        return isOtherwiseEnabled;
    }

    //Check whether column selection is only RowNumberColumn or DescriptionColumn. These cannot be deleted.
    private boolean isOnlyMandatoryColumnSelected(final List<GridData.SelectedCell> selections) {
        boolean isOnlyMandatoryColumnSelected = true;
        for (GridData.SelectedCell sc : selections) {
            final int columnIndex = findUiColumnIndex(sc.getColumnIndex());
            final BaseColumn column = activeDecisionTable.getModel().getExpandedColumns().get(columnIndex);
            if (!((column instanceof RowNumberCol52) || (column instanceof DescriptionCol52)) || (column instanceof RuleNameColumn)) {
                isOnlyMandatoryColumnSelected = false;
            }
        }
        return isOnlyMandatoryColumnSelected;
    }

    private int findUiColumnIndex(final int modelColumnIndex) {
        final List<GridColumn<?>> columns = activeDecisionTable.getView().getModel().getColumns();
        for (int uiColumnIndex = 0; uiColumnIndex < columns.size(); uiColumnIndex++) {
            final GridColumn<?> c = columns.get(uiColumnIndex);
            if (c.getIndex() == modelColumnIndex) {
                return uiColumnIndex;
            }
        }
        throw new IllegalStateException("Column was not found!");
    }
}
