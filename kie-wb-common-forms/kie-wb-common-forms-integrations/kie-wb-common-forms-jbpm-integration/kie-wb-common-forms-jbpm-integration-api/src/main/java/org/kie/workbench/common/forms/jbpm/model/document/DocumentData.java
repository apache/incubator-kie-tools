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

package org.kie.workbench.common.forms.jbpm.model.document;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Bindable class to handle documents
 */
@Portable
@Bindable
public class DocumentData {

    private String contentId;
    private String fileName;
    private long size;
    private String link;
    private long lastModified;
    private DocumentStatus status = DocumentStatus.NEW;

    public DocumentData() {
    }

    public DocumentData(@MapsTo("fileName") String fileName,
                        @MapsTo("size") long size,
                        @MapsTo("link") String link) {
        this.fileName = fileName;
        this.size = size;
        this.link = link;
    }

    public DocumentData(String contentId,
                        String fileName,
                        long size,
                        String link,
                        long lastModified) {
        this.contentId = contentId;
        this.fileName = fileName;
        this.size = size;
        this.link = link;
        this.lastModified = lastModified;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentData that = (DocumentData) o;
        return size == that.size &&
                Objects.equals(contentId, that.contentId) &&
                fileName.equals(that.fileName) &&
                Objects.equals(link, that.link) &&
                status == that.status &&
                Objects.equals(lastModified, that.lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, fileName, size, link, status, lastModified);
    }
}
