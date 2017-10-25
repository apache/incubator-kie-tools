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

package org.guvnor.structure.client.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class RepositoryController {

    public static final String REPOSITORY_TYPE = "repository";
    public static final String REPOSITORY_CREATE = "create";
    public static final String REPOSITORY_READ = "read";
    public static final String REPOSITORY_UPDATE = "update";
    public static final String REPOSITORY_DELETE = "delete";

    private AuthorizationManager authorizationManager;
    private User user;

    @Inject
    public RepositoryController(AuthorizationManager authorizationManager,
                                User user) {
        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    public boolean canCreateRepositories() {
        return authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                              RepositoryAction.CREATE,
                                              user);
    }

    public boolean canReadRepositories() {
        return authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                              RepositoryAction.READ,
                                              user);
    }

    public boolean canReadRepository(Repository repository) {
        return authorizationManager.authorize(repository,
                                              RepositoryAction.READ,
                                              user);
    }

    public boolean canUpdateRepository(String repoId) {
        return authorizationManager.authorize(new ResourceRef(repoId,
                                                              Repository.RESOURCE_TYPE),
                                              RepositoryAction.UPDATE,
                                              user);
    }

    public boolean canUpdateRepository(Repository repository) {
        return authorizationManager.authorize(repository,
                                              RepositoryAction.UPDATE,
                                              user);
    }

    public boolean canDeleteRepository(Repository repository) {
        return authorizationManager.authorize(repository,
                                              RepositoryAction.DELETE,
                                              user);
    }
}