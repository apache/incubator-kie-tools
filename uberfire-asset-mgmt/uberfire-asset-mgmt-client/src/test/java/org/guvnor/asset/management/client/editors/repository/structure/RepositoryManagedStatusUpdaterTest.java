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

package org.guvnor.asset.management.client.editors.repository.structure;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoryManagedStatusUpdaterTest {

    @GwtMock
    Constants constants;

    @Mock
    RepositoryStructureService service;

    @Mock
    ProjectWizard wizard;

    @Mock
    HasBusyIndicator view;

    @Mock
    RepositoryStructureView.Presenter presenter;

    private RepositoryManagedStatusUpdater updater;

    @Mock
    private Repository repository;

    @Mock
    private Repository updatedRepository;

    @Mock
    private ActionHistory history;

    @Captor
    ArgumentCaptor<Callback<Project>> callbackArgumentCaptor;

    @Before
    public void setUp() throws Exception {

        when(service.updateManagedStatus(eq(repository),
                                         anyBoolean())).thenReturn(updatedRepository);

        updater = new RepositoryManagedStatusUpdater(new CallerMock<>(service),
                                                     wizard);

        updater.bind(view,
                     history,
                     presenter);
    }

    @Test
    public void testUpdateNonManaged() throws Exception {
        updater.updateNonManaged(repository,
                                 "master");
        verify(view).showBusyIndicator("CreatingRepositoryStructure");

        verify(service).updateManagedStatus(repository,
                                            false);

        verify(presenter).loadModel(updatedRepository,
                                    "master");
    }

    @Test
    public void testInitSingleProject() throws Exception {
        updater.initSingleProject(repository,
                                  "master");

        verify(wizard).initialise(any(POM.class));

        verify(wizard).start(callbackArgumentCaptor.capture(),
                             eq(false));

        final Project project = new Project();
        callbackArgumentCaptor.getValue().callback(project);

        verify(history).setLastAddedModule(project);

        verify(service).updateManagedStatus(repository,
                                            true);

        verify(presenter).loadModel(updatedRepository,
                                    "master");
    }

    @Test
    public void testInitSingleProjectNullProject() throws Exception {
        updater.initSingleProject(repository,
                                  "master");

        verify(wizard).initialise(any(POM.class));

        verify(wizard).start(callbackArgumentCaptor.capture(),
                             eq(false));

        callbackArgumentCaptor.getValue().callback(null);

        verify(history).setLastAddedModule(null);

        verify(service,
               never()).updateManagedStatus(any(Repository.class),
                                            anyBoolean());

        verify(presenter,
               never()).loadModel(any(Repository.class),
                                  anyString());
    }
}