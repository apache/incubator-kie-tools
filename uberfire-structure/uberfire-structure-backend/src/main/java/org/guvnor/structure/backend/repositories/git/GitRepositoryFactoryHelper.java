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
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.repositories.RepositoryFactoryHelper;
import org.uberfire.io.IOService;

import static org.guvnor.structure.repositories.impl.git.GitRepository.SCHEME;
import static org.kie.soup.commons.validation.Preconditions.checkNotNull;

@ApplicationScoped
public class GitRepositoryFactoryHelper implements RepositoryFactoryHelper {

    private IOService ioService;

    @Inject
    private PasswordService secureService;

    public GitRepositoryFactoryHelper() {
    }

    @Inject
    public GitRepositoryFactoryHelper(@Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public boolean accept(final ConfigGroup repoConfig) {
        checkNotNull("repoConfig",
                     repoConfig);
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem(EnvironmentParameters.SCHEME);
        checkNotNull("schemeConfigItem",
                     schemeConfigItem);
        return SCHEME.equals(schemeConfigItem.getValue());
    }

    @Override
    public Repository newRepository(final ConfigGroup repoConfig) {

        validate(repoConfig);

        return new GitRepositoryBuilder(ioService,
                                        secureService).build(repoConfig);
    }

    private void validate(ConfigGroup repoConfig) {
        checkNotNull("repoConfig",
                     repoConfig);
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem(EnvironmentParameters.SCHEME);
        checkNotNull("schemeConfigItem",
                     schemeConfigItem);
    }
}
