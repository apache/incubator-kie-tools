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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;

import com.google.gwt.core.client.GWT;
import org.dashbuilder.client.navigation.event.NavItemEditCancelledEvent;
import org.dashbuilder.client.navigation.event.NavItemEditStartedEvent;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.ActivityResourceType;

public abstract class NavItemEditor implements IsElement {

    public enum ItemType {
        DIVIDER,
        GROUP,
        PERSPECTIVE,
        RUNTIME_PERSPECTIVE;
    }

    public interface View<P extends NavItemEditor> extends UberElement<P> {

        void clearChildren();

        void addChild(IsElement editor);

        void setExpandEnabled(boolean enabled);

        void setExpanded(boolean expanded);

        void setItemName(String name);

        String getItemName();

        void setItemNameError(boolean hasError);

        void setItemDescription(String description);

        void setItemType(ItemType type);

        void addCommand(String name, Command command);

        void addCommandDivider();

        void setCommandsEnabled(boolean enabled);

        void clearCommands();

        void setItemEditable(boolean editable);

        void setItemDeletable(boolean deletable);

        void startItemEdition();

        void finishItemEdition();

        void setContextWidget(IsElement widget);

        String i18nNewItem(String item);

        String i18nNewItemName(String item);

        String i18nGotoItem(String item);

        String i18nDeleteItem();

        String i18nMoveUp();

        String i18nMoveDown();

        String i18nMoveFirst();

        String i18nMoveLast();

        String generateId();
    }

    View view;
    SyncBeanManager beanManager;
    PlaceManager placeManager;
    PerspectiveTreeProvider perspectiveTreeProvider;
    TargetPerspectiveEditor targetPerspectiveEditor;
    PerspectivePluginManager perspectivePluginManager;
    Event<NavItemEditStartedEvent> navItemEditStartedEvent;
    Event<NavItemEditCancelledEvent> navItemEditCancelledEvent;

    boolean creationEnabled = false;

    boolean moveUpEnabled = true;
    boolean moveDownEnabled = true;
    boolean editEnabled = false;
    boolean deleteEnabled = false;
    boolean itemNameFromPerspective = false;

    private NavItemEditorSettings settings;
    private Class<? extends NavItemEditor> childEditorClass;

    NavItemEditor parentEditor = null;
    List<NavItemEditor> childEditorList = new ArrayList<>();
    NavItem navItem = null;
    NavItem navItemBackup = null;
    ItemType itemType = null;
    String perspectiveId = null;
    boolean expanded;

    Command onUpdateCommand;
    Command onCancelCommand;
    Command onMoveUpCommand;
    Command onMoveDownCommand;
    Command onMoveFirstCommand;
    Command onMoveLastCommand;
    Command onDeleteCommand;
    Command onExpandCommand;

    public static final NavigationConstants i18n = NavigationConstants.INSTANCE;

