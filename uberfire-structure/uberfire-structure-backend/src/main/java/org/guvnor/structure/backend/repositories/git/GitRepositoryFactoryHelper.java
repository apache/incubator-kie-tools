/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.repositories.git;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.backend.repositories.BranchAccessAuthorizer;
import org.guvnor.structure.backend.repositories.git.hooks.PostCommitNotificationService;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryExternalUpdateEvent;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.repositories.RepositoryFactoryHelper;
import org.uberfire.io.IOService;
import org.uberfire.spaces.SpacesAPI;

import static org.guvnor.structure.repositories.impl.git.GitRepository.SCHEME;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.Preconditions.checkNotNull;

@ApplicationScoped
public class GitRepositoryFactoryHelper implements RepositoryFactoryHelper {

    private IOService indexedIOService;

    private IOService notIndexedIOService;

    private SpacesAPI spacesAPI;

    private Event<RepositoryExternalUpdateEvent> repositoryExternalUpdate;

    private PostCommitNotificationService postCommitNotificationService;

    private BranchAccessAuthorizer branchAccessAuthorizer;

    private PasswordService secureService;

    public GitRepositoryFactoryHelper() {
    }

    @Inject
    public GitRepositoryFactoryHelper(@Named("ioStrategy") IOService indexedIOService,
                                      @Named("configIO") IOService notIndexedIOService,
                                      SpacesAPI spacesAPI,
                                      Event<RepositoryExternalUpdateEvent> repositoryExternalUpdate,
                                      PostCommitNotificationService postCommitNotificationService,
                                      BranchAccessAuthorizer branchAccessAuthorizer,
                                      PasswordService secureService) {
        this.indexedIOService = indexedIOService;
        this.notIndexedIOService = notIndexedIOService;
        this.spacesAPI = spacesAPI;
        this.repositoryExternalUpdate = repositoryExternalUpdate;
        this.postCommitNotificationService = postCommitNotificationService;
        this.branchAccessAuthorizer = branchAccessAuthorizer;
        this.secureService = secureService;
    }

    @Override
    public boolean accept(RepositoryInfo repositoryInfo) {
        checkNotNull("repositoryInfo",
                     repositoryInfo);
        final String schemeConfigItem = repositoryInfo.getScheme();
        checkNotEmpty("schemeConfigItem",
                      schemeConfigItem);
        return SCHEME.toString().equals(schemeConfigItem);
    }

    @Override
    public Repository newRepository(RepositoryInfo repositoryInfo) {
        validate(repositoryInfo);

        boolean avoidIndex = repositoryInfo.isAvoidIndex();

        if (avoidIndex) {
            return new GitRepositoryBuilder(notIndexedIOService,
                                            secureService,
                                            spacesAPI,
                                            repositoryExternalUpdate,
                                            postCommitNotificationService,
                                            branchAccessAuthorizer).build(repositoryInfo);
        }

        return new GitRepositoryBuilder(indexedIOService,
                                        secureService,
                                        spacesAPI,
                                        repositoryExternalUpdate,
                                        postCommitNotificationService,
                                        branchAccessAuthorizer).build(repositoryInfo);
    }

    private void validate(RepositoryInfo repositoryInfo) {
        checkNotNull("repositoryInfo",
                     repositoryInfo);
        final String schemeConfigItem = repositoryInfo.getScheme();
        checkNotEmpty("schemeConfigItem",
                      schemeConfigItem);
    }
}
