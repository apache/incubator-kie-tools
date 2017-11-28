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
package org.dashbuilder.client.navigation.widget.editor;

import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.dropdown.PerspectiveDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.PerspectiveNameProvider;
import org.uberfire.mvp.Command;

@Dependent
public class TargetPerspectiveEditor implements IsElement, PerspectiveNameProvider {

    public interface View extends UberElement<TargetPerspectiveEditor> {

        void setPerspectiveSelector(IsWidget perspectiveDropDown);

        void clearNavGroupItems();

        void setNavGroupEnabled(boolean enabled);

        void addNavGroupItem(String name, Command onSelect);

        void setNavGroupSelection(String name, Command onReset);
    }

    View view;
    String navGroupId;
    PerspectiveDropDown perspectiveDropDown;
    PerspectivePluginManager perspectivePluginManager;
    PerspectiveTreeProvider perspectiveTreeProvider;
    List<NavItem> navItemList;
    Command onUpdateCommand;

    @Inject
    public TargetPerspectiveEditor(View view,
                                   PerspectiveDropDown perspectiveDropDown,
                                   PerspectivePluginManager perspectivePluginManager,
                                   PerspectiveTreeProvider perspectiveTreeProvider) {
        this.view = view;
        this.perspectiveDropDown = perspectiveDropDown;
        this.perspectivePluginManager = perspectivePluginManager;
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.perspectiveDropDown.setPerspectiveNameProvider(this);
        this.perspectiveDropDown.setMaxItems(50);
        this.perspectiveDropDown.setWidth(150);
        this.perspectiveDropDown.setOnChange(this::onPerspectiveChanged);
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setNavItemList(List<NavItem> navItemList) {
        this.navItemList = navItemList;
    }

    public void setOnUpdateCommand(Command onUpdateCommand) {
        this.onUpdateCommand = onUpdateCommand;
    }

    public void setPerspectiveIdsExcluded(Set<String> perspectiveIdsExcluded) {
        perspectiveDropDown.setPerspectiveIdsExcluded(perspectiveIdsExcluded);
    }

    public void setPerspectiveId(String perspectiveId) {
        perspectiveDropDown.setSelectedPerspective(perspectiveId);
    }

    public String getPerspectiveId() {
        return perspectiveDropDown.getSelectedPerspective().getIdentifier();
    }

    @Override
    public String getPerspectiveName(String perspectiveId) {
        if (perspectivePluginManager.isRuntimePerspective(perspectiveId)) {
            return perspectiveId;
        }
        return perspectiveTreeProvider.getPerspectiveName(perspectiveId);
    }

    public void setNavGroupEnabled(boolean enabled) {
        view.setNavGroupEnabled(enabled);
    }

    public void setNavGroupId(String navGroupId) {
        this.navGroupId = navGroupId;
    }

    public String getNavGroupId() {
        return navGroupId;
    }

    public void show() {
        view.setPerspectiveSelector(perspectiveDropDown);
        updateNavGroups();
    }

    public void clear() {
        navGroupId = null;
        perspectiveDropDown.clear();
        view.clearNavGroupItems();
    }

    private void updateNavGroups() {
        view.clearNavGroupItems();
        if (navItemList != null) {
            updateNavGroups(navItemList);
        }
    }

    private void updateNavGroups(List<NavItem> navItemList) {
        navItemList.stream()
            .filter(navItem -> navItem instanceof NavGroup)
            .forEach(this::addNavGroup);
    }

    private void addNavGroup(NavItem navItem) {
        // Discard items with no name
        if (navItem.getName() != null) {
            // Check if the group is already selected
            String fullPath = calculateFullPath(navItem);
            if (navGroupId != null && navItem.getId().equals(navGroupId)) {
                view.setNavGroupSelection(fullPath, () -> onGroupSelected(null));
            } else {
                view.addNavGroupItem(fullPath, () -> onGroupSelected(navItem.getId()));
            }
        }
        // Add the children items
        updateNavGroups(((NavGroup) navItem).getChildren());
    }

    private String calculateFullPath(NavItem navItem) {
        StringBuilder out = new StringBuilder();
        NavItem parent = navItem.getParent();
        while (parent != null && parent.getName() != null) {
            out.insert(0, parent.getName() + ">");
            parent = parent.getParent();
        }
        out.append(navItem.getName());
        return out.toString();
    }

    // View callbacks

    public void onGroupSelected(String id) {
        navGroupId = id;
        updateNavGroups();
        if (onUpdateCommand != null) {
            onUpdateCommand.execute();
        }
    }

    public void onPerspectiveChanged() {
        if (onUpdateCommand != null) {
            onUpdateCommand.execute();
        }
    }
}
