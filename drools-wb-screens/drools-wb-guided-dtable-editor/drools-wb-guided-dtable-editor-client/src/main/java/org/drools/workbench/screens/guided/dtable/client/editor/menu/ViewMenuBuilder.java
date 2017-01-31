/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDividerView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory.MenuItemViewHolder;
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
public class ViewMenuBuilder extends BaseMenu implements MenuFactory.CustomMenuBuilder {

    public interface SupportsZoom {

        void setZoom(final int zoomLevel);
    }

    public interface HasMergedView {

        void setMerged(final boolean merged);

        boolean isMerged();
    }

    public interface HasAuditLog {

        void showAuditLog();
    }

    private TranslationService ts;
    private MenuItemFactory menuItemFactory;
    private GuidedDecisionTableModellerView.Presenter modeller;

    MenuItemViewHolder<MenuItemHeaderView> miHeader;
    MenuItemViewHolder<MenuItemWithIconView> miZoom125pct;
    MenuItemViewHolder<MenuItemWithIconView> miZoom100pct;
    MenuItemViewHolder<MenuItemWithIconView> miZoom75pct;
    MenuItemViewHolder<MenuItemWithIconView> miZoom50pct;
    MenuItemViewHolder<MenuItemDividerView> miSeparator;
    MenuItemViewHolder<MenuItemWithIconView> miToggleMergeState;
    MenuItemViewHolder<MenuItemWithIconView> miViewAuditLog;

    @Inject
    public ViewMenuBuilder(final TranslationService ts,
                           final MenuItemFactory menuItemFactory) {
        this.ts = ts;
        this.menuItemFactory = menuItemFactory;
    }

    @PostConstruct
    public void setup() {
        miHeader = menuItemFactory.makeMenuItemHeader(ts.getTranslation(GuidedDecisionTableErraiConstants.ViewMenu_zoom));

        miZoom125pct = menuItemFactory.makeMenuItemWithIcon("125%",
                                                            () -> onZoom(125));
        miZoom100pct = menuItemFactory.makeMenuItemWithIcon("100%",
                                                            () -> onZoom(100));
        miZoom75pct = menuItemFactory.makeMenuItemWithIcon("75%",
                                                           () -> onZoom(75));
        miZoom50pct = menuItemFactory.makeMenuItemWithIcon("50%",
                                                           () -> onZoom(50));
        miSeparator = menuItemFactory.makeMenuItemDivider();
        miToggleMergeState = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.ViewMenu_merged),
                                                                  this::onToggleMergeState);
        miViewAuditLog = menuItemFactory.makeMenuItemWithIcon(ts.getTranslation(GuidedDecisionTableErraiConstants.ViewMenu_auditLog),
                                                              this::onViewAuditLog);

        miZoom125pct.getMenuItemView().setIconType(null);
        miZoom100pct.getMenuItemView().setIconType(IconType.CHECK);
        miZoom75pct.getMenuItemView().setIconType(null);
        miZoom50pct.getMenuItemView().setIconType(null);

        miToggleMergeState.getMenuItem().setEnabled(false);
        miViewAuditLog.getMenuItem().setEnabled(false);
    }

    public void setModeller(final GuidedDecisionTableModellerView.Presenter modeller) {
        this.modeller = modeller;
    }

    @Override
    public void push(final MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return MenuFactory.newTopLevelMenu(ts.getTranslation(GuidedDecisionTableErraiConstants.ViewMenu_title))
                .withItems(getEditMenuItems())
                .endMenu()
                .build()
                .getItems()
                .get(0);
    }

    List<MenuItem> getEditMenuItems() {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(miHeader.getMenuItem());
        menuItems.add(miZoom125pct.getMenuItem());
        menuItems.add(miZoom100pct.getMenuItem());
        menuItems.add(miZoom75pct.getMenuItem());
        menuItems.add(miZoom50pct.getMenuItem());
        menuItems.add(miSeparator.getMenuItem());
        menuItems.add(miToggleMergeState.getMenuItem());
        menuItems.add(miViewAuditLog.getMenuItem());
        return menuItems;
    }

    @Override
    public void onDecisionTableSelectedEvent(final @Observes DecisionTableSelectedEvent event) {
        super.onDecisionTableSelectedEvent(event);
        enableZoomMenu(true);
    }

    @Override
    public void initialise() {
        if (activeDecisionTable == null || !activeDecisionTable.getAccess().isEditable()) {
            miToggleMergeState.getMenuItem().setEnabled(false);
            miToggleMergeState.getMenuItemView().setIconType(null);
            miViewAuditLog.getMenuItem().setEnabled(false);
        } else {
            miToggleMergeState.getMenuItem().setEnabled(true);
            miToggleMergeState.getMenuItemView().setIconType(activeDecisionTable.isMerged() ? IconType.CHECK : null);
            miViewAuditLog.getMenuItem().setEnabled(true);
        }
    }

    public void onDecisionTablePinnedEvent(final @Observes DecisionTablePinnedEvent event) {
        final GuidedDecisionTableModellerView.Presenter modeller = event.getPresenter();
        if (modeller == null) {
            return;
        }
        if (!modeller.equals(this.modeller)) {
            return;
        }
        enableZoomMenu(!event.isPinned());
    }

    private void enableZoomMenu(final boolean enabled) {
        miZoom125pct.getMenuItem().setEnabled(enabled);
        miZoom100pct.getMenuItem().setEnabled(enabled);
        miZoom75pct.getMenuItem().setEnabled(enabled);
        miZoom50pct.getMenuItem().setEnabled(enabled);
    }

    void onZoom(final int zoom) {
        modeller.setZoom(zoom);
        miZoom125pct.getMenuItemView().setIconType(null);
        miZoom100pct.getMenuItemView().setIconType(null);
        miZoom75pct.getMenuItemView().setIconType(null);
        miZoom50pct.getMenuItemView().setIconType(null);
        switch (zoom) {
            case 125:
                miZoom125pct.getMenuItemView().setIconType(IconType.CHECK);
                break;
            case 100:
                miZoom100pct.getMenuItemView().setIconType(IconType.CHECK);
                break;
            case 75:
                miZoom75pct.getMenuItemView().setIconType(IconType.CHECK);
                break;
            case 50:
                miZoom50pct.getMenuItemView().setIconType(IconType.CHECK);
                break;
        }
    }

    void onToggleMergeState() {
        if (activeDecisionTable != null) {
            final boolean newMergeState = !activeDecisionTable.isMerged();
            miToggleMergeState.getMenuItemView().setIconType(newMergeState ? IconType.CHECK : null);
            activeDecisionTable.setMerged(newMergeState);
        }
    }

    void onViewAuditLog() {
        if (activeDecisionTable != null) {
            activeDecisionTable.showAuditLog();
        }
    }
}
