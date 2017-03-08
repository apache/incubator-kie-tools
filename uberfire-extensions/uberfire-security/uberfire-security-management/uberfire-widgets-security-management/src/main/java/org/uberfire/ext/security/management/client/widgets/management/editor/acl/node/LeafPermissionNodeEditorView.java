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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class LeafPermissionNodeEditorView extends Composite
        implements LeafPermissionNodeEditor.View {

    @Inject
    @DataField
    Label nodeName;
    @Inject
    @DataField
    Span nodeNameHelp;
    @Inject
    @DataField
    Div nodeNamePanel;
    @Inject
    @DataField
    FlowPanel nodePermissions;
    private LeafPermissionNodeEditor presenter;

    @Override
    public void init(LeafPermissionNodeEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setNodeName(String name) {
        nodeName.setText(name);
    }

    @Override
    public void setNodePanelWidth(int width) {
        nodeNamePanel.getStyle().setProperty("width",
                                             width + "px");
    }

    @Override
    public void setNodeFullName(String name) {
        nodeName.setTitle(name);
        nodeNameHelp.setTitle(name);
        nodeNameHelp.setClassName("acl-help-panel");
    }

    @Override
    public void addPermission(PermissionSwitchToogle permissionSwitch) {
        nodePermissions.add(permissionSwitch);
    }
}
