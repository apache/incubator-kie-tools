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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Dependent
public class ValueAndDataTypePopoverImpl implements ValueAndDataTypePopoverView.Presenter {

    static final String BINDING_EXCEPTION = "Popover has not been bound.";

    private ValueAndDataTypePopoverView view;
    private Optional<HasValueAndTypeRef> binding = Optional.empty();

    public ValueAndDataTypePopoverImpl() {
        //CDI proxy
    }

    @Inject
    public ValueAndDataTypePopoverImpl(final ValueAndDataTypePopoverView view) {
        this.view = view;

        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public String getPopoverTitle() {
        return binding.orElseThrow(() -> new IllegalStateException(BINDING_EXCEPTION)).getPopoverTitle();
    }

    @Override
    public void bind(final HasValueAndTypeRef bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        binding = Optional.ofNullable(bound);
        refresh();
    }

    @SuppressWarnings("unchecked")
    private void refresh() {
        binding.ifPresent(b -> {
            view.setDMNModel(b.asDMNModelInstrumentedBase());
            view.initValue(b.toWidgetValue(b.getValue()));
            view.initSelectedTypeRef(b.getTypeRef());
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(final String value) {
        binding.ifPresent(b -> b.setValue(b.toModelValue(value)));
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        binding.ifPresent(b -> b.setTypeRef(typeRef));
    }

    @Override
    public String getValueLabel() {
        return binding.orElseThrow(() -> new IllegalStateException(BINDING_EXCEPTION)).getValueLabel();
    }

    @Override
    public String normaliseValue(final String value) {
        return binding.orElseThrow(() -> new IllegalStateException(BINDING_EXCEPTION)).normaliseValue(value);
    }

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer<CanBeClosedByKeyboard> callback) {
        binding.ifPresent(b -> view.setOnClosedByKeyboardCallback(callback));
    }

    @Override
    public void show() {
        binding.ifPresent(b -> view.show(Optional.ofNullable(getPopoverTitle())));
    }

    @Override
    public void hide() {
        binding.ifPresent(b -> view.hide());
    }

    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        hide();
    }
}
