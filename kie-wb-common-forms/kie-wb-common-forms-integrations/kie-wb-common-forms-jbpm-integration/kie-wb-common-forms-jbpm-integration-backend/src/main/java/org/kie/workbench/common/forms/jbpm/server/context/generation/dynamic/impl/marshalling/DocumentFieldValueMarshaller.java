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

package org.kie.workbench.common.forms.jbpm.server.context.generation.dynamic.impl.marshalling;

import java.util.Date;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.document.service.impl.util.DocumentDownloadLinkGenerator;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentStatus;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.UploadedDocumentStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class DocumentFieldValueMarshaller extends AbstractFieldValueMarshaller<Document, DocumentData, DocumentFieldDefinition> {

    public static final String SERVER_TEMPLATE_ID = "serverTemplateId";

    private static final Logger logger = LoggerFactory.getLogger(DocumentFieldValueMarshaller.class);

    private UploadedDocumentStorage documentStorage;

    @Inject
    public DocumentFieldValueMarshaller(UploadedDocumentStorage documentStorage) {
        this.documentStorage = documentStorage;
    }

    @Override
    public Class<DocumentFieldDefinition> getSupportedField() {
        return DocumentFieldDefinition.class;
    }

    @Override
    public Supplier<FieldValueMarshaller<Document, DocumentData, DocumentFieldDefinition>> newInstanceSupplier() {
        return () -> new DocumentFieldValueMarshaller(documentStorage);
    }

    @Override
    public DocumentData toFlatValue() {

        if (originalValue == null) {
            return null;
        }

        String templateId = (String) context.getAttributes().get(SERVER_TEMPLATE_ID);

        return fromDocument(originalValue, templateId);
    }

    static DocumentData fromDocument(Document document, String templateId) {

        String link;

        if (!StringUtils.isEmpty(templateId) && !StringUtils.isEmpty(document.getIdentifier())) {
            link = DocumentDownloadLinkGenerator.generateDownloadLink(templateId, document.getIdentifier());
        } else {
            link = document.getLink();
        }

        DocumentData data = new DocumentData(document.getIdentifier(), document.getName(), document.getSize(), link, document.getLastModified().getTime());

        data.setStatus(DocumentStatus.STORED);

        return data;
    }

    @Override
    public Document toRawValue(DocumentData documentData) {

        if (documentData == null) {
            return null;
        }

        if (documentData.getStatus().equals(DocumentStatus.STORED)) {
            return originalValue;
        }

        return toDocument(documentData, documentStorage);
    }

    static Document toDocument(DocumentData documentData, UploadedDocumentStorage documentStorage) {

        DocumentImpl document = new DocumentImpl();

        document.setIdentifier(documentData.getContentId());
        document.setName(documentData.getFileName());
        document.setSize(documentData.getSize());
        document.setLastModified(new Date(documentData.getLastModified()));

        if (documentData.getStatus().equals(DocumentStatus.NEW)) {
            try {
                byte[] content = documentStorage.getContent(documentData.getContentId());
                document.setContent(content);
            } catch (Exception ex) {
                logger.warn("Error getting content for document '{}' ({}): {}", document.getIdentifier(), document.getName(), ex);
            } finally {
                documentStorage.removeContent(documentData.getContentId());
            }
        } else {
            document.setLink(documentData.getLink());
        }
        return document;
    }
}
