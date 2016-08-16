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

package org.kie.workbench.common.forms.service.impl.fieldProviders;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.model.FieldTypeInfo;
import org.kie.workbench.common.forms.model.impl.basic.slider.DoubleSliderDefinition;
import org.kie.workbench.common.forms.model.impl.basic.slider.IntegerSliderDefinition;
import org.kie.workbench.common.forms.model.impl.basic.slider.SliderBase;

@Dependent
public class SliderFieldProvider extends BasicTypeFieldProvider<SliderBase> {

    @Override
    public String getProviderCode() {
        return SliderBase.CODE;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType( Number.class );
        registerPropertyType( Double.class );
        registerPropertyType( double.class );
        registerPropertyType( Integer.class );
        registerPropertyType( int.class );
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public SliderBase getDefaultField() {
        return new DoubleSliderDefinition();
    }

    @Override
    public SliderBase createFieldByType( FieldTypeInfo typeInfo ) {
        if ( Integer.class.getName().equals( typeInfo.getType() ) ||
                int.class.getName().equals( typeInfo.getType() )) {
            return new IntegerSliderDefinition();
        }
        return new DoubleSliderDefinition();
    }
}
