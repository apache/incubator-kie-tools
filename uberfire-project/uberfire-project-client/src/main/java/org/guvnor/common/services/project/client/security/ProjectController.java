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

package org.guvnor.common.services.project.client.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class ProjectController {

    private AuthorizationManager authorizationManager;
    private User user;

    @Inject
    public ProjectController(final AuthorizationManager authorizationManager,
                             final User user) {
        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    public boolean canCreateProjects() {
        return authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                              RepositoryAction.CREATE,
                                              user);
    }

    public boolean canReadProjects() {
        return authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                              RepositoryAction.READ,
                                              user);
    }

    public boolean canBuildProjects() {
        return authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                              RepositoryAction.BUILD,
                                              user);
    }

    public boolean canReadProject(final WorkspaceProject workspaceProject) {
        return authorizationManager.authorize(workspaceProject.getRepository(),
                                              RepositoryAction.READ,
                                              user);
    }

    public boolean canUpdateProject(final WorkspaceProject workspaceProject) {
        return authorizationManager.authorize(workspaceProject.getRepository(),
                                              RepositoryAction.UPDATE,
                                              user);
    }

    public boolean canDeleteProject(final WorkspaceProject workspaceProject) {
        return authorizationManager.authorize(workspaceProject.getRepository(),
                                              RepositoryAction.DELETE,
                                              user);
    }

    public boolean canBuildProject(final WorkspaceProject workspaceProject) {
        return authorizationManager.authorize(workspaceProject.getRepository(),
                                              RepositoryAction.BUILD,
                                              user);
    }
}