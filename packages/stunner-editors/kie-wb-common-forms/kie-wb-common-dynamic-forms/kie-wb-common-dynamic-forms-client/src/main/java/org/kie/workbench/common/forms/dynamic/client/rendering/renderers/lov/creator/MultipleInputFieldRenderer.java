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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters.ListToListConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInput;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.AbstractMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputFieldType;

@Dependent
@Renderer(type = MultipleInputFieldType.class)
public class MultipleInputFieldRenderer extends FieldRenderer<AbstractMultipleInputFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    private MultipleInput input;

    @Inject
    public MultipleInputFieldRenderer(MultipleInput input) {
        this.input = input;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        input.setPageSize(field.getPageSize());
        input.init(field.getStandaloneClassName());

        if(field.getReadOnly() || !renderMode.equals(RenderMode.EDIT_MODE)) {
            setReadOnly(true);
        }

        formGroup.render(input.asWidget(), field);

        return formGroup;
    }

    @Override
    public String getName() {
        return MultipleInputFieldType.NAME;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        input.setReadOnly(readOnly);
    }

    @Override
    public Converter getConverter() {
        return new ListToListConverter();
    }
}