    public NavItemEditor(View view,
                         SyncBeanManager beanManager,
                         PlaceManager placeManager,
                         PerspectiveTreeProvider perspectiveTreeProvider,
                         TargetPerspectiveEditor targetPerspectiveEditor,
                         PerspectivePluginManager perspectivePluginManager,
                         Event<NavItemEditStartedEvent> navItemEditStartedEvent,
                         Event<NavItemEditCancelledEvent> navItemEditCancelledEvent) {
        this.beanManager = beanManager;
        this.placeManager = placeManager;
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.targetPerspectiveEditor = targetPerspectiveEditor;
        this.targetPerspectiveEditor.setOnUpdateCommand(this::onTargetPerspectiveUpdated);
        this.perspectivePluginManager = perspectivePluginManager;
        this.navItemEditStartedEvent = navItemEditStartedEvent;
        this.navItemEditCancelledEvent = navItemEditCancelledEvent;

        this.view = view;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public NavItemEditorSettings getSettings() {
        if (settings == null) {
            settings = new NavItemEditorSettings();
        }
        return settings;
    }

    public void setSettings(NavItemEditorSettings settings) {
        this.settings = settings;
    }

    public Class<? extends NavItemEditor> getChildEditorClass() {
        return childEditorClass;
    }

    public void setChildEditorClass(Class<? extends NavItemEditor> childEditorClass) {
        this.childEditorClass = childEditorClass;
    }

    public NavItemEditor getParentEditor() {
        return parentEditor;
    }

    public void setParentEditor(NavItemEditor parentEditor) {
        this.parentEditor = parentEditor;
        NavItemEditor rootEditor = getRootEditor();
        if (rootEditor != null && rootEditor.getNavItem() instanceof NavGroup) {
            NavGroup rootGroup = (NavGroup) rootEditor.getNavItem();
            targetPerspectiveEditor.setNavItemList(rootGroup.getChildren());
        }
    }

    public NavItemEditor getRootEditor() {
        return parentEditor == null ? this : parentEditor.getRootEditor();
    }

    public int getLevel() {
        if (parentEditor == null) {
            return 0;
        }
        return parentEditor.getLevel() + 1;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public String getGroupLiteral() {
        return settings != null ? settings.getLiteralGroup() : "Group";
    }

    public String getNewPerspectiveI18n() {
        String newItemName = view.i18nNewItemName(getPerspectiveLiteral());
        return newItemName != null ? newItemName : "- New Perspective -";
    }

    public String getPerspectiveLiteral() {
        return settings != null ? settings.getLiteralPerspective() : "Perspective";
    }

    public String getDividerLiteral() {
        return settings != null ? settings.getLiteralDivider() : "Divider";
    }

    public boolean isNewGroupEnabled() {
        return (settings == null || settings.isNewGroupEnabled(navItem)) && areSubgroupsAllowed();
    }

    public boolean isNewPerspectiveEnabled() {
        return (settings == null || settings.isNewPerspectiveEnabled(navItem))
                && areChildrenAllowed()
                && (!getPerspectiveIds(navItem, true).isEmpty());
    }

    public boolean isNewDividerEnabled() {
        return (settings == null || settings.isNewDividerEnabled(navItem)) && areChildrenAllowed();
    }

    public boolean isGotoPerspectiveEnabled() {
        return (settings == null || settings.isGotoPerspectiveEnabled(navItem)) && perspectiveId != null;
    }

    public boolean isMoveUpEnabled() {
        return moveUpEnabled;
    }

    public void setMoveUpEnabled(boolean moveUpEnabled) {
        this.moveUpEnabled = moveUpEnabled;
    }

    public boolean isMoveDownEnabled() {
        return moveDownEnabled;
    }

    public void setMoveDownEnabled(boolean moveDownEnabled) {
        this.moveDownEnabled = moveDownEnabled;
    }

    public void setOnUpdateCommand(Command onUpdateCommand) {
        this.onUpdateCommand = onUpdateCommand;
    }

    public void setOnCancelCommand(Command onCancelCommand) {
        this.onCancelCommand = onCancelCommand;
    }

    public void setOnMoveFirstCommand(Command onMoveFirstCommand) {
        this.onMoveFirstCommand = onMoveFirstCommand;
    }

    public void setOnMoveLastCommand(Command onMoveLastCommand) {
        this.onMoveLastCommand = onMoveLastCommand;
    }

    public void setOnMoveUpCommand(Command onMoveUpCommand) {
        this.onMoveUpCommand = onMoveUpCommand;
    }

    public void setOnMoveDownCommand(Command onMoveDownCommand) {
        this.onMoveDownCommand = onMoveDownCommand;
    }

    public void setOnDeleteCommand(Command onDeleteCommand) {
        this.onDeleteCommand = onDeleteCommand;
    }

    public void setOnExpandCommand(Command onExpandCommand) {
        this.onExpandCommand = onExpandCommand;
    }

    public NavItem getNavItem() {
        return navItem;
    }

    public void edit(NavItem navItem) {
        this.clear();
        this.navItem = navItem.cloneItem();
        this.navItemBackup = navItem;
        this.doEdit();
    }

    public boolean canBeExpanded() {
        return ItemType.GROUP.equals(itemType) && !childEditorList.isEmpty();
    }

    public void expand() {
        if (!expanded && canBeExpanded()) {
            expanded = true;
            view.setExpanded(expanded);
        }
    }

    public void collapse() {
        if (expanded && canBeExpanded()) {
            expanded = false;
            view.setExpanded(expanded);
        }
    }

    public void expandAll() {
        this.expand();
        childEditorList.forEach(NavItemEditor::expandAll);
    }

    public void collapseAll() {
        this.collapse();
        childEditorList.forEach(NavItemEditor::collapseAll);
    }

    public void expandOrCollapse() {
        if (canBeExpanded()) {
            if (expanded) {
                collapse();
            } else {
                expand();
            }
            if (onExpandCommand != null) {
                onExpandCommand.execute();
            }
        }
    }

    public void startEdition() {
        if (editEnabled) {
            if (itemNameFromPerspective) {
                String perspectiveName = targetPerspectiveEditor.getPerspectiveName(perspectiveId);
                view.setItemName(perspectiveName);
            }
            if (settings == null || settings.isPerspectiveContextEnabled(navItem)) {
                perspectivePluginManager.getLayoutTemplateInfo(perspectiveId, info -> {
                    targetPerspectiveEditor.setNavGroupEnabled(info != null && info.hasNavigationComponents());
                    view.startItemEdition();
                    navItemEditStartedEvent.fire(new NavItemEditStartedEvent(this));
                });
            } else {
                targetPerspectiveEditor.setNavGroupEnabled(false);
                view.startItemEdition();
                navItemEditStartedEvent.fire(new NavItemEditStartedEvent(this));
            }
        }
    }

    public void finishEdition() {
        view.finishItemEdition();
        childEditorList.forEach(NavItemEditor::finishEdition);
    }

    public void cancelEdition() {
        view.finishItemEdition();
        edit(navItemBackup);
        onEditCancelled();
    }

    private void doEdit() {
        NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
        if (navItem.getName() != null) {
            view.setItemName(navItem.getName());
            String newPerspectiveI18n = getNewPerspectiveI18n();
            itemNameFromPerspective = newPerspectiveI18n.equals(navItem.getName());
        }

        if (navItem.getDescription() != null) {
            view.setItemDescription(navItem.getDescription());
        }

        creationEnabled = false;
        editEnabled = navItem.isModifiable();
        deleteEnabled = navItem.isModifiable();

        // Nav group
        if (navItem instanceof NavGroup) {
            view.setItemType(itemType = ItemType.GROUP);
            view.setExpandEnabled(false);
            if (areChildrenAllowed()) {
                view.setExpanded(expanded);
                creationEnabled = true;
                NavGroup navGroup = (NavGroup) navItem;
                registerChildren(navGroup);
            }
        }
        // Divider
        else if (navItem instanceof NavDivider) {
            view.setItemType(itemType = ItemType.DIVIDER);
            view.setItemName("--------------");
            editEnabled = false;
        }
        else {
            if (navCtx.getResourceId() != null) {

                // Nav perspective item
                if (ActivityResourceType.PERSPECTIVE.equals(navCtx.getResourceType())) {

                    Set<String> visiblePerspectiveIds = getPerspectiveIds(navItem, true);
                    if (visiblePerspectiveIds.isEmpty() || visiblePerspectiveIds.contains(navCtx.getResourceId())) {
                        perspectiveId = navCtx.getResourceId();
                    } else if (!visiblePerspectiveIds.isEmpty()) {
                        perspectiveId = visiblePerspectiveIds.iterator().next();
                        navCtx.setResourceId(perspectiveId);
                        navItem.setContext(navCtx.toString());
                    }

                    Set<String> hiddenPerspectiveIds = getPerspectiveIds(navItem, false);
                    boolean isRuntimePerspective = perspectivePluginManager.isRuntimePerspective(perspectiveId);
                    String selectedNavGroupId = navCtx.getNavGroupId();
                    targetPerspectiveEditor.clear();
                    targetPerspectiveEditor.setPerspectiveId(perspectiveId);
                    targetPerspectiveEditor.setNavGroupId(selectedNavGroupId);
                    targetPerspectiveEditor.setPerspectiveIdsExcluded(hiddenPerspectiveIds);
                    targetPerspectiveEditor.show();

                    view.setItemType(itemType = isRuntimePerspective ? ItemType.RUNTIME_PERSPECTIVE : ItemType.PERSPECTIVE);
                    view.setContextWidget(targetPerspectiveEditor);
                }
            } else {
                // Ignore non supported items
            }
        }

        view.setItemEditable(editEnabled);
        view.setItemDeletable(deleteEnabled);
        addCommands();
    }

    public void clear() {
        navItem = null;
        perspectiveId = null;
        view.clearChildren();
        view.clearCommands();
    }

    public boolean areChildrenAllowed() {
        return areChildrenAllowed(0, this);
    }

    public boolean areSubgroupsAllowed() {
        return areSubgroupsAllowed(0, this);
    }

    private boolean areChildrenAllowed(int levels, NavItemEditor editor) {
        if (editor == null) {
            return settings == null || settings.getMaxLevels() < 0 || levels < settings.getMaxLevels();
        } else {
            int itemMaxLevels = settings != null ? settings.getMaxLevels(editor.getNavItem().getId()) : -1;
            return itemMaxLevels != -1 ? levels < itemMaxLevels : areChildrenAllowed(levels+1, editor.getParentEditor());
        }
    }

    private boolean areSubgroupsAllowed(int levels, NavItemEditor editor) {
        if (editor == null) {
            return settings == null || settings.getMaxLevels() < 0 || levels < settings.getMaxLevels() - 1;
        } else {
            int itemMaxLevels = settings != null ? settings.getMaxLevels(editor.getNavItem().getId()) - 1 : -1;
            return itemMaxLevels > -1 ? levels < itemMaxLevels : areSubgroupsAllowed(levels+1, editor.getParentEditor());
        }
    }

    public NavItemEditor createChildEditor(NavItem navItem) {
        Class<? extends NavItemEditor> childEditorClass = getChildEditorClass();
        NavItemEditor navItemEditor = beanManager.lookupBean(childEditorClass).newInstance();
        navItemEditor.setParentEditor(this);
        navItemEditor.setSettings(settings);
        navItemEditor.setOnUpdateCommand(() -> onUpdateChild(navItem, navItemEditor));
        navItemEditor.setOnCancelCommand(() -> onCancelChild(navItem, navItemEditor));
        navItemEditor.setOnDeleteCommand(() -> onDeleteChild(navItemEditor));
        navItemEditor.setOnMoveFirstCommand(() -> onMoveFirstChild(navItemEditor));
        navItemEditor.setOnMoveLastCommand(() -> onMoveLastChild(navItemEditor));
        navItemEditor.setOnMoveUpCommand(() -> onMoveUpChild(navItemEditor));
        navItemEditor.setOnMoveDownCommand(() -> onMoveDownChild(navItemEditor));
        return navItemEditor;
    }

    public Set<String> getPerspectiveIds(NavItem navItem, boolean visible) {
        boolean onlyRuntime = settings == null || settings.onlyRuntimePerspectives(navItem);
        Set<String> runtimeIds = getRuntimePerspectiveIds();
        Set<String> hardCodedIds = getHardCodedPerspectiveIds();
        Set<String> excludedIds = perspectiveTreeProvider.getPerspectiveIdsExcluded();

        if (visible) {
            if (!onlyRuntime) {
                runtimeIds.addAll(hardCodedIds);
            }
            return runtimeIds.stream()
                    .filter(id -> !excludedIds.contains(id))
                    .collect(Collectors.toSet());
        } else {
            if (onlyRuntime) {
                hardCodedIds.addAll(excludedIds);
                return hardCodedIds;
            }
            return excludedIds;
        }
    }

    private Set<String> getRuntimePerspectiveIds() {
        Set<String> result = new HashSet<>();
        perspectivePluginManager.getPerspectivePlugins(plugins -> {
            plugins.forEach(p -> {
                String perspectiveId = p.getName();
                result.add(perspectiveId);
            });
        });
        return result;
    }

    private Set<String> getHardCodedPerspectiveIds() {
        Set<String> result = new HashSet<>();
        Collection<SyncBeanDef<AbstractWorkbenchPerspectiveActivity>> beanDefs =  beanManager.lookupBeans(AbstractWorkbenchPerspectiveActivity.class);
        beanDefs.forEach(beanDef -> {
            AbstractWorkbenchPerspectiveActivity bean = beanDef.getInstance();
            String perspectiveId = bean.getIdentifier();
            result.add(perspectiveId);
            beanManager.destroyBean(bean);
        });
        return result;
    }


    // Item commands

    private void addCommands() {
        boolean dividerRequired = false;

        if (creationEnabled) {
            boolean newGroupEnabled = isNewGroupEnabled();
            if (newGroupEnabled) {
                this.addCommand(view.i18nNewItem(getGroupLiteral()), this::newGroup);
                dividerRequired = true;
            }
            boolean newDividerEnabled = isNewDividerEnabled();
            if (newDividerEnabled) {
                this.addCommand(view.i18nNewItem(getDividerLiteral()), this::newDivider);
                dividerRequired = true;
            }
            boolean newPerspectiveEnabled = isNewPerspectiveEnabled();
            if (newPerspectiveEnabled) {
                this.addCommand(view.i18nNewItem(getPerspectiveLiteral()), this::newPerspective);
                dividerRequired = true;
            }
        }

        if (moveUpEnabled || moveDownEnabled) {
            if (dividerRequired) {
                view.addCommandDivider();
            }
            dividerRequired = true;
            if (moveUpEnabled) {
                this.addCommand(view.i18nMoveFirst(), this::moveFirstCommand);
                this.addCommand(view.i18nMoveUp(), this::moveUpCommand);
            }
            if (moveDownEnabled) {
                this.addCommand(view.i18nMoveDown(), this::moveDownCommand);
                this.addCommand(view.i18nMoveLast(), this::moveLastCommand);
            }
        }
        if (isGotoPerspectiveEnabled()) {
            if (dividerRequired) {
                view.addCommandDivider();
            }
            dividerRequired = true;
            this.addCommand(view.i18nGotoItem(getPerspectiveLiteral()), this::gotoPerspectiveCommand);
        }
    }

    private void addCommand(String name, Command action) {
        view.addCommand(name, action);
        view.setCommandsEnabled(true);
    }

    private void refreshCommands() {
        view.clearCommands();
        this.addCommands();
    }

    public NavItemEditor newGroup() {
        NavGroup navGroup = (NavGroup) navItem;
        String id = "group_" + view.generateId();
        String name = i18n.newItemName(getGroupLiteral());

        NavGroup newItem = NavFactory.get().createNavGroup();
        newItem.setParent(navGroup);
        newItem.setId(id);
        newItem.setName(name);
        newItem.setModifiable(true);

        NavItemEditor childEditor = registerChild(newItem);
        childEditor.startEdition();
        this.expand();
        return childEditor;
    }

    public NavItemEditor newPerspective() {
        NavGroup navGroup = (NavGroup) navItem;
        String id = "perspective_" + view.generateId();
        String name = i18n.newItemName(getPerspectiveLiteral());
        NavItem newItem = NavFactory.get().createNavItem();
        newItem.setParent(navGroup);
        newItem.setId(id);
        newItem.setName(name);
        newItem.setModifiable(true);

        Set<String> visiblePerspectiveIds = getPerspectiveIds(navGroup, true);
        if (!visiblePerspectiveIds.isEmpty()) {
            String firstPerspective = visiblePerspectiveIds.iterator().next();
            newItem.setContext(NavWorkbenchCtx.perspective(firstPerspective).toString());
        }

        NavItemEditor childEditor = registerChild(newItem);
        childEditor.startEdition();
        this.expand();
        return childEditor;
    }

    public NavItemEditor newDivider() {
        NavGroup navGroup = (NavGroup) navItem;
        String id = "divider_" + view.generateId();
        NavDivider newItem = NavFactory.get().createDivider();
        newItem.setId(id);
        newItem.setParent(navGroup);
        newItem.setModifiable(true);

        NavItemEditor childEditor = registerChild(newItem);
        this.onUpdateChild(newItem, childEditor);
        this.expand();
        return childEditor;
    }

    void gotoPerspectiveCommand() {
        if (perspectiveId != null) {
            placeManager.goTo(perspectiveId);
        }
    }

    void deleteItemCommand() {
        if (deleteEnabled && onDeleteCommand != null) {
            onDeleteCommand.execute();
        }
    }

    void moveUpCommand() {
        if (onMoveUpCommand != null) {
            onMoveUpCommand.execute();
        }
    }

    void moveDownCommand() {
        if (onMoveDownCommand != null) {
            onMoveDownCommand.execute();
        }
    }

    void moveFirstCommand() {
        if (onMoveFirstCommand != null) {
            onMoveFirstCommand.execute();
        }
    }

    void moveLastCommand() {
        if (onMoveLastCommand != null) {
            onMoveLastCommand.execute();
        }
    }

    // Children callbacks

    void onItemUpdated() {
        navItemBackup = navItem.cloneItem();
        if (onUpdateCommand != null) {
            onUpdateCommand.execute();
        }
    }

    void onEditCancelled() {
        if (onCancelCommand != null) {
            onCancelCommand.execute();
        }
    }

    void onMoveUpChild(NavItemEditor navItemEditor) {
        if (childEditorList.size() > 1) {
            int idx = childEditorList.indexOf(navItemEditor);
            if (idx > 0 && idx < childEditorList.size()) {
                childEditorList.remove(idx);
                childEditorList.add(idx-1, navItemEditor);
                refreshChildren();

                NavGroup navGroup = (NavGroup) navItem;
                navGroup.getChildren().remove(idx);
                navGroup.getChildren().add(idx-1, navItemEditor.getNavItem());
                onItemUpdated();
            }
        }
    }

    void onMoveDownChild(NavItemEditor navItemEditor) {
        if (childEditorList.size() > 1) {
            int idx = childEditorList.indexOf(navItemEditor);
            if (idx > -1 && idx < childEditorList.size()-1) {
                childEditorList.remove(idx);
                childEditorList.add(idx+1, navItemEditor);
                refreshChildren();

                NavGroup navGroup = (NavGroup) navItem;
                navGroup.getChildren().remove(idx);
                navGroup.getChildren().add(idx+1, navItemEditor.getNavItem());
                onItemUpdated();
            }
        }
    }

    void onMoveFirstChild(NavItemEditor navItemEditor) {
        if (childEditorList.size() > 1) {
            int idx = childEditorList.indexOf(navItemEditor);
            if (idx > 0 && idx < childEditorList.size()) {
                childEditorList.remove(idx);
                childEditorList.add(0, navItemEditor);
                refreshChildren();

                NavGroup navGroup = (NavGroup) navItem;
                navGroup.getChildren().remove(idx);
                navGroup.getChildren().add(0, navItemEditor.getNavItem());
                onItemUpdated();
            }
        }
    }

    void onMoveLastChild(NavItemEditor navItemEditor) {
        if (childEditorList.size() > 1) {
            int idx = childEditorList.indexOf(navItemEditor);
            if (idx > -1 && idx < childEditorList.size()-1) {
                childEditorList.remove(idx);
                childEditorList.add(navItemEditor);
                refreshChildren();

                NavGroup navGroup = (NavGroup) navItem;
                navGroup.getChildren().remove(idx);
                navGroup.getChildren().add(navItemEditor.getNavItem());
                onItemUpdated();
            }
        }
    }

    void onUpdateChild(NavItem oldItem, NavItemEditor childEditor) {
        NavItem newItem = childEditor.getNavItem();
        NavGroup navGroup = (NavGroup) navItem;
        int idx = navGroup.getChildren().indexOf(oldItem);
        if (idx != -1) {
            navGroup.getChildren().remove(idx);
            navGroup.getChildren().add(idx, newItem);
            onItemUpdated();
        }
        else {
            // Creation of a brand new child
            navGroup.getChildren().add(newItem);
            onItemUpdated();
        }
    }

    void onCancelChild(NavItem oldItem, NavItemEditor childEditor) {
        NavGroup navGroup = (NavGroup) navItem;
        int idx = navGroup.getChildren().indexOf(oldItem);
        if (idx == -1) {
            // Cancel of a brand new child creation
            childEditorList.remove(childEditor);
            refreshChildren();
        }
    }

    void onDeleteChild(NavItemEditor navItemEditor) {
        NavGroup navGroup = (NavGroup) navItem;
        navGroup.getChildren().remove(navItemEditor.getNavItem());
        childEditorList.remove(navItemEditor);
        refreshChildren();
        onItemUpdated();
    }

    private void registerChildren(NavGroup navGroup) {
        List<NavItemEditor> oldChildEditorList = new ArrayList<>(childEditorList);
        childEditorList.clear();

        List<NavItem> childList = navGroup.getChildren();
        for (int i=0; i<childList.size(); i++) {
            NavItem childItem = childList.get(i);
            if ((childItem instanceof NavGroup) && !areSubgroupsAllowed()) {
                continue;
            }

            Optional<NavItemEditor> result = oldChildEditorList.stream()
                    .filter(editor -> editor.getNavItem().equals(childItem))
                    .findFirst();

            NavItemEditor childEditor = result.isPresent() ? result.get() : createChildEditor(childItem);
            oldChildEditorList.remove(childEditor);
            childEditor.edit(childItem);
            childEditorList.add(childEditor);
        }
        // Destroy the remaining editors
        oldChildEditorList.forEach(beanManager::destroyBean);
        oldChildEditorList.clear();

        // Refresh the children view
        this.refreshChildren();
    }

    private NavItemEditor registerChild(NavItem item) {
        NavItemEditor childEditor = createChildEditor(item);
        childEditor.edit(item);
        childEditorList.add(childEditor);
        refreshChildren();
        return childEditor;
    }

    private void refreshChildren() {
        view.clearChildren();
        view.setExpandEnabled(canBeExpanded());

        if (childEditorList.isEmpty()) {
            expanded = false;
            view.setExpanded(false);
        }

        for (int i = 0; i< childEditorList.size(); i++) {
            NavItemEditor childEditor = childEditorList.get(i);
            childEditor.setMoveUpEnabled(i > 0);
            childEditor.setMoveDownEnabled(i < childEditorList.size()-1);
            childEditor.refreshCommands();
            view.addChild(childEditor);
        }
    }

    // View actions

    void onChangesOk() {
        boolean error = false;

        // Capture name changes
        String name = view.getItemName();
        if (name != null && !name.trim().isEmpty()) {
            if (!name.equals(navItem.getName())) {
                navItem.setName(name);
                view.setItemName(name);
                String newPerspectiveI18n = getNewPerspectiveI18n();
                itemNameFromPerspective = newPerspectiveI18n.equals(name);
            }
        } else {
            error = true;
            view.setItemNameError(error);
        }

        // Capture perspective changes
        if (!error && ItemType.PERSPECTIVE.equals(itemType) || ItemType.RUNTIME_PERSPECTIVE.equals(itemType)) {
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            String oldPerspectiveId = navCtx.getResourceId();
            String newPerspectiveId = targetPerspectiveEditor.getPerspectiveId();
            if (newPerspectiveId != null && !newPerspectiveId.trim().isEmpty()) {
                if (oldPerspectiveId != null && !oldPerspectiveId.equals(newPerspectiveId)){
                    navCtx.setResourceId(perspectiveId = newPerspectiveId);
                    navItem.setContext(navCtx.toString());
                }
            } else {
                error = true;
            }
            boolean isRuntimePerspective = perspectivePluginManager.isRuntimePerspective(newPerspectiveId);
            String newGroupId =  isRuntimePerspective ? targetPerspectiveEditor.getNavGroupId() : null;
            String oldGroupId = navCtx.getNavGroupId();
            if ((oldGroupId == null && newGroupId != null) || oldGroupId != null && !oldGroupId.equals(newGroupId)) {
                navCtx.setNavGroupId(newGroupId);
                navItem.setContext(navCtx.toString());
            }
        }

        // Process updates
        if (!error) {
            finishEdition();
            onItemUpdated();
        }
    }

    void onItemNameChanged() {
        itemNameFromPerspective = false;
    }

    void onTargetPerspectiveUpdated() {
        String perspectiveId = targetPerspectiveEditor.getPerspectiveId();
        if (itemNameFromPerspective) {
            String perspectiveName = targetPerspectiveEditor.getPerspectiveName(perspectiveId);
            view.setItemName(perspectiveName);
        }
        if (settings == null || settings.isPerspectiveContextEnabled(navItem)) {
            perspectivePluginManager.getLayoutTemplateInfo(perspectiveId, info -> {
                targetPerspectiveEditor.setNavGroupEnabled(info != null && info.hasNavigationComponents());
            });
        }
    }
}
