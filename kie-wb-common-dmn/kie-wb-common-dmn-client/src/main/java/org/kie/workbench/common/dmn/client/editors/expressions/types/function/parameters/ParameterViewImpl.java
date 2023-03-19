/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Templated
@Dependent
public class ParameterViewImpl implements ParameterView {

    @DataField("name")
    private Input name;

    @DataField("typeRefEditor")
    private DataTypePickerWidget typeRefEditor;

    @DataField("remove")
    private Button remove;

    public ParameterViewImpl() {
        //CDI proxy
    }

    @Inject
    public ParameterViewImpl(final Input name,
                             final DataTypePickerWidget typeRefEditor,
                             final Button remove) {
        this.name = name;
        this.typeRefEditor = typeRefEditor;
        this.typeRefEditor.hideManageLabel();
        this.remove = remove;
    }

    @Override
    public void setName(final String name) {
        this.name.setValue(name);
    }

    @Override
    public void setTypeRef(final HasTypeRef hasTypeRef) {
        typeRefEditor.setDMNModel(hasTypeRef.asDMNModelInstrumentedBase());
        typeRefEditor.setValue(hasTypeRef.getTypeRef(), false);
    }

    @Override
    public void addRemoveClickHandler(final Command command) {
        remove.addEventListener(BrowserEvents.CLICK,
                                (e) -> command.execute(),
                                false);
    }

    @Override
    public void addParameterNameChangeHandler(final ParameterizedCommand<String> command) {
        name.addEventListener(BrowserEvents.BLUR,
                              (e) -> command.execute(name.getValue()),
                              false);
    }

    @Override
    public void addParameterTypeRefChangeHandler(final ParameterizedCommand<QName> command) {
        typeRefEditor.addValueChangeHandler(e -> command.execute(e.getValue()));
    }

    @Override
    public void focus() {
        name.focus();
    }
}
