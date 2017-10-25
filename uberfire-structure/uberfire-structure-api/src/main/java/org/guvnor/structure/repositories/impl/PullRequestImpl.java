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

package org.guvnor.structure.repositories.impl;

import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestStatus;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class PullRequestImpl implements PullRequest {

    private long id;
    private String targetBranch;
    private String targetRepository;
    private String sourceBranch;
    private String sourceRepository;
    private PullRequestStatus status;

    public PullRequestImpl() {

    }

    public PullRequestImpl(final String sourceRepository,
                           final String sourceBranch,
                           final String targetRepository,
                           final String targetBranch) {
        this(0l,
             sourceRepository,
             sourceBranch,
             targetRepository,
             targetBranch,
             PullRequestStatus.OPEN);
    }

    public PullRequestImpl(@MapsTo("id") final long id,
                           @MapsTo("sourceRepository") final String sourceRepository,
                           @MapsTo("sourceBranch") final String sourceBranch,
                           @MapsTo("targetRepository") final String targetRepository,
                           @MapsTo("targetBranch") final String targetBranch,
                           @MapsTo("status") final PullRequestStatus status) {

        this.id = id;
        this.sourceRepository = checkNotEmpty("sourceRepository",
                                              sourceRepository);
        this.sourceBranch = checkNotEmpty("sourceBranch",
                                          sourceBranch);
        this.targetRepository = checkNotEmpty("targetRepository",
                                              targetRepository);
        this.targetBranch = checkNotEmpty("targetBranch",
                                          targetBranch);
        this.status = checkNotNull("status",
                                   status);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getTargetRepository() {
        return this.targetRepository;
    }

    @Override
    public String getTargetBranch() {
        return this.targetBranch;
    }

    @Override
    public String getSourceRepository() {
        return this.sourceRepository;
    }

    @Override
    public String getSourceBranch() {
        return this.sourceBranch;
    }

    @Override
    public PullRequestStatus getStatus() {
        return this.status;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof PullRequest) {
            return ((PullRequest) obj).getId() == this.getId();
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = ~~result;
        result = 31 * result + (targetRepository.hashCode());
        result = ~~result;
        result = 31 * result + (targetBranch.hashCode());
        result = ~~result;
        result = 31 * result + (sourceRepository.hashCode());
        result = ~~result;
        result = 31 * result + (sourceBranch.hashCode());
        result = ~~result;
        result = 31 * result + (status.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(" <" + this.getId() + "," + this.getStatus() + "> ");
        builder.append(" { ");
        builder.append(" source:( " + this.getSourceRepository() + "," + this.getSourceBranch() + " ) ");
        builder.append(" -> ");
        builder.append(" target:( " + this.getTargetRepository() + "," + this.getTargetBranch() + " ) ");
        builder.append(" } ");
        return builder.toString();
    }
}
