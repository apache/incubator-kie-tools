/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.serviceEditor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.HasValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;

@Dependent
@Templated
public class GenericServiceTaskEditorWidget extends Composite implements HasValue<GenericServiceTaskValue> {

    private static final String JAVA = "Java";

    private static final String WEBSERVICE = "WebService";

    @Inject
    @DataField("implementation")
    private HTMLSelectElement implementation;

    @Inject
    @DataField("serviceInterface")
    private HTMLInputElement serviceInterface;

    @Inject
    @DataField("serviceOperation")
    private HTMLInputElement serviceOperation;

    private GenericServiceTaskValue value = new GenericServiceTaskValue();

    @PostConstruct
    public void init() {
        serviceInterface.type = "text";
        serviceOperation.type = "text";

        setServiceImplementationOptions(getImplementationOptions());
        implementation.value = (JAVA);
    }

    public void setServiceImplementationOptions(List<String> options) {
        clearSelect(implementation);
        options.forEach(option ->
                                implementation.add(newOption(option, option)));
    }

    public void setReadOnly(boolean readOnly) {
        implementation.disabled = (readOnly);
        serviceInterface.disabled = (readOnly);
        serviceOperation.disabled = (readOnly);
    }

    HTMLOptionElement newOption(final String text,
                                final String value) {
        final HTMLOptionElement option = (HTMLOptionElement) DomGlobal.document.createElement("option");
        option.textContent = (text);
        option.value = (value);
        return option;
    }

    void clearSelect(HTMLSelectElement select) {
        int options = select.options.getLength();
        for (int i = 0; i < options; i++) {
            select.remove(0);
        }
    }

    @EventHandler("implementation")
    void onImplementationChange(@ForEvent("change") final Event event) {
        onChange();
    }

    @EventHandler("serviceInterface")
    void onServiceInterfaceChange(@ForEvent("change") final Event event) {
        onChange();
    }

    @EventHandler("serviceOperation")
    void onServiceOperationChange(@ForEvent("change") final Event event) {
        onChange();
    }

    List<String> getImplementationOptions() {
        List<String> options = new ArrayList<>();
        options.add(JAVA);
        options.add(WEBSERVICE);
        return options;
    }

    @Override
    public GenericServiceTaskValue getValue() {
        return value;
    }

    @Override
    public void setValue(GenericServiceTaskValue value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(GenericServiceTaskValue newValue, boolean fireEvents) {
        GenericServiceTaskValue oldValue = value;
        value = newValue;

        implementation.value = (value.getServiceImplementation());
        serviceInterface.value = (value.getServiceInterface());
        serviceOperation.value = (value.getServiceOperation());

        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<GenericServiceTaskValue> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    protected void onChange() {
        GenericServiceTaskValue newValue = new GenericServiceTaskValue();
        newValue.setServiceOperation(serviceOperation.value);
        newValue.setServiceInterface(serviceInterface.value);
        newValue.setServiceImplementation(implementation.value);
        setValue(newValue, true);
    }
}
