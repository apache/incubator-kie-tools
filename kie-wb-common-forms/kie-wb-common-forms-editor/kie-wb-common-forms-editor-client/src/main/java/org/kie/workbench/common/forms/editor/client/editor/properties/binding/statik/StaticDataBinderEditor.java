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

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBindingEditor;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.StaticFormModel;
import org.kie.workbench.common.forms.model.FieldDefinition;

@StaticFormModel
@Dependent
public class StaticDataBinderEditor implements DataBindingEditor,
                                               StaticDataBinderEditorView.Presenter {

    private StaticDataBinderEditorView view;

    private boolean hasSelectedValue;

    private Consumer<String> bindingChangeConsumer;

    @Inject
    public StaticDataBinderEditor(StaticDataBinderEditorView view) {
        this.view = view;
    }

    @PostConstruct
    public void setUp() {
        view.init(this);
    }

    @Override
    public void init(FieldDefinition fieldDefinition,
                     Supplier<Collection<String>> bindingsSupplier,
                     Consumer<String> bindingChangeConsumer) {

        PortablePreconditions.checkNotNull("fieldDefinition",
                                           fieldDefinition);
        PortablePreconditions.checkNotNull("bindingsSupplier",
                                           bindingsSupplier);
        PortablePreconditions.checkNotNull("bindingChangeConsumer",
                                           bindingChangeConsumer);

        this.bindingChangeConsumer = bindingChangeConsumer;

        view.clear();

        hasSelectedValue = false;

        bindingsSupplier.get().forEach(property -> {
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
    public void onBindingChange() {
        bindingChangeConsumer.accept(view.getFieldBinding());
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
