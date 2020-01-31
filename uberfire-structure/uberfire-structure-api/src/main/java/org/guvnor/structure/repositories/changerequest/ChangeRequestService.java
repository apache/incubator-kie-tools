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

package org.guvnor.structure.repositories.changerequest;

import java.util.List;

import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestCommit;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestCountSummary;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestCommentList;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestList;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.rpc.SessionInfo;
import org.jboss.errai.security.shared.api.identity.User;

/**
 * Service that contains the basic mechanism to administrate change requests.
 * Every change request depends on its repository.
 * The change request id is unique in every repository, but it can be repeated
 * across them.
 */
@Remote
public interface ChangeRequestService {

    /**
     * Creates a change request and stores it into the tracking system.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @param sourceBranch    the branch where you want to get pulled
     * @param targetBranch    the branch where you want impact your changes
     * @param summary         the short summary of the change request
     * @param description     the description of the change request
     * @return The object that represents the change request.
     */
    ChangeRequest createChangeRequest(final String spaceName,
                                      final String repositoryAlias,
                                      final String sourceBranch,
                                      final String targetBranch,
                                      final String summary,
                                      final String description);

    /**
     * Retrieves the list of change requests that the user is able to visualize.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @return The list of change requests.
     */
    List<ChangeRequest> getChangeRequests(final String spaceName,
                                          final String repositoryAlias);

    /**
     * Retrieves the list of change requests that the user is able to visualize.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @param filter          a string to filter the results
     * @return The list of change requests.
     */
    List<ChangeRequest> getChangeRequests(final String spaceName,
                                          final String repositoryAlias,
                                          final String filter);

    /**
     * Retrieves the list of change requests that the user is able to visualize.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @param statusList      change request status to filter the results
     * @return The list of change requests.
     */
    List<ChangeRequest> getChangeRequests(final String spaceName,
                                          final String repositoryAlias,
                                          final List<ChangeRequestStatus> statusList);

    /**
     * Retrieves the list of change requests that the user is able to visualize.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @param statusList      change request status to filter the results
     * @param filter          a string to filter the results
     * @return The list of change requests.
     */
    List<ChangeRequest> getChangeRequests(final String spaceName,
                                          final String repositoryAlias,
                                          final List<ChangeRequestStatus> statusList,
                                          final String filter);

    /**
     * Retrieves the list of change requests that the user is able to visualize.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @param page            the desired page
     * @param pageSize        the size of the page
     * @param filter          a string to filter the results
     * @return The list of change requests.
     */
    PaginatedChangeRequestList getChangeRequests(final String spaceName,
                                                 final String repositoryAlias,
                                                 final Integer page,
                                                 final Integer pageSize,
                                                 final String filter);

    /**
     * Retrieves the list of change requests that the user is able to visualize.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @param page            the desired page
     * @param pageSize        the size of the page
     * @param statusList      change request status to filter the results
     * @param filter          a string to filter the results
     * @return The list of change requests.
     */
    PaginatedChangeRequestList getChangeRequests(final String spaceName,
                                                 final String repositoryAlias,
                                                 final Integer page,
                                                 final Integer pageSize,
                                                 final List<ChangeRequestStatus> statusList,
                                                 final String filter);

    /**
     * Retrieves the change request with the given id.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @return The change request.
     */
    ChangeRequest getChangeRequest(final String spaceName,
                                   final String repositoryAlias,
                                   final Long changeRequestId);

    /**
     * Retrieves the number of change requests that the user is able to visualize.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository alias
     * @return The number of change requests.
     */
    ChangeRequestCountSummary countChangeRequests(final String spaceName,
                                                  final String repositoryAlias);

    /**
     * Obtains differences between branches.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the origin repository
     * @param sourceBranch    the source branch
     * @param targetBranch    the target branch
     * @return The list of differences between files.
     */
    List<ChangeRequestDiff> getDiff(final String spaceName,
                                    final String repositoryAlias,
                                    final String sourceBranch,
                                    final String targetBranch);

