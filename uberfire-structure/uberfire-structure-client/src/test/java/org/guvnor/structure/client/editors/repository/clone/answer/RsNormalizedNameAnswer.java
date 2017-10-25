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

import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RsNormalizedNameAnswer implements Answer<RepositoryService> {

    private String normalizedName;
    private RepositoryService repoService;

    public RsNormalizedNameAnswer(String normalizedName,
                                  RepositoryService repoService) {
        this.normalizedName = normalizedName;
        this.repoService = repoService;
    }

    @Override
    public RepositoryService answer(InvocationOnMock invocation) throws Throwable {

        when(repoService.normalizeRepositoryName(any(String.class))).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return normalizedName;
            }
        });

        @SuppressWarnings("unchecked")
        final RemoteCallback<String> callback = (RemoteCallback<String>) invocation.getArguments()[0];
        callback.callback(normalizedName);

        return repoService;
    }
}