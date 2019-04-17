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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadChunk;

public class DocumentUploadSession {

    private String documentId;
    private String documentName;
    private int maxChunks;
    private Set<DocumentUploadChunk> chunks = new TreeSet<>(Comparator.comparing(DocumentUploadChunk::getIndex));
    private State state = State.UPLOADING;

    public DocumentUploadSession(final String documentId, final String documentName, final int maxChunks) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.maxChunks = maxChunks;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public Set<DocumentUploadChunk> getChunks() {
        return chunks;
    }

    public void add(DocumentUploadChunk chunk) {
        chunks.add(chunk);
    }

    public boolean isComplete() {
        return chunks.size() == maxChunks;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        UPLOADING, MERGING, MERGED, ABORTED
    }
}
