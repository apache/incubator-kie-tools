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

package org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Blob;
import elemental2.dom.File;
import elemental2.dom.FileReader;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadChunk;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadResponse;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.UploadedDocumentService;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DocumentUploadManager {

    public static final int MAX_CHUNK_SIZE = 1024 * 1024;

    private final Caller<UploadedDocumentService> uploadService;

    private FileReader fileReader;
    private List<UploaderSession> sessions = new ArrayList<>();
    private UploaderSession activeSession;

    @Inject
    public DocumentUploadManager(Caller<UploadedDocumentService> uploadService) {
        this.uploadService = uploadService;
    }

    public void upload(String documentId, File file, Command onUploadStart, ParameterizedCommand<Boolean> onUploadFinish) {
        long fileSize = (long) file.size;

        int maxChunks = (int) (fileSize / MAX_CHUNK_SIZE);

        if (fileSize % MAX_CHUNK_SIZE > 0) {
            maxChunks++;
        }

        UploaderSession session = new UploaderSession(documentId, file, maxChunks, onUploadStart, onUploadFinish);
        sessions.add(session);

        startUpload();
    }

    public void remove(String documentId, Command callback) {
        Optional<UploaderSession> optional = sessions.stream()
                .filter(session -> session.documentId.equals(documentId))
                .findAny();
        if (optional.isPresent()) {
            sessions.remove(optional.get());
            callback.execute();
        } else {
            if (activeSession != null && activeSession.documentId.equals(documentId)) {
                fileReader.abort();
                activeSession = null;
                startUpload();
            }
            uploadService.call((RemoteCallback<Void>) response -> callback.execute())
                    .removeContent(documentId);
        }
    }

    private void startUpload() {

        if (activeSession != null || sessions.isEmpty()) {
            return;
        }

        initFileReader();

        activeSession = sessions.remove(0);
        activeSession.onUploadStart.execute();

        upload(0);
    }

    public void initFileReader() {
        fileReader = new FileReader();

        fileReader.onload = event -> {
            if (fileReader.readyState == FileReader.DONE) {
                String[] split = fileReader.result.asString().split(",");

                String content = "";

                if (split.length == 2) {
                    content = split[1];
                }

                if (activeSession == null) {
                    return null;
                }

                DocumentUploadChunk newChunk = new DocumentUploadChunk(activeSession.documentId, activeSession.file.name, activeSession.chunk, activeSession.maxChunks, content);

                uploadService.call((RemoteCallback<DocumentUploadResponse>) response -> {
                    if (response.getState().equals(DocumentUploadResponse.DocumentUploadState.FINISH)) {
                        activeSession.onUploadEnd.execute(response.isSuccess());
                        activeSession = null;
                        startUpload();
                    } else if (!response.isSuccess()) {
                        activeSession.onUploadEnd.execute(response.isSuccess());
                        activeSession = null;
                        startUpload();
                    }
                }, (ErrorCallback<Message>) (message, throwable) -> {
                    activeSession.onUploadEnd.execute(false);
                    activeSession = null;
                    startUpload();
                    return false;
                }).uploadContent(newChunk);

                if (activeSession.nextChunk <= activeSession.file.size) {
                    upload(activeSession.nextChunk);
                }
            }
            return null;
        };
    }

    private void upload(final int sliceStart) {

        activeSession.chunk = sliceStart;

        activeSession.nextChunk = sliceStart + MAX_CHUNK_SIZE + 1;

        Blob fileSlice = activeSession.file.slice(activeSession.chunk, activeSession.nextChunk);

        fileReader.readAsDataURL(fileSlice);
    }

    private class UploaderSession {

        private final String documentId;
        private final File file;
        private final int maxChunks;
        private final Command onUploadStart;
        private final ParameterizedCommand<Boolean> onUploadEnd;

        private int chunk = 0;
        private int nextChunk;

        public UploaderSession(String documentId, File file, int maxChunks, Command onUploadStart, ParameterizedCommand<Boolean> onUploadEnd) {
            this.documentId = documentId;
            this.file = file;
            this.maxChunks = maxChunks;
            this.onUploadStart = onUploadStart;
            this.onUploadEnd = onUploadEnd;
        }
    }
}
