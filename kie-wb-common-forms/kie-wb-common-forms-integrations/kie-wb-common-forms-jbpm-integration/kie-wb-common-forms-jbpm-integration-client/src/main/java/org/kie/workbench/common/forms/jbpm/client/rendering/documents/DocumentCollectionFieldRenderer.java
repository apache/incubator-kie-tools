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

package org.kie.workbench.common.forms.jbpm.client.rendering.documents;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormFieldImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters.ListToListConverter;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.DocumentUpload;
import org.kie.workbench.common.forms.jbpm.client.rendering.util.DocumentSizeHelper;
import org.kie.workbench.common.forms.jbpm.client.resources.i18n.Constants;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.definition.DocumentCollectionFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.type.DocumentCollectionFieldType;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentStatus;
import org.kie.workbench.common.forms.processing.engine.handling.CustomFieldValidator;
import org.kie.workbench.common.forms.processing.engine.handling.ValidationResult;

@Dependent
@Renderer(type = DocumentCollectionFieldType.class)
public class DocumentCollectionFieldRenderer extends FieldRenderer<DocumentCollectionFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    private static final Integer MAX_CONTENT_SIZE = 20 * 1024 * 1024;

    private final TranslationService translationService;
    private final DocumentUpload upload;

    @Inject
    public DocumentCollectionFieldRenderer(final TranslationService translationService, final DocumentUpload upload) {
        this.translationService = translationService;
        this.upload = upload;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        formGroup.render(upload.asWidget(), field);
        upload.setMaxDocuments(field.getMaxDocuments());

        return formGroup;
    }

    @Override
    public String getName() {
        return DocumentCollectionFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        upload.setEnabled(!readOnly);
    }

    @Override
    protected void registerCustomFieldValidators(FormFieldImpl field) {
        CustomFieldValidator<List<DocumentData>> maxContentSizeWarning = values -> {

            long contentSize = values.stream()
                    .filter(documentData -> !documentData.getStatus().equals(DocumentStatus.STORED))
                    .mapToLong(DocumentData::getSize)
                    .sum();

            if (contentSize > MAX_CONTENT_SIZE) {
                String size = DocumentSizeHelper.getFormattedDocumentSize(contentSize);
                String message = translationService.format(Constants.DocumentListFieldRendererMaxContentLengthWarning, size);
                return ValidationResult.warning(message);
            }

            return ValidationResult.valid();
        };
        CustomFieldValidator<List<DocumentData>> maxDocumentsError = values -> {

            int maxDocuments = getField().getMaxDocuments();

            if (maxDocuments > 0 && maxDocuments < values.size()) {
                String message = translationService.format(Constants.DocumentListFieldRendererMaxDocumentsReached, maxDocuments);
                return ValidationResult.error(message);
            }

            return ValidationResult.valid();
        };
        field.getCustomValidators().add(maxContentSizeWarning);
        field.getCustomValidators().add(maxDocumentsError);
    }

    @Override
    public Converter getConverter() {
        return new ListToListConverter();
    }
}
