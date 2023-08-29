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


package org.kie.workbench.common.forms.adf.engine.shared.test;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.AbstractFieldElementProcessor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.HasMaxLengthFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.HasPlaceHolderFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.HasRowsFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.TextAreaFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.MultipleSubFormFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.selectors.SelectorFieldInitilizer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.slider.DoubleSliderFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.slider.IntegerSliderFieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.service.shared.FieldManager;

public class TestFieldElementProcessor extends AbstractFieldElementProcessor {

    public TestFieldElementProcessor(FieldManager fieldManager,
                                     PropertyValueExtractor propertyValueExtractor) {
        super(fieldManager,
              propertyValueExtractor);
        registerInitializer(new HasMaxLengthFieldInitializer());
        registerInitializer(new HasPlaceHolderFieldInitializer());
        registerInitializer(new HasRowsFieldInitializer());
        registerInitializer(new SubFormFieldInitializer());
        registerInitializer(new MultipleSubFormFieldInitializer());
        registerInitializer(new IntegerSliderFieldInitializer());
        registerInitializer(new DoubleSliderFieldInitializer());
        registerInitializer(new TextAreaFieldInitializer());
        registerInitializer(new SelectorFieldInitilizer());
    }
}
