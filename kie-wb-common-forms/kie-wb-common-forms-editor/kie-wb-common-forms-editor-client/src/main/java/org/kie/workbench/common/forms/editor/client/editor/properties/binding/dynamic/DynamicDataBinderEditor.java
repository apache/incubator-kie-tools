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

package org.kie.workbench.common.forms.editor.client.editor.properties.binding.dynamic;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBindingEditor;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DynamicFormModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.commons.validation.PortablePreconditions;

@DynamicFormModel
@Dependent
public class DynamicDataBinderEditor implements DataBindingEditor,
                                                DynamicDataBinderEditorView.Presenter {

    private DynamicDataBinderEditorView view;

    private Consumer<String> bindingChangeConsumer;

    @Inject
    public DynamicDataBinderEditor(DynamicDataBinderEditorView view) {
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

        view.setFieldBinding(Optional.ofNullable(fieldDefinition.getBinding()).orElse(""));
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
