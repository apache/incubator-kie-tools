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

package org.guvnor.structure.repositories.changerequest.portable;

import java.util.Date;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class ChangeRequest {

    private Long id;
    private String spaceName;
    private String repositoryAlias;
    private String sourceBranch;
    private String targetBranch;
    private ChangeRequestStatus status;
    private String authorId;
    private String summary;
    private String description;
    private Date createdDate;
    private Integer changedFilesCount;
    private Integer commentsCount;
    private String startCommitId;
    private String endCommitId;
    private String mergeCommitId;
    private Boolean conflict;

    public ChangeRequest(final long id,
                         final String spaceName,
                         final String repositoryAlias,
                         final String sourceBranch,
                         final String targetBranch,
                         final String authorId,
                         final String summary,
                         final String description,
                         final String startCommitId) {
        this(id,
             spaceName,
             repositoryAlias,
             sourceBranch,
             targetBranch,
             ChangeRequestStatus.OPEN,
             authorId,
             summary,
             description,
             new Date(),
             startCommitId);
    }

    public ChangeRequest(final long id,
                         final String spaceName,
                         final String repositoryAlias,
                         final String sourceBranch,
                         final String targetBranch,
                         final ChangeRequestStatus status,
                         final String authorId,
                         final String summary,
                         final String description,
                         final Date createdDate,
                         final String startCommitId) {
        this(id,
             spaceName,
             repositoryAlias,
             sourceBranch,
             targetBranch,
             status,
             authorId,
             summary,
             description,
             createdDate,
             startCommitId,
             null,
             null);
    }

    public ChangeRequest(final long id,
                         final String spaceName,
                         final String repositoryAlias,
                         final String sourceBranch,
                         final String targetBranch,
                         final ChangeRequestStatus status,
                         final String authorId,
                         final String summary,
                         final String description,
                         final Date createdDate,
                         final String startCommitId,
                         final String endCommitId,
                         final String mergeCommitId) {
        this(id,
             spaceName,
             repositoryAlias,
             sourceBranch,
             targetBranch,
             status,
             authorId,
             summary,
             description,
             createdDate,
             0,
             0,
             startCommitId,
             endCommitId,
             mergeCommitId,
             false);
    }

    public ChangeRequest(@MapsTo("id") final Long id,
                         @MapsTo("spaceName") final String spaceName,
                         @MapsTo("repositoryAlias") final String repositoryAlias,
                         @MapsTo("sourceBranch") final String sourceBranch,
                         @MapsTo("targetBranch") final String targetBranch,
                         @MapsTo("status") final ChangeRequestStatus status,
                         @MapsTo("authorId") final String authorId,
                         @MapsTo("summary") final String summary,
                         @MapsTo("description") final String description,
                         @MapsTo("createdDate") final Date createdDate,
                         @MapsTo("changedFilesCount") final Integer changedFilesCount,
                         @MapsTo("commentsCount") final Integer commentsCount,
                         @MapsTo("startCommitId") final String startCommitId,
                         @MapsTo("endCommitId") final String endCommitId,
                         @MapsTo("mergeCommitId") final String mergeCommitId,
                         @MapsTo("conflict") final Boolean conflict) {

        this.id = checkNotNull("id",
                               id);
        this.spaceName = checkNotEmpty("spaceName",
                                       spaceName);
        this.repositoryAlias = checkNotEmpty("repositoryAlias",
                                             repositoryAlias);
        this.sourceBranch = checkNotEmpty("sourceBranch",
                                          sourceBranch);
        this.targetBranch = checkNotEmpty("targetBranch",
                                          targetBranch);
        this.status = checkNotNull("status",
                                   status);
        this.authorId = checkNotEmpty("authorId",
                                      authorId);
        this.summary = checkNotEmpty("summary",
                                     summary);
        this.description = checkNotEmpty("description",
                                         description);
        this.createdDate = checkNotNull("createdDate",
                                        createdDate);
        this.changedFilesCount = checkNotNull("changedFilesCount",
                                              changedFilesCount);
        this.commentsCount = checkNotNull("commentsCount",
                                          commentsCount);
        this.startCommitId = checkNotEmpty("startCommitId",
                                           startCommitId);
        this.endCommitId = endCommitId; // can be null
        this.mergeCommitId = mergeCommitId; // can be null
        this.conflict = checkNotNull("conflict",
                                     conflict);
    }

    public long getId() {
        return this.id;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public String getRepositoryAlias() {
        return this.repositoryAlias;
    }

    public String getSourceBranch() {
        return this.sourceBranch;
    }

    public String getTargetBranch() {
        return this.targetBranch;
    }

    public ChangeRequestStatus getStatus() {
        return this.status;
    }

    public String getAuthorId() {
        return this.authorId;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Integer getChangedFilesCount() {
        return this.changedFilesCount;
    }

    public Integer getCommentsCount() {
        return this.commentsCount;
    }

    public String getStartCommitId() {
        return startCommitId;
    }

    public String getEndCommitId() {
        return endCommitId;
    }

    public String getMergeCommitId() {
        return mergeCommitId;
    }

    public Boolean isConflict() {
        return conflict;
    }

    @Override
    public String toString() {
        return "(#" + this.id + ") " + this.summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChangeRequest that = (ChangeRequest) o;
        return id.equals(that.id) &&
                spaceName.equals(that.spaceName) &&
                repositoryAlias.equals(that.repositoryAlias) &&
                sourceBranch.equals(that.sourceBranch) &&
                targetBranch.equals(that.targetBranch) &&
                status == that.status &&
                authorId.equals(that.authorId) &&
                summary.equals(that.summary) &&
                description.equals(that.description) &&
                createdDate.equals(that.createdDate) &&
                changedFilesCount.equals(that.changedFilesCount) &&
                commentsCount.equals(that.commentsCount) &&
                startCommitId.equals(that.startCommitId) &&
                endCommitId.equals(that.endCommitId) &&
                mergeCommitId.equals(that.getMergeCommitId()) &&
                conflict.equals(that.conflict);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                            spaceName,
                            repositoryAlias,
                            sourceBranch,
                            targetBranch,
                            status,
                            authorId,
                            summary,
                            description,
                            createdDate,
                            changedFilesCount,
                            commentsCount,
                            startCommitId,
                            endCommitId,
                            mergeCommitId,
                            conflict);
    }
}
