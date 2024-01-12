/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.kogito.client.editor;

import java.lang.reflect.Field;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMMarshallCallback;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoScenarioSimulationBuilder;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.model.KogitoDMNModel;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmo.KogitoAsyncPackageDataModelOracle;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.handlers.ScenarioSimulationKogitoDocksHandler;
import org.drools.workbench.screens.scenariosimulation.kogito.client.popup.ScenarioSimulationKogitoCreationPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoDMNMarshallerService;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.kogito.client.resources.i18n.KogitoClientConstants;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.errorpage.ErrorPage;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorKogitoWrapperTest {

    private static final String JSON_MODEL = "jsonModel";

    @Mock
    private Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallBackMock;
    @Mock
    private Promises promisesMock;
    @Mock
    private ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;
    @Mock
    private ScenarioSimulationView scenarioSimulationViewMock;
    @Mock
    private ErrorPage errorPageMock;
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
    @Mock
    private ScenarioGridWidget scenarioGridWidgetMock;
    @Mock
    private MultiPageEditorContainerView multiPageEditorContainerViewMock;
    @Mock
    private MultiPageEditor multiPageEditorMock;
    @Mock
    private MultiPageEditorViewImpl multiPageEditorViewMock;
    @Mock
    private TranslationService translationServiceMock;
    @Mock
    private NavTabs navBarsMock;
    @Mock
    private TabListItem editorItemMock;
    @Mock
    private TabListItem backgroundItemMock;
    @Mock
    private ScenarioSimulationKogitoCreationPopupPresenter scenarioSimulationKogitoCreationPopupPresenterMock;
    @Mock
    private KogitoScenarioSimulationBuilder kogitoScenarioSimulationBuilderMock;
    @Mock
    private KogitoAsyncPackageDataModelOracle kogitoAsyncPackageDataModelOracleMock;
    @Mock
    private Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Void> resolveCallbackFnMock;
    @Mock
    private Promise.PromiseExecutorCallbackFn.RejectCallbackFn rejectCallbackFnMock;
    @Mock
    private PlaceRequest placeRequestMock;
    @Mock
    private ScenarioSimulationKogitoDocksHandler scenarioSimulationKogitoDocksHandlerMock;
    @Mock
    private AuthoringEditorDock authoringEditorDockMock;
    @Mock
    private ScenarioSimulationKogitoDMNMarshallerService scenarioSimulationKogitoDMNMarshallerServiceMock;
    @Captor
    private ArgumentCaptor<DataManagementStrategy> dataManagementStrategyCaptor;
    @Captor
    private ArgumentCaptor<Page> pageCaptor;
    @Captor
    private ArgumentCaptor<Callback<ScenarioSimulationModel>> callbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<ErrorCallback<Object>> errorCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;
    @Captor
    private ArgumentCaptor<Promise.PromiseExecutorCallbackFn> promiseExecutorCallbackFnArgumentCaptor;

    private ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapperSpy;
    private Path path = PathFactory.newPath("file.scesim", "path/");

    @Before
    public void setup() {
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationEditorPresenterMock.getView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationModelMock.getSettings()).thenReturn(settingsMock);
        when(scenarioSimulationEditorPresenterMock.getContext()).thenReturn(scenarioSimulationContextMock);
        when(scenarioSimulationContextMock.getScenarioGridPanelByGridWidget(GridWidget.SIMULATION)).thenReturn(simulationGridPanelMock);
        when(scenarioSimulationContextMock.getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND)).thenReturn(backgroundGridPanelMock);
        when(multiPageEditorContainerViewMock.getMultiPage()).thenReturn(multiPageEditorMock);
        when(multiPageEditorMock.asWidget()).thenReturn(multiPageEditorViewMock);
        when(multiPageEditorViewMock.getTabBar()).thenReturn(navBarsMock);
        when(navBarsMock.getWidget(1)).thenReturn(editorItemMock);
        when(navBarsMock.getWidget(2)).thenReturn(backgroundItemMock);
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedPath()).thenReturn("selected");
        when(translationServiceMock.getTranslation(KogitoClientConstants.KieEditorWrapperView_EditTabTitle)).thenReturn(KogitoClientConstants.KieEditorWrapperView_EditTabTitle);
        scenarioSimulationEditorKogitoWrapperSpy = spy(new ScenarioSimulationEditorKogitoWrapper() {
            {
                this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenterMock;
                this.promises = promisesMock;
                this.kogitoOracle = kogitoAsyncPackageDataModelOracleMock;
                this.translationService = translationServiceMock;
                this.scenarioSimulationKogitoCreationPopupPresenter = scenarioSimulationKogitoCreationPopupPresenterMock;
                this.scenarioSimulationBuilder = kogitoScenarioSimulationBuilderMock;
                this.authoringWorkbenchDocks = authoringEditorDockMock;
                this.scenarioSimulationKogitoDocksHandler = scenarioSimulationKogitoDocksHandlerMock;
                this.scenarioSimulationKogitoDMNMarshallerService = scenarioSimulationKogitoDMNMarshallerServiceMock;
                this.errorPage = errorPageMock;
            }

            @Override
            public MultiPageEditorContainerView getWidget() {
                return multiPageEditorContainerViewMock;
            }

            @Override
            protected void marshallContent(ScenarioSimulationModel scenarioSimulationModel, Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallbackFn) {
                // JSInterops logic, can't be tested
            }

            @Override
            protected void resetEditorPages() {
                //Do nothing
            }
        });
    }

    @Test
    public void getContent() {
        scenarioSimulationEditorKogitoWrapperSpy.getContent();
        verify(promisesMock, times(1)).create(promiseExecutorCallbackFnArgumentCaptor.capture());
        promiseExecutorCallbackFnArgumentCaptor.getValue().onInvoke(resolveCallBackMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).prepareContent(resolveCallBackMock, rejectCallbackFnMock);
    }

    @Test
    public void manageContent() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("path/file.scesim", "value", resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).showScenarioSimulationCreationPopup(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent("value");
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("path/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void prepareContent() {
        scenarioSimulationEditorKogitoWrapperSpy.prepareContent(resolveCallBackMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).synchronizeColumnsDimension(simulationGridPanelMock, backgroundGridPanelMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).marshallContent(scenarioSimulationModelMock, resolveCallBackMock);
        verify(scenarioSimulationEditorPresenterMock, never()).sendNotification(any(), any());
    }

    @Test
    public void prepareContent_Exception() {
        willThrow(RuntimeException.class).given(scenarioSimulationEditorKogitoWrapperSpy).marshallContent(any(), any());
        scenarioSimulationEditorKogitoWrapperSpy.prepareContent(resolveCallBackMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).synchronizeColumnsDimension(simulationGridPanelMock, backgroundGridPanelMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).marshallContent(scenarioSimulationModelMock, resolveCallBackMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(any(), eq(NotificationEvent.NotificationType.ERROR));
    }

    @Test
    public void manageContentFileWithoutPath() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("file.scesim", "value", resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).showScenarioSimulationCreationPopup(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent("value");
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void manageContentNullPath() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("", "value", resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).showScenarioSimulationCreationPopup(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent("value");
        assertEquals("new-file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void manageContentNullContentAndPath() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent(null, null, resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).showScenarioSimulationCreationPopup(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).gotoPath(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).unmarshallContent(any());
        assertEquals("new-file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void manageContentNullContent() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("path/file.scesim", null, resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).showScenarioSimulationCreationPopup(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).gotoPath(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).unmarshallContent(any());
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("path/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void manageContentWithPathAndNullContent() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("path/file.scesim", null, resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).showScenarioSimulationCreationPopup(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).gotoPath(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).unmarshallContent(any());
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("path/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void manageContentWithException() {
        willThrow(new IllegalStateException("Error message")).given(scenarioSimulationEditorKogitoWrapperSpy).unmarshallContent(any());
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("path/file.scesim", "value", resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).showScenarioSimulationCreationPopup(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent("value");
        verify(errorPageMock).setErrorContent("Error message");
        verify(errorPageMock).setTitle(ScenarioSimulationEditorConstants.INSTANCE.scenarioParsingError());
        verify(errorPageMock).setContent(ScenarioSimulationEditorConstants.INSTANCE.scenarioParsingErrorContent());
        verify(scenarioSimulationViewMock).setContentWidget(errorPageMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).hideDocks();
        verify(scenarioSimulationViewMock, times(1)).setScenarioTabBarVisibility(false);
        verify(rejectCallbackFnMock, times(1)).onInvoke("Error message");
    }

    @Test
    public void onEditTabSelected() {
        scenarioSimulationEditorKogitoWrapperSpy.onEditTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onEditTabSelected();
    }

    @Test
    public void onStartup() throws IllegalAccessException, NoSuchFieldException {
        final Field field = MultiPageEditorContainerPresenter.class.getDeclaredField("multiPageEditorContainerView");
        field.setAccessible(true);
        field.set(scenarioSimulationEditorKogitoWrapperSpy, multiPageEditorContainerViewMock);

        scenarioSimulationEditorKogitoWrapperSpy.onStartup(placeRequestMock);
        verify(authoringEditorDockMock, times(1)).setup("AuthoringPerspective", placeRequestMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setWrapper(scenarioSimulationEditorKogitoWrapperSpy);
    }

    @Test
    public void gotoPath() {
        scenarioSimulationEditorKogitoWrapperSpy.gotoPath(path);
        verify(kogitoAsyncPackageDataModelOracleMock, times(1)).init(path);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPath(isA(ObservablePath.class));
        assertEquals(path, scenarioSimulationEditorKogitoWrapperSpy.getCurrentPath());
    }

    @Test
    public void getJSInteropMarshallCallback() {
        SCESIMMarshallCallback callback = scenarioSimulationEditorKogitoWrapperSpy.getJSInteropMarshallCallback(resolveCallBackMock);
        callback.callEvent("xmlString");
        verify(resolveCallBackMock, times(1)).onInvoke("xmlString");
    }

    @Test
    public void getModelSuccessCallbackMethodRule() {
        when(settingsMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        when(scenarioSimulationEditorPresenterMock.getJsonModel(scenarioSimulationModelMock)).thenReturn(JSON_MODEL);
        scenarioSimulationEditorKogitoWrapperSpy.onModelSuccessCallbackMethod(scenarioSimulationModelMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(ScenarioSimulationEditorConstants.INSTANCE.ruleScenarioNotSupportedNotification(), NotificationEvent.NotificationType.WARNING, false);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(ScenarioSimulationEditorKogitoWrapper.DEFAULT_PACKAGE);
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(dataManagementStrategyCaptor.capture(), eq(scenarioSimulationModelMock));
        assertTrue(dataManagementStrategyCaptor.getValue() instanceof KogitoDMODataManagementStrategy);
        verify(scenarioSimulationViewMock, times(1)).setScenarioGridWidgetAsContent();
        verify(scenarioSimulationViewMock, times(1)).setScenarioTabBarVisibility(true);
    }

    @Test
    public void getModelSuccessCallbackMethodDMN() {
        when(settingsMock.getType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        when(scenarioSimulationEditorPresenterMock.getJsonModel(scenarioSimulationModelMock)).thenReturn(JSON_MODEL);
        scenarioSimulationEditorKogitoWrapperSpy.onModelSuccessCallbackMethod(scenarioSimulationModelMock);
        verify(scenarioSimulationEditorPresenterMock, never()).sendNotification(any(), any(), anyBoolean());
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(ScenarioSimulationEditorKogitoWrapper.DEFAULT_PACKAGE);
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(dataManagementStrategyCaptor.capture(), eq(scenarioSimulationModelMock));
        assertTrue(dataManagementStrategyCaptor.getValue() instanceof KogitoDMNDataManagementStrategy);
        verify(scenarioSimulationViewMock, times(1)).setScenarioGridWidgetAsContent();
        verify(scenarioSimulationViewMock, times(1)).setScenarioTabBarVisibility(true);
    }

    @Test
    public void addBackgroundPage() {
        scenarioSimulationEditorKogitoWrapperSpy.addBackgroundPage(scenarioGridWidgetMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy.getWidget().getMultiPage(), times(1)).addPage(pageCaptor.capture());
        pageCaptor.getValue().onFocus();
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).onBackgroundTabSelected();
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle(), pageCaptor.getValue().getLabel());
    }

    @Test
    public void onBackgroundTabSelected() {
        scenarioSimulationEditorKogitoWrapperSpy.onBackgroundTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onBackgroundTabSelected();
    }

    @Test
    public void showScenarioSimulationCreationPopup() {
        scenarioSimulationEditorKogitoWrapperSpy.showScenarioSimulationCreationPopup(path);
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).show(eq(ScenarioSimulationEditorConstants.INSTANCE.addScenarioSimulation()),
                                                                                  isA(Command.class));
    }

    @Test
    public void newFileEmptySelectedType() {
        Command command = scenarioSimulationEditorKogitoWrapperSpy.createNewFileCommand(path);
        command.execute();
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).getSelectedType();
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(ScenarioSimulationEditorConstants.INSTANCE.missingSelectedType(),
                                                                                 NotificationEvent.NotificationType.ERROR,
                                                                                 false);
        verify(kogitoScenarioSimulationBuilderMock, never()).populateScenarioSimulationModelDMN(any(), any(), any());
        verify(kogitoScenarioSimulationBuilderMock, never()).populateScenarioSimulationModelRULE(any(), any());
    }

    @Test
    public void newFileEmptySelectedDMNPath() {
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedPath()).thenReturn(null);
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        Command command = scenarioSimulationEditorKogitoWrapperSpy.createNewFileCommand(path);
        command.execute();
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).getSelectedType();
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(ScenarioSimulationEditorConstants.INSTANCE.missingDmnPath(),
                                                                                 NotificationEvent.NotificationType.ERROR,
                                                                                 false);
        verify(kogitoScenarioSimulationBuilderMock, never()).populateScenarioSimulationModelDMN(any(), any(), any());
        verify(kogitoScenarioSimulationBuilderMock, never()).populateScenarioSimulationModelRULE(any(), any());
    }

    @Test
    public void newFileRule() {
        ScenarioSimulationModel model = mock(ScenarioSimulationModel.class);
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        Mockito.doNothing().when(scenarioSimulationEditorKogitoWrapperSpy).onModelSuccessCallbackMethod(model);
        Command command = scenarioSimulationEditorKogitoWrapperSpy.createNewFileCommand(path);
        command.execute();
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).getSelectedType();
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(path);
        verify(kogitoScenarioSimulationBuilderMock, times(1)).populateScenarioSimulationModelRULE(eq(""),
                                                                                                  callbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().callback(model);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).onModelSuccessCallbackMethod(model);
    }

    @Test
    public void newFileDMN() {
        ScenarioSimulationModel model = mock(ScenarioSimulationModel.class);
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        Mockito.doNothing().when(scenarioSimulationEditorKogitoWrapperSpy).onModelSuccessCallbackMethod(model);
        Command command = scenarioSimulationEditorKogitoWrapperSpy.createNewFileCommand(path);
        command.execute();
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).getSelectedType();
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(path);
        verify(kogitoScenarioSimulationBuilderMock, times(1)).populateScenarioSimulationModelDMN(eq("selected"),
                                                                                                 callbackArgumentCaptor.capture(),
                                                                                                 errorCallbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().callback(model);
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).onModelSuccessCallbackMethod(model);
        errorCallbackArgumentCaptor.getValue().error("message", new Exception());
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorDetailedLabel("selected", "message"),
                                                                                 NotificationEvent.NotificationType.ERROR,
                                                                                 false);
    }

    @Test
    public void getScenarioSimulationDocksHandler() {
        assertEquals(scenarioSimulationKogitoDocksHandlerMock,
                     scenarioSimulationEditorKogitoWrapperSpy.getScenarioSimulationDocksHandler());
    }

    @Test
    public void getScenarioSimulationEditorPresenter() {
        assertEquals(scenarioSimulationEditorPresenterMock,
                     scenarioSimulationEditorKogitoWrapperSpy.getScenarioSimulationEditorPresenter());
    }

    @Test
    public void getDMNMetadata() {
        when(settingsMock.getDmnFilePath()).thenReturn("src/test.dmn");
        ArgumentCaptor<Path> pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(Callback.class);
        scenarioSimulationEditorKogitoWrapperSpy.getDMNMetadata();
        verify(scenarioSimulationKogitoDMNMarshallerServiceMock, times(1)).getDMNContent(pathArgumentCaptor.capture(),
                                                                                         callbackArgumentCaptor.capture(),
                                                                                         isA(ErrorCallback.class));
        assertEquals("src/test.dmn", pathArgumentCaptor.getValue().toURI());
        assertEquals("test.dmn", pathArgumentCaptor.getValue().getFileName());
        KogitoDMNModel returnCallback = mock(KogitoDMNModel.class);
        when(returnCallback.getNamespace()).thenReturn("DMN-NAMESPACE");
        when(returnCallback.getName()).thenReturn("DMN-Name");
        callbackArgumentCaptor.getValue().callback(returnCallback);
        verify(settingsMock, times(1)).setDmnNamespace("DMN-NAMESPACE");
        verify(settingsMock, times(1)).setDmnName("DMN-Name");
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadSettingsDock();
    }
}