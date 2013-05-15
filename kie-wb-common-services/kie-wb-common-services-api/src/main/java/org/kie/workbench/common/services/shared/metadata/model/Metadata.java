/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.shared.metadata.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.java.nio.base.version.VersionRecord;
import org.uberfire.backend.vfs.Path;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Portable
public class Metadata {

    private Path path;

    //git info
    private String checkinComment;
    private String lastContributor;
    private String creator;
    //git -> basic file attrs
    private Date   lastModified;
    private Date   dateCreated;

    //pure dcore
    private String subject;
    private String type;
    private String externalRelation;
    private String externalSource;
    private String description;

    //not dcore
    private List<String>           categories = new ArrayList<String>();
    private List<DiscussionRecord> discussion = new ArrayList<DiscussionRecord>();
    private List<VersionRecord>    version    = new ArrayList<VersionRecord>();

    public Metadata() {

    }

    public Metadata( final Path path,
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
                     final List<String> categories,
                     final List<DiscussionRecord> discussion,
                     final List<VersionRecord> version ) {
        this.path = path;
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
        this.categories = categories;
        this.discussion = discussion;
        this.version = version;
    }

    public Path getPath() {
        return path;
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

    public List<String> getCategories() {
        return categories;
    }

    public List<DiscussionRecord> getDiscussion() {
        return discussion;
    }

    public List<VersionRecord> getVersion() {
        return version;
    }

    public void setSubject( final String subject ) {
        this.subject = subject;
    }

    public void setType( final String type ) {
        this.type = type;
    }

    public void setExternalRelation( final String externalRelation ) {
        this.externalRelation = externalRelation;
    }

    public void setExternalSource( final String externalSource ) {
        this.externalSource = externalSource;
    }

    public void addDiscussion( final DiscussionRecord discussionRecord ) {
        this.discussion.add( discussionRecord );
    }

    public void eraseDiscussion() {
        this.discussion.clear();
    }

    public void addCategory( final String category ) {
        categories.add( category );
    }

    public void removeCategory( final int idx ) {
        categories.remove( idx );
    }

    public void setDescription( final String description ) {
        this.description = description;
    }
}
