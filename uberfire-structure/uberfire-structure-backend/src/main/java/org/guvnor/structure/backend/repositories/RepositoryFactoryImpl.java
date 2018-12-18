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
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
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
    public Repository newRepository(final ConfigGroup repoConfig) {
        checkNotNull("repoConfig",
                     repoConfig);
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem(EnvironmentParameters.SCHEME);
        checkNotNull("schemeConfigItem",
                     schemeConfigItem);

        //Find a Helper that can create a repository
        Repository repository = null;
        for (RepositoryFactoryHelper helper : helpers) {
            if (helper.accept(repoConfig)) {
                repository = helper.newRepository(repoConfig);
                break;
            }
        }

        //Check one was created
        if (repository == null) {
            throw new IllegalArgumentException("Unrecognized scheme '" + schemeConfigItem.getValue() + "'.");
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> groups = backward.compat(repoConfig).getConfigItem("security:groups");
        if (groups != null) {
            for (String group : groups.getValue()) {
                repository.getGroups().add(group);
            }
        }

        ConfigItem<List<Contributor>> contributors = repoConfig.getConfigItem("contributors");
        if (contributors != null) {
            for (Contributor contributor : contributors.getValue()) {
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
