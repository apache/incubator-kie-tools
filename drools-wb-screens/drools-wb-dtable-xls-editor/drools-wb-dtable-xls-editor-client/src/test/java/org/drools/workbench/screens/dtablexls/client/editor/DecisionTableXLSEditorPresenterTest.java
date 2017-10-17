/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.dtablexls.client.editor;

import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.screens.dtablexls.client.resources.DecisionTableXLSResources;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.drools.workbench.screens.dtablexls.client.resources.images.DecisionTableXLSImageResources;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSResourceType;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSXResourceType;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSContent;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ConcurrentChangePopup.class})
public class DecisionTableXLSEditorPresenterTest {

    @GwtMock
    DecisionTableXLSImageResources decisionTableXLSImageResources;

    @GwtMock
    DecisionTableXLSResources decisionTableXLSResources;

    @GwtMock
    DecisionTableXLSEditorConstants decisionTableXLSEditorConstants;

    @Mock
    DecisionTableXLSResourceType decisionTableXLSResourceType;

    @Mock
    DecisionTableXLSXResourceType decisionTableXLSXResourceType;

    @Mock
    ObservablePath XLSPath;

    @Mock
    ObservablePath XLSXPath;

    @Mock
    DecisionTableXLSEditorView view;

    @Mock
    BusyIndicatorView busyIndicatorView;

    @Mock
    KieEditorWrapperView kieView;

    @Mock
    VersionRecordManager versionRecordManagerMock;

    @Mock
    EventSourceMock<NotificationEvent> notification;

    @Mock
    ValidationPopup validationPopup;

    @Mock
    BasicFileMenuBuilder menuBuilder;

    @Mock
    VersionRecordManager versionRecordManager;

    @Spy
    @InjectMocks
    FileMenuBuilderImpl fileMenuBuilder;

    @Mock
    ProjectController projectController;

    @Mock
    ProjectContext workbenchContext;

    DecisionTableXLSEditorPresenter presenter;

    @Before
    public void setUp() throws Exception {

        when(decisionTableXLSResourceType.getSuffix()).thenReturn("XLS");
        when(decisionTableXLSResourceType.accept(XLSPath)).thenReturn(true);
        when(decisionTableXLSResourceType.accept(XLSXPath)).thenReturn(false);

        when(decisionTableXLSXResourceType.getSuffix()).thenReturn("XLSX");
        when(decisionTableXLSXResourceType.accept(XLSPath)).thenReturn(false);
        when(decisionTableXLSXResourceType.accept(XLSXPath)).thenReturn(true);

        presenter = spy(new DecisionTableXLSEditorPresenter(view,
                                                            decisionTableXLSResourceType,
                                                            decisionTableXLSXResourceType,
                                                            busyIndicatorView,
                                                            notification,
                                                            validationPopup,
                                                            new ServiceMock()
        ) {
            {
                kieView = mock(KieEditorWrapperView.class);
                overviewWidget = mock(OverviewWidgetPresenter.class);
                versionRecordManager = versionRecordManagerMock;
                concurrentUpdateSessionInfo = null;
                fileMenuBuilder = DecisionTableXLSEditorPresenterTest.this.fileMenuBuilder;
                projectController = DecisionTableXLSEditorPresenterTest.this.projectController;
                workbenchContext = DecisionTableXLSEditorPresenterTest.this.workbenchContext;
                versionRecordManager = DecisionTableXLSEditorPresenterTest.this.versionRecordManager;
            }

            protected void addSourcePage() {

            }
        });

        doReturn(mock(MenuItem.class)).when(presenter).getConvertMenu();
    }

    @Test
    public void testXLSSetup() throws Exception {
        presenter.onStartup(XLSPath,
                            mock(PlaceRequest.class));

        verify(view).init(presenter);
        verify(view).setupUploadWidget(decisionTableXLSResourceType);
        verify(view).setPath(any(Path.class));
        verify(view).setReadOnly(false);
    }

    @Test
    public void testXLSXSetup() throws Exception {
        presenter.onStartup(XLSXPath,
                            mock(PlaceRequest.class));

        verify(view).init(presenter);
        verify(view).setupUploadWidget(decisionTableXLSXResourceType);
        verify(view).setPath(any(Path.class));
        verify(view).setReadOnly(false);
    }

    @Test
    public void testOnUploadWhenConcurrentUpdateSessionInfoIsNull() {
        presenter.onUpload();

        verify(view).submit(versionRecordManagerMock.getCurrentPath());

        assertNull(presenter.getConcurrentUpdateSessionInfo());
    }

    @Test
    public void testOnUploadWhenConcurrentUpdateSessionInfoIsNotNull() {
        presenter = spy(new DecisionTableXLSEditorPresenter(null,
                                                            null,
                                                            null,
                                                            busyIndicatorView,
                                                            null,
                                                            null,
                                                            null) {
            {
                concurrentUpdateSessionInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
            }
        });

        presenter.onUpload();

        verify(busyIndicatorView).hideBusyIndicator();
        verify(presenter).showConcurrentUpdateError();
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class),
                                  any(AssetUpdateValidator.class));
    }

    private class ServiceMock
            implements Caller<DecisionTableXLSService> {

        private DecisionTableXLSService decisionTableXLSService = new DecisionTableXLSServiceMock();
        RemoteCallback remoteCallback;

        @Override
        public DecisionTableXLSService call() {
            return decisionTableXLSService;
        }

        @Override
        public DecisionTableXLSService call(RemoteCallback<?> remoteCallback) {
            return call(remoteCallback,
                        null);
        }

        @Override
        public DecisionTableXLSService call(RemoteCallback<?> remoteCallback,
                                            ErrorCallback<?> errorCallback) {
            this.remoteCallback = remoteCallback;
            return decisionTableXLSService;
        }

        private class DecisionTableXLSServiceMock
                implements DecisionTableXLSService {

            @Override
            public ConversionResult convert(Path path) {
                return null;
            }

            @Override
            public DecisionTableXLSContent loadContent(Path path) {
                DecisionTableXLSContent content = new DecisionTableXLSContent();
                content.setOverview(new Overview());
                remoteCallback.callback(content);
                return null;
            }

            @Override
            public String getSource(Path path) {
                return null;
            }

            @Override
            public Path copy(Path path,
                             String newName,
                             String comment) {
                return null;
            }

            @Override
            public Path copy(Path path,
                             String newName,
                             Path targetDirectory,
                             String comment) {
                return null;
            }

            @Override
            public void delete(Path path,
                               String comment) {

            }

            @Override
            public Path rename(Path path,
                               String newName,
                               String comment) {
                return null;
            }

            @Override
            public List<ValidationMessage> validate(Path path,
                                                    Path content) {
                return null;
            }
        }
    }
}