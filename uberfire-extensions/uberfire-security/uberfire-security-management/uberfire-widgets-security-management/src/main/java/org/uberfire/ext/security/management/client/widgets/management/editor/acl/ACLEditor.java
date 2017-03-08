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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionNodeEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionWidgetFactory;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.PermissionTreeFactory;
import org.uberfire.security.impl.authz.DefaultPermissionCollection;

@Dependent
public class ACLEditor implements IsWidget {

    View view;
    PermissionWidgetFactory nodeWidgetFactory;
    PermissionTreeFactory permissionTreeFactory;
    PermissionTree permissionTree;
    List<PermissionNodeEditor> permissionNodeEditorList = new ArrayList<>();
    @Inject
    public ACLEditor(View view,
                     PermissionWidgetFactory nodeWidgetFactory,
                     PermissionTreeFactory permissionTreeFactory) {
        this.view = view;
        this.nodeWidgetFactory = nodeWidgetFactory;
        this.permissionTreeFactory = permissionTreeFactory;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public PermissionCollection getPermissions() {
        PermissionCollection pc = new DefaultPermissionCollection();
        collect(pc,
                permissionNodeEditorList);
        return pc;
    }

    private void collect(PermissionCollection pc,
                         List<PermissionNodeEditor> nodeEditors) {
        if (nodeEditors != null) {
            for (PermissionNodeEditor nodeEditor : nodeEditors) {
                for (Permission p : nodeEditor.getPermissionNode().getPermissionList()) {
                    pc.add(p);
                }
                collect(pc,
                        nodeEditor.getChildEditors());
            }
        }
    }

    public void edit(Role role) {
        edit(permissionTreeFactory.createPermissionTree(role));
    }

    public void edit(Group group) {
        edit(permissionTreeFactory.createPermissionTree(group));
    }

    private void edit(PermissionTree tree) {
        this.permissionTree = tree;

        view.clear();
        permissionNodeEditorList.clear();
        List<PermissionNode> rootNodeList = tree.getRootNodes();
        for (PermissionNode rootNode : rootNodeList) {
            PermissionNodeEditor rootNodeEditor = nodeWidgetFactory.createEditor(rootNode);
            rootNodeEditor.setACLEditor(this);
            rootNodeEditor.edit(rootNode);
            view.addRootNodeWidget(rootNodeEditor);
            permissionNodeEditorList.add(rootNodeEditor);
        }
    }

    public interface View extends UberView<ACLEditor> {

        void clear();

        void addRootNodeWidget(IsWidget rootNodeWidget);
    }
}
