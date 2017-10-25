/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.shared.metadata.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.java.nio.base.version.VersionRecord;

/**
 *
 */
@Portable
public class Metadata {

    private Path path;
    private Path realPath;
    private LockInfo lockInfo;

    //git info
    private String checkinComment;
    private String lastContributor;
    private String creator;
    //git -> basic file attrs
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

    public Metadata() {

    }

    public Metadata(final Path path,
                    final Path realPath,
                    final String checkinComment,
                    final String lastContributor,
                    final String creator,
                    final Date lastModified,
                    final Date dateCreated,
                    final String subject,
                    final String type,
                    final String externalRelation,
                    final String externalSource,
                    final String description,
                    final List<String> tags,
                    final List<DiscussionRecord> discussion,
                    final List<VersionRecord> version,
                    final LockInfo lockInfo,
                    final boolean generated) {
        this.path = path;
        this.realPath = realPath;
        this.checkinComment = checkinComment;
        this.lastContributor = lastContributor;
        this.creator = creator;
        this.lastModified = lastModified;
        this.dateCreated = dateCreated;
        this.subject = subject;
        this.type = type;
        this.externalRelation = externalRelation;
        this.externalSource = externalSource;
        this.description = description;
        this.tags = tags;
        this.discussion = discussion;
        this.version = version;
        this.lockInfo = lockInfo;
        this.generated = generated;
    }

    public Path getPath() {
        return path;
    }

    public Path getRealPath() {
        return realPath;
    }

    public String getCheckinComment() {
        return checkinComment;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public String getCreator() {
        return creator;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }

    public String getExternalRelation() {
        return externalRelation;
    }

    public String getExternalSource() {
        return externalSource;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<DiscussionRecord> getDiscussion() {
        return discussion;
    }

    public List<VersionRecord> getVersion() {
        return version;
    }

    public LockInfo getLockInfo() {
        return lockInfo;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setLockInfo(LockInfo lockInfo) {
        this.lockInfo = lockInfo;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setExternalRelation(final String externalRelation) {
        this.externalRelation = externalRelation;
    }

    public void setExternalSource(final String externalSource) {
        this.externalSource = externalSource;
    }

    public void addDiscussion(final DiscussionRecord discussionRecord) {
        this.discussion.add(discussionRecord);
    }

    public void eraseDiscussion() {
        this.discussion.clear();
    }

    public void addTag(final String tag) {
        tags.add(tag);
    }

    public void removeTag(final int idx) {
        tags.remove(idx);
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Metadata metadata = (Metadata) o;

        if (tags != null ? !tags.equals(metadata.tags) : metadata.tags != null) {
            return false;
        }
        if (checkinComment != null ? !checkinComment.equals(metadata.checkinComment) : metadata.checkinComment != null) {
            return false;
        }
        if (creator != null ? !creator.equals(metadata.creator) : metadata.creator != null) {
            return false;
        }
        if (dateCreated != null ? !dateCreated.equals(metadata.dateCreated) : metadata.dateCreated != null) {
            return false;
        }
        if (description != null ? !description.equals(metadata.description) : metadata.description != null) {
            return false;
        }
        if (discussion != null ? !discussion.equals(metadata.discussion) : metadata.discussion != null) {
            return false;
        }
        if (externalRelation != null ? !externalRelation.equals(metadata.externalRelation) : metadata.externalRelation != null) {
            return false;
        }
        if (externalSource != null ? !externalSource.equals(metadata.externalSource) : metadata.externalSource != null) {
            return false;
        }
        if (lastContributor != null ? !lastContributor.equals(metadata.lastContributor) : metadata.lastContributor != null) {
            return false;
        }
        if (lastModified != null ? !lastModified.equals(metadata.lastModified) : metadata.lastModified != null) {
            return false;
        }
        if (path != null ? !path.equals(metadata.path) : metadata.path != null) {
            return false;
        }
        if (realPath != null ? !realPath.equals(metadata.realPath) : metadata.realPath != null) {
            return false;
        }
        if (subject != null ? !subject.equals(metadata.subject) : metadata.subject != null) {
            return false;
        }
        if (type != null ? !type.equals(metadata.type) : metadata.type != null) {
            return false;
        }
        if (version != null ? !version.equals(metadata.version) : metadata.version != null) {
            return false;
        }
        if (lockInfo != null ? !lockInfo.equals(metadata.lockInfo) : metadata.lockInfo != null) {
            return false;
        }
        if (generated != metadata.generated) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (realPath != null ? realPath.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (checkinComment != null ? checkinComment.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (lastContributor != null ? lastContributor.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (externalRelation != null ? externalRelation.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (externalSource != null ? externalSource.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (discussion != null ? discussion.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (generated ? 1 : 0);
        result = ~~result;
        return result;
    }
}
