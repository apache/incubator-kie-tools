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
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoScenarioSimulationBuilder;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmo.KogitoAsyncPackageDataModelOracle;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.handlers.ScenarioSimulationKogitoDocksHandler;
import org.drools.workbench.screens.scenariosimulation.kogito.client.popup.ScenarioSimulationKogitoCreationPopupPresenter;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.kogito.client.resources.i18n.KogitoClientConstants;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
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
    private Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Object> resolveCallbackFnMock;
    @Mock
    private Promise.PromiseExecutorCallbackFn.RejectCallbackFn rejectCallbackFnMock;
    @Mock
    private PlaceRequest placeRequestMock;
    @Mock
    private ScenarioSimulationKogitoDocksHandler scenarioSimulationKogitoDocksHandlerMock;
    @Mock
    private AuthoringEditorDock authoringEditorDockMock;
    @Captor
    private ArgumentCaptor<DataManagementStrategy> dataManagementStrategyCaptor;
    @Captor
    private ArgumentCaptor<Page> pageCaptor;
    @Captor
    private ArgumentCaptor<RemoteCallback> remoteCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;

    private ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapperSpy;
    private Path path = PathFactory.newPath("file.scesim", "path/");

    @Before
    public void setup() {
        when(fileMenuBuilderMock.build()).thenReturn(menusMock);
        when(menusMock.getItems()).thenReturn(Arrays.asList(menuItemMock));
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationModelMock.getSettings()).thenReturn(settingsMock);
        when(scenarioSimulationEditorPresenterMock.getContext()).thenReturn(scenarioSimulationContextMock);
        when(scenarioSimulationContextMock.getScenarioGridPanelByGridWidget(GridWidget.SIMULATION)).thenReturn(simulationGridPanelMock);
        when(scenarioSimulationContextMock.getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND)).thenReturn(backgroundGridPanelMock);
        when(multiPageEditorContainerViewMock.getMultiPage()).thenReturn(multiPageEditorMock);
        when(multiPageEditorMock.getView()).thenReturn(multiPageEditorViewMock);
        when(multiPageEditorViewMock.getPageIndex(KogitoClientConstants.KieEditorWrapperView_EditTabTitle)).thenReturn(1);
        when(multiPageEditorViewMock.getPageIndex(ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle())).thenReturn(2);
        when(multiPageEditorViewMock.getTabBar()).thenReturn(navBarsMock);
        when(navBarsMock.getWidget(1)).thenReturn(editorItemMock);
        when(navBarsMock.getWidget(2)).thenReturn(backgroundItemMock);
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedPath()).thenReturn("selected");
        when(translationServiceMock.getTranslation(KogitoClientConstants.KieEditorWrapperView_EditTabTitle)).thenReturn(KogitoClientConstants.KieEditorWrapperView_EditTabTitle);
        scenarioSimulationEditorKogitoWrapperSpy = spy(new ScenarioSimulationEditorKogitoWrapper() {
            {
                this.fileMenuBuilder = fileMenuBuilderMock;
                this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenterMock;
                this.promises = promisesMock;
                this.kogitoOracle = kogitoAsyncPackageDataModelOracleMock;
                this.translationService = translationServiceMock;
                this.scenarioSimulationKogitoCreationPopupPresenter = scenarioSimulationKogitoCreationPopupPresenterMock;
                this.scenarioSimulationBuilder = kogitoScenarioSimulationBuilderMock;
                this.authoringWorkbenchDocks = authoringEditorDockMock;
                this.scenarioSimulationKogitoDocksHandler = scenarioSimulationKogitoDocksHandlerMock;
            }

            @Override
            public MultiPageEditorContainerView getWidget() {
                return multiPageEditorContainerViewMock;
            }

            @Override
            protected void resetEditorPages() {
                //Do nothing
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
    public void manageContent() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("path/file.scesim", "value", resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).showScenarioSimulationCreationPopup(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent(eq("value"));
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("path/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void manageContentFileWithoutPath() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("file.scesim", "value", resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).showScenarioSimulationCreationPopup(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent(eq("value"));
        assertEquals("file.scesim", pathArgumentCaptor.getValue().getFileName());
        assertEquals("/", pathArgumentCaptor.getValue().toURI());
    }

    @Test
    public void manageContentNullPath() {
        scenarioSimulationEditorKogitoWrapperSpy.manageContent("", "value", resolveCallbackFnMock, rejectCallbackFnMock);
        verify(scenarioSimulationEditorKogitoWrapperSpy, never()).showScenarioSimulationCreationPopup(any());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(pathArgumentCaptor.capture());
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent(eq("value"));
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
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent(eq("value"));
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(eq("Error message"),
                                                                                                      eq(NotificationEvent.NotificationType.ERROR));
        verify(rejectCallbackFnMock, times(1)).onInvoke("Error message");
    }

    @Test
    public void onEditTabSelected() {
        scenarioSimulationEditorKogitoWrapperSpy.onEditTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onEditTabSelected();
    }

    @Test
    public void wrappedSave() {
        scenarioSimulationEditorKogitoWrapperSpy.wrappedSave("commit");
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).synchronizeColumnsDimension(eq(simulationGridPanelMock),
                                                                                                                    eq(backgroundGridPanelMock));
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
    public void onStartup() {
        Whitebox.setInternalState(scenarioSimulationEditorKogitoWrapperSpy, "multiPageEditorContainerView", multiPageEditorContainerViewMock);
        scenarioSimulationEditorKogitoWrapperSpy.onStartup(placeRequestMock);
        verify(authoringEditorDockMock, times(1)).setup(eq("AuthoringPerspective"), eq(placeRequestMock));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setWrapper(eq(scenarioSimulationEditorKogitoWrapperSpy));
    }

    @Test
    public void gotoPath() {
        scenarioSimulationEditorKogitoWrapperSpy.gotoPath(path);
        verify(kogitoAsyncPackageDataModelOracleMock, times(1)).init(eq(path));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPath(isA(ObservablePath.class));
        assertEquals(path, scenarioSimulationEditorKogitoWrapperSpy.getCurrentPath());
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
        scenarioSimulationEditorKogitoWrapperSpy.onModelSuccessCallbackMethod(scenarioSimulationModelMock);
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
        scenarioSimulationEditorKogitoWrapperSpy.onModelSuccessCallbackMethod(scenarioSimulationModelMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(eq(ScenarioSimulationEditorKogitoWrapper.DEFAULT_PACKAGE));
        verify(((BaseKogitoEditor)scenarioSimulationEditorKogitoWrapperSpy), times(1)).setOriginalContentHash(eq(JSON_MODEL.hashCode()));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(dataManagementStrategyCaptor.capture(), eq(scenarioSimulationModelMock));
        assertTrue(dataManagementStrategyCaptor.getValue() instanceof KogitoDMNDataManagementStrategy);
        verify(scenarioSimulationEditorPresenterMock, times(1)).showDocks(eq(PlaceStatus.CLOSE));
    }

    @Test
    public void addBackgroundPage() {
        scenarioSimulationEditorKogitoWrapperSpy.addBackgroundPage(scenarioGridWidgetMock);
        verify(multiPageEditorViewMock, times(1)).addPage(eq(2), pageCaptor.capture());
        pageCaptor.getValue().onFocus();
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).onBackgroundTabSelected();
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle(), pageCaptor.getValue().getLabel());
    }

    @Test
    public void selectSimulationTab() {
        scenarioSimulationEditorKogitoWrapperSpy.selectSimulationTab();
        verify(editorItemMock, times(1)).showTab(eq(false));
        verify(backgroundItemMock, never()).showTab(anyBoolean());
    }

    @Test
    public void selectBackGroundTab() {
        scenarioSimulationEditorKogitoWrapperSpy.selectBackgroundTab();
        verify(backgroundItemMock, times(1)).showTab(eq(false));
        verify(editorItemMock, never()).showTab(anyBoolean());
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
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(eq(ScenarioSimulationEditorConstants.INSTANCE.missingSelectedType()),
                                                                                                      eq(NotificationEvent.NotificationType.ERROR));
        verify(kogitoScenarioSimulationBuilderMock, never()).populateScenarioSimulationModel(any(), any(), any(), any());
    }

    @Test
    public void newFileEmptySelectedDMNPath() {
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedPath()).thenReturn(null);
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        Command command = scenarioSimulationEditorKogitoWrapperSpy.createNewFileCommand(path);
        command.execute();
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).getSelectedType();
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(eq(ScenarioSimulationEditorConstants.INSTANCE.missingDmnPath()),
                                                                                                      eq(NotificationEvent.NotificationType.ERROR));
        verify(kogitoScenarioSimulationBuilderMock, never()).populateScenarioSimulationModel(any(), any(), any(), any());
    }

    @Test
    public void newFileRule() {
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        Command command = scenarioSimulationEditorKogitoWrapperSpy.createNewFileCommand(path);
        command.execute();
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).getSelectedType();
        verify(kogitoScenarioSimulationBuilderMock, times(1)).populateScenarioSimulationModel(isA(ScenarioSimulationModel.class),
                                                                                                                   eq(ScenarioSimulationModel.Type.RULE),
                                                                                                                   eq(""),
                                                                                                                   remoteCallbackArgumentCaptor.capture());
        remoteCallbackArgumentCaptor.getValue().callback("");
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(eq(path));
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent(isA(String.class));
    }

    @Test
    public void newFileDMN() {
        when(scenarioSimulationKogitoCreationPopupPresenterMock.getSelectedType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        Command command = scenarioSimulationEditorKogitoWrapperSpy.createNewFileCommand(path);
        command.execute();
        verify(scenarioSimulationKogitoCreationPopupPresenterMock, times(1)).getSelectedType();
        verify(kogitoScenarioSimulationBuilderMock, times(1)).populateScenarioSimulationModel(isA(ScenarioSimulationModel.class),
                                                                                                                   eq(ScenarioSimulationModel.Type.DMN),
                                                                                                                   eq("selected"),
                                                                                                                   remoteCallbackArgumentCaptor.capture());
        remoteCallbackArgumentCaptor.getValue().callback("");
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).gotoPath(eq(path));
        verify(scenarioSimulationEditorKogitoWrapperSpy, times(1)).unmarshallContent(isA(String.class));
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
}
