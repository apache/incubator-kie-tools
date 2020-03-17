/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.webapp.client.editor;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.drools.workbench.screens.scenariosimulation.webapp.client.popup.ScenarioKogitoCreationPopupPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.AbstractScenarioSimulationEditorKogitoScreen.TITLE;
import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.ScenarioSimulationEditorKogitoRuntimeScreen.NEW_FILE_NAME;
import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.ScenarioSimulationEditorKogitoRuntimeScreen.SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorKogitoRuntimeScreenTest {

    @Mock
    private PlaceManager placeManagerMock;
    @Mock
    private PlaceRequest placeRequestMock;
    @Mock
    private Consumer<Menus> menusConsumerMock;
    @Mock
    private ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapperMock;
    @Mock
    private ScenarioKogitoCreationPopupPresenter scenarioKogitoCreationPopupPresenterMock;
    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;

    private ScenarioSimulationEditorKogitoRuntimeScreen scenarioSimulationEditorKogitoRuntimeScreenSpy;

    @Before
    public void setup() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy = spy(new ScenarioSimulationEditorKogitoRuntimeScreen(placeManagerMock) {
            {
                this.scenarioSimulationEditorKogitoWrapper = scenarioSimulationEditorKogitoWrapperMock;
                this.scenarioKogitoCreationPopupPresenter = scenarioKogitoCreationPopupPresenterMock;
            }
        });
    }

    @Test
    public void getPlaceRequest() {
        assertEquals(SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST, scenarioSimulationEditorKogitoRuntimeScreenSpy.getPlaceRequest());
    }

    @Test
    public void onStartup() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.onStartup(placeRequestMock);
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).onStartup(eq(placeRequestMock));
    }

    @Test
    public void mayClose() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.mayClose();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).mayClose();
    }

    @Test
    public void getTitleText() {
        assertEquals(TITLE, scenarioSimulationEditorKogitoRuntimeScreenSpy.getTitleText() );
    }

    @Test
    public void getTitle() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.getTitle();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).getTitle();
    }

    @Test
    public void getWidget() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.getWidget();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).getWidget();
    }

    @Test
    public void setMenus() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.setMenus(menusConsumerMock);
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).setMenus(eq(menusConsumerMock));
    }

    @Test
    public void getContent() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.getContent();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).getContent();
    }

    @Test
    public void setContent() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.setContent("path/file.scesim", "value");
        verify(scenarioSimulationEditorKogitoRuntimeScreenSpy, never()).newFile(any());
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).setContent(eq("path/file.scesim"), eq("value"));
        verify(scenarioKogitoCreationPopupPresenterMock, times(1)).hide();
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("path/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void setContentNullPath() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.setContent("", "value");
        verify(scenarioSimulationEditorKogitoRuntimeScreenSpy, never()).newFile(any());
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).setContent(eq("/" + NEW_FILE_NAME), eq("value"));
        verify(scenarioKogitoCreationPopupPresenterMock, times(1)).hide();
        assertEquals(NEW_FILE_NAME, pathArgumentCaptor.getValue().getFileName());
        assertEquals("/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void setContentNullContent() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.setContent(null, null);
        verify(scenarioSimulationEditorKogitoRuntimeScreenSpy, times(1)).newFile(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperMock, never()).gotoPath(any());
        verify(scenarioSimulationEditorKogitoWrapperMock, never()).setContent(any(), any());
        verify(scenarioKogitoCreationPopupPresenterMock, never()).hide();
        assertEquals(NEW_FILE_NAME, pathArgumentCaptor.getValue().getFileName());
        assertEquals("/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void setContentWithPathAndNullContent() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.setContent("path/file.scesim", null);
        verify(scenarioSimulationEditorKogitoRuntimeScreenSpy, times(1)).newFile(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperMock, never()).gotoPath(any());
        verify(scenarioSimulationEditorKogitoWrapperMock, never()).setContent(any(), any());
        verify(scenarioKogitoCreationPopupPresenterMock, never()).hide();
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("path/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void setContentWithPathAndNullContentWindowsLike() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.setContent("path\\file.scesim", null);
        verify(scenarioSimulationEditorKogitoRuntimeScreenSpy, times(1)).newFile(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperMock, never()).gotoPath(any());
        verify(scenarioSimulationEditorKogitoWrapperMock, never()).setContent(any(), any());
        verify(scenarioKogitoCreationPopupPresenterMock, never()).hide();
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("path\\", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void isDirty() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy.isDirty();
        verify(scenarioSimulationEditorKogitoWrapperMock, times(1)).isDirty();
    }
}
