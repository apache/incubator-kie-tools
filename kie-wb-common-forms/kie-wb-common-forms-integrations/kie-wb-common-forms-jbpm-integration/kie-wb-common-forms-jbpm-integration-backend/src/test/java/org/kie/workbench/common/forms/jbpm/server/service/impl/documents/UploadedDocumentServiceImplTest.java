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

package org.kie.workbench.common.forms.jbpm.server.service.impl.documents;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.impl.TestUploadedDocumentStorageImpl;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.impl.UploadedDocumentStorageImpl;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadChunk;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class UploadedDocumentServiceImplTest {

    private static final String DOC_ID = "docId";
    private static final String DOC_NAME = "example.txt";

    private static final String PART_1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    private static final String PART_2 = " In dignissim eu enim eu placerat. Proin pretium at urna vitae vestibulum.";
    private static final String PART_3 = " Cras eu purus nibh. Quisque eget erat a est tristique semper vel at sem. " +
            "Pellentesque neque ipsum, molestie vitae nibh in, consequat dignissim purus. Pellentesque pretium lorem et fermentum vestibulum. " +
            "Morbi a tellus feugiat, ullamcorper ex ut, volutpat eros. Nulla facilisi. Nulla convallis maximus posuere. " +
            "Aenean pulvinar pellentesque augue ut viverra.";

    private TestUploadedDocumentStorageImpl storage;

    private TestUploadedDocumentServiceImpl uploadedDocumentService;

    @Before
    public void init() throws IOException {
        storage = spy(new TestUploadedDocumentStorageImpl());

        storage.init();

        uploadedDocumentService = new TestUploadedDocumentServiceImpl(storage);
    }

    @Test
    public void testSuccessfullyUpload() {

        DocumentUploadChunk chunk1 = new DocumentUploadChunk(DOC_ID, DOC_NAME, 0, 3, Base64.getEncoder().encodeToString(PART_1.getBytes()));

        DocumentUploadResponse response = uploadedDocumentService.uploadContent(chunk1);

        validateResponse(response, DocumentUploadResponse.DocumentUploadState.UPLOADING, true);

        DocumentUploadSession session = uploadedDocumentService.getUploadSessions().get(DOC_ID);

        Assertions.assertThat(session).isNotNull();

        Assertions.assertThat(session.getChunks())
                .isNotNull()
                .hasSize(1)
                .containsExactly(chunk1);

        checkParts(1);

        DocumentUploadChunk chunk2 = new DocumentUploadChunk(DOC_ID, DOC_NAME, 1, 3, Base64.getEncoder().encodeToString(PART_2.getBytes()));

        response = uploadedDocumentService.uploadContent(chunk2);

        validateResponse(response, DocumentUploadResponse.DocumentUploadState.UPLOADING, true);

        session = uploadedDocumentService.getUploadSessions().get(DOC_ID);

        Assertions.assertThat(session).isNotNull();

        Assertions.assertThat(session.getChunks())
                .isNotNull()
                .hasSize(2)
                .containsExactly(chunk1, chunk2);

        checkParts(2);

        DocumentUploadChunk chunk3 = new DocumentUploadChunk(DOC_ID, DOC_NAME, 3, 3, Base64.getEncoder().encodeToString(PART_3.getBytes()));

        response = uploadedDocumentService.uploadContent(chunk3);

        validateResponse(response, DocumentUploadResponse.DocumentUploadState.FINISH, true);

        session = uploadedDocumentService.getUploadSessions().get(DOC_ID);

        Assertions.assertThat(session).isNull();

        File file = storage.getRootFolder().resolve(DOC_ID).resolve(DOC_NAME).toFile();

        Assertions.assertThat(file)
                .isNotNull()
                .exists();

        String content = new String(storage.getContent(DOC_ID));

        Assertions.assertThat(content)
                .isNotNull()
                .isEqualTo(PART_1 + PART_2 + PART_3);
    }

    @Test
    public void testSuccesfullyUploadAndCleanup() {
        testSuccessfullyUpload();

        uploadedDocumentService.removeContent(DOC_ID);

        File file = storage.getRootFolder().resolve(DOC_ID).resolve(DOC_NAME).toFile();

        Assertions.assertThat(file)
                .isNotNull()
                .doesNotExist();
    }

    @Test
    public void testAbortUpload() {
        DocumentUploadChunk chunk = new DocumentUploadChunk(DOC_ID, DOC_NAME, 0, 3, Base64.getEncoder().encodeToString(PART_1.getBytes()));

        DocumentUploadResponse response = uploadedDocumentService.uploadContent(chunk);

        validateResponse(response, DocumentUploadResponse.DocumentUploadState.UPLOADING, true);

        DocumentUploadSession session = uploadedDocumentService.getUploadSessions().get(DOC_ID);

        Assertions.assertThat(session).isNotNull();

        Assertions.assertThat(session.getChunks())
                .isNotNull()
                .hasSize(1)
                .containsExactly(chunk);

        checkParts(1);

        uploadedDocumentService.removeContent(DOC_ID);

        assertEquals(DocumentUploadSession.State.ABORTED, session.getState());
    }

    @Test
    public void testUploadWithError() throws Exception {
        doThrow(new RuntimeException("Exception")).when(storage).uploadContentChunk(any());

        DocumentUploadChunk chunk = new DocumentUploadChunk(DOC_ID, DOC_NAME, 0, 3, Base64.getEncoder().encodeToString(PART_1.getBytes()));

        DocumentUploadResponse response = uploadedDocumentService.uploadContent(chunk);

        validateResponse(response, DocumentUploadResponse.DocumentUploadState.FINISH, false);
    }

    @Test
    public void testMergeWithError() throws Exception {
        doThrow(new RuntimeException("Exception")).when(storage).merge(any());

        DocumentUploadChunk chunk = new DocumentUploadChunk(DOC_ID, DOC_NAME, 0, 1, Base64.getEncoder().encodeToString(PART_1.getBytes()));

        DocumentUploadResponse response = uploadedDocumentService.uploadContent(chunk);

        validateResponse(response, DocumentUploadResponse.DocumentUploadState.FINISH, false);
    }

    @After
    public void cleanup() {
        uploadedDocumentService.clear();

        verify(storage).clear();

        Assertions.assertThat(storage.getRootFolder().toFile())
                .doesNotExist();
    }

    private void checkParts(int parts) {
        File file = storage.getRootFolder().resolve(DOC_ID).resolve(UploadedDocumentStorageImpl.PARTS_FOLDER).toFile();

        Assertions.assertThat(file.list())
                .hasSize(parts);
    }

    private void validateResponse(DocumentUploadResponse response, DocumentUploadResponse.DocumentUploadState state, boolean isSuccess) {
        Assertions.assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("state", state)
                .hasFieldOrPropertyWithValue("success", isSuccess);
    }
}
