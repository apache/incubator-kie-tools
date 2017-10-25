/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.repositories;

import java.util.List;

import org.uberfire.java.nio.base.FileDiff;

/**
 * Service that contains the basic mechanism to administrate pull requests.
 * Every pull request depends on its repository (target repository).
 * The pull request id is unique in every repository, but it can be repeated
 * across them.
 */
public interface PullRequestService {

    /**
     * Creates a pull request and stores it into the tracking system.
     * @param sourceRepository the origin repository
     * @param sourceBranch the branch on the origin repository you want to get pulled
     * @param targetRepository the upstream repository
     * @param targetBranch the branch where you want impact your changes
     * @return The object that represents the pull request.
     */
    PullRequest createPullRequest(String sourceRepository,
                                  String sourceBranch,
                                  String targetRepository,
                                  String targetBranch);

    /**
     * Accepts the provided pull request and merges branches into target.
     * @param pullRequest the pull request you want to accept
     * @return the final state of the pull request as MERGED
     */
    PullRequest acceptPullRequest(PullRequest pullRequest);

    /**
     * Rejects the provided pull request and changes its status to REJECTED
     * @param pullRequest the pull request you want to reject.
     * @return the final state of the pull request as REJECTED.
     */
    PullRequest rejectPullRequest(PullRequest pullRequest);

    /**
     * Closes the provided pull request and changes its status to CLOSED
     * @param pullRequest the pull request you want to close.
     * @return the final state of the pull request as CLOSED.
     */
    PullRequest closePullRequest(PullRequest pullRequest);

    /**
     * Deletes the pull request. This method removes the pull request
     * from tracking repository. The pull request cannot be recovered.
     * @param pullRequest the pull request you want to delete.
     */
    void deletePullRequest(PullRequest pullRequest);

    /**
     * Retrieves pull requests filtered by repository and branch
     * @param page the page you want to get
     * @param pageSize the number of pull requests per page
     * @param repository the repository used as filter
     * @param branch the branch used as filter
     * @return the page of pull requests filtered by repository
     */
    List<PullRequest> getPullRequestsByBranch(Integer page,
                                              Integer pageSize,
                                              String repository,
                                              String branch);

    /**
     * Retrieves pull requests filtered by repository
     * @param page the page you want to get
     * @param pageSize the number of pull requests per page
     * @param repository the repository used as filter
     * @return the page of pull requests filtered by repository
     */
    List<PullRequest> getPullRequestsByRepository(Integer page,
                                                  Integer pageSize,
                                                  String repository);

    /**
     * Retrieves pull requests filtered by repository and branch
     * @param page the page you want to get
     * @param pageSize the number of pull requests per page
     * @param repository the repository used as filter
     * @param status the status used as filter
     * @return the page of pull requests filtered by repository
     */
    List<PullRequest> getPullRequestsByStatus(Integer page,
                                              Integer pageSize,
                                              String repository,
                                              PullRequestStatus status);

    /**
     * Obtains differences between all files involved in the pull request.
     * @param pullRequest the pull request to diff.
     * @return The list of segment differences from files.
     */
    List<FileDiff> diff(PullRequest pullRequest);
}
