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

package org.kie.workbench.common.dmn.client.editors.types;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Templated
@ApplicationScoped
public class DataTypeEditorViewImpl implements DataTypeEditorView {

    static final String OPEN = "open";

    @DataField("typeRefSelector")
    private DataTypePickerWidget typeRefEditor;

    private Presenter presenter;

    public DataTypeEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public DataTypeEditorViewImpl(final DataTypePickerWidget typeRefEditor) {
        this.typeRefEditor = typeRefEditor;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        typeRefEditor.addValueChangeHandler(e -> presenter.setTypeRef(e.getValue()));
    }

    @Override
    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        typeRefEditor.setDMNModel(dmnModel);
    }

    @Override
    public void initSelectedTypeRef(final QName typeRef) {
        typeRefEditor.setValue(typeRef,
                               false);
    }

    @Override
    public void show() {
        getElement().getClassList().add(OPEN);
    }

    @Override
    public void hide() {
        getElement().getClassList().remove(OPEN);
    }
}
