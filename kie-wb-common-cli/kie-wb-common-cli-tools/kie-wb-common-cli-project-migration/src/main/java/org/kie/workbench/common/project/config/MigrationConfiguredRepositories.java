/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.project.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.backend.repositories.ConfiguredRepositoriesImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.repositories.RepositoryFactory;

@Alternative
@ApplicationScoped
public class MigrationConfiguredRepositories extends ConfiguredRepositoriesImpl {

    public MigrationConfiguredRepositories() {
        super();
    }

    @Inject
    public MigrationConfiguredRepositories(final MigrationConfigurationServiceImpl configurationService,
                                           final RepositoryFactory repositoryFactory,
                                           final @Named("system") Repository systemRepository) {
        super(configurationService,
              repositoryFactory,
              systemRepository);
    }
}
