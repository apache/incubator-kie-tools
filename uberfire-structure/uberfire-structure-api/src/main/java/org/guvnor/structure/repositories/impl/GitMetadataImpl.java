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

package org.guvnor.structure.repositories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.guvnor.structure.repositories.GitMetadata;
import org.guvnor.structure.repositories.PullRequest;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Represents information about a repository. It contains the origin name
 * the forks it has, the repository name,
 * and the list of pull request that repository has.
 */
@Portable
public class GitMetadataImpl implements GitMetadata {

    private String origin;
    private List<String> forks;
    private String name;
    private List<PullRequest> pullRequests;

    public GitMetadataImpl(String name) {
        this(name,
             "",
             new ArrayList<>(),
             new ArrayList<>());
    }

    public GitMetadataImpl(String name,
                           String origin) {
        this(name,
             origin,
             new ArrayList<>(),
             new ArrayList<>());
    }

    public GitMetadataImpl(String name,
                           List<String> forks) {
        this(name,
             "",
             forks,
             new ArrayList<>());
    }

    public GitMetadataImpl(String name,
                           String origin,
                           List<String> forks) {
        this(name,
             origin,
             forks,
             new ArrayList<>());
    }

    public GitMetadataImpl(@MapsTo("name") String name,
                           @MapsTo("origin") String origin,
                           @MapsTo("forks") List<String> forks,
                           @MapsTo("pullRequests") List<PullRequest> pullRequests) {
        this.name = checkNotEmpty("name",
                                  name);
        this.origin = checkNotNull("origin",
                                   origin);
        this.forks = checkNotNull("forks",
                                  forks);
        this.pullRequests = checkNotNull("pullRequests",
                                         pullRequests);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getForks() {
        return new ArrayList<>(forks);
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    @Override
    public List<PullRequest> getPullRequests() {
        return new ArrayList<>(this.pullRequests);
    }

    @Override
    public List<PullRequest> getPullRequests(final Predicate<? super PullRequest> filter) {
        final List<PullRequest> prs = this.getPullRequests();
        return prs.stream().filter(filter).collect(Collectors.toList());
    }

    @Override
    public PullRequest getPullRequest(long id) {
        final List<PullRequest> prs = this.getPullRequests(elem -> elem.getId() == id);
        if (prs.size() == 0) {
            throw new NoSuchElementException("The Pull Request with ID #" + id + " not found");
        }
        final PullRequest pr = prs.get(0);

        return new PullRequestImpl(pr.getId(),
                                   pr.getSourceRepository(),
                                   pr.getSourceBranch(),
                                   pr.getTargetRepository(),
                                   pr.getTargetBranch(),
                                   pr.getStatus());
    }

    @Override
    public boolean exists(final PullRequest pullRequest) {
        return this.getPullRequests().stream().anyMatch(
                pr -> {
                    return pr.getSourceBranch().equals(pullRequest.getSourceBranch())
                            && pr.getSourceRepository().equals(pullRequest.getSourceRepository())
                            && pr.getTargetBranch().equals(pullRequest.getTargetBranch())
                            && pr.getTargetRepository().equals(pullRequest.getTargetRepository())
                            && pr.getStatus().equals(pullRequest.getStatus());
                });
    }
}
