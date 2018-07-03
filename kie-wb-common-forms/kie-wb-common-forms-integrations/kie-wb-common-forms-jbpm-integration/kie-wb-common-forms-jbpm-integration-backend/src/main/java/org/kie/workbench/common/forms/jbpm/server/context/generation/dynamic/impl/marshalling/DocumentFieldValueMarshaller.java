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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.document.service.impl.util.DocumentDownloadLinkGenerator;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.document.UploadedDocumentManager;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentData;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentStatus;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class DocumentFieldValueMarshaller extends AbstractFieldValueMarshaller<Document, DocumentData, DocumentFieldDefinition> {

    public static final String SERVER_TEMPLATE_ID = "serverTemplateId";

    private static final Logger logger = LoggerFactory.getLogger(DocumentFieldValueMarshaller.class);

    protected UploadedDocumentManager uploadedDocumentManager;

    @Inject
    public DocumentFieldValueMarshaller(UploadedDocumentManager uploadedDocumentManager) {
        this.uploadedDocumentManager = uploadedDocumentManager;
    }

    @Override
    public Class<DocumentFieldDefinition> getSupportedField() {
        return DocumentFieldDefinition.class;
    }

    @Override
    public Supplier<FieldValueMarshaller<Document, DocumentData, DocumentFieldDefinition>> newInstanceSupplier() {
        return () -> new DocumentFieldValueMarshaller(uploadedDocumentManager);
    }

    @Override
    public DocumentData toFlatValue() {

        if (originalValue == null) {
            return null;
        }

        String templateId = (String) context.getAttributes().get(SERVER_TEMPLATE_ID);

        String link;

        if (!StringUtils.isEmpty(templateId) & !StringUtils.isEmpty(originalValue.getIdentifier())) {
            link = DocumentDownloadLinkGenerator.generateDownloadLink(templateId, originalValue.getIdentifier());
        } else {
            link = originalValue.getLink();
        }

        DocumentData data = new DocumentData(originalValue.getIdentifier(), originalValue.getName(), originalValue.getSize(), link);

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

        File content = uploadedDocumentManager.getFile(documentData.getContentId());

        if (content != null) {

            try {
                Document doc = new DocumentImpl(documentData.getFileName(),
                                                content.length(),
                                                new Date(content.lastModified()));
                doc.setContent(getFileContent(content));
                uploadedDocumentManager.removeFile(documentData.getContentId());
                return doc;
            } catch (IOException e) {
                logger.warn("Error reading file content: ",
                            e);
            }
        }
        return null;
    }

    protected byte[] getFileContent(File content) throws IOException {
        return FileUtils.readFileToByteArray(content);
    }
}
