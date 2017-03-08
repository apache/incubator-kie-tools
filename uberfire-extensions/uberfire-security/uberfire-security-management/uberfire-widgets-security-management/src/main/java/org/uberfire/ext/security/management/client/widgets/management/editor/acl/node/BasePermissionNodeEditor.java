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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLEditor;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.PermissionNode;

public abstract class BasePermissionNodeEditor implements PermissionNodeEditor {

    protected ACLEditor aclEditor = null;
    protected PermissionNode permissionNode;
    protected Map<Permission, PermissionSwitchToogle> permissionSwitchMap = new HashMap<>();
    protected int width = 240;
    protected int leftMargin = 0;
    protected int treeLevel = 0;
    protected int padding = 15;
    private PermissionNodeEditor parentEditor = null;
    private List<PermissionNodeEditor> childEditorList = new ArrayList<>();

    @Override
    public ACLEditor getACLEditor() {
        return aclEditor;
    }

    @Override
    public void setACLEditor(ACLEditor aclEditor) {
        this.aclEditor = aclEditor;
    }

    @Override
    public PermissionNode getPermissionNode() {
        return permissionNode;
    }

    @Override
    public PermissionNodeEditor getParentEditor() {
        return parentEditor;
    }

    @Override
    public void setParentEditor(PermissionNodeEditor editor) {
        this.parentEditor = editor;
    }

    @Override
    public List<PermissionNodeEditor> getChildEditors() {
        return childEditorList;
    }

    @Override
    public void addChildEditor(PermissionNodeEditor editor) {
        editor.setParentEditor(this);
        childEditorList.add(editor);
    }

    @Override
    public void removeChildEditor(PermissionNodeEditor editor) {
        editor.setParentEditor(null);
        childEditorList.remove(editor);
    }

    @Override
    public boolean hasChildEditors() {
        return !childEditorList.isEmpty();
    }

    @Override
    public void clearChildEditors() {
        childEditorList.clear();
    }

    @Override
    public boolean isAnException(Permission permission) {
        for (Permission p : permissionNode.getPermissionList()) {
            if (p.impliesName(permission)) {
                return !p.getResult().equals(permission.getResult());
            }
        }
        return false;
    }

    @Override
    public int getExceptionNumber(Permission permission) {
        int count = 0;
        for (PermissionNodeEditor nodeEditor : getChildEditors()) {
            if (nodeEditor.getPermissionNode() != null && nodeEditor.getPermissionNode().getPermissionList() != null) {
                for (Permission p : nodeEditor.getPermissionNode().getPermissionList()) {
                    if (permission.impliesName(p) && !permission.getResult().equals(p.getResult())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public int getTreeLevel() {
        return treeLevel;
    }

    @Override
    public void setTreeLevel(int treeLevel) {
        this.treeLevel = treeLevel;
        this.onNodePanelWidthChanged();
    }

    @Override
    public void setLeftMargin(int margin) {
        this.leftMargin = margin;
        this.onNodePanelWidthChanged();
    }

    @Override
    public int getNodePanelWidth() {
        return width - leftMargin - (treeLevel * padding);
    }

    /**
     * Invoked when any of the parent permissions of a permission editor changes.
     * <p>
     * <p>By default, it does nothing as it is up to each subclass to provide its own implementation.</p>
     * <p>
     * <p>Only leaf or intermediate nodes are invoked.</p>
     */
    @Override
    public void onParentPermissionChanged(Permission permission,
                                          boolean on) {

    }

    /**
     * Invoked when any of the permissions of a child editor changes.
     * <p>
     * <p>By default, it does nothing as it is up to each subclass to provide its own implementation.</p>
     * <p>
     * <p>This method is never invoked on leaf nodes.</p>
     */
    @Override
    public void onChildPermissionChanged(PermissionNodeEditor childEditor,
                                         Permission permission,
                                         boolean on) {

    }

    /**
     * Invoked when the width of a node editor panel changes.
     * <p>
     * <p>By default, it  does nothing as it is up to each subclass to provide its own implementation.</p>
     */
    protected void onNodePanelWidthChanged() {

    }

    /**
     * Invoked when a permission toogle switch changes its value.
     * @param permission The changing permission
     * @param on The switch status
     */
    protected void onPermissionChanged(Permission permission,
                                       boolean on) {
        notifyPermissionChange(permission,
                               on);
        processPermissionDependencies(permission);
    }

    /**
     * Invoked when a permission toogle switch changes its value.
     * @param permission The changing permission
     * @param on The switch status
     */
    protected void notifyPermissionChange(Permission permission,
                                          boolean on) {
        // Notify the parent editor
        if (parentEditor != null) {
            parentEditor.onChildPermissionChanged(this,
                                                  permission,
                                                  on);
        }
        // Notify the children editors
        for (PermissionNodeEditor child : getChildEditors()) {
            child.onParentPermissionChanged(permission,
                                            on);
        }
    }

    /**
     * Make sure all the permission switch controls are updated according the inter-dependencies
     * declared between them.
     * <p>
     * <p>For instance, given an update & delete permissions that depends on a read permission,
     * if the read permission is turned off then the update & delete permission switches are
     * turned off as well.</p>
     */
    protected void processAllPermissionDependencies() {
        for (Permission permission : permissionSwitchMap.keySet()) {
            processPermissionDependencies(permission);
        }
    }

    /**
     * Updates any permission switch which has a dependency with the given permission.
     * @param permission The permission which dependencies needs to be revisited.
     */
    protected void processPermissionDependencies(Permission permission) {
        List<Permission> dependencyList = this.getPermissionNode().getDependencies(permission);
        if (dependencyList != null) {
            PermissionSwitchToogle permissionSwitch = permissionSwitchMap.get(permission);
            for (Permission dep : dependencyList) {
                PermissionSwitchToogle depSwitch = permissionSwitchMap.get(dep);

                if (!permissionSwitch.isOn()) {
                    dep.setResult(AuthorizationResult.ACCESS_DENIED);
                    depSwitch.setOn(false);
                    depSwitch.setEnabled(false);

                    // Notify the dependant switch change
                    this.notifyPermissionChange(dep,
                                                false);
                } else {
                    depSwitch.setEnabled(true);
                }
            }
        }
    }

    /**
     * Links the given switch widget with the specified permission instance.
     * @param permission The permission
     * @param permissionSwitch The switch widget related
     */
    protected void registerPermissionSwitch(Permission permission,
                                            PermissionSwitchToogle permissionSwitch) {
        permissionSwitchMap.put(permission,
                                permissionSwitch);
    }
}
