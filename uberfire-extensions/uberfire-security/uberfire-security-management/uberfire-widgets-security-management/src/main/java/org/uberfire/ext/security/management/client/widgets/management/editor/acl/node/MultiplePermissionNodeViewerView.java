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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.security.management.client.resources.i18n.SecurityManagementConstants;

@Dependent
@Templated
public class MultiplePermissionNodeViewerView extends Composite
        implements MultiplePermissionNodeViewer.View {

    @Inject
    @DataField
    Anchor nodeAnchor;
    @Inject
    @DataField
    Div nodeAnchorPanel;
    @Inject
    @DataField
    FlowPanel nodePermissions;
    @Inject
    @DataField
    Div collapsePanel;
    @Inject
    @DataField
    FlowPanel nodeChildren;
    private MultiplePermissionNodeViewer presenter;

    @Override
    public void init(MultiplePermissionNodeViewer presenter) {
        this.presenter = presenter;

        String collapseId = Document.get().createUniqueId();
        collapsePanel.setId(collapseId);

        nodeAnchor.setDataToggle(Toggle.COLLAPSE);
        nodeAnchor.setDataTarget("#" + collapseId);
    }

    @Override
    public void setNodeName(String name) {
        nodeAnchor.setText(name);
    }

    @Override
    public void setNodeFullName(String name) {
        nodeAnchor.setTitle(name);
    }

    @Override
    public void setPermissionsVisible(boolean enabled) {
        nodePermissions.setVisible(enabled);
    }

    @Override
    public void addChildViewer(PermissionNodeViewer viewer) {
        nodeChildren.add(viewer);
    }

    private Label createLabel(String text,
                              String color) {
        Label l = new Label(text);
        l.getElement().getStyle().setMarginLeft(3,
                                                Style.Unit.PX);
        if (color != null) {
            l.getElement().getStyle().setColor(color);
        }
        return l;
    }

    private void addPermissionMessage(String preffix,
                                      String permission,
                                      String inffix,
                                      String resource,
                                      String suffix,
                                      String color) {
        HorizontalPanel panel = new HorizontalPanel();
        if (preffix != null) {
            Label l = createLabel(preffix,
                                  null);
            panel.add(l);
        }
        if (permission != null) {
            Label l = createLabel(permission,
                                  color);
            panel.add(l);
        }
        if (inffix != null) {
            Label l = createLabel(inffix,
                                  null);
            panel.add(l);
        }
        if (resource != null) {
            Label l = createLabel(resource,
                                  null);
            panel.add(l);
        }
        if (suffix != null) {
            Label l = createLabel(suffix,
                                  null);
            panel.add(l);
        }
        nodePermissions.add(panel);
    }

    @Override
    public void addAllItemsGrantedPermission(String permission,
                                             String resource) {
        String can = SecurityManagementConstants.INSTANCE.can();
        addPermissionMessage(can,
                             permission,
                             null,
                             resource,
                             null,
                             "#00618a");
    }

    @Override
    public void addAllItemsDeniedPermission(String permission,
                                            String resource) {
        String cant = SecurityManagementConstants.INSTANCE.cant();
        addPermissionMessage(cant,
                             permission,
                             null,
                             resource,
                             null,
                             "#a30000");
    }

    @Override
    public void addItemsGrantedPermission(String permission,
                                          String resource) {
        String can = SecurityManagementConstants.INSTANCE.can();
        String all = SecurityManagementConstants.INSTANCE.all();
        String but = SecurityManagementConstants.INSTANCE.but();
        addPermissionMessage(can,
                             permission,
                             all,
                             resource,
                             but + ":",
                             "#00618a");
    }

    @Override
    public void addItemsDeniedPermission(String permission,
                                         String resource) {
        String canOnly = SecurityManagementConstants.INSTANCE.canOnly();
        String following = SecurityManagementConstants.INSTANCE.following();
        addPermissionMessage(canOnly,
                             permission,
                             following,
                             resource + ":",
                             null,
                             "#a30000");
    }

    @Override
    public void addItemException(String item) {
        HorizontalPanel panel = new HorizontalPanel();
        Label l = createLabel("- " + item,
                              null);
        panel.add(l);
        panel.getElement().getStyle().setMarginLeft(15,
                                                    Style.Unit.PX);
        nodePermissions.add(panel);
    }
}
