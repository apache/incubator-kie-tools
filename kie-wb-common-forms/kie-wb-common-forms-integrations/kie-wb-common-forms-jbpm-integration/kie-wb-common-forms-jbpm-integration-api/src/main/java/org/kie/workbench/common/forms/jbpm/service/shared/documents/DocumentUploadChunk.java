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

package org.kie.workbench.common.forms.jbpm.service.shared.documents;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DocumentUploadChunk {

    private String documentId;
    private String documentName;
    private int index;
    private int maxChunks;
    private String content;

    public DocumentUploadChunk(@MapsTo("documentId") String documentId, @MapsTo("documentName") String documentName, @MapsTo("index") int index, @MapsTo("maxChunks") int maxChunks, @MapsTo("content") String content) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.index = index;
        this.maxChunks = maxChunks;
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public int getIndex() {
        return index;
    }

    public int getMaxChunks() {
        return maxChunks;
    }

    public String getContent() {
        return content;
    }

    public void clearContent() {
        this.content = "";
    }
}
