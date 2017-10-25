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

/**
 * Represents a Pull Request information to be tracked as metadata.
 */
public interface PullRequest {

    /**
     * Returns the generated pull request id. Is unique for each repository,
     * so you can have the same id in two different repositories
     * @return the pull request id.
     */
    long getId();

    /**
     * Returns the target repository
     * @return the name of target repository
     */
    String getTargetRepository();

    /**
     * Returns the target branch.
     * @return the name of the target branch
     */
    String getTargetBranch();

    /**
     * Returns the source repository.
     * @return the name of the source repository
     */
    String getSourceRepository();

    /**
     * Returns the source branch.
     * @return the name of the source branch.
     */
    String getSourceBranch();

    /**
     * Returns pull request status. Check {@link PullRequestStatus} to see the different values it can take.
     * @return the pull request status
     */
    PullRequestStatus getStatus();
}
