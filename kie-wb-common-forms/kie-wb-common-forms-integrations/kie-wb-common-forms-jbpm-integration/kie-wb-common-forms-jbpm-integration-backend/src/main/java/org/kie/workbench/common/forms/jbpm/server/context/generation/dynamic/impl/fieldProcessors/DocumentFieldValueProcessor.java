/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.context.generation.dynamic.impl.fieldProcessors;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.document.UploadedDocumentManager;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentData;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentStatus;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.DocumentFieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class DocumentFieldValueProcessor implements FieldValueProcessor<DocumentFieldDefinition, Document, DocumentData> {
    private static final Logger logger = LoggerFactory.getLogger( DocumentFieldValueProcessor.class );

    protected UploadedDocumentManager uploadedDocumentManager;

    @Inject
    public DocumentFieldValueProcessor( UploadedDocumentManager uploadedDocumentManager ) {
        this.uploadedDocumentManager = uploadedDocumentManager;
    }

    @Override
    public Class<DocumentFieldDefinition> getSupportedField() {
        return DocumentFieldDefinition.class;
    }

    @Override
    public DocumentData toFlatValue( DocumentFieldDefinition field,
                                     Document document,
                                     BackendFormRenderingContext context ) {

        if ( document == null ) {
            return null;
        }

        DocumentData data = new DocumentData( document.getName(), document.getSize(), document.getLink() );
        data.setStatus( DocumentStatus.STORED );
        return data;
    }

    @Override
    public Document toRawValue( DocumentFieldDefinition field,
                                DocumentData documentData,
                                Document originalValue,
                                BackendFormRenderingContext context ) {

        if ( documentData == null ) {
            return null;
        }

        if ( documentData.getStatus().equals( DocumentStatus.STORED ) ) {
            return originalValue;
        }

        File content = uploadedDocumentManager.getFile( documentData.getContentId() );

        if ( content != null ) {

            try {
                String id = UUID.randomUUID().toString();
                Document doc = new DocumentImpl( id,
                                                 documentData.getFileName(),
                                                 content.length(),
                                                 new Date( content.lastModified() ),
                                                 "" );
                doc.setContent( getFileContent( content ) );
                uploadedDocumentManager.removeFile( documentData.getContentId() );
                return doc;
            } catch ( IOException e ) {
                logger.warn( "Error reading file content: ", e );
            }
        }
        return null;
    }

    protected byte[] getFileContent( File content ) throws IOException {
        return FileUtils.readFileToByteArray( content );
    }
}
