/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.Predicate;

/**
 * Represents information about a repository. It contains the origin name
 * the forks it has, the repository name,
 * and the list of pull request that repository has.
 */
public interface GitMetadata {

    /**
     * Returns repository name
     * @return the repository name
     */
    String getName();

    /**
     * Returns the list of forks names it has
     * @return the list of forks names
     */
    List<String> getForks();

    /**
     * The name of the its origin repository
     * @return
     */
    String getOrigin();

    /**
     * The complete list of pull requests. It does not filter by status.
     * @return the list of pull requests.
     */
    List<PullRequest> getPullRequests();

    /**
     * A filtered pull request list.
     * @param filter the filter expression
     * @return the filtered pull request list.
     */
    List<PullRequest> getPullRequests(Predicate<? super PullRequest> filter);

    /**
     * Return a single pull request by id
     * @param id the pull request id
     * @return the pull request found
     */
    PullRequest getPullRequest(long id);

    /**
     * Return if pull request already exists comparing
     * branches name, repositories name and statuses
     * but does not check pull request ID.
     * @param pullRequest the pull request to check
     * @return if pull request is present in metadata, returns true.
     */
    boolean exists(PullRequest pullRequest);
}
