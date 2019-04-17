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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.UploadedDocumentStorage;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadChunk;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadResponse;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.UploadedDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@SessionScoped
public class UploadedDocumentServiceImpl implements UploadedDocumentService,
                                                    Serializable {

    private static final Logger logger = LoggerFactory.getLogger(UploadedDocumentServiceImpl.class);

    protected Map<String, DocumentUploadSession> uploadSessions = new HashMap<>();

    private UploadedDocumentStorage storage;

    @Inject
    public UploadedDocumentServiceImpl(UploadedDocumentStorage storage) {
        this.storage = storage;
    }

    @Override
    public DocumentUploadResponse uploadContent(final DocumentUploadChunk chunk) {

        try {
            DocumentUploadSession session = getSession(chunk);

            if (session.isComplete()) {
                return merge(session);
            } else {
                return new DocumentUploadResponse(DocumentUploadResponse.DocumentUploadState.UPLOADING, true);
            }
        } catch (Exception ex) {
            logger.warn("Cannot upload chunk {}: {}", chunk.getDocumentName(), ex);
            return new DocumentUploadResponse(DocumentUploadResponse.DocumentUploadState.FINISH, false);
        }
    }

    private DocumentUploadResponse merge(DocumentUploadSession session) {
        try {

            session.setState(DocumentUploadSession.State.MERGING);

            storage.merge(session);

            uploadSessions.remove(session.getDocumentId());

            return new DocumentUploadResponse(DocumentUploadResponse.DocumentUploadState.FINISH, true);
        } catch (Exception ex) {
            logger.warn("Error uploading content: ", ex);
            return new DocumentUploadResponse(DocumentUploadResponse.DocumentUploadState.FINISH, false);
        }
    }

    private DocumentUploadSession getSession(DocumentUploadChunk chunk) throws Exception {
        DocumentUploadSession session = uploadSessions.get(chunk.getDocumentId());
        if (session == null) {
            session = new DocumentUploadSession(chunk.getDocumentId(), chunk.getDocumentName(), chunk.getMaxChunks());
            uploadSessions.put(session.getDocumentId(), session);
        }

        session.add(chunk);
        storage.uploadContentChunk(chunk);

        return session;
    }

    @Override
    public void removeContent(String id) {
        DocumentUploadSession session = uploadSessions.remove(id);

        if (session != null) {
            session.setState(DocumentUploadSession.State.ABORTED);
            storage.removeContent(id);
        } else {
            storage.removeContent(id);
        }
    }

    @PreDestroy
    public void clear() {
        uploadSessions.clear();
        storage.clear();
    }
}
