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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@ApplicationScoped
public class DataTypeEditorImpl implements DataTypeEditorView.Presenter {

    private DataTypeEditorView view;
    private Optional<HasTypeRef> binding = Optional.empty();

    public DataTypeEditorImpl() {
        //CDI proxy
    }

    @Inject
    public DataTypeEditorImpl(final DataTypeEditorView view) {
        this.view = view;

        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void bind(final HasTypeRef bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        binding = Optional.ofNullable(bound);
        refresh();
    }

    private void refresh() {
        binding.ifPresent(b -> {
            view.setDMNModel(b.asDMNModelInstrumentedBase());
            view.initSelectedTypeRef(b.getTypeRef());
        });
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        binding.ifPresent(b -> b.setTypeRef(typeRef));
    }

    @Override
    public void show() {
        binding.ifPresent(b -> view.show());
    }

    @Override
    public void hide() {
        binding.ifPresent(b -> view.hide());
    }
}
