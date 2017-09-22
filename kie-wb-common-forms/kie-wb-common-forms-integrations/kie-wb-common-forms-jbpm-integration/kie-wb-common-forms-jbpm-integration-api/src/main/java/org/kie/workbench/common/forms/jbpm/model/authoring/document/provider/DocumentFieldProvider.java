/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.model.authoring.document.provider;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.type.DocumentFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class DocumentFieldProvider extends BasicTypeFieldProvider<DocumentFieldDefinition> {

    @Override
    public Class<DocumentFieldType> getFieldType() {
        return DocumentFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return DocumentFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public DocumentFieldDefinition getDefaultField() {
        return new DocumentFieldDefinition();
    }

    @Override
    public int getPriority() {
        return 15;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(DocumentFieldType.DOCUMENT_TYPE);
        registerPropertyType(DocumentFieldType.DOCUMENT_IMPL_TYPE);
    }

    @Override
    public DocumentFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return new DocumentFieldDefinition();
    }
}
