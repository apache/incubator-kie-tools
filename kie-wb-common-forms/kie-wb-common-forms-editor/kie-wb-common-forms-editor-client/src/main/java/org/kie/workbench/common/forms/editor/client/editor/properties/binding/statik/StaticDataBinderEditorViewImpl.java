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

package org.kie.workbench.common.forms.editor.client.editor.properties.binding.statik;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class StaticDataBinderEditorViewImpl implements StaticDataBinderEditorView,
                                                       IsElement {

    private Presenter presenter;

    @Inject
    @DataField
    private Select bindings;

    @Inject
    private Document document;

    @Override
    public void clear() {
        while (bindings.getLength() > 0) {
            bindings.remove(0);
        }
    }

    @Override
    public void addModelField(String property,
                              boolean selected) {
        Option option = (Option) document.createElement("option");

        option.setText(property);
        option.setValue(property);
        option.setSelected(selected);
        bindings.add(option);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("bindings")
    public void onChange(@ForEvent("change") Event event) {
        presenter.onBindingChange(bindings.getValue());
    }
}
