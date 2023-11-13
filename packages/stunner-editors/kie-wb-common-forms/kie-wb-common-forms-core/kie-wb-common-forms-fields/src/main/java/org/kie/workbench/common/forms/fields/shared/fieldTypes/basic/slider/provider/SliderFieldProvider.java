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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.provider;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.DoubleSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.IntegerSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.SliderBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class SliderFieldProvider extends BasicTypeFieldProvider<SliderBaseDefinition> {

    @Override
    public Class<SliderFieldType> getFieldType() {
        return SliderFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return SliderBaseDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(Number.class);
        registerPropertyType(Double.class);
        registerPropertyType(double.class);
        registerPropertyType(Integer.class);
        registerPropertyType(int.class);
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public SliderBaseDefinition getDefaultField() {
        return new DoubleSliderDefinition();
    }

    @Override
    public SliderBaseDefinition createFieldByType(TypeInfo typeInfo) {
        if (Integer.class.getName().equals(typeInfo.getClassName()) ||
                int.class.getName().equals(typeInfo.getClassName())) {
            return new IntegerSliderDefinition();
        }
        return new DoubleSliderDefinition();
    }
}
