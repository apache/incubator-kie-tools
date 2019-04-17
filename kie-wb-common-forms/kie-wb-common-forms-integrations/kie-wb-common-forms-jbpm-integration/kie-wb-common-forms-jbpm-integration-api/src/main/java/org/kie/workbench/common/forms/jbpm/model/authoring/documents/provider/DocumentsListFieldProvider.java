/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.model.authoring.documents.provider;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.definition.DocumentListFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.type.DocumentListFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class DocumentsListFieldProvider extends BasicTypeFieldProvider<DocumentListFieldDefinition> {

    @Override
    public Class<DocumentListFieldType> getFieldType() {
        return DocumentListFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return DocumentListFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public DocumentListFieldDefinition getDefaultField() {
        return new DocumentListFieldDefinition();
    }

    @Override
    public int getPriority() {
        return 16;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(DocumentListFieldType.DOCUMENTS_TYPE);
    }

    @Override
    public DocumentListFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return new DocumentListFieldDefinition();
    }
}
