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
package org.drools.workbench.screens.scenariosimulation.kogito.client.editor;

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMMarshallCallback;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMODataManagementStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.promise.Promises;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorKogitoWrapperTest {

    private static final String JSON_MODEL = "jsonModel";

    @Mock
    private FileMenuBuilder fileMenuBuilderMock;
    @Mock
    private Menus menusMock;
    @Mock
    private Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallBackMock;
    @Mock
    private Promises promisesMock;
    @Mock
    private ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;
    @Mock
    private MenuItem menuItemMock;
    @Mock
    private ScenarioSimulationModel scenarioSimulationModelMock;
    @Mock
    private ScenarioSimulationContext scenarioSimulationContextMock;
    @Mock
    private Settings settingsMock;
    @Mock
    private ScenarioGridPanel simulationGridPanelMock;
    @Mock
    private ScenarioGridPanel backgroundGridPanelMock;
    @Captor
    private ArgumentCaptor<DataManagementStrategy> dataManagementStrategyCaptor;

    private ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapperSpy;

    @Before
    public void setup() {
        when(fileMenuBuilderMock.build()).thenReturn(menusMock);
        when(menusMock.getItems()).thenReturn(Arrays.asList(menuItemMock));
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationModelMock.getSettings()).thenReturn(settingsMock);
        when(scenarioSimulationEditorPresenterMock.getContext()).thenReturn(scenarioSimulationContextMock);
        when(scenarioSimulationContextMock.getScenarioGridPanelByGridWidget(GridWidget.SIMULATION)).thenReturn(simulationGridPanelMock);
        when(scenarioSimulationContextMock.getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND)).thenReturn(backgroundGridPanelMock);
        scenarioSimulationEditorKogitoWrapperSpy = spy(new ScenarioSimulationEditorKogitoWrapper() {
            {
                this.fileMenuBuilder = fileMenuBuilderMock;
                this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenterMock;
                this.promises = promisesMock;
            }
        });
    }

    @Test
    public void buildMenuBar() {
        scenarioSimulationEditorKogitoWrapperSpy.buildMenuBar();
        verify(fileMenuBuilderMock, times(1)).build();
        verify(menuItemMock, times(1)).setEnabled(eq(true));
    }

    @Test
    public void getContent() {
        scenarioSimulationEditorKogitoWrapperSpy.getContent();
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModel();
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).transform(eq(scenarioSimulationModelMock));
    }

    @Test
    public void onEditTabSelected() {
        scenarioSimulationEditorKogitoWrapperSpy.onEditTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onEditTabSelected();
    }

    @Test
    public void wrappedSave() {
        scenarioSimulationEditorKogitoWrapperSpy.wrappedSave("commit");
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).synchronizeColumnsDimension(eq(simulationGridPanelMock), eq(backgroundGridPanelMock));
    }

    @Test
    public void transform() {
        scenarioSimulationEditorKogitoWrapperSpy.transform(scenarioSimulationModelMock);
        verify(promisesMock, times(1)).create(isA(Promise.PromiseExecutorCallbackFn.class));
    }

    @Test
    public void makeMenuBar() {
        scenarioSimulationEditorPresenterMock.makeMenuBar(fileMenuBuilderMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).makeMenuBar(eq(fileMenuBuilderMock));
    }

    @Test
    public void getJSInteropMarshallCallback() {
        SCESIMMarshallCallback callback = scenarioSimulationEditorKogitoWrapperSpy.getJSInteropMarshallCallback(resolveCallBackMock);
        callback.callEvent("xmlString");
        verify(resolveCallBackMock, times(1)).onInvoke(eq("xmlString"));
    }

    @Test
    public void getModelSuccessCallbackMethodRule() {
        when(settingsMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        when(scenarioSimulationEditorPresenterMock.getJsonModel(eq(scenarioSimulationModelMock))).thenReturn(JSON_MODEL);
        scenarioSimulationEditorKogitoWrapperSpy.getModelSuccessCallbackMethod(scenarioSimulationModelMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(eq(ScenarioSimulationEditorKogitoWrapper.DEFAULT_PACKAGE));
        verify(((BaseKogitoEditor) scenarioSimulationEditorKogitoWrapperSpy), times(1)).setOriginalContentHash(eq(JSON_MODEL.hashCode()));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(dataManagementStrategyCaptor.capture(), eq(scenarioSimulationModelMock));
        assertTrue(dataManagementStrategyCaptor.getValue() instanceof KogitoDMODataManagementStrategy);
        verify(scenarioSimulationEditorPresenterMock, times(1)).showDocks(eq(PlaceStatus.CLOSE));
    }

    @Test
    public void getModelSuccessCallbackMethodDMN() {
        when(settingsMock.getType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        when(scenarioSimulationEditorPresenterMock.getJsonModel(eq(scenarioSimulationModelMock))).thenReturn(JSON_MODEL);
        scenarioSimulationEditorKogitoWrapperSpy.getModelSuccessCallbackMethod(scenarioSimulationModelMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(eq(ScenarioSimulationEditorKogitoWrapper.DEFAULT_PACKAGE));
        verify(((BaseKogitoEditor)scenarioSimulationEditorKogitoWrapperSpy), times(1)).setOriginalContentHash(eq(JSON_MODEL.hashCode()));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(dataManagementStrategyCaptor.capture(), eq(scenarioSimulationModelMock));
        assertTrue(dataManagementStrategyCaptor.getValue() instanceof KogitoDMNDataManagementStrategy);
        verify(scenarioSimulationEditorPresenterMock, times(1)).showDocks(eq(PlaceStatus.CLOSE));
    }

    @Test
    public void onBackgroundTabSelected() {
        scenarioSimulationEditorKogitoWrapperSpy.onBackgroundTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onBackgroundTabSelected();
    }
}
