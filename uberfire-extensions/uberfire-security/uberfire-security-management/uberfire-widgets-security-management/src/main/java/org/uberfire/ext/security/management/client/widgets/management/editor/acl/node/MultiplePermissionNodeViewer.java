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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.HasResources;
import org.uberfire.security.client.authz.tree.PermissionNode;

@Dependent
public class MultiplePermissionNodeViewer extends BasePermissionNodeViewer {

    View view;
    PermissionWidgetFactory widgetFactory;
    PermissionNode permissionNode;
    List<PermissionNode> overwriteList = new ArrayList<>();
    List<PermissionNodeViewer> childViewerList = new ArrayList<>();
    @Inject
    public MultiplePermissionNodeViewer(View view,
                                        PermissionWidgetFactory widgetFactory) {
        this.view = view;
        this.widgetFactory = widgetFactory;
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
    public PermissionNode getPermissionNode() {
        return permissionNode;
    }

    @Override
    public List<PermissionNodeViewer> getChildren() {
        return childViewerList;
    }

    @Override
    public void show(PermissionNode node) {
        permissionNode = node;
        overwriteList.clear();
        childViewerList.clear();

        String name = node.getNodeName();
        String fullName = node.getNodeFullName();

        view.setNodeName(name);
        if (fullName != null && !fullName.equals(name)) {
            view.setNodeFullName(fullName);
        }

        // Expand the node and check children permissions
        permissionNode.expand(children -> {
            initChildren(children);
            showPermissions();
        });
    }

    protected void initChildren(List<PermissionNode> children) {
        for (PermissionNode child : children) {

            List<Permission> implied = permissionNode.impliesName(child);
            if (hasResources() && !implied.isEmpty()) {
                overwriteList.add(child);
            } else {
                registerChild(child);
            }
        }
    }

    protected void registerChild(PermissionNode child) {
        PermissionNodeViewer nodeViewer = widgetFactory.createViewer(child);
        nodeViewer.setTreeLevel(getTreeLevel() + 1);
        childViewerList.add(nodeViewer);
        view.addChildViewer(nodeViewer);
        nodeViewer.show(child);
    }

    protected List<PermissionNode> getOverwrites(Permission parent) {
        List<PermissionNode> result = new ArrayList<>();
        for (PermissionNode node : overwriteList) {
            for (Permission p : node.getPermissionList()) {
                if (parent.impliesName(p) && !parent.impliesResult(p)) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    protected void showPermissions() {
        List<Permission> permissionList = permissionNode.getPermissionList();
        boolean permissionsEnabled = hasResources() && !permissionList.isEmpty();
        view.setPermissionsVisible(permissionsEnabled);

        if (permissionsEnabled) {

            // For every permission show what children permissions are available
            for (Permission permission : permissionList) {
                String resourceName = permissionNode.getNodeName().toLowerCase();
                String permissionGrantName = permissionNode.getPermissionGrantName(permission).toLowerCase();
                List<PermissionNode> overwrites = getOverwrites(permission);

                if (overwrites.isEmpty()) {

                    if (AuthorizationResult.ACCESS_GRANTED.equals(permission.getResult())) {
                        // Can read all "items"
                        view.addAllItemsGrantedPermission(permissionGrantName,
                                                          resourceName);
                    } else {
                        // Can't read any "items"
                        view.addAllItemsDeniedPermission(permissionGrantName,
                                                         resourceName);
                    }
                } else {
                    if (AuthorizationResult.ACCESS_GRANTED.equals(permission.getResult())) {
                        // Can read all "items" but: a, b, ...
                        view.addItemsGrantedPermission(permissionGrantName,
                                                       resourceName);
                    } else {
                        // Can only read the following "items": a, b, ...
                        view.addItemsDeniedPermission(permissionGrantName,
                                                      resourceName);
                    }
                    // The items added as exceptions
                    for (PermissionNode overwrite : overwrites) {
                        String itemName = overwrite.getNodeName();
                        view.addItemException(itemName);
                    }
                }
            }
        }
    }

    public interface View extends UberView<MultiplePermissionNodeViewer> {

        void setNodeName(String name);

        void setNodeFullName(String name);

        void addChildViewer(PermissionNodeViewer viewer);

        void setPermissionsVisible(boolean enabled);

        void addAllItemsGrantedPermission(String permission,
                                          String resource);

        void addAllItemsDeniedPermission(String permission,
                                         String resource);

        void addItemsGrantedPermission(String permission,
                                       String resource);

        void addItemsDeniedPermission(String permission,
                                      String resource);

        void addItemException(String item);
    }
}
