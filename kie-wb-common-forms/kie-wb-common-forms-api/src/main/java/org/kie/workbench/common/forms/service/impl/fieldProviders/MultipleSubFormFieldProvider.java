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

import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldTypeInfo;
import org.kie.workbench.common.forms.service.FieldProvider;
import org.kie.workbench.common.forms.service.MultipleFieldProvider;

@Dependent
public class MultipleSubFormFieldProvider
        implements ModelTypeFieldProvider<MultipleSubFormFieldDefinition>,
        MultipleFieldProvider<MultipleSubFormFieldDefinition>,FieldProvider<MultipleSubFormFieldDefinition> {

    @Override
    public String getProviderCode() {
        return MultipleSubFormFieldDefinition.CODE;
    }

    @Override
    public MultipleSubFormFieldDefinition getDefaultField() {
        return new MultipleSubFormFieldDefinition();
    }

    @Override
    public MultipleSubFormFieldDefinition getFieldByType( FieldTypeInfo typeInfo ) {
        return new MultipleSubFormFieldDefinition();
    }

    @Override
    public boolean isCompatible( FieldDefinition field ) {
        Assert.notNull( "Field cannot be null", field );

        return field instanceof MultipleSubFormFieldDefinition;
    }
}
