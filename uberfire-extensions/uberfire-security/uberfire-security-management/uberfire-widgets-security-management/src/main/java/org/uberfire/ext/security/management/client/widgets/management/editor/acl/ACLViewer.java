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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionNodeViewer;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionWidgetFactory;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.PermissionTreeFactory;

@Dependent
public class ACLViewer implements IsWidget {

    View view;
    PermissionWidgetFactory nodeWidgetFactory;
    PermissionTreeFactory permissionTreeFactory;
    PermissionTree permissionTree;
    @Inject
    public ACLViewer(View view,
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

    public void show(Role role) {
        show(permissionTreeFactory.createPermissionTree(role));
    }

    public void show(Group group) {
        show(permissionTreeFactory.createPermissionTree(group));
    }

    public void show(User user) {
        show(permissionTreeFactory.createPermissionTree(user,
                                                        VotingStrategy.PRIORITY));
    }

    private void show(PermissionTree tree) {
        view.clear();
        permissionTree = tree;

        if (tree != null) {
            List<PermissionNode> rootNodeList = tree.getRootNodes();
            for (PermissionNode rootNode : rootNodeList) {
                PermissionNodeViewer rootNodeViewer = nodeWidgetFactory.createViewer(rootNode);
                rootNodeViewer.show(rootNode);
                view.addRootNodeWidget(rootNodeViewer);
            }
        }
    }

    public interface View extends UberView<ACLViewer> {

        void clear();

        void addRootNodeWidget(IsWidget rootNodeWidget);
    }
}
