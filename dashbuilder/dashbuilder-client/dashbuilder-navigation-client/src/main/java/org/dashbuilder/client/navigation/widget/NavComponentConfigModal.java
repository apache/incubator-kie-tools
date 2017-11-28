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
package org.dashbuilder.client.navigation.widget;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.TargetDivList;
import org.uberfire.mvp.Command;

@Dependent
public class NavComponentConfigModal implements IsWidget {

    public interface View extends UberView<NavComponentConfigModal> {

        void clearNavGroupItems();

        void setNavGroupEnabled(boolean enabled);

        void addNavGroupItem(String name, Command onSelect);

        void setNavGroupSelection(String name, Command onReset);

        void setNavGroupHelpText(String text);

        void setDefaultNavItemEnabled(boolean enabled);

        void setDefaultNavItemVisible(boolean enabled);

        void clearDefaultItems();

        void defaultItemsNotFound();

        void setDefaultItemSelection(String name, Command onReset);

        void addDefaultItem(String name, Command onSelect);

        void setTargetDivVisible(boolean enabled);

        void clearTargetDivItems();

        void targetDivsNotFound();

        void addTargetDivItem(String name, Command onSelect);

        void setTargetDivSelection(String name, Command onReset);

        void show();

        void hide();
    }

    View view;
    String groupId = null;
    NavGroup group = null;
    String defaultItemId = null;
    String targetDivId = null;
    List<NavItem> navItemList = null;
    List<String> targetDivIdList = null;
    Command onOk = null;
    Command onCancel = null;

    @Inject
    public NavComponentConfigModal(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public View getView() {
        return view;
    }

    public void setTargetDivIdList(List<String> targetDivIdList) {
        this.targetDivIdList = targetDivIdList;
    }

    public void setOnOk(Command onOk) {
        this.onOk = onOk;
    }

    public void setOnCancel(Command onCancel) {
        this.onCancel = onCancel;
    }

    public void setNavGroupHelpHint(String text) {
        view.setNavGroupHelpText(text);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getDefaultItemId() {
        return defaultItemId;
    }

    public String getTargetDivId() {
        return targetDivId;
    }

    public void setTargetDivSupported(boolean supported) {
        view.setTargetDivVisible(supported);
    }

    public void setTargetDiv(String targetDivId) {
        this.targetDivId = targetDivId;
    }

    public void setDefaultNavItemSupported(boolean supported) {
        view.setDefaultNavItemVisible(supported);
    }

    public void setDefaultNavItemId(String navItemId) {
        defaultItemId = navItemId;
    }

    public void setNavGroup(List<NavItem> navItemList, String selectedGroupId) {
        this.navItemList = navItemList;
        this.groupId = selectedGroupId;
        this.group = null;
    }

    private void updateNavGroups() {
        group = null;
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
        String fullPath = calculateFullPath(navItem);
        if (groupId == null || navItem.getId().equals(groupId)) {
            groupId = navItem.getId();
            group = (NavGroup) navItem;
            view.setNavGroupSelection(fullPath, () -> {});
        } else {
            view.addNavGroupItem(fullPath, () -> onGroupSelected(navItem.getId()));
        }
        // Add the children items
        updateNavGroups(((NavGroup) navItem).getChildren());
    }

    private void updateDefaultItems() {
        view.clearDefaultItems();
        view.setDefaultNavItemEnabled(group != null);
        if (group == null || group.getChildren().isEmpty()) {
            view.defaultItemsNotFound();
        } else {
            NavGroup clone = (NavGroup) group.cloneItem();
            clone.setParent(null);
            updateDefaultItems(clone, 1);
        }
    }

    private void updateDefaultItems(NavGroup navGroup, int level) {
        for (NavItem navItem : navGroup.getChildren()) {

            // Divider N/A
            if (navItem instanceof NavDivider) {
                continue;
            }
            // Add the default item. Skip groups.
            if (!(navItem instanceof NavGroup)) {
                String fullPath = calculateFullPath(navItem);
                if (defaultItemId != null && navItem.getId().equals(defaultItemId)) {
                    view.setDefaultItemSelection(fullPath, () -> onDefaultItemSelected(null));
                } else {
                    view.addDefaultItem(fullPath, () -> onDefaultItemSelected(navItem.getId()));
                }
            }
            // Append children
            if (navItem instanceof NavGroup) {
                updateDefaultItems((NavGroup) navItem, level+1);
            }
        }
    }

    private void updateTargetDivs() {
        view.clearTargetDivItems();

        if (targetDivIdList == null || targetDivIdList.isEmpty()) {
            view.targetDivsNotFound();
        } else {
            for (String divId : targetDivIdList) {
                if (targetDivId == null || !targetDivIdList.contains(targetDivId) || divId.equals(targetDivId)) {
                    targetDivId = divId;
                    view.setTargetDivSelection(divId, () -> onTargetDivSelected(null));
                } else {
                    view.addTargetDivItem(divId, () -> onTargetDivSelected(divId));
                }
            }
        }
    }

    public String calculateFullPath(NavItem navItem) {
        StringBuilder out = new StringBuilder();
        NavItem parent = navItem.getParent();
        while (parent != null) {
            out.insert(0, parent.getName() + ">");
            parent = parent.getParent();
        }
        out.append(navItem.getName());
        return out.toString();
    }

    public void clear() {
        groupId = null;
        group = null;
        defaultItemId = null;
        targetDivId = null;
        navItemList = null;
        targetDivIdList = null;
        view.clearNavGroupItems();
        view.clearDefaultItems();
        view.clearTargetDivItems();
    }

    public void show() {
        updateNavGroups();
        updateDefaultItems();
        updateTargetDivs();
        view.show();
    }

    // View callbacks

    public void onGroupSelected(String id) {
        groupId = id;
        defaultItemId = null;
        updateNavGroups();
        updateDefaultItems();
    }

    public void onDefaultItemSelected(String id) {
        defaultItemId = id;
        updateDefaultItems();
    }

    public void onTargetDivSelected(String id) {
        targetDivId = id;
        updateTargetDivs();
    }

    public void onOk() {
        if (groupId != null) {
            view.hide();
            if (onOk != null) {
                onOk.execute();
            }
        }
    }

    public void onCancel() {
        view.hide();
        if (onCancel != null) {
            onCancel.execute();
        }
    }
}
