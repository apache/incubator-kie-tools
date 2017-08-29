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

package org.kie.workbench.common.forms.data.modeller.service.indexing;

import javax.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.editor.backend.indexing.FormModelVisitor;
import org.kie.workbench.common.forms.editor.backend.indexing.FormModelVisitorProvider;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

@Dependent
public class DataModellerFormModelVisitor extends FormModelVisitor<DataObjectFormModel> implements FormModelVisitorProvider<DataObjectFormModel> {

    @Override
    public Class<DataObjectFormModel> getModelType() {
        return DataObjectFormModel.class;
    }

    @Override
    public FormModelVisitor<DataObjectFormModel> getVisitor() {
        return new DataModellerFormModelVisitor();
    }

    @Override
    public void index(FormDefinition formDefinition, DataObjectFormModel formModel) {
        ResourceReference reference = addResourceReference(formModel.getClassName(),
                                                           ResourceType.JAVA);

        formDefinition.getFields().forEach(fieldDefinition -> visitField(reference, fieldDefinition));
    }

    protected void visitField(ResourceReference reference, FieldDefinition fieldDefinition) {
        if (!StringUtils.isEmpty(fieldDefinition.getBinding())) {
            reference.addPartReference(fieldDefinition.getBinding(), PartType.FIELD);
        }
    }
}
