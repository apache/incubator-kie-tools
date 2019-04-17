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

package org.kie.workbench.common.forms.jbpm.model.authoring.documents.definition;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.type.DocumentListFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class DocumentListFieldDefinition extends AbstractFieldDefinition {

    public static final DocumentListFieldType FIELD_TYPE = new DocumentListFieldType();

    @FormField(
            labelKey = "maxDocuments",
            helpMessageKey = "maxDocuments.helpMessage",
            afterElement = "label"
    )
    private Integer maxDocuments = 0;

    public DocumentListFieldDefinition() {
        super(DocumentListFieldType.DOCUMENTS_TYPE);
    }

    public Integer getMaxDocuments() {
        return maxDocuments;
    }

    public void setMaxDocuments(Integer maxDocuments) {
        this.maxDocuments = maxDocuments;
    }

    @Override
    public DocumentListFieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if(other instanceof DocumentListFieldDefinition) {
            this.maxDocuments = ((DocumentListFieldDefinition)other).maxDocuments;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DocumentListFieldDefinition that = (DocumentListFieldDefinition) o;
        return Objects.equals(maxDocuments, that.maxDocuments);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (maxDocuments != null ? maxDocuments.hashCode() : 0);
        return result;
    }
}
