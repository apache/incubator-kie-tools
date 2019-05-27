/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchStandaloneModeTest {

    @Mock
    private VFSServiceProxy vfsService;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ManagedInstance<WorkbenchCustomStandalonePerspectiveDefinition> workbenchCustomStandalonePerspectiveDefinitions;

    @Spy
    @InjectMocks
    private Workbench workbench;

    @Test
    public void handleStandaloneModeWithPerspectiveParameterTest() {
        final Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("perspective", Collections.singletonList("MyPerspective"));

        workbench.handleStandaloneMode(parameters);

        verify(placeManager).goTo(new DefaultPlaceRequest(parameters.get("perspective").get(0)));
    }

    @Test
    public void handleStandaloneModeWithPathParameterAndNoCustomStandaloneEditorPerspectiveDefinitionTest() {
        doReturn(true).when(workbenchCustomStandalonePerspectiveDefinitions).isUnsatisfied();

        final Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("path", Collections.singletonList("git://master@MySpace/MyProject/src/main/java/com/myspace/myproject/myasset.java"));

        workbench.handleStandaloneMode(parameters);

        verify(placeManager).goTo(new DefaultPlaceRequest("StandaloneEditorPerspective"));
    }

    @Test
    public void handleStandaloneModeWithPathParameterAndCustomStandaloneEditorPerspectiveDefinitionNotOpeningEditorByDefaultTest() {
        doReturn(false).when(workbenchCustomStandalonePerspectiveDefinitions).isUnsatisfied();
        final WorkbenchCustomStandalonePerspectiveDefinition workbenchCustomStandalonePerspectiveDefinition = mock(WorkbenchCustomStandalonePerspectiveDefinition.class);
        doReturn("MyCustomStandalonePerspective").when(workbenchCustomStandalonePerspectiveDefinition).getStandalonePerspectiveIdentifier();
        doReturn(false).when(workbenchCustomStandalonePerspectiveDefinition).openPathAutomatically();
        doReturn(workbenchCustomStandalonePerspectiveDefinition).when(workbenchCustomStandalonePerspectiveDefinitions).get();

        final Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("path", Collections.singletonList("git://master@MySpace/MyProject/src/main/java/com/myspace/myproject/myasset.java"));

        workbench.handleStandaloneMode(parameters);

        verify(placeManager, times(1)).goTo(any(PlaceRequest.class));
        verify(placeManager).goTo(new DefaultPlaceRequest("MyCustomStandalonePerspective"));
        verify(vfsService, never()).get(any(), any());
    }

    @Test
    public void handleStandaloneModeWithPathParameterAndCustomStandaloneEditorPerspectiveDefinitionOpeningEditorByDefaultTest() {
        doNothing().when(workbench).openEditor(any());

        final Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("path", Collections.singletonList("git://master@MySpace/MyProject/src/main/java/com/myspace/myproject/myasset.java"));

        final Path path = mock(Path.class);
        doReturn(false).when(workbenchCustomStandalonePerspectiveDefinitions).isUnsatisfied();
        final WorkbenchCustomStandalonePerspectiveDefinition workbenchCustomStandalonePerspectiveDefinition = mock(WorkbenchCustomStandalonePerspectiveDefinition.class);
        doReturn("MyCustomStandalonePerspective").when(workbenchCustomStandalonePerspectiveDefinition).getStandalonePerspectiveIdentifier();
        doReturn(true).when(workbenchCustomStandalonePerspectiveDefinition).openPathAutomatically();
        doReturn(workbenchCustomStandalonePerspectiveDefinition).when(workbenchCustomStandalonePerspectiveDefinitions).get();
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(1, ParameterizedCommand.class).execute(path);
            return null;
        }).when(vfsService).get(same(parameters.get("path").get(0)), any(ParameterizedCommand.class));

        workbench.handleStandaloneMode(parameters);

        verify(placeManager, times(1)).goTo(any(PlaceRequest.class));
        verify(placeManager).goTo(new DefaultPlaceRequest("MyCustomStandalonePerspective"));
        verify(vfsService).get(same(parameters.get("path").get(0)), any(ParameterizedCommand.class));
        verify(workbench).openEditor(path);
    }
}
