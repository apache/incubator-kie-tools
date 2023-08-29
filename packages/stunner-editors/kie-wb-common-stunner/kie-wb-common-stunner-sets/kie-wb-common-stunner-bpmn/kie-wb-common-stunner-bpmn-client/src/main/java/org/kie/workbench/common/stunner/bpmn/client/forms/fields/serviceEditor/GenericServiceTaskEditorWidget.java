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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.serviceEditor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;

@Dependent
@Templated
public class GenericServiceTaskEditorWidget extends Composite implements HasValue<GenericServiceTaskValue> {

    private static final String JAVA = "Java";

    private static final String WEBSERVICE = "WebService";

    @Inject
    @DataField("implementation")
    private Select implementation;

    @Inject
    @DataField("serviceInterface")
    private TextInput serviceInterface;

    @Inject
    @DataField("serviceOperation")
    private TextInput serviceOperation;

    private GenericServiceTaskValue value = new GenericServiceTaskValue();

    @PostConstruct
    public void init() {
        setServiceImplementationOptions(getImplementationOptions());
        implementation.setValue(JAVA);
    }

    public void setServiceImplementationOptions(List<String> options) {
        clearSelect(implementation);
        options.forEach(option ->
                                implementation.add(newOption(option, option)));
    }

    public void setReadOnly(boolean readOnly) {
        implementation.setDisabled(readOnly);
        serviceInterface.setDisabled(readOnly);
        serviceOperation.setDisabled(readOnly);
    }

    Option newOption(final String text,
                             final String value) {
        final Option option = (Option) Window.getDocument().createElement("option");
        option.setTextContent(text);
        option.setValue(value);
        return option;
    }

    void clearSelect(Select select) {
        int options = select.getOptions().getLength();
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

        implementation.setValue(value.getServiceImplementation());
        serviceInterface.setValue(value.getServiceInterface());
        serviceOperation.setValue(value.getServiceOperation());

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
        newValue.setServiceOperation(serviceOperation.getValue());
        newValue.setServiceInterface(serviceInterface.getValue());
        newValue.setServiceImplementation(implementation.getValue());
        setValue(newValue, true);
    }
}
