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
import java.util.Date;

import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.backend.server.document.UploadedDocumentManager;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentData;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentStatus;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.DocumentFieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class DocumentFieldValueProcessorTest {

    @Mock
    protected UploadedDocumentManager uploadedDocumentManager;

    @Mock
    protected File uploaded;

    @Mock
    protected BackendFormRenderingContext context;

    protected DocumentFieldValueProcessor processor;

    protected DocumentFieldDefinition field;

    @Before
    public void initTest() {
        when( uploadedDocumentManager.getFile( anyString() )).thenReturn( uploaded );
        when( uploaded.length() ).thenReturn( new Long(1024) );

        field = new DocumentFieldDefinition();
        field.setBinding( "document" );
        field.setName( "document" );
        field.setLabel( "document" );

        processor = new TestDocumentFieldValueProcessor( uploadedDocumentManager );
    }

    @Test
    public void testNull2FlatValue() {
        DocumentData documentData = processor.toFlatValue( field, null, context );
        assertNull( "DocumentData must be null!", documentData );
    }

    @Test
    public void testDocument2FlatValue() {
        Document doc = new DocumentImpl( "id", "docName", 1024, new Date() );

        DocumentData documentData = processor.toFlatValue( field, doc, context );

        assertNotNull( "DocumentData cannot be null!", documentData );
        assertEquals( "Names are not equal", doc.getName(), documentData.getFileName() );
        assertEquals( "Sizes are not equal", doc.getSize(), documentData.getSize() );
        assertEquals( "Link must be empty", "", documentData.getLink() );
    }

    @Test
    public void testNullFlatValue2Document() {

        Document doc = processor.toRawValue( field, null, null, context );

        assertNull( "Document must be null!", doc );
    }

    @Test
    public void testNewFlatValue2Document() {
        DocumentData data = new DocumentData( "test", 1024, null );
        data.setContentId( "content" );

        Document doc = processor.toRawValue( field, data, null, context );

        verify( uploadedDocumentManager ).getFile( anyString() );
        verify( uploadedDocumentManager ).removeFile( anyString() );

        assertNotNull( "Document cannot be null!", doc );
        assertEquals( "Names are not equal", data.getFileName(), doc.getName() );
        assertEquals( "Sizes are not equal", data.getSize(), doc.getSize() );
    }

    @Test
    public void testExistingFlatValue2Document() {
        Document doc = new DocumentImpl( "id", "docName", 1024, new Date(), "aLink" );

        DocumentData data = new DocumentData( doc.getName(), doc.getSize(), doc.getLink() );
        data.setStatus( DocumentStatus.STORED );

        Document rawDoc = processor.toRawValue( field, data, doc, context );

        assertEquals( "Documents must be equal!", doc, rawDoc );

        verify( uploadedDocumentManager, never() ).getFile( anyString() );
        verify( uploadedDocumentManager, never() ).removeFile( anyString() );
    }

}
