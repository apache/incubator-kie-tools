/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.service.SourceService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class SourceServiceImpl
        implements SourceService {

    private OrganizationalUnitService organizationalUnitService;

    private RepositoryService repositoryService;

    private ProjectService<? extends Project> projectService;

    private AuthorizationManager authorizationManager;

    private User identity;

    public SourceServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public SourceServiceImpl(OrganizationalUnitService organizationalUnitService,
                             RepositoryService repositoryService,
                             ProjectService<? extends Project> projectService,
                             AuthorizationManager authorizationManager,
                             User identity) {
        this.organizationalUnitService = organizationalUnitService;
        this.repositoryService = repositoryService;
        this.projectService = projectService;
        this.authorizationManager = authorizationManager;
        this.identity = identity;
    }

    @Override
    public Collection<String> getOrganizationUnits() {
        return organizationalUnitService.getOrganizationalUnits().stream()
                .map(OrganizationalUnit::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> getRepositories(final String organizationalUnit) {
        checkNotNull("organizationalUnit",
                     organizationalUnit);
        OrganizationalUnit ou = organizationalUnitService.getOrganizationalUnit(organizationalUnit);
        if (ou == null) {
            return new ArrayList<>();
        } else {
            return organizationalUnitService.getOrganizationalUnit(organizationalUnit)
                    .getRepositories()
                    .stream()
                    .filter(repository -> authorizationManager.authorize(repository,
                                                                         identity))
                    .map(Repository::getAlias)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Collection<String> getBranches(final String repository) {
        checkNotNull("repository",
                     repository);
        final Repository repo = repositoryService.getRepository(repository);
        return repo != null ? repo.getBranches() : new ArrayList<>();
    }

    @Override
    public Collection<Project> getProjects(final String repositoryAlias,
                                           final String branch) {
        checkNotNull("repositoryAlias",
                     repositoryAlias);
        checkNotNull("branch",
                     branch);
        final Repository repo = repositoryService.getRepository(repositoryAlias);
        if (repo == null) {
            return new ArrayList<>();
        } else {
            return projectService.getProjects(repo,
                                              branch);
        }
    }
}
