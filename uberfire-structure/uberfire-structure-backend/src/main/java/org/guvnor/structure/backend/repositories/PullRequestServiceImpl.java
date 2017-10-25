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

package org.guvnor.structure.backend.repositories;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.GitMetadata;
import org.guvnor.structure.repositories.GitMetadataStore;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestAlreadyExistsException;
import org.guvnor.structure.repositories.PullRequestService;
import org.guvnor.structure.repositories.PullRequestStatus;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryNotFoundException;
import org.guvnor.structure.repositories.impl.GitMetadataImpl;
import org.guvnor.structure.repositories.impl.PullRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.FileDiff;
import org.uberfire.java.nio.base.options.MergeCopyOption;
import org.uberfire.java.nio.file.Path;

import static java.lang.Integer.min;
import static java.util.stream.Collectors.toMap;
import static org.uberfire.backend.server.util.Paths.convert;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class PullRequestServiceImpl implements PullRequestService {

    private final GitMetadataStore metadataStore;
    private final IOService ioService;
    private final ConfiguredRepositories configuredRepositories;

    private Logger logger = LoggerFactory.getLogger(PullRequestServiceImpl.class);

    @Inject
    public PullRequestServiceImpl(GitMetadataStore metadataStore,
                                  @Named("ioStrategy") IOService ioService,
                                  ConfiguredRepositories configuredRepositories) {
        this.metadataStore = metadataStore;
        this.ioService = ioService;
        this.configuredRepositories = configuredRepositories;
    }

    @Override
    public PullRequest createPullRequest(String sourceRepository,
                                         String sourceBranch,
                                         String targetRepository,
                                         String targetBranch) {

        checkNotEmpty("sourceRepository",
                      sourceRepository);
        checkNotEmpty("sourceBranch",
                      sourceBranch);
        checkNotEmpty("targetRepository",
                      targetRepository);
        checkNotEmpty("targetBranch",
                      targetBranch);

        final Optional<GitMetadata> targetRepositoryMetadata = this.metadataStore.read(targetRepository);

        PullRequestImpl storablePullRequest;
        if (targetRepositoryMetadata.isPresent()) {

            GitMetadata metadata = targetRepositoryMetadata.get();

            long generatedId = this.generatePullRequestId(metadata);

            List<PullRequest> pullRequests = metadata.getPullRequests();

            if (pullRequests == null) {
                pullRequests = new ArrayList<>();
            }

            storablePullRequest = new PullRequestImpl(generatedId,
                                                      sourceRepository,
                                                      sourceBranch,
                                                      targetRepository,
                                                      targetBranch,
                                                      PullRequestStatus.OPEN);

            if (metadata.exists(storablePullRequest)) {
                throw new PullRequestAlreadyExistsException(storablePullRequest);
            }

            pullRequests.add(storablePullRequest);

            GitMetadata newMetadata = new GitMetadataImpl(metadata.getName(),
                                                          metadata.getOrigin(),
                                                          metadata.getForks(),
                                                          pullRequests);

            this.metadataStore.write(metadata.getName(),
                                     newMetadata);

            if (logger.isDebugEnabled()) {
                logger.debug("Pull request PR-{} created. Target repository: {} / {}",
                             storablePullRequest.getId(),
                             storablePullRequest.getTargetRepository(),
                             storablePullRequest.getTargetBranch());
            }
        } else {
            throw new RepositoryNotFoundException(
                    String.format("The repository or branch does not exists %s/%s",
                                  targetRepository,
                                  targetBranch));
        }

        return storablePullRequest;
    }

    @Override
    public PullRequest acceptPullRequest(final PullRequest pullRequest) {
        checkNotNull("pullRequest",
                     pullRequest);
        checkNotNull("id",
                     pullRequest.getId());
        checkNotEmpty("targetRepository",
                      pullRequest.getTargetRepository());

        String repository = pullRequest.getTargetRepository();
        long id = pullRequest.getId();
        final GitMetadata metadata = this.getRepositoryMetadata(repository);
        final PullRequest acceptPullRequest = metadata.getPullRequest(id);
        this.createHiddenBranch(acceptPullRequest);
        this.mergePullRequest(acceptPullRequest);
        this.changePullRequestStatus(repository,
                                     id,
                                     PullRequestStatus.MERGED);
        return this.getRepositoryMetadata(repository).getPullRequest(id);
    }

    @Override
    public PullRequest rejectPullRequest(final PullRequest pullRequest) {
        checkNotNull("pullRequest",
                     pullRequest);
        checkNotNull("id",
                     pullRequest.getId());
        checkNotEmpty("targetRepository",
                      pullRequest.getTargetRepository());

        String repository = pullRequest.getTargetRepository();
        long id = pullRequest.getId();

        this.changePullRequestStatus(repository,
                                     id,
                                     PullRequestStatus.REJECTED);
        return this.getRepositoryMetadata(repository).getPullRequest(id);
    }

    @Override
    public PullRequest closePullRequest(final PullRequest pullRequest) {
        checkNotNull("pullRequest",
                     pullRequest);
        checkNotNull("id",
                     pullRequest.getId());
        checkNotEmpty("targetRepository",
                      pullRequest.getTargetRepository());

        String repository = pullRequest.getTargetRepository();
        long id = pullRequest.getId();

        this.changePullRequestStatus(repository,
                                     id,
                                     PullRequestStatus.CLOSED);
        return this.getRepositoryMetadata(repository).getPullRequest(id);
    }

    public List<PullRequest> getPullRequestsByBranch(Integer page,
                                                     Integer pageSize,
                                                     final String repository,
                                                     final String branch) {
        GitMetadata metadata = getRepositoryMetadata(repository);
        List<PullRequest> pullRequests = metadata.getPullRequests(elem -> elem.getTargetBranch().equals(branch));
        return this.paginate(page,
                             pageSize,
                             pullRequests);
    }

    @Override
    public List<PullRequest> getPullRequestsByRepository(Integer page,
                                                         Integer pageSize,
                                                         final String repository) {

        GitMetadata metadata = getRepositoryMetadata(repository);
        List<PullRequest> pullRequests = metadata.getPullRequests();
        return this.paginate(page,
                             pageSize,
                             pullRequests);
    }

    public List<PullRequest> getPullRequestsByStatus(Integer page,
                                                     Integer pageSize,
                                                     final String repository,
                                                     final PullRequestStatus status) {

        final List<PullRequest> pullRequests = this.getPullRequestsByRepository(page,
                                                                                pageSize,
                                                                                repository);
        final List<PullRequest> finalPullRequests = pullRequests.stream().filter(elem -> elem.getStatus().equals(status)).collect(Collectors.toList());
        return this.paginate(page,
                             pageSize,
                             finalPullRequests);
    }

    @Override
    public void deletePullRequest(final PullRequest pullRequest) {

        checkNotNull("pullRequest",
                     pullRequest);
        checkNotNull("id",
                     pullRequest.getId());
        checkNotEmpty("targetRepository",
                      pullRequest.getTargetRepository());

        String repository = pullRequest.getTargetRepository();
        long id = pullRequest.getId();

        GitMetadata metadata = getRepositoryMetadata(repository);
        PullRequest removablePullRequest = metadata.getPullRequest(id);

        List<PullRequest> finalPullRequests = metadata.getPullRequests();
        finalPullRequests.remove(removablePullRequest);

        GitMetadata storableMetadata = new GitMetadataImpl(metadata.getName(),
                                                           metadata.getOrigin(),
                                                           metadata.getForks(),
                                                           finalPullRequests);
        this.metadataStore.write(storableMetadata.getName(),
                                 storableMetadata);

        this.deleteHiddenBranch(removablePullRequest);
    }

    protected List<PullRequest> paginate(Integer page,
                                         Integer pageSize,
                                         List<PullRequest> pullRequests) {

        if (page == 0 && pageSize == 0) {
            return pullRequests;
        }

        Integer finalPageSize = pageSize == 0 ? 10 : pageSize;

        final Map<Integer, List<PullRequest>> map = IntStream.iterate(0,
                                                                      i -> i + finalPageSize)
                .limit((pullRequests.size() + finalPageSize - 1) / finalPageSize)
                .boxed()
                .collect(toMap(i -> i / finalPageSize,
                               i -> pullRequests.subList(i,
                                                         min(i + finalPageSize,
                                                             pullRequests.size()))));

        return map.getOrDefault(page,
                                new ArrayList<>());
    }

    @Override
    public List<FileDiff> diff(final PullRequest pullRequest) {

        final Repository repository = configuredRepositories.getRepositoryByRepositoryAlias(pullRequest.getTargetRepository());
        this.createHiddenBranch(pullRequest);
        String diff = String.format("diff:%s,%s",
                                    pullRequest.getTargetBranch(),
                                    this.buildHiddenBranchName(pullRequest));
        final List<FileDiff> diffs = (List<FileDiff>) this.ioService.readAttributes(convert(repository.getRoot()),
                                                                                    diff);
        this.deleteHiddenBranch(pullRequest);
        return diffs;
    }

    protected void changePullRequestStatus(final String repository,
                                           final long id,
                                           final PullRequestStatus status) {

        checkNotEmpty("repository",
                      repository);
        checkNotNull("status",
                     status);

        GitMetadata metadata = getRepositoryMetadata(repository);
        PullRequest pullRequest = metadata.getPullRequest(id);
        PullRequestImpl finalPullRequest = new PullRequestImpl(pullRequest.getId(),
                                                               pullRequest.getSourceRepository(),
                                                               pullRequest.getSourceBranch(),
                                                               pullRequest.getTargetRepository(),
                                                               pullRequest.getTargetBranch(),
                                                               status);

        List<PullRequest> finalPullRequests = metadata.getPullRequests(elem -> elem.getId() != id);
        finalPullRequests.add(finalPullRequest);

        GitMetadata storableMetadata = new GitMetadataImpl(metadata.getName(),
                                                           metadata.getOrigin(),
                                                           metadata.getForks(),
                                                           finalPullRequests);
        this.metadataStore.write(storableMetadata.getName(),
                                 storableMetadata);
    }

    private GitMetadata getRepositoryMetadata(final String repository) {
        final Optional<GitMetadata> optional = this.metadataStore.read(repository);

        if (!optional.isPresent()) {
            throw new RepositoryNotFoundException(
                    String.format("The repository does not exists <<%s>>",
                                  repository));
        }

        return optional.get();
    }

    private synchronized long generatePullRequestId(final GitMetadata metadata) {

        List<PullRequest> pullRequests = metadata.getPullRequests();
        final Optional<PullRequest> last = pullRequests.stream().max((first, second) -> Long.compare(first.getId(),
                                                                                                     second.getId()));
        return last.map(pr -> pr.getId() + 1).orElse(1l);
    }

    protected void createHiddenBranch(final PullRequest pullRequest) {
        final Path source = this.buildPath(pullRequest.getSourceRepository(),
                                           pullRequest.getSourceBranch());
        final Path target = this.buildHiddenPath(pullRequest);
        ioService.copy(source,
                       target);
        if (logger.isDebugEnabled()) {
            logger.debug("Hidden branch {} created.",
                         target.toString());
        }
    }

    private void mergePullRequest(final PullRequest pullRequest) {
        final Path source = this.buildHiddenPath(pullRequest);
        final Path target = this.buildPath(pullRequest.getTargetRepository(),
                                           pullRequest.getTargetBranch());
        ioService.copy(source,
                       target,
                       new MergeCopyOption());
        if (logger.isDebugEnabled()) {
            logger.debug("Merged from <{}> to <{}>",
                         source.toString(),
                         target.toString());
        }
    }

    private void deleteHiddenBranch(final PullRequest pullRequest) {

        final Path path = this.buildHiddenPath(pullRequest);
        ioService.delete(path);
        if (logger.isDebugEnabled()) {
            logger.debug("Hidden branch {} deleted",
                         pullRequest.toString());
        }
    }

    protected Path buildHiddenPath(PullRequest pullRequest) {
        String branchName = buildHiddenBranchName(pullRequest);
        return this.buildPath(pullRequest.getTargetRepository(),
                              branchName);
    }

    private String buildHiddenBranchName(PullRequest pullRequest) {
        return String.format("PR-%s-%s/%s-%s",
                             pullRequest.getId(),
                             pullRequest.getSourceRepository(),
                             pullRequest.getSourceBranch(),
                             pullRequest.getTargetBranch());
    }

    protected Path buildPath(final String repository,
                             final String branch) {

        return ioService.get(URI.create(String.format("git://%s@%s",
                                                      branch,
                                                      repository)));
    }
}
