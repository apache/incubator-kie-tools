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

package org.kie.workbench.common.dmn.client.property.dmn;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class DocumentationLinksFieldProvider extends BasicTypeFieldProvider<DocumentationLinksFieldDefinition> {

    //Arbitrary 'magic' number
    static final int PRIORITY = 980;

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(DocumentationLinks.class);
    }

    @Override
    public DocumentationLinksFieldDefinition createFieldByType(final TypeInfo typeInfo) {
        return getDefaultField();
    }

    @Override
    public Class<DocumentationLinksFieldType> getFieldType() {
        return DocumentationLinksFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return DocumentationLinksFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public DocumentationLinksFieldDefinition getDefaultField() {
        return new DocumentationLinksFieldDefinition();
    }
}
