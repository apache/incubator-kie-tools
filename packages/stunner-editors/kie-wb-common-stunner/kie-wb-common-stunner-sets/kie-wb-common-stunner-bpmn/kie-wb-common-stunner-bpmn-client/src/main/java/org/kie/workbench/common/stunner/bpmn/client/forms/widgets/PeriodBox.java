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


package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.Node;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Duration;

@Dependent
@Templated
public class PeriodBox extends Composite implements IsWidget,
                                                    HasValue<String> {

    private String current;

    @DataField
    @Inject
    private HTMLInputElement numberPeriod;

    @Inject
    @DataField
    private HTMLLabelElement inputPeriodLabel;

    @DataField
    @Inject
    private HelpBlock error;

    @DataField
    private Select unitPeriod = new Select();

    @Inject
    private HTMLDivElement divPeriodInputGroup;

    private HandlerManager handlerManager = createHandlerManager();

    @PostConstruct
    public void init() {
        initTypeSelector();
        initChangeHandlers();
    }

    private void initChangeHandlers() {
        numberPeriod.addEventListener("change", event -> {
            String value = numberPeriod.value;
            if (value.startsWith("-")) {
                addStyleName(ValidationState.ERROR.getCssName());
            } else if (value.matches("[0-9]*")) {
                if (getStyleName().contains(ValidationState.ERROR.getCssName())) {
                    removeStyleName(ValidationState.ERROR.getCssName());
                    error.setText("");
                }
            } else {
                value = "0";
            }
            setValue(value, true);
        });

        unitPeriod.addValueChangeHandler(event -> {
            String newValue = getValue();
            setValue(newValue, true);
        });
    }

    private void initTypeSelector() {
        Arrays.stream(Duration.values()).forEach(p -> {
            createOptionAndAddtoSelect(unitPeriod, p.getType(), p.getAlias());
        });
    }

    private void createOptionAndAddtoSelect(Select typeSelector, String name, String value) {
        Option option = new Option();
        option.setValue(value);
        option.setText(name);
        typeSelector.add(option);
    }

    @Override
    public String getValue() {
        return numberPeriod.value + "" + unitPeriod.getSelectedItem().getValue();
    }

    @Override
    public void setValue(String value) {
        parse(value);
        setValue(value, false);
    }

    public void showLabel(boolean show) {
        inputPeriodLabel.style.display = show ? "block" : "none";
     }

    private void parse(String value) {
        if (value != null && value.length() >= 2) {
            setNumberPeriod(value.substring(0, value.length() - 1));
            setUnitPeriod(value.substring(value.length() - 1));
        }
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        String oldValue = current;
        current = value;
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }

    private void setNumberPeriod(String unit) {
        if (unit.matches("[0-9]*")) {
            int intValue = 0;
            try {
                intValue = Integer.valueOf(unit);
            } catch (NumberFormatException e) {
                // nothing to do here, here 0 as value
            }
            numberPeriod.value = (intValue < 0 ? 0 : intValue) + "";
        } else {
            numberPeriod.value = "0";
        }
    }

    private void setUnitPeriod(String duration) {
        unitPeriod.setValue(Duration.get(duration).getAlias());
    }

    private void unitPeriodSelectorWidth() {
        for (int i = 0; i < divPeriodInputGroup.childNodes.length; i++) {
            Node element = divPeriodInputGroup.childNodes.item(i);
            if (element.nodeName.toLowerCase().equals("div")) {
                HTMLDivElement div = (HTMLDivElement) element;
                div.style.width = CSSProperties.WidthUnionType.of("90px");
            }
        }
    }

    public void clear() {
        setValue("1H");
        if (this.getStyleName().contains(ValidationState.ERROR.getCssName())) {
            this.removeStyleName(ValidationState.ERROR.getCssName());
        }
        error.setText("");
    }

    public void setErrorText(String text) {
        this.addStyleName(ValidationState.ERROR.getCssName());
        error.setError(text);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }
}
