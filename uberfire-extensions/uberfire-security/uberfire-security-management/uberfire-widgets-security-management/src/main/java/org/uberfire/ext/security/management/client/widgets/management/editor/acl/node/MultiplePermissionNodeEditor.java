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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeAddedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeRemovedEvent;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.HasResources;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.DefaultLoadOptions;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;

@Dependent
public class MultiplePermissionNodeEditor extends BasePermissionNodeEditor {

    public interface View extends UberView<MultiplePermissionNodeEditor> {

        void setNodeName(String name);

        void setNodePanelWidth(int width);

        void setNodeFullName(String name);

        void setResourceName(String name);

        void addPermission(PermissionSwitchToogle permissionSwitch);

        void addChildEditor(PermissionNodeEditor editor, boolean dynamic);

        void addChildSeparator();

        boolean hasChildren();

        void clearChildren();

        String getChildSelectorHint(String resourceName);

        String getChildSearchHint(String resourceName);

        String getChildrenNotFoundMsg(String resourceName);

        void setChildSelector(IsWidget childSelector);

        void showChildSelector();

        void hideChildSelector();

        void setAddChildEnabled(boolean enabled);

        void setClearChildrenEnabled(boolean enabled);

        void setExpanded(boolean expanded);
    }

    View view;
    PermissionWidgetFactory widgetFactory;
    LiveSearchDropDown liveSearchDropDown;
    Event<PermissionChangedEvent> permissionChangedEvent;
    Event<PermissionNodeAddedEvent> permissionNodeAddedEvent;
    Event<PermissionNodeRemovedEvent> permissionNodeRemovedEvent;
    Map<String,PermissionNode> childSelectorNodeMap = new TreeMap<>();
    boolean expanded = false;

