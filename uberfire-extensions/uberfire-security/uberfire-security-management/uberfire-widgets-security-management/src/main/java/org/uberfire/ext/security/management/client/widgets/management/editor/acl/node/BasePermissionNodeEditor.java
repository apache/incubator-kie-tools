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

import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLEditor;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.PermissionNode;

public abstract class BasePermissionNodeEditor implements PermissionNodeEditor {

    protected ACLEditor aclEditor = null;
    protected PermissionNode permissionNode;
    private PermissionNodeEditor parentEditor = null;
    private List<PermissionNodeEditor> childEditorList = new ArrayList<>();
    protected int width = 240;
    protected int leftMargin = 0;
    protected int treeLevel = 0;
    protected int padding = 15;

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
        return width - leftMargin - (treeLevel*padding);
    }

    @Override
    public void onParentPermissionChanged(Permission permission, boolean on) {

    }

    @Override
    public void onChildPermissionChanged(PermissionNodeEditor childEditor, Permission permission, boolean on) {

    }

    protected void onPermissionChanged(Permission permission, boolean on) {
        if (parentEditor != null) {
            parentEditor.onChildPermissionChanged(this, permission, on);
        }
        for (PermissionNodeEditor child : getChildEditors()) {
            child.onParentPermissionChanged(permission, on);
        }
    }

    protected void onNodePanelWidthChanged() {

    }
}
