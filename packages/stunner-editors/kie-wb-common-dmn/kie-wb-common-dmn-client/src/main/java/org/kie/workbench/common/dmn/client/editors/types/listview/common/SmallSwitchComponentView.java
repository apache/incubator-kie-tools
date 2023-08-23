/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SmallSwitchComponentView implements SmallSwitchComponent.View {

    @DataField("input-checkbox")
    private final HTMLInputElement inputCheckbox;

    private SmallSwitchComponent presenter;

    private Consumer<Boolean> onValueChanged;

    @Inject
    public SmallSwitchComponentView(final HTMLInputElement inputCheckbox) {
        this.inputCheckbox = inputCheckbox;
    }

    @Override
    public void init(final SmallSwitchComponent presenter) {
        this.presenter = presenter;
    }

    @EventHandler("input-checkbox")
    public void onInputCheckBoxChange(final ChangeEvent e) {
        callOnValueChanged();
    }

    void callOnValueChanged() {
        if (!Objects.isNull(onValueChanged)) {
            onValueChanged.accept(isChecked());
        }
    }

    @Override
    public boolean getValue() {
        return isChecked();
    }

    @Override
    public void setValue(final boolean value) {
        inputCheckbox.checked = value;
    }

    @Override
    public void setOnValueChanged(final Consumer<Boolean> onValueChanged) {
        this.onValueChanged = onValueChanged;
    }

    private boolean isChecked() {
        return inputCheckbox.checked;
    }
}
