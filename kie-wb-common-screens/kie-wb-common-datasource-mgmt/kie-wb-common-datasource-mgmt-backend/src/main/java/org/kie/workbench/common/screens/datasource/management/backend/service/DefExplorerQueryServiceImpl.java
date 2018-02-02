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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQuery;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryResult;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.security.authz.AuthorizationManager;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class DefExplorerQueryServiceImpl
        implements DefExplorerQueryService {

    @Inject
    private WorkspaceProjectService projectService;
    @Inject
    private DataSourceDefQueryService queryService;
    @Inject
    private KieModuleService moduleService;
    @Inject
    private OrganizationalUnitService organizationalUnitService;
    @Inject
    private AuthorizationManager authorizationManager;
    @Inject
    private User identity;

    public DefExplorerQueryResult executeQuery(final DefExplorerQuery query) {
        checkNotNull("query",
                     query);
        if (query.isGlobalQuery()) {
            DefExplorerQueryResult result = new DefExplorerQueryResult();
            result.setDataSourceDefs(queryService.findGlobalDataSources(true));
            result.setDriverDefs(queryService.findGlobalDrivers());
            return result;
        } else {
            return resolveQuery(query);
        }
    }

    private DefExplorerQueryResult resolveQuery(final DefExplorerQuery query) {

        final DefExplorerQueryResult result = new DefExplorerQueryResult();

        //load the organizational units.
        final Collection<OrganizationalUnit> organizationalUnits = resolveOrganizationalUnits();
        //piggyback the organizational units.
        result.getOrganizationalUnits().addAll(organizationalUnits);
        if (query.getOrganizationalUnit() == null ||
                !containsOU(organizationalUnits,
                            query.getOrganizationalUnit())) {
            //if no OU was set for filtering or the selected OU has been removed or has changed in backend.
            return result;
        }

        //set the repositories for current OU.
        final Map<String, Repository> repositories = resolveRepositories(query.getOrganizationalUnit());
        //piggyback the repositories.
        result.getRepositories().addAll(repositories.values());
        if (query.getRepository() == null ||
                !repositories.containsKey(query.getRepository().getAlias())) {
            //if no Repository was set for filtering or the selected Repository has been removed or has
            // changed in backend.
            return result;
        }

        //load the modules for current OU/Repository and the selected branch.
        final Map<String, Module> modules = resolveModules(repositories,
                                                           query.getBranchName());
        result.getModules().addAll(modules.values());
        if (query.getModule() == null || !modules.containsKey(query.getModule().getModuleName())) {
            //if no Module was set for filtering or the selected Module has been removed or has
            // changed in backend.
            return result;
        }

        //get the data sources and drivers for the selected module.
        result.setDataSourceDefs(queryService.findModuleDataSources(query.getModule()));
        result.setDriverDefs(queryService.findModuleDrivers(query.getModule()));
        return result;
    }

    private boolean containsOU(final Collection<OrganizationalUnit> organizationalUnits,
                               final OrganizationalUnit ou) {
        for (OrganizationalUnit unit : organizationalUnits) {
            if (unit.getName().equals(ou.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves the organizational units accessible by current user.
     */
    private Set<OrganizationalUnit> resolveOrganizationalUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Set<OrganizationalUnit> authorizedOrganizationalUnits = new HashSet<>();
        for (OrganizationalUnit organizationalUnit : organizationalUnits) {
            if (authorizationManager.authorize(organizationalUnit,
                                               identity)) {
                authorizedOrganizationalUnits.add(organizationalUnit);
            }
        }
        return authorizedOrganizationalUnits;
    }

    /**
     * Given an organizational unit, resolves the repositories accessible by current user in the given OU.
     */
    private Map<String, Repository> resolveRepositories(final OrganizationalUnit organizationalUnit) {
        final Map<String, Repository> authorizedRepositories = new HashMap<>();
        if (organizationalUnit == null) {
            return authorizedRepositories;
        }
        //Reload OrganizationalUnit as the organizational unit's repository list might have been changed server-side
        final Collection<Repository> repositories = organizationalUnitService.getOrganizationalUnit(organizationalUnit.getName()).getRepositories();
        for (Repository repository : repositories) {
            if (authorizationManager.authorize(repository,
                                               identity)) {
                authorizedRepositories.put(repository.getAlias(),
                                           repository);
            }
        }
        return authorizedRepositories;
    }

    /**
     * Resolves all the modules accessible by current user in the given branch name.
     */
    private Map<String, Module> resolveModules(final Map<String, Repository> repositories,
                                               final String branchName) {
        final Map<String, Module> authorizedModules = new HashMap<>();

        for (final Repository repository : repositories.values()) {
            if (containsBranch(repository.getBranches(),
                               branchName)) {
                final Module module = projectService.resolveProject(repository).getMainModule();
                authorizedModules.put(module.getModuleName(),
                                      module);
            }
        }

        return authorizedModules;
    }

    private boolean containsBranch(final Collection<Branch> branches,
                                   final String branchName) {
        for (final Branch branch : branches) {
            if (branchName.equals(branch.getName())) {
                return true;
            }
        }

        return false;
    }
}
