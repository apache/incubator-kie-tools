/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.menu.SaveAllMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class KieMultipleDocumentEditorTest {

    private TestMultipleDocumentEditor editor;

    @Mock
    private KieEditorView editorView;

    @Mock
    private KieMultipleDocumentEditorWrapperView kieEditorWrapperView;

    @Mock
    private OverviewWidgetPresenter overviewWidget;

    @Mock
    private ImportsWidgetPresenter importsWidget;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleEvent;

    @Mock
    private ProjectContext workbenchContext;

    @Mock
    private VersionRecordManager versionRecordManager;

    @Mock
    private VersionService versionService;
    private CallerMock<VersionService> versionServiceCaller;

    @Mock
    private EventSourceMock<RestoreEvent> restoreEvent;

    @Spy
    private RestoreVersionCommandProvider restoreVersionCommandProvider = getRestoreVersionCommandProvider();

    @Spy
    private BasicFileMenuBuilder basicFileMenuBuilder = getBasicFileMenuBuilder();

    @Spy
    private FileMenuBuilder fileMenuBuilder = getFileMenuBuilder();

    @Mock
    private SaveAllMenuBuilder saveAllMenuBuilder;

    @Mock
    private RegisteredDocumentsMenuBuilder registeredDocumentsMenuBuilder;

    @Mock
    private DefaultFileNameValidator fileNameValidator;

    @Mock
    private MenuItem saveMenuItem;

    @Mock
    private MenuItem versionManagerMenuItem;

    @Mock
    private User user;

    private Command concurrentRenameCommand;
    private Command concurrentDeleteCommand;

    @Before
    public void setup() {
        concurrentRenameCommand = null;
        concurrentDeleteCommand = null;

        versionServiceCaller = new CallerMock<>( versionService );

        final TestMultipleDocumentEditor wrapped = new TestMultipleDocumentEditor( editorView ) {
            @Override
            void doConcurrentRename( final TestDocument document,
                                     final ObservablePath.OnConcurrentRenameEvent info ) {
                if ( concurrentRenameCommand != null ) {
                    concurrentRenameCommand.execute();
                }
            }

            @Override
            void doConcurrentDelete( final TestDocument document,
                                     final ObservablePath.OnConcurrentDelete info ) {
                if ( concurrentDeleteCommand != null ) {
                    concurrentDeleteCommand.execute();
                }
            }

            @Override
            void doSave( final TestDocument document ) {
                super.getSaveCommand( document ).execute( "commit" );
            }
        };
        wrapped.setKieEditorWrapperView( kieEditorWrapperView );
        wrapped.setOverviewWidget( overviewWidget );
        wrapped.setImportsWidget( importsWidget );
        wrapped.setNotificationEvent( notificationEvent );
        wrapped.setChangeTitleEvent( changeTitleEvent );
        wrapped.setWorkbenchContext( workbenchContext );
        wrapped.setVersionRecordManager( versionRecordManager );
        wrapped.setRegisteredDocumentsMenuBuilder( registeredDocumentsMenuBuilder );
        wrapped.setFileMenuBuilder( fileMenuBuilder );
        wrapped.setSaveAllMenuBuilder( saveAllMenuBuilder );
        wrapped.setFileNameValidator( fileNameValidator );

        this.editor = spy( wrapped );

        when( versionRecordManager.newSaveMenuItem( any( Command.class ) ) ).thenReturn( saveMenuItem );
        when( versionRecordManager.buildMenu() ).thenReturn( versionManagerMenuItem );
    }

    @Test
    public void testSetupMenuBar() {
        editor.setupMenuBar();

        verify( fileMenuBuilder,
                times( 1 ) ).addSave( any( MenuItem.class ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addCopy( any( BasicFileMenuBuilder.PathProvider.class ),
                                      eq( fileNameValidator ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addRename( any( BasicFileMenuBuilder.PathProvider.class ),
                                        eq( fileNameValidator ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addDelete( any( BasicFileMenuBuilder.PathProvider.class ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addValidate( any( Command.class ) );
        verify( fileMenuBuilder,
                times( 3 ) ).addNewTopLevelMenu( any( MenuItem.class ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullDocument() {
        registerDocument( null );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterDocument() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument( document );

        verify( path,
                times( 1 ) ).onRename( any( Command.class ) );
        verify( path,
                times( 1 ) ).onConcurrentRename( any( ParameterizedCommand.class ) );
        verify( path,
                times( 1 ) ).onDelete( any( Command.class ) );
        verify( path,
                times( 1 ) ).onConcurrentDelete( any( ParameterizedCommand.class ) );
        verify( path,
                times( 1 ) ).onConcurrentUpdate( any( ParameterizedCommand.class ) );
        verify( registeredDocumentsMenuBuilder,
                times( 1 ) ).registerDocument( document );
    }

    @Test
    public void testRegisterDocumentAlreadyRegistered() {
        final TestDocument document = createTestDocument();
        registerDocument( document );
        registerDocument( document );

        assertEquals( 1,
                      editor.documents.size() );
    }

    @Test
    public void testRenameCommand() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument( document );

        final ArgumentCaptor<Command> renameCommandCaptor = ArgumentCaptor.forClass( Command.class );
        verify( path,
                times( 1 ) ).onRename( renameCommandCaptor.capture() );
        final Command renameCommand = renameCommandCaptor.getValue();
        assertNotNull( renameCommand );
        renameCommand.execute();

        verify( editorView,
                times( 2 ) ).refreshTitle( any( String.class ) );
        verify( editorView,
                times( 1 ) ).showBusyIndicator( any( String.class ) );
        verify( editor,
                times( 2 ) ).getDocumentTitle( eq( document ) );
        verify( editor,
                times( 1 ) ).refreshDocument( eq( document ) );
        verify( changeTitleEvent,
                times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentRenameCommandIgnore() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentRenameCommand = editor.getConcurrentRenameOnIgnoreCommand();

        editor.setupMenuBar();
        registerDocument( document );

        final ArgumentCaptor<ParameterizedCommand> renameCommandCaptor = ArgumentCaptor.forClass( ParameterizedCommand.class );
        verify( path,
                times( 1 ) ).onConcurrentRename( renameCommandCaptor.capture() );
        final ParameterizedCommand renameCommand = renameCommandCaptor.getValue();
        assertNotNull( renameCommand );

        final ObservablePath.OnConcurrentRenameEvent info = mock( ObservablePath.OnConcurrentRenameEvent.class );
        renameCommand.execute( info );

        verify( editor,
                times( 1 ) ).enableMenus( eq( false ) );
        verify( editor,
                times( 4 ) ).enableMenuItem( eq( false ),
                                             any( MenuItems.class ) );
        verify( saveMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
        verify( versionManagerMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentRenameCommandReopen() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentRenameCommand = editor.getConcurrentRenameOnReopenCommand( document );

        editor.setupMenuBar();
        registerDocument( document );

        final ArgumentCaptor<ParameterizedCommand> renameCommandCaptor = ArgumentCaptor.forClass( ParameterizedCommand.class );
        verify( path,
                times( 1 ) ).onConcurrentRename( renameCommandCaptor.capture() );
        final ParameterizedCommand renameCommand = renameCommandCaptor.getValue();
        assertNotNull( renameCommand );

        final ObservablePath.OnConcurrentRenameEvent info = mock( ObservablePath.OnConcurrentRenameEvent.class );
        renameCommand.execute( info );

        verify( document,
                times( 1 ) ).setConcurrentUpdateSessionInfo( eq( null ) );
        verify( editorView,
                times( 2 ) ).refreshTitle( any( String.class ) );
        verify( editorView,
                times( 1 ) ).showBusyIndicator( any( String.class ) );
        verify( editor,
                times( 2 ) ).getDocumentTitle( eq( document ) );
        verify( editor,
                times( 1 ) ).refreshDocument( eq( document ) );
        verify( changeTitleEvent,
                times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
    }

    @Test
    public void testDeleteCommand() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        editor.setupMenuBar();
        registerDocument( document );

        final ArgumentCaptor<Command> deleteCommandCaptor = ArgumentCaptor.forClass( Command.class );
        verify( path,
                times( 1 ) ).onDelete( deleteCommandCaptor.capture() );
        final Command deleteCommand = deleteCommandCaptor.getValue();
        assertNotNull( deleteCommand );
        deleteCommand.execute();

        verify( editor,
                times( 1 ) ).enableMenus( eq( false ) );
        verify( editor,
                times( 4 ) ).enableMenuItem( eq( false ),
                                             any( MenuItems.class ) );
        verify( saveMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
        verify( versionManagerMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
        verify( editor,
                times( 1 ) ).removeDocument( eq( document ) );
        verify( registeredDocumentsMenuBuilder,
                times( 1 ) ).deregisterDocument( document );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentDeleteCommandIgnore() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentDeleteCommand = editor.getConcurrentDeleteOnIgnoreCommand();

        editor.setupMenuBar();
        registerDocument( document );

        final ArgumentCaptor<ParameterizedCommand> deleteCommandCaptor = ArgumentCaptor.forClass( ParameterizedCommand.class );
        verify( path,
                times( 1 ) ).onConcurrentDelete( deleteCommandCaptor.capture() );
        final ParameterizedCommand deleteCommand = deleteCommandCaptor.getValue();
        assertNotNull( deleteCommand );

        final ObservablePath.OnConcurrentDelete info = mock( ObservablePath.OnConcurrentDelete.class );
        deleteCommand.execute( info );

        verify( editor,
                times( 1 ) ).enableMenus( eq( false ) );
        verify( editor,
                times( 4 ) ).enableMenuItem( eq( false ),
                                             any( MenuItems.class ) );
        verify( saveMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
        verify( versionManagerMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentDeleteCommandClose() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentDeleteCommand = editor.getConcurrentDeleteOnClose( document );

        editor.setupMenuBar();
        registerDocument( document );

        final ArgumentCaptor<ParameterizedCommand> deleteCommandCaptor = ArgumentCaptor.forClass( ParameterizedCommand.class );
        verify( path,
                times( 1 ) ).onConcurrentDelete( deleteCommandCaptor.capture() );
        final ParameterizedCommand deleteCommand = deleteCommandCaptor.getValue();
        assertNotNull( deleteCommand );

        final ObservablePath.OnConcurrentDelete info = mock( ObservablePath.OnConcurrentDelete.class );
        deleteCommand.execute( info );

        verify( editor,
                times( 1 ) ).enableMenus( eq( false ) );
        verify( editor,
                times( 4 ) ).enableMenuItem( eq( false ),
                                             any( MenuItems.class ) );
        verify( saveMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
        verify( versionManagerMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
        verify( editor,
                times( 1 ) ).removeDocument( eq( document ) );
        verify( registeredDocumentsMenuBuilder,
                times( 1 ) ).deregisterDocument( document );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentUpdate() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument( document );

        final ArgumentCaptor<ParameterizedCommand> updateCommandCaptor = ArgumentCaptor.forClass( ParameterizedCommand.class );
        verify( path,
                times( 1 ) ).onConcurrentUpdate( updateCommandCaptor.capture() );
        final ParameterizedCommand updateCommand = updateCommandCaptor.getValue();
        assertNotNull( updateCommand );

        final ObservablePath.OnConcurrentUpdateEvent info = mock( ObservablePath.OnConcurrentUpdateEvent.class );
        updateCommand.execute( info );

        verify( document,
                times( 1 ) ).setConcurrentUpdateSessionInfo( eq( info ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeregisterNullDocument() {
        editor.deregisterDocument( null );
    }

    @Test()
    public void testDeregisterDocument() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument( document );
        editor.deregisterDocument( document );

        verify( path,
                times( 1 ) ).dispose();
        verify( registeredDocumentsMenuBuilder,
                times( 1 ) ).deregisterDocument( document );
    }

    @Test
    public void testDeregisterDocumentNotRegistered() {
        final TestDocument document = createTestDocument();

        editor.deregisterDocument( document );

        assertEquals( 0,
                      editor.documents.size() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullDocument() {
        editor.activateDocument( null,
                                 mock( Overview.class ),
                                 mock( AsyncPackageDataModelOracle.class ),
                                 mock( Imports.class ),
                                 false );

        verify( editor,
                never() ).initialiseVersionManager( any( TestDocument.class ) );
        verify( editor,
                never() ).initialiseKieEditorTabs( any( TestDocument.class ),
                                                   any( Overview.class ),
                                                   any( AsyncPackageDataModelOracle.class ),
                                                   any( Imports.class ),
                                                   any( Boolean.class ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullOverview() {
        editor.activateDocument( createTestDocument(),
                                 null,
                                 mock( AsyncPackageDataModelOracle.class ),
                                 mock( Imports.class ),
                                 false );

        verify( editor,
                never() ).initialiseVersionManager( any( TestDocument.class ) );
        verify( editor,
                never() ).initialiseKieEditorTabs( any( TestDocument.class ),
                                                   any( Overview.class ),
                                                   any( AsyncPackageDataModelOracle.class ),
                                                   any( Imports.class ),
                                                   any( Boolean.class ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullAsyncPackageDataModelOracle() {
        editor.activateDocument( createTestDocument(),
                                 mock( Overview.class ),
                                 null,
                                 mock( Imports.class ),
                                 false );

        verify( editor,
                never() ).initialiseVersionManager( any( TestDocument.class ) );
        verify( editor,
                never() ).initialiseKieEditorTabs( any( TestDocument.class ),
                                                   any( Overview.class ),
                                                   any( AsyncPackageDataModelOracle.class ),
                                                   any( Imports.class ),
                                                   any( Boolean.class ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullImports() {
        editor.activateDocument( createTestDocument(),
                                 mock( Overview.class ),
                                 mock( AsyncPackageDataModelOracle.class ),
                                 null,
                                 false );

        verify( editor,
                never() ).initialiseVersionManager( any( TestDocument.class ) );
        verify( editor,
                never() ).initialiseKieEditorTabs( any( TestDocument.class ),
                                                   any( Overview.class ),
                                                   any( AsyncPackageDataModelOracle.class ),
                                                   any( Imports.class ),
                                                   any( Boolean.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testActivateDocument() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        final Overview overview = mock( Overview.class );
        final AsyncPackageDataModelOracle dmo = mock( AsyncPackageDataModelOracle.class );
        final Imports imports = mock( Imports.class );
        final boolean isReadOnly = false;

        registerDocument( document );
        editor.activateDocument( document,
                                 overview,
                                 dmo,
                                 imports,
                                 isReadOnly );

        assertEquals( document,
                      editor.getActiveDocument() );
        verify( versionRecordManager,
                times( 1 ) ).init( eq( null ),
                                   eq( path ),
                                   any( Callback.class ) );
        verify( kieEditorWrapperView,
                times( 1 ) ).clear();
        verify( kieEditorWrapperView,
                times( 1 ) ).addMainEditorPage( eq( editorView ) );
        verify( kieEditorWrapperView,
                times( 1 ) ).addOverviewPage( eq( overviewWidget ),
                                              any( com.google.gwt.user.client.Command.class ) );
        verify( kieEditorWrapperView,
                times( 1 ) ).addSourcePage( eq( editor.sourceWidget ) );
        verify( kieEditorWrapperView,
                times( 1 ) ).addImportsTab( eq( importsWidget ) );
        verify( overviewWidget,
                times( 1 ) ).setContent( eq( overview ),
                                         eq( path ) );
        verify( importsWidget,
                times( 1 ) ).setContent( eq( dmo ),
                                         eq( imports ),
                                         eq( isReadOnly ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocumentNotRegistered() {
        final TestDocument document = createTestDocument();

        activateDocument( document );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVersionRecordManagerSelectionChange() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument( document );
        activateDocument( document );

        final ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass( Callback.class );
        verify( versionRecordManager,
                times( 1 ) ).init( eq( null ),
                                   eq( path ),
                                   callbackArgumentCaptor.capture() );

        final Callback<VersionRecord> callback = callbackArgumentCaptor.getValue();
        assertNotNull( callback );

        final VersionRecord versionRecord = mock( VersionRecord.class );
        final ObservablePath newPath = mock( ObservablePath.class );
        when( versionRecord.id() ).thenReturn( "id" );
        when( versionRecordManager.getCurrentPath() ).thenReturn( newPath );
        when( versionRecordManager.isLatest( versionRecord ) ).thenReturn( false );
        callback.callback( versionRecord );

        verify( versionRecordManager,
                times( 1 ) ).setVersion( eq( "id" ) );
        verify( document,
                times( 1 ) ).setVersion( eq( "id" ) );
        verify( document,
                times( 1 ) ).setCurrentPath( eq( newPath ) );
        verify( document,
                times( 1 ) ).setReadOnly( eq( true ) );
        verify( editor,
                times( 1 ) ).refreshDocument( document );
    }

    @Test
    public void testGetWidget() {
        editor.getWidget();
        verify( kieEditorWrapperView,
                times( 1 ) ).asWidget();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTitleWidgetNullDocument() {
        editor.getTitleWidget( null );

        verify( editorView,
                never() ).refreshTitle( any( String.class ) );
        verify( editorView,
                never() ).getTitleWidget();
    }

    @Test
    public void testGetTitleWidget() {
        final TestDocument document = createTestDocument();

        editor.getTitleWidget( document );

        verify( editorView,
                times( 1 ) ).refreshTitle( any( String.class ) );
        verify( editorView,
                times( 1 ) ).getTitleWidget();
    }

    @Test
    public void testMayCloseChange() {
        final TestDocument document = createTestDocument();

        registerDocument( document );
        activateDocument( document );

        editor.mayClose( 0,
                         null );
        verify( editorView,
                times( 1 ) ).confirmClose();
    }

    @Test
    public void testMayCloseNoChange() {
        final TestDocument document = createTestDocument();

        registerDocument( document );
        activateDocument( document );

        editor.mayClose( null,
                         null );
        verify( editorView,
                never() ).confirmClose();
    }

    @Test
    public void testOnClose() {
        editor.onClose();

        verify( versionRecordManager,
                times( 1 ) ).clear();
    }

    @Test
    public void testUpdateSource() {
        editor.updateSource( "source" );

        verify( editor.sourceWidget,
                times( 1 ) ).setContent( eq( "source" ) );
    }

    @Test
    public void testOnSourceTabSelected() {
        final TestDocument document = createTestDocument();

        registerDocument( document );
        activateDocument( document );

        editor.onSourceTabSelected();

        verify( editor,
                times( 1 ) ).onSourceTabSelected( eq( document ) );
    }

    @Test
    public void testGetSaveMenuItem() {
        editor.getSaveMenuItem();
        editor.getSaveMenuItem();

        verify( versionRecordManager,
                times( 1 ) ).newSaveMenuItem( any( Command.class ) );
    }

    @Test
    public void testSave() {
        final TestDocument document = createTestDocument();
        registerDocument( document );
        activateDocument( document );

        editor.getSaveMenuItem();

        final ArgumentCaptor<Command> saveCommandCaptor = ArgumentCaptor.forClass( Command.class );
        verify( versionRecordManager,
                times( 1 ) ).newSaveMenuItem( saveCommandCaptor.capture() );

        final Command saveCommand = saveCommandCaptor.getValue();
        assertNotNull( saveCommand );
        saveCommand.execute();

        verify( editorView,
                times( 1 ) ).showSaving();
        verify( editor,
                times( 1 ) ).doSave( eq( document ) );
        verify( editor,
                times( 1 ) ).doSaveCheckForAndHandleConcurrentUpdate( eq( document ) );
        verify( editor,
                times( 1 ) ).onSave( eq( document ),
                                     eq( "commit" ) );
        verify( document,
                times( 1 ) ).setConcurrentUpdateSessionInfo( eq( null ) );
    }

    @Test
    public void testSaveAll() {
        final TestDocument document1 = createTestDocument();
        final TestDocument document2 = createTestDocument();
        registerDocument( document1 );
        registerDocument( document2 );

        editor.doSaveAll();

        verify( editorView,
                times( 2 ) ).showSaving();

        verify( editor,
                times( 1 ) ).doSave( eq( document1 ) );
        verify( editor,
                times( 1 ) ).doSaveCheckForAndHandleConcurrentUpdate( eq( document1 ) );
        verify( editor,
                times( 1 ) ).onSave( eq( document1 ),
                                     any( String.class ) );
        verify( document1,
                times( 1 ) ).setConcurrentUpdateSessionInfo( eq( null ) );

        verify( editor,
                times( 1 ) ).doSave( eq( document2 ) );
        verify( editor,
                times( 1 ) ).doSaveCheckForAndHandleConcurrentUpdate( eq( document2 ) );
        verify( editor,
                times( 1 ) ).onSave( eq( document2 ),
                                     any( String.class ) );
        verify( document2,
                times( 1 ) ).setConcurrentUpdateSessionInfo( eq( null ) );
    }

    @Test
    public void testSaveAllWithOneDocumentReadOnly() {
        final TestDocument document1 = createTestDocument();
        final TestDocument document2 = createTestDocument();
        registerDocument( document1 );
        registerDocument( document2 );

        when( document1.isReadOnly() ).thenReturn( true );
        when( document2.isReadOnly() ).thenReturn( false );

        editor.doSaveAll();

        verify( editorView,
                times( 1 ) ).showSaving();

        verify( editor,
                never() ).doSave( eq( document1 ) );
        verify( editor,
                never() ).doSaveCheckForAndHandleConcurrentUpdate( eq( document1 ) );
        verify( editor,
                never() ).onSave( eq( document1 ),
                                  any( String.class ) );
        verify( document1,
                never() ).setConcurrentUpdateSessionInfo( eq( null ) );

        verify( editor,
                times( 1 ) ).doSave( eq( document2 ) );
        verify( editor,
                times( 1 ) ).doSaveCheckForAndHandleConcurrentUpdate( eq( document2 ) );
        verify( editor,
                times( 1 ) ).onSave( eq( document2 ),
                                     any( String.class ) );
        verify( document2,
                times( 1 ) ).setConcurrentUpdateSessionInfo( eq( null ) );
    }

    @Test
    public void testSaveAllWithOneDocumentConcurrentUpdate() {
        final TestDocument document1 = createTestDocument();
        final TestDocument document2 = createTestDocument();
        registerDocument( document1 );
        registerDocument( document2 );

        doNothing().when( editor ).showConcurrentUpdatePopup( any( TestDocument.class ) );
        when( document1.getConcurrentUpdateSessionInfo() ).thenReturn( mock( ObservablePath.OnConcurrentUpdateEvent.class ) );
        when( document2.getConcurrentUpdateSessionInfo() ).thenReturn( null );

        editor.doSaveAll();

        verify( editorView,
                times( 1 ) ).showSaving();

        verify( editor,
                never() ).doSave( eq( document1 ) );
        verify( editor,
                times( 1 ) ).doSaveCheckForAndHandleConcurrentUpdate( eq( document1 ) );
        verify( editor,
                times( 1 ) ).showConcurrentUpdatePopup( eq( document1 ) );
        verify( editor,
                never() ).onSave( eq( document1 ),
                                  any( String.class ) );

        verify( editor,
                times( 1 ) ).doSave( eq( document2 ) );
        verify( editor,
                times( 1 ) ).doSaveCheckForAndHandleConcurrentUpdate( eq( document2 ) );
        verify( editor,
                times( 1 ) ).onSave( eq( document2 ),
                                     any( String.class ) );
        verify( document2,
                times( 1 ) ).setConcurrentUpdateSessionInfo( eq( null ) );
    }

    @Test
    public void testDoSaveCheckForAndHandleConcurrentUpdate_NoConcurrentUpdate() {
        final TestDocument document = createTestDocument();

        editor.doSaveCheckForAndHandleConcurrentUpdate( document );

        verify( editor,
                times( 1 ) ).doSave( eq( document ) );
    }

    @Test
    public void testDoSaveCheckForAndHandleConcurrentUpdate_ConcurrentUpdate() {
        final TestDocument document = createTestDocument();
        when( document.getConcurrentUpdateSessionInfo() ).thenReturn( mock( ObservablePath.OnConcurrentUpdateEvent.class ) );
        doNothing().when( editor ).showConcurrentUpdatePopup( any( TestDocument.class ) );

        editor.doSaveCheckForAndHandleConcurrentUpdate( document );

        verify( editor,
                times( 1 ) ).showConcurrentUpdatePopup( eq( document ) );
        verify( editor,
                never() ).doSave( eq( document ) );
    }

    @Test
    public void testGetVersionManagerMenuItem() {
        editor.getVersionManagerMenuItem();
        editor.getVersionManagerMenuItem();

        verify( versionRecordManager,
                times( 1 ) ).buildMenu();
    }

    @Test
    public void testOnRestore() {
        final TestDocument document = createTestDocument();
        final ObservablePath currentPath = document.getCurrentPath();
        final ObservablePath latestPath = mock( ObservablePath.class );
        registerDocument( document );
        activateDocument( document );

        when( versionRecordManager.getCurrentPath() ).thenReturn( currentPath );
        when( versionRecordManager.getPathToLatest() ).thenReturn( latestPath );

        editor.onRestore( new RestoreEvent( currentPath ) );

        verify( document,
                times( 1 ) ).setVersion( eq( null ) );
        verify( document,
                times( 1 ) ).setLatestPath( latestPath );
        verify( document,
                times( 1 ) ).setCurrentPath( latestPath );
        verify( editor,
                times( 2 ) ).initialiseVersionManager( eq( document ) );
        verify( editor,
                times( 1 ) ).refreshDocument( eq( document ) );
        verify( notificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }

    @Test
    public void testOnRepositoryRemoved() {
        final Repository repository = mock( Repository.class );
        when( workbenchContext.getActiveRepository() ).thenReturn( repository );

        editor.setupMenuBar();

        editor.onRepositoryRemoved( new RepositoryRemovedEvent( repository ) );

        verify( editor,
                times( 1 ) ).enableMenus( eq( false ) );
        verify( editor,
                times( 4 ) ).enableMenuItem( eq( false ),
                                             any( MenuItems.class ) );
        verify( saveMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
        verify( versionManagerMenuItem,
                times( 1 ) ).setEnabled( eq( false ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenDocumentInEditor_NoDocuments() {
        //Mock no other documents being available.
        doAnswer( ( invocation ) -> {
            final Callback<List<Path>> callback = (Callback) invocation.getArguments()[ 0 ];
            callback.callback( Collections.emptyList() );
            return null;
        } ).when( editor ).getAvailableDocumentPaths( any( Callback.class ) );

        editor.openDocumentInEditor();

        verify( kieEditorWrapperView,
                times( 1 ) ).showNoAdditionalDocuments();
        verify( kieEditorWrapperView,
                never() ).showAdditionalDocuments( any( List.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenDocumentInEditor_OneDocumentAlreadyRegistered() {
        //Mock one document being available, but it's the same as already registered.
        final TestDocument document = createTestDocument();
        final ObservablePath currentPath = document.getCurrentPath();
        registerDocument( document );

        doAnswer( ( invocation ) -> {
            final Callback<List<Path>> callback = (Callback) invocation.getArguments()[ 0 ];
            callback.callback( new ArrayList<Path>() {{
                add( currentPath );
            }} );
            return null;
        } ).when( editor ).getAvailableDocumentPaths( any( Callback.class ) );

        editor.openDocumentInEditor();

        verify( kieEditorWrapperView,
                times( 1 ) ).showNoAdditionalDocuments();
        verify( kieEditorWrapperView,
                never() ).showAdditionalDocuments( any( List.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenDocumentInEditor_OneDocumentNotAlreadyRegistered() {
        //Mock rtwo documents being available, one is already registered; the other is not.
        final TestDocument document = createTestDocument();
        final ObservablePath currentPath = document.getCurrentPath();
        registerDocument( document );

        final Path newDocumentPath = mock( Path.class );

        doAnswer( ( invocation ) -> {
            final Callback<List<Path>> callback = (Callback) invocation.getArguments()[ 0 ];
            callback.callback( new ArrayList<Path>() {{
                add( currentPath );
                add( newDocumentPath );
            }} );
            return null;
        } ).when( editor ).getAvailableDocumentPaths( any( Callback.class ) );

        editor.openDocumentInEditor();

        final ArgumentCaptor<List> pathsArgumentCaptor = ArgumentCaptor.forClass( List.class );
        verify( kieEditorWrapperView,
                times( 1 ) ).showAdditionalDocuments( pathsArgumentCaptor.capture() );

        final List<Path> paths = pathsArgumentCaptor.getValue();
        assertNotNull( paths );
        assertEquals( 1,
                      paths.size() );
        assertEquals( newDocumentPath,
                      paths.get( 0 ) );
    }

    private TestDocument createTestDocument() {
        final ObservablePath path = mock( ObservablePath.class );
        final PlaceRequest placeRequest = mock( PlaceRequest.class );
        return spy( new TestDocument( path,
                                      placeRequest ) );
    }

    private void registerDocument( final TestDocument document ) {
        editor.registerDocument( document );
    }

    private void activateDocument( final TestDocument document ) {
        final Overview overview = mock( Overview.class );
        final AsyncPackageDataModelOracle dmo = mock( AsyncPackageDataModelOracle.class );
        final Imports imports = mock( Imports.class );
        final boolean isReadOnly = false;

        editor.activateDocument( document,
                                 overview,
                                 dmo,
                                 imports,
                                 isReadOnly );
    }

    private RestoreVersionCommandProvider getRestoreVersionCommandProvider() {
        final RestoreVersionCommandProvider restoreVersionCommandProvider = new RestoreVersionCommandProvider();
        setField( restoreVersionCommandProvider,
                  "versionService",
                  versionServiceCaller );
        setField( restoreVersionCommandProvider,
                  "restoreEvent",
                  restoreEvent );
        setField( restoreVersionCommandProvider,
                  "busyIndicatorView",
                  editorView );
        return restoreVersionCommandProvider;
    }

    private BasicFileMenuBuilder getBasicFileMenuBuilder() {
        final BasicFileMenuBuilder basicFileMenuBuilder = new BasicFileMenuBuilderImpl();
        setField( basicFileMenuBuilder,
                  "restoreVersionCommandProvider",
                  restoreVersionCommandProvider );
        setField( basicFileMenuBuilder,
                  "notification",
                  notificationEvent );
        setField( restoreVersionCommandProvider,
                  "busyIndicatorView",
                  editorView );
        return basicFileMenuBuilder;
    }

    private FileMenuBuilder getFileMenuBuilder() {
        final FileMenuBuilder fileMenuBuilder = new FileMenuBuilderImpl();
        setField( fileMenuBuilder,
                  "menuBuilder",
                  basicFileMenuBuilder );
        return fileMenuBuilder;
    }

    private void setField( final Object o,
                           final String fieldName,
                           final Object value ) {
        try {
            final Field field = o.getClass().getDeclaredField( fieldName );
            field.setAccessible( true );
            field.set( o,
                       value );

        } catch ( NoSuchFieldException | IllegalAccessException e ) {
            fail( e.getMessage() );
        }
    }

}
