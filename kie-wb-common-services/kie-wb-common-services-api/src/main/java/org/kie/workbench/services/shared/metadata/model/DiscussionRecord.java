/*
 * Copyright 2010 JBoss Inc
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

package org.kie.workbench.services.shared.metadata.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.Date;

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

    public DiscussionRecord( final String author,
                             final String note ) {
        this.author = author;
        this.note = note;
    }

    public DiscussionRecord( final long timestamp,
                             final String author,
                             final String note ) {
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
}
