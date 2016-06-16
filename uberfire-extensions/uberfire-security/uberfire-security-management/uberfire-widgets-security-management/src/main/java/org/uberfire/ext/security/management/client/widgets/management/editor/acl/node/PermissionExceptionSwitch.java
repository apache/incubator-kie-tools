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

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class PermissionExceptionSwitch implements PermissionSwitchToogle {

    public interface View extends UberView<PermissionExceptionSwitch> {

        void init(String textOn, String textOff, boolean on);

        void allow();

        void deny();

        void none();

        boolean isOn();

        void setOn(boolean on);
    }

    View view;
    Command onChange;
    boolean isException;
    boolean overwriteOn;

    @Inject
    public PermissionExceptionSwitch(View view) {
        this.view = view;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void init(String switchOnName, String switchOffName, boolean on, boolean isException) {
        this.isException = isException;
        this.overwriteOn = isException;

        view.init(switchOnName, switchOffName, on);
        updateOverwrite();
    }

    @Override
    public void setOnChange(Command onChange) {
        this.onChange = onChange;
    }

    public boolean isOn() {
        return view.isOn();
    }

    @Override
    public void toogle() {
        if (isException) {
            overwriteOn = !overwriteOn;
            updateOverwrite();
        } else {
            view.setOn(!view.isOn());
        }
    }

    public void onChange() {
        onChange.execute();
        isException = true;
        overwriteOn = !overwriteOn;
        updateOverwrite();
    }

    private void updateOverwrite() {
        if (overwriteOn) {
            if (view.isOn()) {
                view.allow();
            } else {
                view.deny();
            }
        } else {
            view.none();
        }
    }
}
