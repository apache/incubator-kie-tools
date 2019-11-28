/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.showcase.client.navigator.DMNVFSService;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditorTest;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.kogito.client.PromiseMock;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramEditorTest extends AbstractDMNDiagramEditorTest {

    private final String FILE_NAME = "file name.dmn";

    private final String CONTENT = "xml-content-of-dmn-file";

    @Mock
    private MenuItem saveMenuItem;

    @Mock
    private DeletePopUpPresenter deletePopUpPresenter;

    @Mock
    private CopyPopUpPresenter copyPopUpPresenter;

    @Mock
    private RenamePopUpPresenter renamePopUpPresenter;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    private BasicFileMenuBuilderImpl basicFileMenuBuilder;

    @Mock
    private DMNVFSService vfsService;

    @Mock
    private Promises promises;

    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;

    @Captor
    private ArgumentCaptor<ServiceCallback<String>> serviceCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;

    @Override
    public void setup() {
        super.setup();

        //Mock interaction of FileMenuBuilderImpl and BasicFileMenuBuilderImpl
        this.basicFileMenuBuilder = new BasicFileMenuBuilderImpl(deletePopUpPresenter,
                                                                 copyPopUpPresenter,
                                                                 renamePopUpPresenter,
                                                                 busyIndicatorView,
                                                                 notificationEventSourceMock,
                                                                 restoreVersionCommandProvider);
        doAnswer(i -> {
            DMNDiagramEditorTest.this.basicFileMenuBuilder.addSave(saveMenuItem);
            return fileMenuBuilder;
        }).when(fileMenuBuilder).addSave(any(Command.class));

        doAnswer(i -> basicFileMenuBuilder.build()).when(fileMenuBuilder).build();
    }

    @Override
    protected AbstractDMNDiagramEditor getEditor() {
        return new DMNDiagramEditor(view,
                                    fileMenuBuilder,
                                    placeManager,
                                    multiPageEditorContainerView,
                                    changeTitleWidgetEventSourceMock,
                                    notificationEventSourceMock,
                                    onDiagramFocusEventSourceMock,
                                    xmlEditorView,
                                    sessionEditorPresenters,
                                    sessionViewerPresenters,
                                    dmnEditorMenuSessionItems,
                                    errorPopupPresenter,
                                    diagramClientErrorHandler,
                                    clientTranslationService,
                                    documentationView,
                                    editorSearchIndex,
                                    searchBarComponent,
                                    sessionManager,
                                    sessionCommandManager,
                                    refreshFormPropertiesEventSourceMock,
                                    decisionNavigatorDock,
                                    diagramPropertiesDock,
                                    diagramPreviewDock,
                                    layoutHelper,
                                    layoutExecutor,
                                    dataTypesPage,
                                    includedModelsPage,
                                    importsPageProvider,
                                    clientDiagramService,
                                    vfsService,
                                    promises) {

            @Override
            protected PlaceRequest getPlaceRequest() {
                return place;
            }

            @Override
            protected ElementWrapperWidget<?> getWidget(final HTMLElement element) {
                return searchBarComponentWidget;
            }

            @Override
            protected void scheduleOnDataTypeEditModeToggleCallback(final DataTypeEditModeToggleEvent event) {
                //Override deferral to DomGlobal's timer for Unit Tests
                getOnDataTypeEditModeToggleCallback(event).onInvoke(event);
            }
        };
    }

    @Test
    @Override
    public void testOnStartup() {
        super.testOnStartup();

        verify(editor).setContent(eq(""));
    }

    @Test
    @Override
    public void testOnDataTypeEditModeToggleEnabled() {
        super.testOnDataTypeEditModeToggleEnabled();

        verify(saveMenuItem).setEnabled(eq(false));
    }

    @Test
    @Override
    public void testOnDataTypeEditModeToggleDisabled() {
        super.testOnDataTypeEditModeToggleDisabled();

        verify(saveMenuItem).setEnabled(eq(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDoSave() {
        final PromiseMock promise = new PromiseMock();
        doReturn(promise.asPromise()).when(editor).getContent();
        promise.then(() -> CONTENT);

        openDiagram();

        reset(view);

        verify(fileMenuBuilder).addSave(commandArgumentCaptor.capture());

        final Command command = commandArgumentCaptor.getValue();
        assertThat(command).isNotNull();

        command.execute();

        verify(vfsService).saveFile(pathArgumentCaptor.capture(),
                                    eq(CONTENT),
                                    serviceCallbackArgumentCaptor.capture());

        final Path path = pathArgumentCaptor.getValue();
        assertThat(path).isNotNull();
        assertThat(path).isEqualTo(metadata.getPath());

        final ServiceCallback<String> serviceCallback = serviceCallbackArgumentCaptor.getValue();
        assertThat(serviceCallback).isNotNull();

        serviceCallback.onSuccess(CONTENT);

        verify(editor).resetContentHash();
        verify(notificationEventSourceMock).fire(any(NotificationEvent.class));
        verify(view).hideBusyIndicator();
    }

    @Override
    public void testSetContentSuccess() {
        place.addParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, FILE_NAME);

        super.testSetContentSuccess();

        assertMetadataPath();
    }

    @Override
    protected void openDiagram() {
        place.addParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, FILE_NAME);

        super.openDiagram();

        assertMetadataPath();
    }

    private void assertMetadataPath() {
        final Path path = metadata.getPath();
        assertThat(path).isNotNull();
        assertThat(path.getFileName()).isEqualTo(FILE_NAME);
        assertThat(path.toURI()).isEqualTo(ROOT + "/" + URIUtil.encode(FILE_NAME));
    }
}
