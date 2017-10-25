/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.clone.answer;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RsCreateRepositoryAnswer implements Answer<RepositoryService> {

    private Repository repository;
    private RepositoryService repoService;

    public RsCreateRepositoryAnswer(Repository repository,
                                    RepositoryService repoService) {
        this.repository = repository;
        this.repoService = repoService;
    }

    @Override
    public RepositoryService answer(InvocationOnMock invocation) throws Throwable {

        when(repoService.createRepository(any(OrganizationalUnit.class),
                                          any(String.class),
                                          any(String.class),
                                          any(RepositoryEnvironmentConfigurations.class))).then(new Answer<Repository>() {

            @Override
            public Repository answer(InvocationOnMock invocation) throws Throwable {
                return repository;
            }
        });

        @SuppressWarnings("unchecked")
        final RemoteCallback<Repository> callback = (RemoteCallback<Repository>) invocation.getArguments()[0];
        callback.callback(repository);

        return repoService;
    }
}