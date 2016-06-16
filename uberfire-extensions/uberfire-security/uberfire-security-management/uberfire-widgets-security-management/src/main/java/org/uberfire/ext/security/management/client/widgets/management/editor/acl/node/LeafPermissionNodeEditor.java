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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionChangedEvent;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.PermissionNode;

@Dependent
public class LeafPermissionNodeEditor extends BasePermissionNodeEditor {

    public interface View extends UberView<LeafPermissionNodeEditor> {

        void setNodeName(String name);

        void setNodePanelWidth(int width);

        void setNodeFullName(String name);

        void addPermission(PermissionSwitchToogle permissionSwitch);
    }

    View view;
    PermissionWidgetFactory widgetFactory;
    Event<PermissionChangedEvent> permissionChangedEvent;
    Map<Permission, PermissionSwitchToogle> permissionSwitchMap = new HashMap<>();

    @Inject
    public LeafPermissionNodeEditor(View view, PermissionWidgetFactory widgetFactory, Event<PermissionChangedEvent> permissionChangedEvent) {
        this.view = view;
        this.widgetFactory = widgetFactory;
        this.permissionChangedEvent = permissionChangedEvent;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void edit(PermissionNode node) {
        permissionNode = node;
        permissionSwitchMap.clear();

        String name = node.getNodeName();
        String fullName = node.getNodeFullName();

        view.setNodeName(name);
        view.setNodePanelWidth(getNodePanelWidth());

        if (fullName != null && !fullName.equals(name)) {
            view.setNodeFullName(fullName);
        }

        PermissionNodeEditor parentEditor = getParentEditor();
        for (Permission permission : permissionNode.getPermissionList()) {
            String grantName = node.getPermissionGrantName(permission);
            String denyName = node.getPermissionDenyName(permission);
            boolean granted = AuthorizationResult.ACCESS_GRANTED.equals(permission.getResult());

            PermissionSwitchToogle switchToogle = null;
            if (parentEditor == null || parentEditor.getPermissionNode().getPermissionList().isEmpty()) {
                PermissionSwitch permissionSwitch = widgetFactory.createSwitch();
                permissionSwitch.init(grantName, denyName, granted, 0);
                switchToogle = permissionSwitch;
            }
            else {
                PermissionExceptionSwitch permissionSwitch = widgetFactory.createExceptionSwitch();
                boolean isException = parentEditor.isAnException(permission);
                permissionSwitch.init(grantName, denyName, granted, isException);
                switchToogle = permissionSwitch;
            }
            initPermissionSwitchToogle(switchToogle, permission);
            permissionSwitchMap.put(permission, switchToogle);
            view.addPermission(switchToogle);
        }
    }

    private void initPermissionSwitchToogle(PermissionSwitchToogle permissionSwitch, Permission permission) {
        permissionSwitch.setOnChange(() -> {
            permission.setResult(permissionSwitch.isOn() ? AuthorizationResult.ACCESS_GRANTED : AuthorizationResult.ACCESS_DENIED);
            onPermissionChanged(permission, permissionSwitch.isOn());
            permissionChangedEvent.fire(new PermissionChangedEvent(getACLEditor(), permission, permissionSwitch.isOn()));
        });
    }

    @Override
    public void onParentPermissionChanged(Permission permission, boolean on) {
        for (Permission p : permissionSwitchMap.keySet()) {
            if (permission.impliesName(p)) {
                PermissionSwitchToogle switchToogle = permissionSwitchMap.get(p);
                switchToogle.toogle();
            }
        }
    }

    @Override
    protected void onNodePanelWidthChanged() {
        int width = getNodePanelWidth();
        view.setNodePanelWidth(width);
    }
}
