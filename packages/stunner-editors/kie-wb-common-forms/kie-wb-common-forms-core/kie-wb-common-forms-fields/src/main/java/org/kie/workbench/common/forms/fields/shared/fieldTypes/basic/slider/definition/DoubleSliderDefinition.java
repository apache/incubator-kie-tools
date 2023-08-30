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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class DoubleSliderDefinition extends SliderBaseDefinition<Double> {

    @FormField(
            labelKey = "slider.min",
            afterElement = "label"
    )
    protected Double min;

    @FormField(
            labelKey = "slider.max",
            afterElement = "min"
    )
    protected Double max;

    @FormField(
            labelKey = "slider.precision",
            afterElement = "max"
    )
    protected Double precision;

    @FormField(
            labelKey = "slider.step",
            afterElement = "precision"
    )
    protected Double step;

    public DoubleSliderDefinition() {
        super(Double.class.getName());
        min = new Double(0.0);
        max = new Double(50.0);
        precision = new Double(1.0);
        step = new Double(1.0);
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    public void setMin(Double min) {
        this.min = min;
    }

    @Override
    public Double getMax() {
        return max;
    }

    @Override
    public void setMax(Double max) {
        this.max = max;
    }

    @Override
    public Double getPrecision() {
        return precision;
    }

    @Override
    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    @Override
    public Double getStep() {
        return step;
    }

    @Override
    public void setStep(Double step) {
        this.step = step;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof SliderBaseDefinition) {
            SliderBaseDefinition otherSlider = (SliderBaseDefinition) other;
            min = otherSlider.getMin().doubleValue();
            max = otherSlider.getMax().doubleValue();
            precision = otherSlider.getPrecision().doubleValue();
            step = otherSlider.getStep().doubleValue();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        DoubleSliderDefinition that = (DoubleSliderDefinition) o;

        if (min != null ? !min.equals(that.min) : that.min != null) {
            return false;
        }
        if (max != null ? !max.equals(that.max) : that.max != null) {
            return false;
        }
        if (precision != null ? !precision.equals(that.precision) : that.precision != null) {
            return false;
        }
        return step != null ? step.equals(that.step) : that.step == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (step != null ? step.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