    /**
     * Obtains differences between branches involved in the given change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the origin repository
     * @param changeRequestId the id of the change request
     * @return The list of differences between files.
     */
    List<ChangeRequestDiff> getDiff(final String spaceName,
                                    final String repositoryAlias,
                                    final Long changeRequestId);

    /**
     * Deletes all change requests associated with the given branch.
     *
     * @param spaceName            the space containing the origin repository
     * @param repositoryAlias      the origin repository
     * @param associatedBranchName branch name
     */
    void deleteChangeRequests(final String spaceName,
                              final String repositoryAlias,
                              final String associatedBranchName);

    /**
     * Deletes all change requests associated with the given branch.
     *
     * @param spaceName            the space containing the origin repository
     * @param repositoryAlias      the origin repository
     * @param associatedBranchName branch name
     * @param userIdentififer      user identifier
     */
    void deleteChangeRequests(final String spaceName,
                              final String repositoryAlias,
                              final String associatedBranchName,
                              final String userIdentifier);

    /**
     * Rejects the change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     */
    void rejectChangeRequest(final String spaceName,
                             final String repositoryAlias,
                             final Long changeRequestId);

    /**
     * Merges the change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @return True if the merge operation succeeded, otherwise false.
     */
    Boolean mergeChangeRequest(final String spaceName,
                               final String repositoryAlias,
                               final Long changeRequestId);

    /**
     * Reverts the change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @return True if the revert operation succeeded, otherwise false.
     */
    Boolean revertChangeRequest(final String spaceName,
                                final String repositoryAlias,
                                final Long changeRequestId);

    /**
     * Closes the change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     */
    void closeChangeRequest(final String spaceName,
                            final String repositoryAlias,
                            final Long changeRequestId);

    /**
     * Reopens the change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     */
    void reopenChangeRequest(final String spaceName,
                             final String repositoryAlias,
                             final Long changeRequestId);

    /**
     * Updates the change request summary.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @param updatedSummary  updated summary of the change request
     */
    void updateChangeRequestSummary(final String spaceName,
                                    final String repositoryAlias,
                                    final Long changeRequestId,
                                    final String updatedSummary);

    /**
     * Updates the change request description.
     *
     * @param spaceName          the space containing the origin repository
     * @param repositoryAlias    the repository used as a filter
     * @param changeRequestId    the id of the change request
     * @param updatedDescription updated description of the change request
     */
    void updateChangeRequestDescription(final String spaceName,
                                        final String repositoryAlias,
                                        final Long changeRequestId,
                                        final String updatedDescription);

    /**
     * Obtains all the comments associated with a change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @param page            the desired page
     * @param pageSize        the size of the page
     * @return The list of comments
     */
    PaginatedChangeRequestCommentList getComments(final String spaceName,
                                                  final String repositoryAlias,
                                                  final Long changeRequestId,
                                                  final Integer page,
                                                  final Integer pageSize);

    /**
     * Adds a comment to the change request comment list.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @param text            the comment text
     */
    void addComment(final String spaceName,
                    final String repositoryAlias,
                    final Long changeRequestId,
                    final String text);

    /**
     * Deletes a comment from the change request comment list.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @param commentId       the id of the comment
     */
    void deleteComment(final String spaceName,
                       final String repositoryAlias,
                       final Long changeRequestId,
                       final Long commentId);

    /**
     * Get commits from the change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @return The list of commits
     */
    List<ChangeRequestCommit> getCommits(final String spaceName,
                                         final String repositoryAlias,
                                         final Long changeRequestId);

    /**
     * Squash change request.
     *
     * @param spaceName       the space containing the origin repository
     * @param repositoryAlias the repository used as a filter
     * @param changeRequestId the id of the change request
     * @param commitMessage   the comment of squash commit
     * @return True if the squash operation succeeded, otherwise false.
     */
    Boolean squashChangeRequest(final String spaceName,
                                final String repositoryAlias,
                                final Long changeRequestId,
                                final String commitMessage);
}
