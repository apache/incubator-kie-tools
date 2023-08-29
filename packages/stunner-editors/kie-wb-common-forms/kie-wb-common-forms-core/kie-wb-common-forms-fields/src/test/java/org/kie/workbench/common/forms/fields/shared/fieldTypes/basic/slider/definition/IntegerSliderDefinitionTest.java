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

import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;

public class IntegerSliderDefinitionTest extends AbstractFieldDefinitionTest<IntegerSliderDefinition> {

    @Override
    protected IntegerSliderDefinition getEmptyFieldDefinition() {
        return new IntegerSliderDefinition();
    }

    @Override
    protected IntegerSliderDefinition getFullFieldDefinition() {
        IntegerSliderDefinition integerSliderDefinition = new IntegerSliderDefinition();

        integerSliderDefinition.setMin(-5);
        integerSliderDefinition.setMax(25);
        integerSliderDefinition.setStep(1);

        return integerSliderDefinition;
    }
}
