/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.ClientSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.impl.AbstractClientSessionPresenter;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.command.impl.*;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.client.util.ClientSessionUtils;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ProjectDiagramEditorTest {

    @Mock
    EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;
    @Mock
    EventSourceMock<NotificationEvent> notification;
    @Mock
    VersionRecordManager versionRecordManager;
    @Mock
    BasicFileMenuBuilder menuBuilder;
    @Mock
    DefaultFileNameValidator fileNameValidator;
    @Mock
    PlaceRequest placeRequest;
    @Mock
    AbstractProjectDiagramEditor.View view;
    @Mock
    PlaceManager placeManager;
    @Mock
    ErrorPopupPresenter errorPopupPresenter;
    @Mock
    EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    @Mock
    SavePopUpPresenter savePopUpPresenter;
    @Mock
    ClientResourceType resourceType;
    @Mock
    ClientProjectDiagramService projectDiagramServices;
    @Mock
    AbstractClientSessionManager clientSessionManager;
    @Mock
    AbstractClientSessionPresenter clientSessionPresenter;
    @Mock
    BS3PaletteFactory paletteFactory;
    @Mock
    ClientSessionUtils sessionUtils;
    @Mock
    SessionCommandFactory sessionCommandFactory;
    @Mock
    ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder;

    @Mock
    ClearSelectionSessionCommand sessionClearSelectionCommand;
    @Mock
    VisitGraphSessionCommand sessionVisitGraphCommand;
    @Mock
    SwitchGridSessionCommand sessionSwitchGridCommand;
    @Mock
    ClearSessionCommand sessionClearCommand;
    @Mock
    DeleteSelectionSessionCommand sessionDeleteSelectionCommand;
    @Mock
    UndoSessionCommand sessionUndoCommand;
    @Mock
    RedoSessionCommand sessionRedoCommand;
    @Mock
    ValidateSessionCommand sessionValidateCommand;
    @Mock
    RefreshSessionCommand sessionRefreshCommand;

    @Mock
    AbstractClientFullSession fullSession;
    @Mock
    ClientSessionPresenter.View clientSessionPresenterView;
    @Mock
    ObservablePath path;

    private ProjectDiagramEditorStub tested;

    @Before
    public void setup() throws Exception {
        when( versionRecordManager.getCurrentPath() ).thenReturn( path );
        when( sessionCommandFactory.newClearCommand() ).thenReturn( sessionClearCommand );
        when( sessionCommandFactory.newClearSelectionCommand() ).thenReturn( sessionClearSelectionCommand );
        when( sessionCommandFactory.newVisitGraphCommand() ).thenReturn( sessionVisitGraphCommand );
        when( sessionCommandFactory.newSwitchGridCommand() ).thenReturn( sessionSwitchGridCommand );
        when( sessionCommandFactory.newDeleteSelectedElementsCommand() ).thenReturn( sessionDeleteSelectionCommand );
        when( sessionCommandFactory.newUndoCommand() ).thenReturn( sessionUndoCommand );
        when( sessionCommandFactory.newRedoCommand() ).thenReturn( sessionRedoCommand );
        when( sessionCommandFactory.newValidateCommand() ).thenReturn( sessionValidateCommand );
        when( sessionCommandFactory.newRefreshSessionCommand() ).thenReturn( sessionRefreshCommand );
        when( clientSessionManager.newFullSession() ).thenReturn( fullSession );
        when( clientSessionPresenter.getView() ).thenReturn( clientSessionPresenterView );
        when( clientSessionPresenter.setDisplayErrors( anyBoolean() ) ).thenReturn( clientSessionPresenter );
        this.tested = new ProjectDiagramEditorStub( view, placeManager, errorPopupPresenter,
                changeTitleNotificationEvent, savePopUpPresenter, resourceType, projectDiagramServices,
                clientSessionManager, clientSessionPresenter, paletteFactory, sessionUtils,
                sessionCommandFactory, menuItemsBuilder );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testInit() {
        tested.init();
        verify( clientSessionPresenter, times( 1 ) ).initialize( eq( fullSession ), anyInt(), anyInt() );
        verify( view, times( 1 ) ).init( eq( tested ) );
        verify( view, times( 1 ) ).setWidget( eq( clientSessionPresenterView ) );
        verify( sessionClearSelectionCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionVisitGraphCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionSwitchGridCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionClearCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionDeleteSelectionCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionUndoCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionRedoCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionValidateCommand, times( 1 ) ).bind( eq( fullSession ) );
        verify( sessionRefreshCommand, times( 1 ) ).bind( eq( fullSession ) );
    }

    // TODO: @Test - versionRecordManager is not being set.
    @SuppressWarnings( "unchecked" )
    public void testLoadContent() {
        tested.loadContent();
        verify( projectDiagramServices, times( 1 ) ).getByPath( eq( path ), any( ServiceCallback.class ) );
    }

}
