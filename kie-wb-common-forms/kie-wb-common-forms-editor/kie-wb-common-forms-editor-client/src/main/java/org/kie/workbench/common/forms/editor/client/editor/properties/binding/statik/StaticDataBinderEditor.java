/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.properties.binding.statik;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRendererHelper;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBindingEditor;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.StaticFormModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.mvp.Command;

@StaticFormModel
@Dependent
public class StaticDataBinderEditor implements DataBindingEditor,
                                               StaticDataBinderEditorView.Presenter {

    private StaticDataBinderEditorView view;

    private boolean hasSelectedValue;

    protected Command onChangeCallback;

    @Inject
    public StaticDataBinderEditor(StaticDataBinderEditorView view) {
        this.view = view;
    }

    @PostConstruct
    public void setUp() {
        view.init(this);
    }

    @Override
    public void init(final FieldDefinition fieldDefinition,
                     final FieldPropertiesRendererHelper helper,
                     final Command onChangeCallback) {

        view.clear();

        this.onChangeCallback = onChangeCallback;

        hasSelectedValue = false;

        helper.getAvailableModelFields(fieldDefinition).forEach(property -> {
            if (property != null) {

                boolean isSelected = property.equals(fieldDefinition.getBinding());

                view.addModelField(property,
                                   isSelected);

                if (isSelected) {
                    hasSelectedValue = true;
                }
            }
        });

        view.addModelField("",
                           !hasSelectedValue);
    }

    @Override
    public String getBinding() {
        return view.getFieldBinding();
    }

    @Override
    public void onBindingChange() {
        if (onChangeCallback != null) {
            onChangeCallback.execute();
        }
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
