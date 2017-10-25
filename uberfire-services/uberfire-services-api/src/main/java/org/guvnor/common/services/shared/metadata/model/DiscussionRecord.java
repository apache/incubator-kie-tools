/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.shared.metadata.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * This is a discussion record item.
 */
@Portable
public class DiscussionRecord {

    private Long timestamp = new Date().getTime();
    private String note;
    private String author;

    public DiscussionRecord() {
    }

    public DiscussionRecord(final String author,
                            final String note) {
        this.author = author;
        this.note = note;
    }

    public DiscussionRecord(final long timestamp,
                            final String author,
                            final String note) {
        this.timestamp = timestamp;
        this.author = author;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public String getAuthor() {
        return author;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiscussionRecord that = (DiscussionRecord) o;

        if (author != null ? !author.equals(that.author) : that.author != null) {
            return false;
        }
        if (note != null ? !note.equals(that.note) : that.note != null) {
            return false;
        }
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
