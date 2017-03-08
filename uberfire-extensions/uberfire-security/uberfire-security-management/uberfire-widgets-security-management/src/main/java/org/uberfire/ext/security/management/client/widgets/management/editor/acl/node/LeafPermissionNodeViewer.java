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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.PermissionNode;

@Dependent
public class LeafPermissionNodeViewer extends BasePermissionNodeViewer {

    View view;
    PermissionNode permissionNode;
    @Inject
    public LeafPermissionNodeViewer(View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public PermissionNode getPermissionNode() {
        return permissionNode;
    }

    @Override
    public List<PermissionNodeViewer> getChildren() {
        return null;
    }

    @Override
    public void show(PermissionNode node) {
        permissionNode = node;

        String name = node.getNodeName();
        String fullName = node.getNodeFullName();

        view.setNodeName(name);
        if (fullName != null && !fullName.equals(name)) {
            view.setNodeFullName(fullName);
        }

        for (Permission permission : permissionNode.getPermissionList()) {

            if (AuthorizationResult.ACCESS_GRANTED.equals(permission.getResult())) {
                String granted = node.getPermissionGrantName(permission).toLowerCase();
                view.permissionGranted(granted);
            } else {
                String denied = node.getPermissionDenyName(permission).toLowerCase();
                view.permissionDenied(denied);
            }
        }
    }

    public interface View extends UberView<LeafPermissionNodeViewer> {

        void setNodeName(String name);

        void setNodeFullName(String name);

        void permissionGranted(String permission);

        void permissionDenied(String permission);
    }
}
