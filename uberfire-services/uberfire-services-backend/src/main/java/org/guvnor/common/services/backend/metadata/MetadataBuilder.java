/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.java.nio.base.version.VersionRecord;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 *
 */
public final class MetadataBuilder {

    private Path path;
    private Path realPath;
    private LockInfo lockInfo;

    //git info
    private String checkinComment;
    private String lastContributor;
    private String creator;

    private Date lastModified;
    private Date dateCreated;

    //pure dcore
    private String subject;
    private String type;
    private String externalRelation;
    private String externalSource;
    private String description;

    //not dcore
    private List<String> tags = new ArrayList<String>();
    private List<DiscussionRecord> discussion = new ArrayList<DiscussionRecord>();
    private List<VersionRecord> version = new ArrayList<VersionRecord>();

    private boolean generated;

    private MetadataBuilder() {

    }

    public static MetadataBuilder newMetadata() {
        return new MetadataBuilder();
    }

    public MetadataBuilder withPath(final Path path) {
        this.path = checkNotNull("path",
                                 path);
        return this;
    }

    public MetadataBuilder withRealPath(final Path realPath) {
        this.realPath = checkNotNull("realPath",
                                     realPath);
        return this;
    }

    public MetadataBuilder withCheckinComment(final String checkinComment) {
        this.checkinComment = checkinComment;
        return this;
    }

    public MetadataBuilder withLastContributor(final String lastContributor) {
        this.lastContributor = lastContributor;
        return this;
    }

    public MetadataBuilder withCreator(final String creator) {
        this.creator = checkNotEmpty("creator",
                                     creator);
        return this;
    }

    public MetadataBuilder withLastModified(final Date date) {
        this.lastModified = checkNotNull("date",
                                         date);
        return this;
    }

    public MetadataBuilder withDateCreated(final Date date) {
        this.dateCreated = checkNotNull("date",
                                        date);
        return this;
    }

    public MetadataBuilder withSubject(final String subject) {
        this.subject = subject;
        return this;
    }

    public MetadataBuilder withType(final String type) {
        this.type = type;
        return this;
    }

    public MetadataBuilder withExternalRelation(final String externalRelation) {
        this.externalRelation = externalRelation;
        return this;
    }

    public MetadataBuilder withExternalSource(final String externalSource) {
        this.externalSource = externalSource;
        return this;
    }

    public MetadataBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public MetadataBuilder withTags(final List<String> tags) {
        this.tags = tags;
        return this;
    }

    public MetadataBuilder withDiscussion(final List<DiscussionRecord> discussion) {
        this.discussion = discussion;
        return this;
    }

    public MetadataBuilder withVersion(final List<VersionRecord> version) {
        this.version = version;
        return this;
    }

    public MetadataBuilder withLockInfo(final LockInfo lockInfo) {
        this.lockInfo = lockInfo;
        return this;
    }

    public MetadataBuilder withGenerated(final boolean generated) {
        this.generated = generated;
        return this;
    }

    public Metadata build() {
        return new Metadata(path,
                            realPath,
                            checkinComment,
                            lastContributor,
                            creator,
                            lastModified,
                            dateCreated,
                            subject,
                            type,
                            externalRelation,
                            externalSource,
                            description,
                            tags,
                            discussion,
                            version,
                            lockInfo,
                            generated);
    }
}
