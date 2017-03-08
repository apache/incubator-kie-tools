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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class LeafPermissionNodeViewerView extends Composite
        implements LeafPermissionNodeViewer.View {

    @Inject
    @DataField
    Label nodeName;
    @DataField
    Element nodePermissions = DOM.createDiv();
    private LeafPermissionNodeViewer presenter;

    @Override
    public void init(LeafPermissionNodeViewer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setNodeName(String name) {
        nodeName.setText(name + ":");
    }

    @Override
    public void setNodeFullName(String name) {
        nodeName.setTitle(name);
    }

    @Override
    public void permissionGranted(String permission) {
        addPermission(permission,
                      "#00618a");
    }

    @Override
    public void permissionDenied(String permission) {
        addPermission(permission,
                      "#a30000");
    }

    private void addPermission(String permission,
                               String color) {
        Element div = DOM.createDiv();
        div.getStyle().setDisplay(Style.Display.TABLE_CELL);
        div.getStyle().setPaddingLeft(5,
                                      Style.Unit.PX);
        div.getStyle().setColor(color);
        div.setInnerText(permission);
        nodePermissions.appendChild(div);
    }
}
