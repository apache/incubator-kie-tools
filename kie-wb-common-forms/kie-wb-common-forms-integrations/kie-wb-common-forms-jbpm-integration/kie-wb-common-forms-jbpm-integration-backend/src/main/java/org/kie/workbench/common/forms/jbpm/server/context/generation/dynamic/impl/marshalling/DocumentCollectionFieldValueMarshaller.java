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

package org.kie.workbench.common.forms.jbpm.server.context.generation.dynamic.impl.marshalling;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.document.Document;
import org.jbpm.document.DocumentCollection;
import org.jbpm.document.Documents;
import org.jbpm.document.service.impl.DocumentCollectionImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.definition.DocumentCollectionFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.type.DocumentCollectionFieldType;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentStatus;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.UploadedDocumentStorage;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.forms.jbpm.server.context.generation.dynamic.impl.marshalling.DocumentFieldValueMarshaller.fromDocument;
import static org.kie.workbench.common.forms.jbpm.server.context.generation.dynamic.impl.marshalling.DocumentFieldValueMarshaller.toDocument;

@Dependent
public class DocumentCollectionFieldValueMarshaller extends AbstractFieldValueMarshaller<DocumentCollection, Collection<DocumentData>, DocumentCollectionFieldDefinition> {

    public static final String SERVER_TEMPLATE_ID = "serverTemplateId";

    private static final Logger logger = LoggerFactory.getLogger(DocumentCollectionFieldValueMarshaller.class);

    private UploadedDocumentStorage documentStorage;

    @Inject
    public DocumentCollectionFieldValueMarshaller(UploadedDocumentStorage documentStorage) {
        this.documentStorage = documentStorage;
    }

    @Override
    public void init(DocumentCollection originalValue, DocumentCollectionFieldDefinition fieldDefinition, FormDefinition currentForm, BackendFormRenderingContext currentContext) {
        super.init(originalValue, fieldDefinition, currentForm, currentContext);
        this.originalValue = Optional.ofNullable(originalValue).orElseGet(this::getInstance);
    }

    @Override
    public Class<DocumentCollectionFieldDefinition> getSupportedField() {
        return DocumentCollectionFieldDefinition.class;
    }

    @Override
    public Supplier<FieldValueMarshaller<DocumentCollection, Collection<DocumentData>, DocumentCollectionFieldDefinition>> newInstanceSupplier() {
        return () -> new DocumentCollectionFieldValueMarshaller(documentStorage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<DocumentData> toFlatValue() {

        String templateId = (String) context.getAttributes().get(SERVER_TEMPLATE_ID);

        return (List<DocumentData>) originalValue.getDocuments().stream()
                .map(document -> fromDocument((Document) document, templateId))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public DocumentCollection toRawValue(Collection<DocumentData> flatValue) {

        if (flatValue == null) {
            return getInstance();
        }

        boolean newValues = flatValue.stream().anyMatch(documentData -> documentData.getStatus().equals(DocumentStatus.NEW));

        if (!newValues && originalValue.getDocuments().size() == flatValue.size()) {
            return originalValue;
        }

        originalValue = getInstance();

        flatValue.stream()
                .map(documentData -> toDocument(documentData, documentStorage))
                .filter(Objects::nonNull)
                .forEach(document -> originalValue.addDocument(document));

        return originalValue;
    }

    private DocumentCollection getInstance() {
        if (DocumentCollectionFieldType.DOCUMENTS_TYPE.equals(fieldDefinition.getStandaloneClassName())) {
            // Handling Documents just in case it is still being used
            return new Documents();
        }

        return new DocumentCollectionImpl();
    }
}
