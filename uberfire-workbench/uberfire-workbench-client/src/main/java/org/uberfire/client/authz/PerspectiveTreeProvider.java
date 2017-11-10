/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.authz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.resources.i18n.PermissionTreeI18n;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.uberfire.client.authz.PerspectiveAction.CREATE;
import static org.uberfire.client.authz.PerspectiveAction.DELETE;
import static org.uberfire.client.authz.PerspectiveAction.READ;
import static org.uberfire.client.authz.PerspectiveAction.UPDATE;

@ApplicationScoped
public class PerspectiveTreeProvider implements PermissionTreeProvider {

    private ActivityBeansCache activityBeansCache;
    private PermissionManager permissionManager;
    private PermissionTreeI18n i18n;
    private boolean active = true;
    private String resourceName = null;
    private String rootNodeName = null;
    private int rootNodePosition = 0;
    private Map<String, String> perspectiveNameMap = new HashMap<>();
    private Set<String> perspectiveIdsExcluded = new HashSet<>();

    public PerspectiveTreeProvider() {
    }

    @Inject
    public PerspectiveTreeProvider(ActivityBeansCache activityBeansCache,
                                   PermissionManager permissionManager,
                                   PermissionTreeI18n i18n) {
        this.activityBeansCache = activityBeansCache;
        this.permissionManager = permissionManager;
        this.i18n = i18n;
        this.resourceName = i18n.perspectiveResourceName();
        this.rootNodeName = i18n.perspectivesNodeName();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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

    public void excludePerspectiveId(String perspectiveId) {
        perspectiveIdsExcluded.add(perspectiveId);
    }

    public Set<String> getPerspectiveIdsExcluded() {
        return perspectiveIdsExcluded;
    }

    @Override
    public PermissionNode buildRootNode() {
        PermissionResourceNode rootNode = new PermissionResourceNode(resourceName,
                                                                     this);
        rootNode.setNodeName(rootNodeName);
        rootNode.setNodeFullName(i18n.perspectivesNodeHelp());
        rootNode.setPositionInTree(rootNodePosition);

        Permission readPermission = newPermission(READ);
        Permission updatePermission = newPermission(UPDATE);
        Permission deletePermission = newPermission(DELETE);
        Permission createPermission = newPermission(CREATE);

        rootNode.addPermission(readPermission,
                               i18n.perspectiveRead());
        rootNode.addPermission(updatePermission,
                               i18n.perspectiveUpdate());
        rootNode.addPermission(deletePermission,
                               i18n.perspectiveDelete());
        rootNode.addPermission(createPermission,
                               i18n.perspectiveCreate());

        rootNode.addDependencies(readPermission,
                                 updatePermission,
                                 deletePermission,
                                 createPermission);

        return rootNode;
    }

    @Override
    public void loadChildren(PermissionNode parent,
                             LoadOptions options,
                             LoadCallback callback) {
        if (parent.getNodeName().equals(rootNodeName)) {
            callback.afterLoad(buildPerspectiveNodes(options));
        }
    }

    private Permission newPermission(ResourceAction action) {
        return permissionManager.createPermission(ActivityResourceType.PERSPECTIVE,
                                                  action,
                                                  true);
    }

    private Permission newPermission(Resource resource,
                                     ResourceAction action) {
        return permissionManager.createPermission(resource,
                                                  action,
                                                  true);
    }

    private List<PermissionNode> buildPerspectiveNodes(LoadOptions options) {

        List<PermissionNode> nodes = new ArrayList<>();
        for (SyncBeanDef<Activity> beanDef : activityBeansCache.getPerspectiveActivities()) {
            PerspectiveActivity p = (PerspectiveActivity) beanDef.getInstance();
            if (match(p, options)) {
                nodes.add(toPerspectiveNode(p));
            }
        }

        int max = options.getMaxNodes();
        return max > 0 && max < nodes.size() ? nodes.subList(0,
                                                             max) : nodes;
    }

    private PermissionNode toPerspectiveNode(PerspectiveActivity p) {
        String id = p.getIdentifier();
        String name = getPerspectiveName(id);

        PermissionLeafNode node = new PermissionLeafNode();
        node.setNodeName(name);

        Permission readPermission = newPermission(p,
                                                  READ);
        node.addPermission(readPermission,
                           i18n.perspectiveRead());

        // Only runtime created perspectives can be modified
        if (!(p instanceof AbstractWorkbenchPerspectiveActivity)) {
            Permission updatePermission = newPermission(p,
                                                        UPDATE);
            Permission deletePermission = newPermission(p,
                                                        DELETE);
            node.addPermission(updatePermission,
                               i18n.perspectiveUpdate());
            node.addPermission(deletePermission,
                               i18n.perspectiveDelete());
            node.addDependencies(readPermission,
                                 updatePermission,
                                 deletePermission);
        }
        return node;
    }

    public String getPerspectiveName(String perspectiveId) {
        // Look for preset names first
        if (perspectiveNameMap.containsKey(perspectiveId)) {
            return perspectiveNameMap.get(perspectiveId);
        }
        // For user created perspectives, the perspective id. is always the name set by its author
        SyncBeanDef<Activity> beanDef = activityBeansCache.getActivity(perspectiveId);
        if (beanDef != null) {
            Activity activity = beanDef.getInstance();
            if (activity != null && !(activity instanceof AbstractWorkbenchPerspectiveActivity)) {
                return perspectiveId;
            }
        }
        // By default, to avoid displaying FQCN, the name is the string after the last dot
        int lastDot = perspectiveId.lastIndexOf(".");
        return lastDot != -1 ? perspectiveId.substring(lastDot + 1) : perspectiveId;
    }

    public void setPerspectiveName(String perspectiveId,
                                   String name) {
        perspectiveNameMap.put(perspectiveId,
                               name);
    }

    private boolean match(Resource r,
                          LoadOptions options) {
        String namePattern = options.getNodeNamePattern();
        Collection<String> includedIds = options.getResourceIds();

        if (perspectiveIdsExcluded.contains(r.getIdentifier())) {
            return false;
        }
        if (includedIds != null && !includedIds.isEmpty()) {
            if (includedIds.contains(r.getIdentifier())) {
                return true;
            }
        }
        if (namePattern != null) {
            String perspectiveName = getPerspectiveName(r.getIdentifier());
            if (perspectiveName.toLowerCase().contains(namePattern.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}