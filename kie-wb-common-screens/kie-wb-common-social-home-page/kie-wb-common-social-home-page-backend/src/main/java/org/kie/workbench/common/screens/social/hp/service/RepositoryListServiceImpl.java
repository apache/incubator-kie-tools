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
package org.kie.workbench.common.screens.social.hp.service;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.social.hp.security.SocialEventRepositoryConstraint;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@Service
public class RepositoryListServiceImpl implements RepositoryListService {

    @Inject
    SocialEventRepositoryConstraint repositoryConstraint;

    @Override
    public List<String> getRepositories() {

        List<String> repositoriesName = new ArrayList<String>();

        final Collection<Repository> repositories = repositoryConstraint.getAuthorizedRepositories();
        for ( Repository repository : repositories ) {
            repositoriesName.add( repository.getAlias() );
        }

        return repositoriesName;

    }

}
