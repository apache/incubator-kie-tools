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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.document.Document;
import org.jbpm.document.Documents;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.document.service.impl.util.DocumentDownloadLinkGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.definition.DocumentListFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentStatus;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.UploadedDocumentStorage;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentListFieldValueMarshallerTest {

    private static final String SERVER_TEMPLATE_ID = "templateId";
    private static final String DOCUMENT_ID = "docId";
    private static final String DOCUMENT_ID2 = "docId_3";
    private static final String DOCUMENT_ID3 = "docId_2";

    private static final String EXPECTED_DOWNLOAD_LINK = DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID, DOCUMENT_ID);

    @Mock
    protected UploadedDocumentStorage documentStorage;

    @Mock
    private FormDefinition form;

    @Mock
    protected BackendFormRenderingContext context;

    protected DocumentListFieldValueMarshaller marshaller;

    protected DocumentListFieldDefinition field;

    @Before
    public void initTest() {
        when(documentStorage.getContent(anyString())).thenReturn(new byte[]{});

        field = new DocumentListFieldDefinition();
        field.setBinding("documents");
        field.setName("documents");
        field.setLabel("documents");

        marshaller = new DocumentListFieldValueMarshaller(documentStorage);
    }

    @Test
    public void testNull2FlatValue() {

        marshaller.init(null, field, form, context);

        Collection<DocumentData> documents = marshaller.toFlatValue();

        Assertions.assertThat(documents)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    public void testDocuments2FlatValueEmptyLinkPattern() {
        Document doc = new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date());

        marshaller.init(new Documents(Collections.singletonList(doc)), field, form, context);

        Collection<DocumentData> documents = marshaller.toFlatValue();

        Assertions.assertThat(documents)
                .isNotNull()
                .hasSize(1);

        Assertions.assertThat(documents.iterator().next())
                .isNotNull()
                .hasFieldOrPropertyWithValue("status", DocumentStatus.STORED)
                .hasFieldOrPropertyWithValue("contentId", DOCUMENT_ID)
                .hasFieldOrPropertyWithValue("fileName", doc.getName())
                .hasFieldOrPropertyWithValue("size", doc.getSize())
                .hasFieldOrPropertyWithValue("link", doc.getLink());
    }

    @Test
    public void testDocuments2FlatValue() {

        Document doc = spy(new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date()));

        Map result = new HashMap();

        result.put(DocumentFieldValueMarshaller.SERVER_TEMPLATE_ID, SERVER_TEMPLATE_ID);

        when(context.getAttributes()).thenReturn(result);

        marshaller.init(new Documents(Collections.singletonList(doc)), field, form, context);

        Collection<DocumentData> documents = marshaller.toFlatValue();

        Assertions.assertThat(documents)
                .isNotNull()
                .hasSize(1);

        Assertions.assertThat(documents.iterator().next())
                .isNotNull()
                .hasFieldOrPropertyWithValue("status", DocumentStatus.STORED)
                .hasFieldOrPropertyWithValue("contentId", DOCUMENT_ID)
                .hasFieldOrPropertyWithValue("fileName", doc.getName())
                .hasFieldOrPropertyWithValue("size", doc.getSize())
                .hasFieldOrPropertyWithValue("link", EXPECTED_DOWNLOAD_LINK);
    }

    @Test
    public void testNullFlatValue2Document() {

        marshaller.init(null, field, form, context);

        Documents documents = marshaller.toRawValue(null);

        Assertions.assertThat(documents)
                .isNotNull();

        Assertions.assertThat(documents.getDocuments())
                .isNotNull()
                .hasSize(0);
    }

    @Test
    public void testNewFlatValue2Documents() {
        marshaller.init(null, field, form, context);

        DocumentData data = new DocumentData(DOCUMENT_ID, 1024, null);

        data.setContentId("content");

        Documents documents = marshaller.toRawValue(Collections.singletonList(data));

        verify(documentStorage).getContent(anyString());

        verify(documentStorage).removeContent(anyString());

        Assertions.assertThat(documents)
                .isNotNull();

        Assertions.assertThat(documents.getDocuments())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    public void testExistingFlatValue2Documents() {
        Document doc = new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date(), "aLink");

        Documents documents = new Documents(Collections.singletonList(doc));

        marshaller.init(documents, field, form, context);

        DocumentData data = new DocumentData(doc.getName(), doc.getSize(), doc.getLink());

        data.setStatus(DocumentStatus.STORED);

        Documents rawDocuments = marshaller.toRawValue(Collections.singletonList(data));

        Assertions.assertThat(rawDocuments)
                .isSameAs(documents);

        verify(documentStorage, never()).getContent(anyString());

        verify(documentStorage, never()).removeContent(anyString());
    }

    @Test
    public void testAddingNewDocuments() {
        Document doc = new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date(), "aLink");

        Documents documents = new Documents(Collections.singletonList(doc));

        marshaller.init(documents, field, form, context);

        DocumentData data1 = new DocumentData(DOCUMENT_ID, doc.getName(), doc.getSize(), doc.getLink(), System.currentTimeMillis());
        data1.setStatus(DocumentStatus.STORED);

        DocumentData data2 = new DocumentData(DOCUMENT_ID2, DOCUMENT_ID2, 1024, "", System.currentTimeMillis());
        DocumentData data3 = new DocumentData(DOCUMENT_ID3, DOCUMENT_ID3, 1024, "", System.currentTimeMillis());

        Documents rawDocuments = marshaller.toRawValue(Arrays.asList(data1, data2, data3));

        verify(documentStorage, times(2)).getContent(anyString());

        verify(documentStorage, times(2)).removeContent(anyString());

        Assertions.assertThat(rawDocuments)
                .isNotSameAs(documents);

        Assertions.assertThat(rawDocuments.getDocuments())
                .isNotNull()
                .hasSize(3);

        compareDoc(rawDocuments.getDocuments().get(0), data1);
        compareDoc(rawDocuments.getDocuments().get(1), data2);
        compareDoc(rawDocuments.getDocuments().get(2), data3);
    }

    @Test
    public void testAddingAndRemovingDocuments() {
        Document doc = new DocumentImpl(DOCUMENT_ID, "docName", 1024, new Date(), "aLink");

        Documents documents = new Documents(Collections.singletonList(doc));

        marshaller.init(documents, field, form, context);

        DocumentData data1 = new DocumentData(DOCUMENT_ID2, DOCUMENT_ID2, 1024, "", System.currentTimeMillis());
        DocumentData data2 = new DocumentData(DOCUMENT_ID3, DOCUMENT_ID3, 1024, "", System.currentTimeMillis());

        Documents rawDocuments = marshaller.toRawValue(Arrays.asList(data1, data2));

        verify(documentStorage, times(2)).getContent(anyString());

        verify(documentStorage, times(2)).removeContent(anyString());

        Assertions.assertThat(rawDocuments)
                .isNotSameAs(documents);

        Assertions.assertThat(rawDocuments.getDocuments())
                .isNotNull()
                .hasSize(2);

        compareDoc(rawDocuments.getDocuments().get(0), data1);
        compareDoc(rawDocuments.getDocuments().get(1), data2);
    }

    private void compareDoc(Document document, DocumentData documentData) {
        Assertions.assertThat(document)
                .hasFieldOrPropertyWithValue("identifier", documentData.getContentId())
                .hasFieldOrPropertyWithValue("name", documentData.getFileName())
                .hasFieldOrPropertyWithValue("size", documentData.getSize())
                .hasFieldOrPropertyWithValue("lastModified", new Date(documentData.getLastModified()));
    }
}
