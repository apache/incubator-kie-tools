/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projectimportsscreen.client.forms.answer;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.project.ProjectImportsContent;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class LoadContentAnswer implements Answer<ProjectImportsService> {

    private ProjectImportsService importsService;
    private ProjectImportsContent importsContent;
    private HasBusyIndicatorDefaultErrorCallback errorCallback;

    public LoadContentAnswer(ProjectImportsService importsService,
                             ProjectImportsContent importsContent,
                             HasBusyIndicatorDefaultErrorCallback errorCallback) {
        this.importsService = importsService;
        this.importsContent = importsContent;
        this.errorCallback = errorCallback;
    }

    @Override
    public ProjectImportsService answer(InvocationOnMock invocation) throws Throwable {
        when(importsService.loadContent(any(Path.class))).thenAnswer(new Answer<ProjectImportsContent>() {

            @Override
            public ProjectImportsContent answer(InvocationOnMock invocation) throws Throwable {
                return importsContent;
            }
        });

        if (errorCallback == null) {
            @SuppressWarnings("unchecked")
            final RemoteCallback<ProjectImportsContent> callback = (RemoteCallback<ProjectImportsContent>) invocation.getArguments()[0];
            callback.callback(importsContent);
        } else {
            errorCallback.error(null,
                                null);
        }

        return importsService;
    }
}
