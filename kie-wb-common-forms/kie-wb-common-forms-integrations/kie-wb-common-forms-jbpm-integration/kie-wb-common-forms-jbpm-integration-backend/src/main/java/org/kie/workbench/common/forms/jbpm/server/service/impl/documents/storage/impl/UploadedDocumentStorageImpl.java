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

package org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.DocumentUploadSession;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage.UploadedDocumentStorage;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class UploadedDocumentStorageImpl implements UploadedDocumentStorage,
                                                    Serializable {

    private static final Logger logger = LoggerFactory.getLogger(UploadedDocumentStorageImpl.class);

    public static final String UPLOADS_FOLDER = "uploads";
    public static final String PARTS_FOLDER = "parts";
    public static final String PART_EXTENSION = ".part";

    protected Path rootFolder;

    private Map<String, File> uploadedFiles = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        rootFolder = Files.createTempDirectory(UPLOADS_FOLDER);
    }

    @Override
    public void uploadContentChunk(DocumentUploadChunk chunk) throws Exception {
        File docFolder = resolveDocStorage(chunk.getDocumentId());

        File chunkFile = docFolder.toPath().resolve(PARTS_FOLDER).resolve(resolveChunkFileName(chunk)).toFile();

        FileUtils.writeByteArrayToFile(chunkFile, Base64.getDecoder().decode(chunk.getContent()));

        chunk.clearContent();
    }

    @Override
    public void merge(DocumentUploadSession session) {
        try {
            File destination = doMerge(session);

            if (session.getState().equals(DocumentUploadSession.State.MERGED)) {
                uploadedFiles.put(session.getDocumentId(), destination);
            }
        } catch (Exception ex) {
            logger.warn("Cannot merge document {}: {}", session.getDocumentName(), ex);
            throw new RuntimeException("Cannot merge document " + session.getDocumentName());
        }
    }

    private File doMerge(DocumentUploadSession session) throws Exception {
        File docFolder = resolveDocStorage(session.getDocumentId());

        File destination = docFolder.toPath().resolve(session.getDocumentName()).toFile();

        if (destination.exists()) {
            destination.delete();
        }

        destination.createNewFile();

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination))) {
            for (DocumentUploadChunk chunk : session.getChunks()) {
                if (!session.getState().equals(DocumentUploadSession.State.MERGING)) {
                    return null;
                }
                IOUtils.copy(new FileInputStream(resolveChunkFile(chunk)), out);
            }
            FileUtils.deleteQuietly(docFolder.toPath().resolve(PARTS_FOLDER).toFile());
        }
        session.setState(DocumentUploadSession.State.MERGED);

        return destination;
    }

    private File resolveChunkFile(DocumentUploadChunk chunk) {
        File docFolder = resolveDocStorage(chunk.getDocumentId());

        return docFolder.toPath().resolve(PARTS_FOLDER).resolve(resolveChunkFileName(chunk)).toFile();
    }

    private String resolveChunkFileName(DocumentUploadChunk chunk) {
        return chunk.getDocumentId() + "_" + chunk.getIndex() + PART_EXTENSION;
    }

    private File resolveDocStorage(String documentId) {
        Path docPath = rootFolder.resolve(documentId);
        File file = docPath.toFile();

        if (!file.exists()) {
            try {
                return Files.createDirectory(docPath).toFile();
            } catch (IOException e) {

            }
        }

        return file;
    }

    @Override
    public void uploadContent(String id, byte[] content) {
        File file = rootFolder.resolve(id).toFile();

        try {
            FileUtils.writeByteArrayToFile(file, content);
        } catch (IOException ex) {
            logger.warn("Cannot upload content for document {}: {}", id, ex);
        }

        uploadedFiles.put(id, file);
    }

    @Override
    public byte[] getContent(String id) {
        File file = uploadedFiles.get(id);

        if (file != null) {
            try {
                return FileUtils.readFileToByteArray(file);
            } catch (IOException ex) {
                logger.warn("Cannot read content for document {}: {}", id, ex);
            }
        }
        return new byte[0];
    }

    @Override
    public void removeContent(String id) {
        File file = uploadedFiles.remove(id);
        if (file != null) {
            FileUtils.deleteQuietly(file.getParentFile());
        } else {
            FileUtils.deleteQuietly(resolveDocStorage(id));
        }
    }

    public void clear() {
        uploadedFiles.clear();
        FileUtils.deleteQuietly(rootFolder.toFile());
    }
}
