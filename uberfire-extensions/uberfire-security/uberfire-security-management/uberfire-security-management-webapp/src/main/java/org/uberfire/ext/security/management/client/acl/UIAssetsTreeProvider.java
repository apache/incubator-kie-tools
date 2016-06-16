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

package org.uberfire.ext.security.management.client.acl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionGroupNode;

/**
 * This is a dummy example (only for demo purposes) of how to aggregate existing tree nodes from existing providers
 */
@ApplicationScoped
public class UIAssetsTreeProvider implements PermissionTreeProvider {

    private PerspectiveTreeProvider perspectiveTreeProvider;
    private boolean active = true;
    private String rootNodeName = null;
    private int rootNodePosition = 0;

    public UIAssetsTreeProvider() {
    }

    @Inject
    public UIAssetsTreeProvider(PerspectiveTreeProvider perspectiveTreeProvider) {
        this.perspectiveTreeProvider = perspectiveTreeProvider;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRootNodeName() {
        return rootNodeName;
    }

    public void setRootNodeName(String rootNodeName) {
        this.rootNodeName = rootNodeName;
    }

    public int getRootNodePosition() {
        return rootNodePosition;
    }

    public void setRootNodePosition(int rootNodePosition) {
        this.rootNodePosition = rootNodePosition;
    }

    private String _getRooNodeName() {
        return rootNodeName != null ? rootNodeName : "UI Assets";
    }

    @Override
    public PermissionNode buildRootNode() {
        PermissionGroupNode rootNode = new PermissionGroupNode(this);
        rootNode.setPositionInTree(rootNodePosition);
        rootNode.setNodeName(_getRooNodeName());
        return rootNode;
    }

    @Override
    public void loadChildren(PermissionNode parent, LoadOptions options, LoadCallback callback) {
        if (parent.getNodeName().equals(_getRooNodeName())) {
            List<PermissionNode> result = new ArrayList<>();
            result.add(perspectiveTreeProvider.buildRootNode());
            callback.afterLoad(result);
        }
    }
}