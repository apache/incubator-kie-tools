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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import java.util.HashMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectScreenPresenterRefreshTest
        extends ProjectScreenPresenterTestBase {

    @Before
    public void setup() {
        ApplicationPreferences.setUp(new HashMap<>());

        mockBuildOptions();

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOMMetaData(new Metadata());
        model.setKModuleMetaData(new Metadata());
        model.setProjectImportsMetaData(new Metadata());
        mockProjectScreenService(model);

        mockWorkspaceProjectContext(new POM(),
                           repository,
                           module,
                           pomPath);

        constructProjectScreenPresenter(module);
        presenter.setupPathToPomXML();
    }

    @Test
    public void refreshWhenPomXMLFileContentChanges() throws Exception {
        final ResourceUpdatedEvent resourceUpdatedEvent = mock(ResourceUpdatedEvent.class);

        final SessionInfo sessionInfo = mock(SessionInfo.class);
        when(resourceUpdatedEvent.getSessionInfo()).thenReturn(sessionInfo);

        when(sessionInfo.getIdentity()).thenReturn(user);

        when(resourceUpdatedEvent.getPath()).thenReturn(observablePathToPomXML);

        reset(view);

        presenter.onResourceUpdated(resourceUpdatedEvent);

        verify(view).setPOM(any(POM.class));
    }

    @Test
    public void doNotRefreshWhenAnotherPomXMLFileContentChanges() throws Exception {
        final ResourceUpdatedEvent resourceUpdatedEvent = mock(ResourceUpdatedEvent.class);

        final SessionInfo sessionInfo = mock(SessionInfo.class);
        when(resourceUpdatedEvent.getSessionInfo()).thenReturn(sessionInfo);
        when(sessionInfo.getIdentity()).thenReturn(user);
        when(resourceUpdatedEvent.getPath()).thenReturn(mock(Path.class));

        reset(view);

        presenter.onResourceUpdated(resourceUpdatedEvent);

        verify(view,
               never()).setPOM(any(POM.class));
    }

    @Test
    public void doNotRefreshWhenAnotherUserMakesThePomXMLFileContentChange() throws Exception {
        final ResourceUpdatedEvent resourceUpdatedEvent = mock(ResourceUpdatedEvent.class);

        final SessionInfo sessionInfo = mock(SessionInfo.class);
        when(resourceUpdatedEvent.getSessionInfo()).thenReturn(sessionInfo);
        when(sessionInfo.getIdentity()).thenReturn(mock(User.class));
        when(resourceUpdatedEvent.getPath()).thenReturn(mock(Path.class));

        reset(view);

        presenter.onResourceUpdated(resourceUpdatedEvent);

        verify(view,
               never()).setPOM(any(POM.class));
    }
}
