/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;

public class DoubleSliderDefinitionTest extends AbstractFieldDefinitionTest<DoubleSliderDefinition> {

    @Override
    protected DoubleSliderDefinition getEmptyFieldDefinition() {
        return new DoubleSliderDefinition();
    }

    @Override
    protected DoubleSliderDefinition getFullFieldDefinition() {
        DoubleSliderDefinition doubleSliderDefinition = new DoubleSliderDefinition();

        doubleSliderDefinition.setMin(-5.3);
        doubleSliderDefinition.setMax(25.8);
        doubleSliderDefinition.setStep(1.23);
        doubleSliderDefinition.setPrecision(2.0);

        return doubleSliderDefinition;
    }
}
