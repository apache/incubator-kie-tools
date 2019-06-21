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

package org.guvnor.structure.backend.repositories;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.guvnor.structure.server.repositories.RepositoryFactoryHelper;

import static org.guvnor.structure.backend.repositories.SystemRepository.SYSTEM_REPO;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class RepositoryFactoryImpl implements RepositoryFactory {

    @Inject
    @Any
    private Instance<RepositoryFactoryHelper> helpers;

    @Inject
    private BackwardCompatibleUtil backward;

    @Override
    public Repository newRepository(RepositoryInfo repositoryInfo) {
        checkNotNull("repositoryInfo",
                     repositoryInfo);
        final String schemeConfigItem = repositoryInfo.getScheme();
        checkNotNull("schemeConfigItem",
                     schemeConfigItem);

        //Find a Helper that can create a repository
        Repository repository = null;
        for (RepositoryFactoryHelper helper : helpers) {
            if (helper.accept(repositoryInfo)) {
                repository = helper.newRepository(repositoryInfo);
                break;
            }
        }

        //Check one was created
        if (repository == null) {
            throw new IllegalArgumentException("Unrecognized scheme '" + schemeConfigItem + "'.");
        }

        //Copy in Security Roles required to access this resource
        List<String> groups = repositoryInfo.getSecurityGroups();
        if (groups != null) {
            for (String group : groups) {
                repository.getGroups().add(group);
            }
        }

        List<Contributor> contributors = repositoryInfo.getContributors();
        if (contributors != null) {
            for (Contributor contributor : contributors) {
                repository.getContributors().add(contributor);
            }
        }

        return repository;
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return SYSTEM_REPO;
    }
}
