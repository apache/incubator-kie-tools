/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.model.impl.basic.slider;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
public class DoubleSliderDefinition extends SliderBase<Double> {

    @FieldDef( label = "Min. Value" )
    protected Double min;

    @FieldDef( label = "Max. Value" )
    protected Double max;

    @FieldDef( label = "Precision" )
    protected Double precision;

    @FieldDef( label = "Step")
    protected Double step;

    public DoubleSliderDefinition() {
        min = new Double( 0.0 );
        max = new Double( 50.0 );
        precision = new Double( 1.0 );
        step =  new Double( 1.0 );
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    public void setMin( Double min ) {
        this.min = min;
    }

    @Override
    public Double getMax() {
        return max;
    }

    @Override
    public void setMax( Double max ) {
        this.max = max;
    }

    @Override
    public Double getPrecision() {
        return precision;
    }

    @Override
    public void setPrecision( Double precision ) {
        this.precision = precision;
    }

    @Override
    public Double getStep() {
        return step;
    }

    @Override
    public void setStep( Double step ) {
        this.step = step;
    }

    @Override
    protected void doCopyFrom( FieldDefinition other ) {
        if ( other instanceof SliderBase ) {
            SliderBase otherSlider = (SliderBase) other;
            min = otherSlider.getMin().doubleValue();
            max = otherSlider.getMax().doubleValue();
            precision = otherSlider.getPrecision().doubleValue();
            step = otherSlider.getStep().doubleValue();
        }
    }
}
