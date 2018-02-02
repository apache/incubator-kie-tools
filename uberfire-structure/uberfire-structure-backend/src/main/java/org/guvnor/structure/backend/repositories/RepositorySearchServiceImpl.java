/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositorySearchService;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.spaces.Space;

@Service
@ApplicationScoped
public class RepositorySearchServiceImpl implements RepositorySearchService {

    private RepositoryService repositoryService;
    private OrganizationalUnitService orgUnitService;

    @Inject
    public RepositorySearchServiceImpl(RepositoryService repositoryService, OrganizationalUnitService orgUnitService) {
        this.repositoryService = repositoryService;
        this.orgUnitService = orgUnitService;
    }

    @Override
    public Collection<Repository> searchByAlias(String pattern,
                                                int maxItems,
                                                boolean caseSensitive) {
        List<Repository> results = new ArrayList<>();
        for (Space space : orgUnitService.getAllUserSpaces()) {
            for (Repository repo : repositoryService.getAllRepositories(space)) {
                String alias = repo.getAlias();
                if (caseSensitive ? alias.contains(pattern) : alias.toLowerCase().contains(pattern.toLowerCase())) {
                    results.add(repo);
                    if (maxItems > 0 && results.size() >= maxItems) {
                        return results;
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Collection<Repository> searchById(Collection<String> ids) {
        List<Repository> results = new ArrayList<>();
        for (Space space : orgUnitService.getAllUserSpaces()) {
            for (Repository repo : repositoryService.getAllRepositories(space)) {
                if (ids.contains(repo.getIdentifier())) {
                    results.add(repo);
                }
            }
        }
        return results;
    }
}
