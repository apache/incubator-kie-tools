/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.resources.i18n.PermissionTreeI18n;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.uberfire.client.authz.PerspectiveAction.READ;

@ApplicationScoped
public class EditorTreeProvider implements PermissionTreeProvider {

    private ActivityBeansCache activityBeansCache;
    private SyncBeanManager iocManager;
    private PermissionManager permissionManager;
    private PermissionTreeI18n i18n;
    private String resourceName = null;
    private String rootNodeName = null;
    private int rootNodePosition = 0;
    private Set<RegisteredEditor> registeredEditors = new HashSet<>();

    public EditorTreeProvider() {
        //CDI proxy
    }

    @Inject
    public EditorTreeProvider(final ActivityBeansCache activityBeansCache,
                              final SyncBeanManager iocManager,
                              final PermissionManager permissionManager,
                              final PermissionTreeI18n i18n) {
        this.activityBeansCache = activityBeansCache;
        this.iocManager = iocManager;
        this.permissionManager = permissionManager;
        this.i18n = i18n;
        this.resourceName = i18n.editorResourceName();
        this.rootNodeName = i18n.editorsNodeName();
    }

    @Override
    public PermissionNode buildRootNode() {
        final PermissionResourceNode rootNode = new PermissionResourceNode(resourceName,
                                                                           this);
        rootNode.setNodeName(rootNodeName);
        rootNode.setPositionInTree(rootNodePosition);
        rootNode.setNodeFullName(i18n.editorsNodeHelp());
        rootNode.addPermission(newPermission(READ),
                               i18n.editorRead());

        return rootNode;
    }

    @Override
    public void loadChildren(final PermissionNode parent,
                             final LoadOptions options,
                             final LoadCallback callback) {
        if (parent.getNodeName().equals(rootNodeName)) {
            callback.afterLoad(buildEditorNodes(options));
        }
    }

    public int getRootNodePosition() {
        return rootNodePosition;
    }

    public void setRootNodePosition(int rootNodePosition) {
        this.rootNodePosition = rootNodePosition;
    }

    public void registerEditor(final String editorId,
                               final String editorName) {
        final SyncBeanDef<Activity> editorBeanDef = activityBeansCache.getActivity(editorId);
        WorkbenchEditorActivity editor = null;
        if (editorBeanDef != null) {
            try {
                editor = (WorkbenchEditorActivity) editorBeanDef.getInstance();

                // We only need the Editor's Resource definition and not an Editor instance itself as
                // this can interfere with Event handling across multiple instances at runtime. Therefore
                // extract the required information before disposing of the Editor instance.
                final String identifier = editor.getIdentifier();
                final ResourceType resourceType = editor.getResourceType();
                final Resource resource = new Resource() {
                    @Override
                    public String getIdentifier() {
                        return identifier;
                    }

                    @Override
                    public ResourceType getResourceType() {
                        return resourceType;
                    }
                };
                registeredEditors.add(new RegisteredEditor(editorId,
                                                           editorName,
                                                           resource));
            } finally {
                if (editor != null) {
                    iocManager.destroyBean(editor);
                }
            }
        }
    }

    private Permission newPermission(final ResourceAction action) {
        return permissionManager.createPermission(ActivityResourceType.EDITOR,
                                                  action,
                                                  true);
    }

    private Permission newPermission(final Resource resource,
                                     final ResourceAction action) {
        return permissionManager.createPermission(resource,
                                                  action,
                                                  true);
    }

    private List<PermissionNode> buildEditorNodes(final LoadOptions options) {
        final List<PermissionNode> nodes = new ArrayList<>();
        registeredEditors.stream()
                .filter(e -> match(e, options))
                .forEach(e -> nodes.add(toEditorNode(e)));

        final int max = options.getMaxNodes();
        return max > 0 && max < nodes.size() ? nodes.subList(0,
                                                             max) : nodes;
    }

    private PermissionNode toEditorNode(final RegisteredEditor editor) {
        final PermissionLeafNode node = new PermissionLeafNode();
        node.setNodeName(editor.editorName);

        final Permission readPermission = newPermission(editor.resource,
                                                        READ);
        node.addPermission(readPermission,
                           i18n.editorRead());

        return node;
    }

    private boolean match(final RegisteredEditor editor,
                          final LoadOptions options) {
        final String identifier = editor.editorId;
        final String namePattern = options.getNodeNamePattern();
        final Collection<String> includedIds = options.getResourceIds();

        if (includedIds != null && !includedIds.isEmpty()) {
            if (includedIds.contains(identifier)) {
                return true;
            }
        }
        if (namePattern != null) {
            final String editorName = editor.editorName;
            if (editorName.toLowerCase().contains(namePattern.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private class RegisteredEditor {

        private String editorId;
        private String editorName;
        private Resource resource;

        public RegisteredEditor(final String editorId,
                                final String editorName,
                                final Resource resource) {
            this.editorId = editorId;
            this.editorName = editorName;
            this.resource = resource;
        }
    }
}