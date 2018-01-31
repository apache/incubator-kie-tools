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
package org.dashbuilder.client.navigation.widget.editor;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavItemEditCancelledEvent;
import org.dashbuilder.client.navigation.event.NavItemEditStartedEvent;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

@Dependent
public class NavTreeEditor extends NavItemEditor {

    public interface View extends NavItemEditor.View<NavTreeEditor> {

        void setChangedFlag(boolean on);
    }

    public static final NavigationConstants i18n = NavigationConstants.INSTANCE;
    public static final String ROOT_GROUP_ID = "NavTreeEditorRootGroupId";

    NavTreeEditorView view;
    NavigationManager navigationManager;
    NavTree navTree;
    Command onSaveCommand;
    Optional<NavItemEditor> currentlyEditedItem = Optional.empty();
    LoadingBox loadingBox;

    @Inject
    public NavTreeEditor(NavTreeEditorView view,
                         NavigationManager navigationManager,
                         SyncBeanManager beanManager,
                         PlaceManager placeManager,
                         PerspectiveTreeProvider perspectiveTreeProvider,
                         TargetPerspectiveEditor targetPerspectiveEditor,
                         PerspectivePluginManager perspectivePluginManager,
                         Event<NavItemEditStartedEvent> navItemEditStartedEvent,
                         Event<NavItemEditCancelledEvent> navItemEditCancelledEvent,
                         LoadingBox loadingBox) {

        super(view, beanManager,
                placeManager,
                perspectiveTreeProvider,
                targetPerspectiveEditor,
                perspectivePluginManager,
                navItemEditStartedEvent,
                navItemEditCancelledEvent);

        this.view = view;
        this.navigationManager = navigationManager;
        this.loadingBox = loadingBox;
        this.view.init(this);

        super.setChildEditorClass(NavItemDefaultEditor.class);
    }

    public void setOnSaveCommand(Command onSaveCommand) {
        this.onSaveCommand = onSaveCommand;
    }

    @Override
    public String getGroupLiteral() {
        return "Tree";
    }

    public NavTree getNavTree() {
        return navTree;
    }

    public void edit(NavTree navTree) {
        this.navTree = navTree.cloneTree();
        this.currentlyEditedItem = Optional.empty();
        view.setChangedFlag(false);

        NavGroup rootGroup = NavFactory.get().createNavGroup(navTree);
        rootGroup.setId(ROOT_GROUP_ID);

        // Only allow the creation of groups in the first tree level
        getSettings().setNewDividerEnabled(ROOT_GROUP_ID, false);
        getSettings().setNewPerspectiveEnabled(ROOT_GROUP_ID, false);

        super.edit(rootGroup);
        super.expand();
    }

    @Override
    void onItemUpdated() {
        view.setChangedFlag(true);
        currentlyEditedItem = Optional.empty();
        super.onItemUpdated();
    }

    public void newTree() {
        saveDefaultNavTree();
        newGroup();
    }

    // View actions

    void saveDefaultNavTree() {

        final boolean hasNoSavedTree = !navigationManager.hasNavTree();

        if (hasNoSavedTree) {
            showLoading();
            navigationManager.saveNavTree(navigationManager.getNavTree(), this::hideLoading);
        }
    }

    void showLoading() {
        loadingBox.show();
    }

    void hideLoading() {
        loadingBox.hide();
    }

    void onSaveClicked() {
        NavGroup rootGroup = (NavGroup) super.getNavItem();
        NavTree modifiedTree = NavFactory.get().createNavTree(rootGroup);
        navigationManager.saveNavTree(modifiedTree, () -> {
            navTree = modifiedTree;
            view.setChangedFlag(false);
            currentlyEditedItem = Optional.empty();
            if (onSaveCommand != null) {
                onSaveCommand.execute();
            }
        });
    }

    void onCancelClicked() {
        edit(navTree);
    }

    // Keep track of the item under edition so as to avoid editing multiple items simultaneously

    void onItemEditStarted(@Observes NavItemEditStartedEvent event) {
        currentlyEditedItem.filter(e -> !e.equals(event.getNavItemEditor())).ifPresent(NavItemEditor::cancelEdition);
        currentlyEditedItem = Optional.of(event.getNavItemEditor());
    }

    void onItemEditCancelled(@Observes NavItemEditCancelledEvent event) {
        currentlyEditedItem = Optional.empty();
    }

    NavItemEditor getCurrentlyEditedItem() {
        return currentlyEditedItem.isPresent() ? currentlyEditedItem.get() : null;
    }

    // NavItemEditorSettings proxy methods

    public NavItemEditorSettings setMaxLevels(String navItemId, int maxLevels) {
        return getSettings().setMaxLevels(navItemId, maxLevels);
    }

    public NavItemEditorSettings.Flags setNewGroupEnabled(String navItemId, boolean enabled) {
        return getSettings().setNewGroupEnabled(navItemId, enabled);
    }

    public NavItemEditorSettings.Flags setNewPerspectiveEnabled(String navItemId, boolean enabled) {
        return getSettings().setNewPerspectiveEnabled(navItemId, enabled);
    }

    public NavItemEditorSettings.Flags setNewDividerEnabled(String navItemId, boolean enabled) {
        return getSettings().setNewDividerEnabled(navItemId, enabled);
    }

    public NavItemEditorSettings.Flags setOnlyRuntimePerspectives(String navItemId, boolean enabled) {
        return getSettings().setOnlyRuntimePerspectives(navItemId, enabled);
    }

    public NavItemEditorSettings.Flags setPerspectiveContextEnabled(String navItemId, boolean enabled) {
        return getSettings().setPerspectiveContextEnabled(navItemId, enabled);
    }

}
