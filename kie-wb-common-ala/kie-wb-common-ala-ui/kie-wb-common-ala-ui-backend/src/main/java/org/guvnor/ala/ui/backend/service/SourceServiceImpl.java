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
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.service.SourceService;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class SourceServiceImpl
        implements SourceService {

    private OrganizationalUnitService organizationalUnitService;

    private RepositoryService repositoryService;

    private ModuleService<? extends Module> moduleService;

    private AuthorizationManager authorizationManager;

    private User identity;

    public SourceServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public SourceServiceImpl(OrganizationalUnitService organizationalUnitService,
                             RepositoryService repositoryService,
                             ModuleService<? extends Module> moduleService,
                             AuthorizationManager authorizationManager,
                             User identity) {
        this.organizationalUnitService = organizationalUnitService;
        this.repositoryService = repositoryService;
        this.moduleService = moduleService;
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
    public Collection<String> getBranches(final Space space, final String repositoryName) {
        checkNotNull("repositoryName",
                     repositoryName);
        final Repository repository = repositoryService.getRepositoryFromSpace(space, repositoryName);
        return repository != null ? toBranchNames(repository.getBranches()) : new ArrayList<>();
    }

    @Override
    public Collection<Module> getModules(final Space space,
                                         final String repositoryAlias,
                                         final String branchName) {
        checkNotNull("repositoryAlias",
                     repositoryAlias);
        checkNotNull("branchName",
                     branchName);
        final Repository repository = repositoryService.getRepositoryFromSpace(space, repositoryAlias);
        if (repository == null) {
            return new ArrayList<>();
        } else {
            final Optional<Branch> branch = repository.getBranch(branchName);
            if (branch.isPresent()) {
                return moduleService.getAllModules(branch.get());
            } else {
                return new ArrayList<>();
            }
        }
    }

    private Collection<String> toBranchNames(final Collection<Branch> branches) {
        final ArrayList<String> result = new ArrayList<>();

        for (final Branch branch : branches) {
            result.add(branch.getName());
        }

        return result;
    }
}
