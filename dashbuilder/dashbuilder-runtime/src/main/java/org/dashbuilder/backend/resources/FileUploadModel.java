/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.resources;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * File sent by upload.
 *
 */
public class FileUploadModel {

    private byte[] fileData;
    private String fileName;

    public byte[] getFileData() {
        return fileData;
    }

    public String getFileName() {
        return fileName;
    }

    @FormParam("inputFileName")
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    @FormParam("selectedFile")
    @PartType("application/octet-stream")
    public void setFileData(final byte[] fileData) {
        this.fileData = fileData;
    }
}