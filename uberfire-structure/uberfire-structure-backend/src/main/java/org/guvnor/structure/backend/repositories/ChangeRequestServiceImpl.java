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

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestPredicates;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestAlreadyOpenException;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestComment;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestCommit;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestCountSummary;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestListUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatusUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeType;
import org.guvnor.structure.repositories.changerequest.portable.NothingToMergeException;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestCommentList;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestList;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.base.TextualDiff;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.GitHookSupport;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;
import org.uberfire.java.nio.fs.jgit.util.model.MessageCommitInfo;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;
import org.uberfire.java.nio.fs.jgit.ws.JGitWatchEvent;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import static java.lang.Integer.min;
import static java.util.stream.Collectors.toMap;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private static final String SPACE_NAME_PARAM = "spaceName";
    private static final String REPOSITORY_ALIAS_PARAM = "repositoryAlias";
    private static final String STATUS_LIST_PARAM = "statusList";
    private static final String CHANGE_REQUEST_ID_PARAM = "changeRequestId";
    private static final String COMMIT_MESSAGE_PARAM = "commitMessage";
    private static final String PAGE_PARAM = "page";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String SOURCE_BRANCH_PARAM = "sourceBranch";
    private static final String TARGET_BRANCH_PARAM = "targetBranch";
    private static final String SUMMARY_PARAM = "summary";
    private static final String DESCRIPTION_PARAM = "description";
    private static final String ASSOCIATED_BRANCH_NAME_PARAM = "associatedBranchName";
    private static final String TEXT_PARAM = "text";
    private static final String COMMENT_ID_PARAM = "commentId";
    private static final String UPDATED_SUMMARY_PARAM = "updatedSummary";
    private static final String UPDATED_DESCRIPTION_PARAM = "updatedDescription";

    private final SpaceConfigStorageRegistry spaceConfigStorageRegistry;
    private final RepositoryService repositoryService;
    private final SpacesAPI spaces;
    private final Event<ChangeRequestListUpdatedEvent> changeRequestListUpdatedEvent;
    private final Event<ChangeRequestUpdatedEvent> changeRequestUpdatedEvent;
    private final Event<ChangeRequestStatusUpdatedEvent> changeRequestStatusUpdatedEventEvent;
    private final BranchAccessAuthorizer branchAccessAuthorizer;
    private final SessionInfo sessionInfo;

    private Logger logger = LoggerFactory.getLogger(ChangeRequestServiceImpl.class);

    @Inject
    public ChangeRequestServiceImpl(final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                                    final RepositoryService repositoryService,
                                    final SpacesAPI spaces,
                                    final Event<ChangeRequestListUpdatedEvent> changeRequestListUpdatedEvent,
                                    final Event<ChangeRequestUpdatedEvent> changeRequestUpdatedEvent,
                                    final Event<ChangeRequestStatusUpdatedEvent> changeRequestStatusUpdatedEventEvent,
                                    final BranchAccessAuthorizer branchAccessAuthorizer,
                                    final SessionInfo sessionInfo) {
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
        this.repositoryService = repositoryService;
        this.spaces = spaces;
        this.changeRequestListUpdatedEvent = changeRequestListUpdatedEvent;
        this.changeRequestUpdatedEvent = changeRequestUpdatedEvent;
        this.changeRequestStatusUpdatedEventEvent = changeRequestStatusUpdatedEventEvent;
        this.branchAccessAuthorizer = branchAccessAuthorizer;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public ChangeRequest createChangeRequest(final String spaceName,
                                             final String repositoryAlias,
                                             final String sourceBranch,
                                             final String targetBranch,
                                             final String summary,
                                             final String description) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotEmpty(SOURCE_BRANCH_PARAM, sourceBranch);
        checkNotEmpty(TARGET_BRANCH_PARAM, targetBranch);
        checkNotEmpty(SUMMARY_PARAM, summary);
        checkNotEmpty(DESCRIPTION_PARAM, description);

        checkChangeRequestAlreadyOpen(spaceName,
                                      repositoryAlias,
                                      sourceBranch,
                                      targetBranch);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        long changeRequestId = this.generateChangeRequestId(spaceName,
                                                            repositoryAlias);

        final String startCommitId = getCommonCommitId(repository,
                                                       sourceBranch,
                                                       targetBranch);

        final ChangeRequest newChangeRequest = new ChangeRequest(changeRequestId,
                                                                 spaceName,
                                                                 repositoryAlias,
                                                                 sourceBranch,
                                                                 targetBranch,
                                                                 sessionInfo.getIdentity().getIdentifier(),
                                                                 summary,
                                                                 description,
                                                                 startCommitId);

        spaceConfigStorageRegistry.get(spaceName).saveChangeRequest(repositoryAlias,
                                                                    newChangeRequest);

        changeRequestListUpdatedEvent.fire(new ChangeRequestListUpdatedEvent(repository.getIdentifier()));

        return newChangeRequest;
    }

    @Override
    public List<ChangeRequest> getChangeRequests(final String spaceName,
                                                 final String repositoryAlias) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);

        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     true,
                                                     ChangeRequestPredicates.matchAll());

        return computeFullContent(spaceName,
                                  repositoryAlias,
                                  changeRequests);
    }

    @Override
    public List<ChangeRequest> getChangeRequests(final String spaceName,
                                                 final String repositoryAlias,
                                                 final String filter) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);

        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     true,
                                                     ChangeRequestPredicates
                                                             .matchSearchFilter(filter,
                                                                                ChangeRequestServiceImpl
                                                                                ::composeSearchableElement));

        return computeFullContent(spaceName,
                                  repositoryAlias,
                                  changeRequests);
    }

    @Override
    public List<ChangeRequest> getChangeRequests(final String spaceName,
                                                 final String repositoryAlias,
                                                 final List<ChangeRequestStatus> statusList) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotEmpty(STATUS_LIST_PARAM, statusList);

        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     true,
                                                     ChangeRequestPredicates.matchInStatusList(statusList));

        return computeFullContent(spaceName,
                                  repositoryAlias,
                                  changeRequests);
    }

    @Override
    public List<ChangeRequest> getChangeRequests(final String spaceName,
                                                 final String repositoryAlias,
                                                 final List<ChangeRequestStatus> statusList,
                                                 final String filter) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotEmpty(STATUS_LIST_PARAM, statusList);

        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(
                        spaceName,
                        repositoryAlias,
                        true,
                        ChangeRequestPredicates.matchSearchFilterAndStatusList(filter,
                                                                               ChangeRequestServiceImpl::
                                                                                       composeSearchableElement,
                                                                               statusList));

        return computeFullContent(spaceName,
                                  repositoryAlias,
                                  changeRequests);
    }

    @Override
    public PaginatedChangeRequestList getChangeRequests(final String spaceName,
                                                        final String repositoryAlias,
                                                        final Integer page,
                                                        final Integer pageSize,
                                                        final String filter) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(PAGE_PARAM, page);
        checkNotNull(PAGE_SIZE_PARAM, pageSize);

        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     true,
                                                     ChangeRequestPredicates
                                                             .matchSearchFilter(filter,
                                                                                ChangeRequestServiceImpl::
                                                                                        composeSearchableElement));

        final List<ChangeRequest> paginatedChangeRequests = paginateChangeRequests(changeRequests,
                                                                                   page,
                                                                                   pageSize);

        return new PaginatedChangeRequestList(computeFullContent(spaceName,
                                                                 repositoryAlias,
                                                                 paginatedChangeRequests),
                                              page,
                                              pageSize,
                                              changeRequests.size());
    }

    @Override
    public PaginatedChangeRequestList getChangeRequests(final String spaceName,
                                                        final String repositoryAlias,
                                                        final Integer page,
                                                        final Integer pageSize,
                                                        final List<ChangeRequestStatus> statusList,
                                                        final String filter) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(PAGE_PARAM, page);
        checkNotNull(PAGE_SIZE_PARAM, pageSize);
        checkNotEmpty(STATUS_LIST_PARAM, statusList);

        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     true,
                                                     ChangeRequestPredicates
                                                             .matchSearchFilterAndStatusList(filter,
                                                                                             ChangeRequestServiceImpl::
                                                                                                     composeSearchableElement,
                                                                                             statusList));

        final List<ChangeRequest> paginatedChangeRequests = paginateChangeRequests(changeRequests,
                                                                                   page,
                                                                                   pageSize);

        return new PaginatedChangeRequestList(computeFullContent(spaceName,
                                                                 repositoryAlias,
                                                                 paginatedChangeRequests),
                                              page,
                                              pageSize,
                                              changeRequests.size());
    }

    @Override
    public ChangeRequest getChangeRequest(final String spaceName,
                                          final String repositoryAlias,
                                          final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        return getChangeRequestById(spaceName,
                                    repositoryAlias,
                                    true,
                                    changeRequestId);
    }

    @Override
    public ChangeRequestCountSummary countChangeRequests(final String spaceName,
                                                         final String repositoryAlias) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);

        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     false,
                                                     ChangeRequestPredicates.matchAll());

        final Integer total = changeRequests.size();
        final long open = changeRequests.stream()
                .filter(elem -> elem.getStatus() == ChangeRequestStatus.OPEN)
                .count();

        return new ChangeRequestCountSummary(total,
                                             (int) open);
    }

    @Override
    public List<ChangeRequestDiff> getDiff(final String spaceName,
                                           final String repositoryAlias,
                                           final String sourceBranch,
                                           final String targetBranch) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(SOURCE_BRANCH_PARAM, sourceBranch);
        checkNotNull(TARGET_BRANCH_PARAM, targetBranch);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        return getDiff(repository,
                       sourceBranch,
                       targetBranch,
                       null,
                       null);
    }

    @Override
    public List<ChangeRequestDiff> getDiff(final String spaceName,
                                           final String repositoryAlias,
                                           final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        return getDiff(repository,
                       changeRequest.getSourceBranch(),
                       changeRequest.getTargetBranch(),
                       changeRequest.getStartCommitId(),
                       changeRequest.getEndCommitId());
    }

    @Override
    public void deleteChangeRequests(final String spaceName,
                                     final String repositoryAlias,
                                     final String associatedBranchName) {
        deleteChangeRequests(spaceName,
                             repositoryAlias,
                             associatedBranchName,
                             sessionInfo.getIdentity().getIdentifier());
    }

    @Override
    public void deleteChangeRequests(final String spaceName,
                                     final String repositoryAlias,
                                     final String associatedBranchName,
                                     final String userIdentifier) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotEmpty(ASSOCIATED_BRANCH_NAME_PARAM, associatedBranchName);

        final List<ChangeRequest> changeRequestsToDelete =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     false,
                                                     ChangeRequestPredicates
                                                             .matchSourceOrTargetBranch(associatedBranchName),
                                                     userIdentifier);

        if (!changeRequestsToDelete.isEmpty()) {
            changeRequestsToDelete.forEach(elem -> spaceConfigStorageRegistry.get(spaceName)
                    .deleteChangeRequest(repositoryAlias,
                                         elem.getId()));

            final Repository repository = resolveRepository(spaceName,
                                                            repositoryAlias);

            changeRequestListUpdatedEvent.fire(new ChangeRequestListUpdatedEvent(repository.getIdentifier()));
        }
    }

    @Override
    public void rejectChangeRequest(final String spaceName,
                                    final String repositoryAlias,
                                    final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        if (changeRequest.getStatus() != ChangeRequestStatus.OPEN) {
            throw new IllegalStateException("Cannot reject a change request that is not open");
        }

        this.updateNotMergedChangeRequestStatus(spaceName,
                                                repositoryAlias,
                                                changeRequest,
                                                ChangeRequestStatus.REJECTED);
    }

    @Override
    public Boolean mergeChangeRequest(final String spaceName,
                                      final String repositoryAlias,
                                      final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        if (changeRequest.getStatus() != ChangeRequestStatus.OPEN) {
            throw new IllegalStateException("Cannot accept a change request that is not open");
        }

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final CommitInfo commitInfo = buildCommitInfo(String.format(MessageCommitInfo.MERGE_MESSAGE,
                                                                    changeRequest.getSourceBranch()));

        return tryMergeChangeRequest(repository,
                                     changeRequest,
                                     commitInfo,
                                     false);
    }

    @Override
    public Boolean revertChangeRequest(final String spaceName,
                                       final String repositoryAlias,
                                       final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        if (changeRequest.getStatus() != ChangeRequestStatus.ACCEPTED) {
            throw new IllegalStateException("Cannot revert a change request that is not accepted");
        }

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        return tryRevertChangeRequest(repository,
                                      changeRequest);
    }

    @Override
    public void closeChangeRequest(final String spaceName,
                                   final String repositoryAlias,
                                   final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        if (changeRequest.getStatus() != ChangeRequestStatus.OPEN) {
            throw new IllegalStateException("Cannot close a change request that is not open");
        }

        this.updateNotMergedChangeRequestStatus(spaceName,
                                                repositoryAlias,
                                                changeRequest,
                                                ChangeRequestStatus.CLOSED);
    }

    @Override
    public void reopenChangeRequest(final String spaceName,
                                    final String repositoryAlias,
                                    final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        if (changeRequest.getStatus() != ChangeRequestStatus.CLOSED &&
                changeRequest.getStatus() != ChangeRequestStatus.REJECTED) {
            throw new IllegalStateException("Cannot reopen a change request that is not closed/rejected");
        }

        checkChangeRequestAlreadyOpen(spaceName,
                                      repositoryAlias,
                                      changeRequest.getSourceBranch(),
                                      changeRequest.getTargetBranch());

        this.updateNotMergedChangeRequestStatus(spaceName,
                                                repositoryAlias,
                                                changeRequest,
                                                ChangeRequestStatus.OPEN);
    }

    @Override
    public void updateChangeRequestSummary(final String spaceName,
                                           final String repositoryAlias,
                                           final Long changeRequestId,
                                           final String updatedSummary) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);
        checkNotEmpty(UPDATED_SUMMARY_PARAM, updatedSummary);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final ChangeRequest oldChangeRequest = getChangeRequestById(spaceName,
                                                                    repositoryAlias,
                                                                    false,
                                                                    changeRequestId);

        final ChangeRequest updatedChangeRequest = new ChangeRequest(oldChangeRequest.getId(),
                                                                     oldChangeRequest.getSpaceName(),
                                                                     oldChangeRequest.getRepositoryAlias(),
                                                                     oldChangeRequest.getSourceBranch(),
                                                                     oldChangeRequest.getTargetBranch(),
                                                                     oldChangeRequest.getStatus(),
                                                                     oldChangeRequest.getAuthorId(),
                                                                     updatedSummary,
                                                                     oldChangeRequest.getDescription(),
                                                                     oldChangeRequest.getCreatedDate(),
                                                                     oldChangeRequest.getStartCommitId(),
                                                                     oldChangeRequest.getEndCommitId(),
                                                                     oldChangeRequest.getMergeCommitId());

        spaceConfigStorageRegistry.get(spaceName).saveChangeRequest(repositoryAlias,
                                                                    updatedChangeRequest);

        changeRequestUpdatedEvent.fire(new ChangeRequestUpdatedEvent(repository.getIdentifier(),
                                                                     updatedChangeRequest.getId(),
                                                                     sessionInfo.getIdentity().getIdentifier()));
    }

    @Override
    public void updateChangeRequestDescription(final String spaceName,
                                               final String repositoryAlias,
                                               final Long changeRequestId,
                                               final String updatedDescription) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);
        checkNotEmpty(UPDATED_DESCRIPTION_PARAM, updatedDescription);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final ChangeRequest oldChangeRequest = getChangeRequestById(spaceName,
                                                                    repositoryAlias,
                                                                    false,
                                                                    changeRequestId);

        final ChangeRequest updatedChangeRequest = new ChangeRequest(oldChangeRequest.getId(),
                                                                     oldChangeRequest.getSpaceName(),
                                                                     oldChangeRequest.getRepositoryAlias(),
                                                                     oldChangeRequest.getSourceBranch(),
                                                                     oldChangeRequest.getTargetBranch(),
                                                                     oldChangeRequest.getStatus(),
                                                                     oldChangeRequest.getAuthorId(),
                                                                     oldChangeRequest.getSummary(),
                                                                     updatedDescription,
                                                                     oldChangeRequest.getCreatedDate(),
                                                                     oldChangeRequest.getStartCommitId(),
                                                                     oldChangeRequest.getEndCommitId(),
                                                                     oldChangeRequest.getMergeCommitId());

        spaceConfigStorageRegistry.get(spaceName).saveChangeRequest(repositoryAlias,
                                                                    updatedChangeRequest);

        changeRequestUpdatedEvent.fire(new ChangeRequestUpdatedEvent(repository.getIdentifier(),
                                                                     updatedChangeRequest.getId(),
                                                                     sessionInfo.getIdentity().getIdentifier()));
    }

    @Override
    public PaginatedChangeRequestCommentList getComments(final String spaceName,
                                                         final String repositoryAlias,
                                                         final Long changeRequestId,
                                                         final Integer page,
                                                         final Integer pageSize) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final List<ChangeRequestComment> comments =
                spaceConfigStorageRegistry.get(spaceName).loadChangeRequestComments(repositoryAlias,
                                                                                    changeRequestId)
                        .stream()
                        .sorted(Comparator.comparing(ChangeRequestComment::getCreatedDate).reversed())
                        .collect(Collectors.toList());

        final List<ChangeRequestComment> paginatedList = paginateComments(comments,
                                                                          page,
                                                                          pageSize);

        return new PaginatedChangeRequestCommentList(paginatedList,
                                                     page,
                                                     pageSize,
                                                     comments.size());
    }

    @Override
    public void addComment(final String spaceName,
                           final String repositoryAlias,
                           final Long changeRequestId,
                           final String text) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);
        checkNotEmpty(TEXT_PARAM, text);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final Long commentId = generateCommentId(spaceName,
                                                 repositoryAlias,
                                                 changeRequestId);

        final ChangeRequestComment newComment = new ChangeRequestComment(commentId,
                                                                         sessionInfo.getIdentity().getIdentifier(),
                                                                         new Date(),
                                                                         text);

        spaceConfigStorageRegistry.get(spaceName).saveChangeRequestComment(repositoryAlias,
                                                                           changeRequestId,
                                                                           newComment);

        changeRequestUpdatedEvent.fire(new ChangeRequestUpdatedEvent(repository.getIdentifier(),
                                                                     changeRequestId,
                                                                     sessionInfo.getIdentity().getIdentifier()));
    }

    @Override
    public void deleteComment(final String spaceName,
                              final String repositoryAlias,
                              final Long changeRequestId,
                              final Long commentId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);
        checkNotNull(COMMENT_ID_PARAM, commentId);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        spaceConfigStorageRegistry.get(spaceName).deleteChangeRequestComment(repositoryAlias,
                                                                             changeRequestId,
                                                                             commentId);

        changeRequestUpdatedEvent.fire(new ChangeRequestUpdatedEvent(repository.getIdentifier(),
                                                                     changeRequestId,
                                                                     sessionInfo.getIdentity().getIdentifier()));
    }

    @Override
    public List<ChangeRequestCommit> getCommits(final String spaceName,
                                                final String repositoryAlias,
                                                final Long changeRequestId) {
        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        final Git git = getGitFromBranch(repository,
                                         changeRequest.getSourceBranch());

        final String startCommitId = changeRequest.getStartCommitId();
        final String endCommitId = git.getLastCommit(changeRequest.getSourceBranch()).getName();

        return git.listCommits(startCommitId, endCommitId)
                .stream()
                .map(c -> new ChangeRequestCommit(c.getName(),
                                                  c.getFullMessage()))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean squashChangeRequest(final String spaceName,
                                       final String repositoryAlias,
                                       final Long changeRequestId,
                                       final String commitMessage) {

        checkNotEmpty(SPACE_NAME_PARAM, spaceName);
        checkNotEmpty(REPOSITORY_ALIAS_PARAM, repositoryAlias);
        checkNotNull(CHANGE_REQUEST_ID_PARAM, changeRequestId);
        checkNotNull(COMMIT_MESSAGE_PARAM, commitMessage);

        final ChangeRequest changeRequest = getChangeRequestById(spaceName,
                                                                 repositoryAlias,
                                                                 false,
                                                                 changeRequestId);

        if (changeRequest.getStatus() != ChangeRequestStatus.OPEN) {
            throw new IllegalStateException("Cannot squash a change request that is not open");
        }

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final CommitInfo commitInfo = buildCommitInfo(commitMessage);

        return tryMergeChangeRequest(repository,
                                     changeRequest,
                                     commitInfo,
                                     true);
    }

    private ChangeRequest getChangeRequestById(final String spaceName,
                                               final String repositoryAlias,
                                               final boolean withFullContent,
                                               final Long changeRequestId) {
        final List<ChangeRequest> changeRequests =
                this.getFilteredChangeRequestsFromStorage(spaceName,
                                                          repositoryAlias,
                                                          false,
                                                          ChangeRequestPredicates.matchId(changeRequestId));

        if (changeRequests.isEmpty()) {
            throw new NoSuchElementException("Unable to find the change request with id #" + changeRequestId);
        }

        return withFullContent ? computeFullContent(spaceName,
                                                    repositoryAlias,
                                                    changeRequests).get(0) : changeRequests.get(0);
    }

    private org.uberfire.backend.vfs.Path createPath(final String branchPath,
                                                     final String filePath) {
        return PathFactory.newPath(filePath,
                                   branchPath + filePath);
    }

    private Repository resolveRepository(final String spaceName,
                                         final String repositoryAlias) {
        Repository repository = repositoryService.getRepositoryFromSpace(spaces.getSpace(spaceName), repositoryAlias);

        if (repository == null) {
            final String msg = String.format("The repository %s was not found in the space %s",
                                             repositoryAlias,
                                             spaceName);

            throw new NoSuchElementException(msg);
        }

        return repository;
    }

    private List<ChangeRequest> getFilteredChangeRequestsFromStorage(final String spaceName,
                                                                     final String repositoryAlias,
                                                                     final boolean sorted,
                                                                     final Predicate<ChangeRequest> predicate) {
        return getFilteredChangeRequestsFromStorage(spaceName,
                                             repositoryAlias,
                                             sorted,
                                             predicate,
                                             sessionInfo.getIdentity().getIdentifier());
    }

    private List<ChangeRequest> getFilteredChangeRequestsFromStorage(final String spaceName,
                                                                     final String repositoryAlias,
                                                                     final boolean sorted,
                                                                     final Predicate<ChangeRequest> predicate,
                                                                     final String userIdentifier) {
        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final List<String> branchesUserCanRead = repository.getBranches()
                .stream()
                .map(Branch::getName)
                .filter(branchName -> branchAccessAuthorizer.authorize(userIdentifier,
                                                                       repository.getSpace().getName(),
                                                                       repository.getIdentifier(),
                                                                       repository.getAlias(),
                                                                       branchName,
                                                                       BranchAccessAuthorizer.AccessType.READ))
                .collect(Collectors.toList());

        final Stream<ChangeRequest> changeRequestStream =
                spaceConfigStorageRegistry.get(spaceName).loadChangeRequests(repositoryAlias)
                        .stream()
                        .filter(ChangeRequestPredicates.matchTargetBranchListAndOtherPredicate(branchesUserCanRead,
                                                                                               predicate));
        if (sorted) {
            return changeRequestStream
                    .sorted(Comparator.comparing(ChangeRequest::getCreatedDate).reversed())
                    .collect(Collectors.toList());
        } else {
            return changeRequestStream.collect(Collectors.toList());
        }
    }

    private List<ChangeRequest> computeFullContent(final String spaceName,
                                                   final String repositoryAlias,
                                                   final List<ChangeRequest> changeRequests) {
        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        return changeRequests
                .stream()
                .map(elem -> {
                    final int changedFilesCount = countChangeRequestDiffs(repository,
                                                                          elem);

                    final int commentsCount = countChangeRequestComments(spaceName,
                                                                         repositoryAlias,
                                                                         elem.getId());

                    final boolean hasConflicts = !isChangeRequestConflictFree(repository,
                                                                              elem);

                    return new ChangeRequest(elem.getId(),
                                             elem.getSpaceName(),
                                             elem.getRepositoryAlias(),
                                             elem.getSourceBranch(),
                                             elem.getTargetBranch(),
                                             elem.getStatus(),
                                             elem.getAuthorId(),
                                             elem.getSummary(),
                                             elem.getDescription(),
                                             elem.getCreatedDate(),
                                             changedFilesCount,
                                             commentsCount,
                                             elem.getStartCommitId(),
                                             elem.getEndCommitId(),
                                             elem.getMergeCommitId(),
                                             hasConflicts);
                })
                .collect(Collectors.toList());
    }

    private static String composeSearchableElement(final ChangeRequest element) {
        return element.toString().toLowerCase();
    }

    private List<ChangeRequest> paginateChangeRequests(final List<ChangeRequest> changeRequests,
                                                       final Integer page,
                                                       final Integer pageSize) {
        if (page == 0 && pageSize == 0) {
            return changeRequests;
        }

        final Map<Integer, List<ChangeRequest>> map = IntStream.iterate(0,
                                                                        i -> i + pageSize)
                .limit((changeRequests.size() + pageSize - 1) / pageSize)
                .boxed()
                .collect(toMap(i -> i / pageSize,
                               i -> changeRequests.subList(i,
                                                           min(i + pageSize,
                                                               changeRequests.size()))));

        List<ChangeRequest> paginatedChangeRequests = new ArrayList<>();

        if (map.containsKey(page)) {
            paginatedChangeRequests.addAll(map.get(page));
        }

        return paginatedChangeRequests;
    }

    private Integer countChangeRequestComments(final String spaceName,
                                               final String repositoryAlias,
                                               final Long changeRequestId) {
        return spaceConfigStorageRegistry.get(spaceName).getChangeRequestCommentIds(repositoryAlias,
                                                                                    changeRequestId).size();
    }

    private List<ChangeRequestComment> paginateComments(final List<ChangeRequestComment> comments,
                                                        final Integer page,
                                                        final Integer pageSize) {
        if (page == 0 && pageSize == 0) {
            return comments;
        }

        final Map<Integer, List<ChangeRequestComment>> map = IntStream.iterate(0,
                                                                               i -> i + pageSize)
                .limit((comments.size() + pageSize - 1) / pageSize)
                .boxed()
                .collect(toMap(i -> i / pageSize,
                               i -> comments.subList(i,
                                                     min(i + pageSize,
                                                         comments.size()))));

        List<ChangeRequestComment> paginatedComments = new ArrayList<>();

        if (map.containsKey(page)) {
            paginatedComments.addAll(map.get(page));
        }

        return paginatedComments;
    }

    private long generateChangeRequestId(final String spaceName,
                                         final String repositoryAlias) {
        Optional<Long> maxId = spaceConfigStorageRegistry.get(spaceName)
                .getChangeRequestIds(repositoryAlias)
                .stream()
                .max(Long::compare);

        return maxId.orElse(0L) + 1;
    }

    private long generateCommentId(final String spaceName,
                                   final String repositoryAlias,
                                   final Long changeRequestId) {
        Optional<Long> maxId = spaceConfigStorageRegistry.get(spaceName)
                .getChangeRequestCommentIds(repositoryAlias,
                                            changeRequestId)
                .stream()
                .max(Long::compare);

        return maxId.orElse(0L) + 1;
    }

    private void updateNotMergedChangeRequestStatus(final String spaceName,
                                                    final String repositoryAlias,
                                                    final ChangeRequest oldChangeRequest,
                                                    final ChangeRequestStatus status) {
        this.updateChangeRequestStatus(spaceName,
                                       repositoryAlias,
                                       oldChangeRequest,
                                       status,
                                       null);
    }

    private void updateChangeRequestStatus(final String spaceName,
                                           final String repositoryAlias,
                                           final ChangeRequest oldChangeRequest,
                                           final ChangeRequestStatus status,
                                           final String mergeCommitId) {
        if (mergeCommitId == null && status == ChangeRequestStatus.ACCEPTED) {
            throw new IllegalStateException("Must have a merge commit id to update change request to ACCEPTED.");
        }

        final Repository repository = resolveRepository(spaceName,
                                                        repositoryAlias);

        final String startCommitId = resolveStartCommitIdOnStatusUpdated(repository,
                                                                         oldChangeRequest,
                                                                         status);

        final String endCommitId = resolveEndCommitIdOnStatusUpdated(repository,
                                                                     oldChangeRequest,
                                                                     status);

        final ChangeRequest updatedChangeRequest = new ChangeRequest(oldChangeRequest.getId(),
                                                                     oldChangeRequest.getSpaceName(),
                                                                     oldChangeRequest.getRepositoryAlias(),
                                                                     oldChangeRequest.getSourceBranch(),
                                                                     oldChangeRequest.getTargetBranch(),
                                                                     status,
                                                                     oldChangeRequest.getAuthorId(),
                                                                     oldChangeRequest.getSummary(),
                                                                     oldChangeRequest.getDescription(),
                                                                     oldChangeRequest.getCreatedDate(),
                                                                     startCommitId,
                                                                     endCommitId,
                                                                     mergeCommitId);

        spaceConfigStorageRegistry.get(spaceName).saveChangeRequest(repositoryAlias,
                                                                    updatedChangeRequest);

        changeRequestStatusUpdatedEventEvent.fire(
                new ChangeRequestStatusUpdatedEvent(repository.getIdentifier(),
                                                    updatedChangeRequest.getId(),
                                                    oldChangeRequest.getStatus(),
                                                    status,
                                                    sessionInfo.getIdentity().getIdentifier()));
    }

    private String resolveStartCommitIdOnStatusUpdated(final Repository repository,
                                                       final ChangeRequest changeRequest,
                                                       final ChangeRequestStatus newStatus) {
        if (newStatus == ChangeRequestStatus.OPEN) {
            return getCommonCommitId(repository,
                                     changeRequest.getSourceBranch(),
                                     changeRequest.getTargetBranch());
        }

        return changeRequest.getStartCommitId();
    }

    private String resolveEndCommitIdOnStatusUpdated(final Repository repository,
                                                     final ChangeRequest changeRequest,
                                                     final ChangeRequestStatus newStatus) {
        if (newStatus == ChangeRequestStatus.OPEN) {
            return null;
        }

        if (changeRequest.getStatus() == ChangeRequestStatus.OPEN) {
            return getLastCommitId(repository, changeRequest.getSourceBranch());
        }

        return changeRequest.getEndCommitId();
    }

    private List<ChangeRequestDiff> getDiff(final Repository repository,
                                            final String sourceBranchName,
                                            final String targetBranchName,
                                            final String startCommitId,
                                            final String lastCommitId) {
        final Branch sourceBranch = resolveBranch(repository,
                                                  sourceBranchName);

        final Branch targetBranch = resolveBranch(repository,
                                                  targetBranchName);

        final List<String> conflicts = getConflicts(repository,
                                                    sourceBranchName,
                                                    targetBranchName);

        return getTextualDiff(repository,
                              sourceBranchName,
                              targetBranchName,
                              startCommitId,
                              lastCommitId)
                .stream()
                .sorted(Comparator.comparing(TextualDiff::getChangeType))
                .map(textualDiff -> new ChangeRequestDiff(
                        createPath(sourceBranch.getPath().toURI(), textualDiff.getOldFilePath()),
                        createPath(targetBranch.getPath().toURI(), textualDiff.getNewFilePath()),
                        ChangeType.valueOf(textualDiff.getChangeType()),
                        textualDiff.getLinesAdded(),
                        textualDiff.getLinesDeleted(),
                        textualDiff.getDiffText(),
                        conflicts.contains(textualDiff.getOldFilePath()) ||
                                conflicts.contains(textualDiff.getNewFilePath())
                )).collect(Collectors.toList());
    }

    private Branch resolveBranch(final Repository repository,
                                 final String branchName) {
        return repository.getBranch(branchName)
                .orElseThrow(() -> new IllegalStateException("The branch " + branchName + " does not exist"));
    }

    private int countChangeRequestDiffs(final Repository repository,
                                        final ChangeRequest changeRequest) {
        return getDiffEntries(repository,
                              changeRequest.getSourceBranch(),
                              changeRequest.getTargetBranch(),
                              changeRequest.getStartCommitId(),
                              changeRequest.getEndCommitId()).size();
    }

    private boolean isChangeRequestConflictFree(final Repository repository,
                                                final ChangeRequest changeRequest) {
        return getConflicts(repository,
                            changeRequest.getSourceBranch(),
                            changeRequest.getTargetBranch()).isEmpty();
    }

    private boolean tryMergeChangeRequest(final Repository repository,
                                          final ChangeRequest changeRequest,
                                          final CommitInfo commitInfo,
                                          final boolean squash) {
        final String sourceBranchName = changeRequest.getSourceBranch();
        final String targetBranchName = changeRequest.getTargetBranch();

        final JGitFileSystem fs = getFileSystemFromBranch(repository,
                                                          targetBranchName);

        boolean isDone = false;

        try {
            fs.lock();

            final List<String> mergeCommitIds = new ArrayList<>(fs.getGit().merge(sourceBranchName,
                                                                                  targetBranchName,
                                                                                  true,
                                                                                  squash,
                                                                                  commitInfo));

            if (mergeCommitIds.isEmpty()) {
                throw new NothingToMergeException();
            }

            ((GitHookSupport) fs.provider()).executePostCommitHook(fs);

            final RevCommit mergeCommit = getLastCommit(repository,
                                                        targetBranchName);
            final String mergeCommitId = mergeCommit.getName();

            final List<DiffEntry> changesToNotify = getDiffEntries(repository,
                                                                   changeRequest.getSourceBranch(),
                                                                   changeRequest.getTargetBranch(),
                                                                   changeRequest.getStartCommitId(),
                                                                   changeRequest.getEndCommitId());

            this.notifyFileChanges(fs,
                                   targetBranchName,
                                   changesToNotify,
                                   getFullCommitMessage(mergeCommit));

            this.updateChangeRequestStatus(repository.getSpace().getName(),
                                           repository.getAlias(),
                                           changeRequest,
                                           ChangeRequestStatus.ACCEPTED,
                                           mergeCommitId);

            isDone = true;
        } catch (GitException e) {
            logger.debug(String.format("Cannot merge change request %s: %s", changeRequest.getId(), e));
        } finally {
            fs.unlock();
        }

        return isDone;
    }

    private boolean tryRevertChangeRequest(final Repository repository,
                                           final ChangeRequest changeRequest) {
        boolean isDone = false;

        final String sourceBranchName = changeRequest.getSourceBranch();
        final String targetBranchName = changeRequest.getTargetBranch();

        final JGitFileSystem fs = getFileSystemFromBranch(repository,
                                                          targetBranchName);

        try {
            fs.lock();

            final String beforeRevertCommitId = getLastCommitId(repository,
                                                                targetBranchName);

            isDone = fs.getGit().revertMerge(sourceBranchName,
                                             targetBranchName,
                                             changeRequest.getStartCommitId(),
                                             changeRequest.getMergeCommitId());

            if (isDone) {
                ((GitHookSupport) fs.provider()).executePostCommitHook(fs);

                final RevCommit revertCommit = getLastCommit(repository,
                                                             targetBranchName);

                final List<DiffEntry> changesToNotify = getDiffEntries(repository,
                                                                       targetBranchName,
                                                                       targetBranchName,
                                                                       beforeRevertCommitId,
                                                                       revertCommit.getName());

                notifyFileChanges(fs,
                                  targetBranchName,
                                  changesToNotify,
                                  getFullCommitMessage(revertCommit));
            }
        } catch (GitException e) {
            logger.debug(String.format("Failed to revert change request #%s: %s.",
                                       changeRequest.getId(),
                                       e));
        } finally {
            fs.unlock();
        }

        this.updateNotMergedChangeRequestStatus(repository.getSpace().getName(),
                                                repository.getAlias(),
                                                changeRequest,
                                                isDone ? ChangeRequestStatus.REVERTED :
                                                        ChangeRequestStatus.REVERT_FAILED);

        return isDone;
    }

    private void checkChangeRequestAlreadyOpen(final String spaceName,
                                               final String repositoryAlias,
                                               final String sourceBranchName,
                                               final String targetBranchName) {
        final List<ChangeRequest> changeRequests =
                getFilteredChangeRequestsFromStorage(spaceName,
                                                     repositoryAlias,
                                                     false,
                                                     ChangeRequestPredicates
                                                             .matchSourceAndTargetAndStatus(sourceBranchName,
                                                                                            targetBranchName,
                                                                                            ChangeRequestStatus.OPEN));

        if (!changeRequests.isEmpty()) {
            throw new ChangeRequestAlreadyOpenException(changeRequests.get(0).getId());
        }
    }

    private void notifyFileChanges(final JGitFileSystem fs,
                                   final String targetBranchName,
                                   final List<DiffEntry> changesToNotify,
                                   final String message) {
        final String rootPath = "/";
        final String host = targetBranchName + "@" + fs.getName();

        final Function<String, Path> createPathFn = pathStr -> {
            final PathInfo pathInfo = fs.getGit().getPathInfo(targetBranchName,
                                                              pathStr);

            return !pathStr.equals(DiffEntry.DEV_NULL) ? createJGitPathImpl(fs,
                                                                            rootPath + pathInfo.getPath(),
                                                                            host,
                                                                            pathInfo.getObjectId(),
                                                                            false) : null;
        };

        final List watchEvents = changesToNotify
                .stream()
                .map(entry -> new JGitWatchEvent(sessionInfo.getId(),
                                                 sessionInfo.getIdentity().getIdentifier(),
                                                 message,
                                                 entry.getChangeType().toString(),
                                                 createPathFn.apply(entry.getOldPath()),
                                                 createPathFn.apply(entry.getNewPath())))
                .collect(Collectors.toList());

        if (!watchEvents.isEmpty()) {
            final Path root = JGitPathImpl.createRoot(fs,
                                                      rootPath,
                                                      host,
                                                      false);

            fs.publishEvents(root,
                             watchEvents);
        }
    }

    private RevCommit getLastCommit(final Repository repository,
                                    final String branchName) {
        final Git git = getGitFromBranch(repository,
                                         branchName);

        final RevCommit lastCommit = git.getLastCommit(branchName);

        if (lastCommit != null) {
            return lastCommit;
        }

        throw new IllegalStateException("The branch " + branchName + " does not have a last commit");
    }

    private String getLastCommitId(final Repository repository,
                                   final String branchName) {
        return getLastCommit(repository,
                             branchName).getName();
    }

    private String getCommonCommitId(final Repository repository,
                                     final String sourceBranchName,
                                     final String targetBranchName) {
        final Git git = getGitFromBranch(repository,
                                         sourceBranchName);

        try {
            return git.getCommonAncestorCommit(sourceBranchName,
                                               targetBranchName).getName();
        } catch (GitException e) {
            logger.error(String.format("Failed to get common commit for branches %s and %s: %s",
                                       sourceBranchName,
                                       targetBranchName,
                                       e));
        }

        throw new IllegalStateException(String.format("Branches %s and %s do not have a common ancestor commit",
                                                      sourceBranchName,
                                                      targetBranchName));
    }

    private List<TextualDiff> getTextualDiff(final Repository repository,
                                             final String sourceBranchName,
                                             final String targetBranchName,
                                             final String startCommitId,
                                             final String endCommitId) {
        final Optional<Branch> sourceBranch = repository.getBranch(sourceBranchName);
        final Optional<Branch> targetBranch = repository.getBranch(targetBranchName);

        if (sourceBranch.isPresent() && targetBranch.isPresent()) {
            final Git git = getGitFromBranch(repository,
                                             sourceBranchName);

            return git.textualDiffRefs(targetBranchName,
                                       sourceBranchName,
                                       startCommitId,
                                       endCommitId);
        }

        return Collections.emptyList();
    }

    private List<DiffEntry> getDiffEntries(final Repository repository,
                                           final String sourceBranchName,
                                           final String targetBranchName,
                                           final String startCommitId,
                                           final String endCommitId) {
        final Optional<Branch> sourceBranch = repository.getBranch(sourceBranchName);
        final Optional<Branch> targetBranch = repository.getBranch(targetBranchName);

        if (sourceBranch.isPresent() && targetBranch.isPresent()) {
            final Git git = getGitFromBranch(repository,
                                             sourceBranchName);

            return git.listDiffs(startCommitId,
                                 endCommitId != null ? endCommitId :
                                         getLastCommitId(repository,
                                                         sourceBranchName));
        }

        return Collections.emptyList();
    }

    private List<String> getConflicts(final Repository repository,
                                      final String sourceBranchName,
                                      final String targetBranchName) {
        final Optional<Branch> sourceBranch = repository.getBranch(sourceBranchName);
        final Optional<Branch> targetBranch = repository.getBranch(targetBranchName);

        if (sourceBranch.isPresent() && targetBranch.isPresent()) {
            final Git git = getGitFromBranch(repository,
                                             sourceBranchName);

            return git.conflictBranchesChecker(targetBranchName,
                                               sourceBranchName);
        }

        return Collections.emptyList();
    }

    private Git getGitFromBranch(final Repository repository,
                                 final String branchName) {
        return getFileSystemFromBranch(repository,
                                       branchName).getGit();
    }

    private CommitInfo buildCommitInfo(final String message) {
        return new CommitInfo(sessionInfo.getId(),
                              sessionInfo.getIdentity().getIdentifier(),
                              null,
                              message,
                              null,
                              null);
    }

    String getFullCommitMessage(final RevCommit commit) {
        return commit.getFullMessage();
    }

    JGitFileSystem getFileSystemFromBranch(final Repository repository,
                                           final String branchName) {
        final Branch branch = resolveBranch(repository,
                                            branchName);

        return ((JGitPathImpl) Paths.convert(branch.getPath())).getFileSystem();
    }

    JGitPathImpl createJGitPathImpl(final JGitFileSystem fs,
                                    final String path,
                                    final String host,
                                    final ObjectId objectId,
                                    final boolean isRealPath) {
        return JGitPathImpl.create(fs,
                                   path,
                                   host,
                                   objectId,
                                   isRealPath);
    }
}
