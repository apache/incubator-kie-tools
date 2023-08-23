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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@Templated
@Dependent
public class DataTypeConstraintRangeView implements DataTypeConstraintRange.View {

    private final TypedValueComponentSelector startValueComponentSelector;

    private final TypedValueComponentSelector endValueComponentSelector;

    private TypedValueSelector startValueComponent;

    private TypedValueSelector endValueComponent;

    private DataTypeConstraintRange presenter;

    @DataField("end-value-container")
    private final HTMLDivElement endValueContainer;

    @DataField("start-value-container")
    private final HTMLDivElement startValueContainer;

    @DataField("include-end-value")
    private final HTMLInputElement includeEndValue;

    @DataField("include-start-value")
    private final HTMLInputElement includeStartValue;

    @Inject
    public DataTypeConstraintRangeView(final HTMLDivElement startValueContainer,
                                       final HTMLDivElement endValueContainer,
                                       final HTMLInputElement includeStartValue,
                                       final HTMLInputElement includeEndValue,
                                       final TypedValueComponentSelector startValueComponentSelector,
                                       final TypedValueComponentSelector endValueComponentSelector) {
        this.startValueComponentSelector = startValueComponentSelector;
        this.endValueComponentSelector = endValueComponentSelector;
        this.startValueContainer = startValueContainer;
        this.endValueContainer = endValueContainer;
        this.includeStartValue = includeStartValue;
        this.includeEndValue = includeEndValue;
    }

    @Override
    public void init(final DataTypeConstraintRange presenter) {
        this.presenter = presenter;
    }

    void setupInputFields() {
        startValueComponent.setOnInputChangeCallback(this::onValueChanged);
        endValueComponent.setOnInputChangeCallback(this::onValueChanged);
    }

    @Override
    public String getStartValue() {
        return startValueComponent.getValue();
    }

    @Override
    public String getEndValue() {
        return endValueComponent.getValue();
    }

    @Override
    public void setStartValue(final String value) {
        startValueComponent.setValue(value);
    }

    @Override
    public void setEndValue(final String value) {
        endValueComponent.setValue(value);
    }

    @Override
    public boolean getIncludeStartValue() {
        return this.includeStartValue.checked;
    }

    @Override
    public void setIncludeStartValue(boolean includeStartValue) {
        this.includeStartValue.checked = includeStartValue;
    }

    @Override
    public boolean getIncludeEndValue() {
        return this.includeEndValue.checked;
    }

    @Override
    public void setIncludeEndValue(boolean includeEndValue) {
        this.includeEndValue.checked = includeEndValue;
    }

    @Override
    public void setPlaceholders(final String placeholder) {
        startValueComponent.setPlaceholder(placeholder);
        endValueComponent.setPlaceholder(placeholder);
    }

    @Override
    public void setComponentSelector(final String type) {
        startValueComponent = this.startValueComponentSelector.makeSelectorForType(type);
        RemoveHelper.removeChildren(startValueContainer);
        startValueContainer.appendChild(startValueComponent.getElement());

        endValueComponent = this.endValueComponentSelector.makeSelectorForType(type);
        RemoveHelper.removeChildren(endValueContainer);
        endValueContainer.appendChild(endValueComponent.getElement());

        setupInputFields();
    }

    void onValueChanged(final Object event) {
        if (StringUtils.isEmpty(startValueComponent.getValue()) || StringUtils.isEmpty(endValueComponent.getValue())) {
            presenter.disableOkButton();
        } else {
            presenter.enableOkButton();
        }
    }
}