    @Inject
    public MultiplePermissionNodeEditor(View view,
                                        LiveSearchDropDown liveSearchDropDown,
                                        PermissionWidgetFactory widgetFactory,
                                        Event<PermissionChangedEvent> permissionChangedEvent,
                                        Event<PermissionNodeAddedEvent> permissionNodeAddedEvent,
                                        Event<PermissionNodeRemovedEvent> permissionNodeRemovedEvent) {
        this.view = view;
        this.liveSearchDropDown = liveSearchDropDown;
        this.widgetFactory = widgetFactory;
        this.permissionChangedEvent = permissionChangedEvent;
        this.permissionNodeAddedEvent = permissionNodeAddedEvent;
        this.permissionNodeRemovedEvent = permissionNodeRemovedEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public boolean hasResources() {
        return permissionNode instanceof HasResources;
    }

    @Override
    public void edit(PermissionNode node) {
        permissionNode = node;
        permissionSwitchMap.clear();

        String name = node.getNodeName();
        String fullName = node.getNodeFullName();

        view.setNodeName(name);
        view.setNodePanelWidth(getNodePanelWidth());
        view.setClearChildrenEnabled(false);
        if (fullName != null && !fullName.equals(name)) {
            view.setNodeFullName(fullName);
        }

        // Resources are only supported for dynamic nodes
        view.setAddChildEnabled(false);
        if (hasResources()) {
            String resourceName = ((PermissionResourceNode) permissionNode).getResourceName();
            liveSearchDropDown.setSelectorHint(view.getChildSelectorHint(resourceName));
            liveSearchDropDown.setSearchHint(view.getChildSearchHint(resourceName));
            liveSearchDropDown.setNotFoundMessage(view.getChildrenNotFoundMsg(resourceName));
            liveSearchDropDown.setMaxItems(50);
            liveSearchDropDown.setWidth(220);
            liveSearchDropDown.setSearchService(childrenSearchService);
            liveSearchDropDown.setOnChange(() -> onChildSelected(liveSearchDropDown.getSelectedItem()));

            view.setAddChildEnabled(true);
            view.setResourceName(resourceName);
            view.setChildSelector(liveSearchDropDown);
        }

        // Init the switch control for every permission
        for (Permission permission : permissionNode.getPermissionList()) {
            String grantName = permissionNode.getPermissionGrantName(permission);
            String denyName = permissionNode.getPermissionDenyName(permission);
            boolean granted = AuthorizationResult.ACCESS_GRANTED.equals(permission.getResult());

            PermissionSwitch permissionSwitch = widgetFactory.createSwitch();
            permissionSwitch.init(grantName, denyName, granted, 0);
            permissionSwitch.setOnChange(() -> {
                permission.setResult(permissionSwitch.isOn() ? AuthorizationResult.ACCESS_GRANTED : AuthorizationResult.ACCESS_DENIED);

                // Notify the change in the permission
                super.onPermissionChanged(permission, permissionSwitch.isOn());
                permissionChangedEvent.fire(new PermissionChangedEvent(getACLEditor(), permission, permissionSwitch.isOn()));
            });
            super.registerPermissionSwitch(permission, permissionSwitch);
        }
        // Update the switches status according to the inter-dependencies between their permissions
        super.processAllPermissionDependencies();

        // Add the switch controls to the view once initialized
        for (PermissionSwitchToogle switchToogle : permissionSwitchMap.values()) {
            view.addPermission(switchToogle);
        }

        // Load the children in order to initialize the exception counters properly
        loadChildren();
    }

    @Override
    protected void notifyPermissionChange(Permission permission, boolean on) {
        super.notifyPermissionChange(permission, on);

        // Update the exception count
        PermissionSwitchToogle permissionSwitch = permissionSwitchMap.get(permission);
        int n = getExceptionNumber(permission);
        permissionSwitch.setNumberOfExceptions(n);
    }

    public void expand() {
        expanded = true;
        List<PermissionNodeEditor> childEditors = getChildEditors();

        view.setExpanded(true);
        view.clearChildren();
        for (int i=0; i<childEditors.size(); i++) {
            PermissionNodeEditor nodeEditor = childEditors.get(i);
            view.addChildEditor(nodeEditor, hasResources());
            if (i < childEditors.size() - 1) {
                view.addChildSeparator();
            }
        }
        if (!childEditors.isEmpty()) {
            view.setClearChildrenEnabled(hasResources());
        }
    }

    public void collapse() {
        permissionNode.collapse();
        expanded = false;
        view.setExpanded(false);
        view.clearChildren();
    }

    protected void loadChildren() {
        permissionNode.expand(children -> {
            for (PermissionNode child : children) {
                registerChild(child);
            }
            updateExceptionCounters();
        });
    }

    protected PermissionNodeEditor registerChild(PermissionNode child) {
        PermissionNodeEditor nodeEditor = widgetFactory.createEditor(child);
        nodeEditor.setACLEditor(this.getACLEditor());
        nodeEditor.setTreeLevel(getTreeLevel()+1);
        nodeEditor.setParentEditor(this);
        nodeEditor.edit(child);
        super.addChildEditor(nodeEditor);
        return nodeEditor;
    }

    @Override
    public void onChildPermissionChanged(PermissionNodeEditor childEditor, Permission permission, boolean on) {
        updateExceptionCounters();
    }

    @Override
    protected void onNodePanelWidthChanged() {
        int width = getNodePanelWidth();
        view.setNodePanelWidth(width);
    }

    private void updateExceptionCounters() {
        for (Permission p : permissionSwitchMap.keySet()) {
            PermissionSwitchToogle pswitch = permissionSwitchMap.get(p);
            int n = getExceptionNumber(p);
            pswitch.setNumberOfExceptions(n);
        }
    }

    // View events

    public void onNodeClick() {
        if (expanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void onAddChildStart() {
        view.showChildSelector();
    }

    public void onAddChildCancel() {
        view.hideChildSelector();
    }

    public void onClearChildren() {
        for (PermissionNodeEditor child : new ArrayList<>(getChildEditors())) {
            removeChild(child);
        }
        view.setClearChildrenEnabled(false);
        updateExceptionCounters();
    }

    public void onRemoveChild(PermissionNodeEditor child) {
        removeChild(child);
        updateExceptionCounters();
        view.setClearChildrenEnabled(hasResources() && hasChildEditors());
    }

    protected void removeChild(PermissionNodeEditor child) {
        super.removeChildEditor(child);

        liveSearchDropDown.clear();
        view.hideChildSelector();
        view.clearChildren();

        List<PermissionNodeEditor> childEditors = getChildEditors();
        for (int i=0; i<childEditors.size(); i++) {
            PermissionNodeEditor nodeEditor = childEditors.get(i);
            view.addChildEditor(nodeEditor, hasResources());
            if (i < childEditors.size() - 1) {
                view.addChildSeparator();
            }
        }
        permissionNodeRemovedEvent.fire(new PermissionNodeRemovedEvent(getACLEditor(), permissionNode, child.getPermissionNode()));
    }

    public void onChildSelected(String childName) {
        PermissionNode childNode = childSelectorNodeMap.remove(childName);
        overwritePermissions(childNode);
        PermissionNodeEditor childEditor = registerChild(childNode);
        if (view.hasChildren()) {
            view.addChildSeparator();
        }
        view.addChildEditor(childEditor, hasResources());
        view.setClearChildrenEnabled(true);
        view.hideChildSelector();
        liveSearchDropDown.clear();

        updateExceptionCounters();

        permissionNodeAddedEvent.fire(new PermissionNodeAddedEvent(getACLEditor(), permissionNode, childNode));
    }

    protected void overwritePermissions(PermissionNode child) {
        for (Permission p1 : permissionNode.getPermissionList()) {
            for (Permission p2 : child.getPermissionList()) {
                if (p1.impliesName(p2)) {
                    p2.setResult(p1.getResult().invert());
                }
            }
        }
    }

    LiveSearchService childrenSearchService = (pattern, maxResults, callback) -> {

        PermissionTreeProvider provider = permissionNode.getPermissionTreeProvider();
        DefaultLoadOptions loadOptions = new DefaultLoadOptions();
        loadOptions.setNodeNamePattern(pattern);
        loadOptions.setMaxNodes(maxResults);

        provider.loadChildren(permissionNode, loadOptions, children -> {

            childSelectorNodeMap.clear();

            for (PermissionNode childNode : children) {
                String childName = childNode.getNodeName();
                if (!childAlreadyAdded(childName)) {
                    childSelectorNodeMap.put(childName, childNode);
                }
            }
            List<String> result = new ArrayList<>(childSelectorNodeMap.keySet());
            callback.afterSearch(result);
        });
    };

    protected boolean childAlreadyAdded(String nodeName) {
        for (PermissionNodeEditor childEditor : getChildEditors()) {
            String existingName = childEditor.getPermissionNode().getNodeName();
            if (existingName.equals(nodeName)) {
                return true;
            }
        }
        return false;
    }
}
