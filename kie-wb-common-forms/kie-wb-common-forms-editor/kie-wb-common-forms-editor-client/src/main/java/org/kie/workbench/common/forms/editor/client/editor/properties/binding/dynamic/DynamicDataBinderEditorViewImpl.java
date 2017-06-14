/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor.properties.binding.dynamic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class DynamicDataBinderEditorViewImpl implements DynamicDataBinderEditorView,
                                                        IsElement {

    private Presenter presenter;

    @Inject
    @DataField
    private TextInput binding;

    @Override
    public void clear() {
        binding.setValue("");
    }

    @Override
    public void setFieldBinding(String fieldBinding) {
        binding.setValue(fieldBinding);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("binding")
    public void onChange(@ForEvent("change") Event event) {
        presenter.onBindingChange();
    }

    @Override
    public String getFieldBinding() {
        return binding.getValue();
    }
}
