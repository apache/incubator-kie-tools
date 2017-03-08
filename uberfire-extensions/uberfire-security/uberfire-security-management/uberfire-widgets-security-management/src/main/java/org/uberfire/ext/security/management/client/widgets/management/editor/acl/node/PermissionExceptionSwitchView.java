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
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.security.management.client.resources.i18n.SecurityManagementConstants;

@Dependent
@Templated
public class PermissionExceptionSwitchView extends Composite
        implements PermissionExceptionSwitch.View {

    @Inject
    @DataField
    FlowPanel togglePanel;

    @Inject
    @DataField
    Span badge;

    ToggleSwitch switchControl;
    PermissionExceptionSwitch presenter;

    @Override
    public void init(PermissionExceptionSwitch presenter) {
        this.presenter = presenter;
    }

    @Override
    public void init(String textOn,
                     String textOff) {
        switchControl = new ToggleSwitch();
        switchControl.setAnimate(true);
        switchControl.setSize(SizeType.MINI);
        switchControl.setOnText(textOn != null ? textOn : SecurityManagementConstants.INSTANCE.switchAllow());
        switchControl.setOffText(textOff != null ? textOff : SecurityManagementConstants.INSTANCE.switchDeny());
        switchControl.addValueChangeHandler(event -> presenter.onChange());
        togglePanel.add(switchControl);
    }

    @Override
    public void setExceptionEnabled(boolean enabled) {
        if (enabled) {
            badge.getStyle().setProperty("display",
                                         "table-cell");
        } else {
            badge.getStyle().setProperty("display",
                                         "none");
        }
    }

    @Override
    public boolean isOn() {
        return switchControl.getValue();
    }

    @Override
    public void setOn(boolean on) {
        switchControl.setValue(on);
    }

    @Override
    public void setEnabled(boolean enabled) {
        switchControl.setEnabled(enabled);
    }
}
