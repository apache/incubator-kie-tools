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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLEditor;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.PermissionNode;

public interface PermissionNodeEditor extends IsWidget {

    ACLEditor getACLEditor();

    void setACLEditor(ACLEditor editor);

    int getTreeLevel();

    void setTreeLevel(int level);

    int getNodePanelWidth();

    void setLeftMargin(int margin);

    PermissionNode getPermissionNode();

    void edit(PermissionNode node);

    PermissionNodeEditor getParentEditor();

    void setParentEditor(PermissionNodeEditor editor);

    List<PermissionNodeEditor> getChildEditors();

    void addChildEditor(PermissionNodeEditor editor);

    void removeChildEditor(PermissionNodeEditor editor);

    boolean hasChildEditors();

    void clearChildEditors();

    void onParentPermissionChanged(Permission permission,
                                   boolean on);

    void onChildPermissionChanged(PermissionNodeEditor childEditor,
                                  Permission permission,
                                  boolean on);

    boolean isAnException(Permission permission);

    int getExceptionNumber(Permission permission);
}
