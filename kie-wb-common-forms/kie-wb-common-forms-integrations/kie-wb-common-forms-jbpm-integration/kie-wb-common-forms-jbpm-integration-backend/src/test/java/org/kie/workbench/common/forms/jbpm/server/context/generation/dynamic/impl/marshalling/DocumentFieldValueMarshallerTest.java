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
import java.util.HashMap;
import java.util.Map;

import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.document.service.impl.util.DocumentDownloadLinkGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentStatus;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.UploadedDocumentStorage;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentFieldValueMarshallerTest {

    private static final String SERVER_TEMPLATE_ID = "templateId";
    private static final String DOCUMENT_ID = "docId";

    private static final String EXPECTED_DOWNLOAD_LINK = DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID, DOCUMENT_ID);

    @Mock
    protected UploadedDocumentStorage documentStorage;

    @Mock
    private FormDefinition form;

    @Mock
    protected BackendFormRenderingContext context;

    protected DocumentFieldValueMarshaller marshaller;

    protected DocumentFieldDefinition field;

    @Before
    public void initTest() {
        when(documentStorage.getContent(anyString())).thenReturn(new byte[]{});

        field = new DocumentFieldDefinition();
        field.setBinding("document");
        field.setName("document");
        field.setLabel("document");

        marshaller = new DocumentFieldValueMarshaller(documentStorage);
    }

    @Test
    public void testNull2FlatValue() {

        marshaller.init(null, field, form, context);

        DocumentData documentData = marshaller.toFlatValue();

        assertNull("DocumentData must be null!", documentData);
    }

    @Test
    public void testDocument2FlatValueEmptyLinkPattern() {
        Document doc = spy(new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date()));

        marshaller.init(doc, field, form, context);

        DocumentData documentData = marshaller.toFlatValue();

        verify(doc).getLink();

        assertNotNull(documentData);

        assertEquals(doc.getName(), documentData.getFileName());

        assertEquals(doc.getSize(), documentData.getSize());

        assertEquals("", documentData.getLink());
    }

    @Test
    public void testDocument2FlatValue() {

        Document doc = spy(new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date()));

        Map result = new HashMap();

        result.put(DocumentFieldValueMarshaller.SERVER_TEMPLATE_ID, SERVER_TEMPLATE_ID);

        when(context.getAttributes()).thenReturn(result);

        marshaller.init(doc, field, form, context);

        DocumentData documentData = marshaller.toFlatValue();

        verify(doc, never()).getLink();

        assertNotNull(documentData);

        assertEquals(doc.getName(), documentData.getFileName());

        assertEquals(doc.getSize(), documentData.getSize());

        assertEquals(EXPECTED_DOWNLOAD_LINK, documentData.getLink());
    }

    @Test
    public void testNullFlatValue2Document() {

        marshaller.init(null, field, form, context);

        Document doc = marshaller.toRawValue(null);

        assertNull("Document must be null!", doc);
    }

    @Test
    public void testNewFlatValue2Document() {
        marshaller.init(null, field, form, context);

        DocumentData data = new DocumentData(DOCUMENT_ID, 1024, null);

        data.setContentId("content");

        Document doc = marshaller.toRawValue(data);

        verify(documentStorage).getContent(anyString());

        verify(documentStorage).removeContent(anyString());

        assertNotNull("Document cannot be null!", doc);

        assertEquals("Names are not equal", data.getFileName(), doc.getName());

        assertEquals("Sizes are not equal", data.getSize(), doc.getSize());
    }

    @Test
    public void testExistingFlatValue2Document() {
        Document doc = new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date(), "aLink");

        marshaller.init(doc, field, form, context);

        DocumentData data = new DocumentData(doc.getName(), doc.getSize(), doc.getLink());

        data.setStatus(DocumentStatus.STORED);

        Document rawDoc = marshaller.toRawValue(data);

        assertEquals("Documents must be equal!", doc, rawDoc);

        verify(documentStorage, never()).getContent(anyString());

        verify(documentStorage, never()).removeContent(anyString());
    }
}
