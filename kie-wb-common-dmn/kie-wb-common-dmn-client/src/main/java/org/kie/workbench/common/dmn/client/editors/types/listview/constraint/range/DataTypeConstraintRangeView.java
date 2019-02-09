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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@Templated
@Dependent
public class DataTypeConstraintRangeView implements DataTypeConstraintRange.View {

    private DataTypeConstraintRange presenter;

    @DataField("start-value")
    private final HTMLInputElement startValue;

    @DataField("end-value")
    private final HTMLInputElement endValue;

    @DataField("include-end-value")
    private final HTMLInputElement includeEndValue;

    @DataField("include-start-value")
    private final HTMLInputElement includeStartValue;

    @Inject
    public DataTypeConstraintRangeView(final HTMLInputElement startValue,
                                       final HTMLInputElement endValue,
                                       final HTMLInputElement includeStartValue,
                                       final HTMLInputElement includeEndValue) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.includeStartValue = includeStartValue;
        this.includeEndValue = includeEndValue;
    }

    @Override
    public void init(final DataTypeConstraintRange presenter) {
        this.presenter = presenter;
        setupInputFields();
    }

    void setupInputFields() {
        startValue.onkeyup = this::onKeyUp;
        endValue.onkeyup = this::onKeyUp;
    }

    @Override
    public String getStartValue() {
        return startValue.value;
    }

    @Override
    public String getEndValue() {
        return endValue.value;
    }

    @Override
    public void setStartValue(final String value) {
        startValue.value = value;
    }

    @Override
    public void setEndValue(final String value) {
        endValue.value = value;
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

    Object onKeyUp(final Event event) {
        if (StringUtils.isEmpty(startValue.value) || StringUtils.isEmpty(endValue.value)) {
            presenter.disableOkButton();
        } else {
            presenter.enableOkButton();
        }
        return this;
    }
}
