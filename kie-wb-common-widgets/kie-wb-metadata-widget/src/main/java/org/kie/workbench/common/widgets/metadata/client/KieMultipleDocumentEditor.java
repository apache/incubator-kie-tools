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

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.menu.SaveAllMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.*;

/**
 * A base for Multi-Document-Interface editors. This base implementation adds default Menus for Save", "Copy",
 * "Rename", "Delete", "Validate" and "VersionRecordManager" drop-down that can be overriden by subclasses.
 * {@link KieDocument} documents are first registered and then activated. Registration ensures the document
 * is configured for optimistic concurrent lock handling. Activation updates the content of the editor to
 * reflect the active document.
 * @param <D> Document type
 */
public abstract class KieMultipleDocumentEditor<D extends KieDocument> implements KieMultipleDocumentEditorPresenter<D> {

    //Injected
    private KieMultipleDocumentEditorWrapperView kieEditorWrapperView;
    private OverviewWidgetPresenter overviewWidget;
    private ImportsWidgetPresenter importsWidget;
    private Event<NotificationEvent> notificationEvent;
    private Event<ChangeTitleWidgetEvent> changeTitleEvent;
    private ProjectContext workbenchContext;

    protected FileMenuBuilder fileMenuBuilder;
    protected SaveAllMenuBuilder saveAllMenuBuilder;
    protected VersionRecordManager versionRecordManager;
    protected RegisteredDocumentsMenuBuilder registeredDocumentsMenuBuilder;
    protected DefaultFileNameValidator fileNameValidator;

    //Constructed
    private BaseEditorView editorView;
    private SaveOperationService saveOperationService = new SaveOperationService();
    protected ViewDRLSourceWidget sourceWidget = GWT.create( ViewDRLSourceWidget.class );

    private MenuItem saveMenuItem;
    private MenuItem saveAllMenuItem;
    private MenuItem versionMenuItem;
    private MenuItem registeredDocumentsMenuItem;

    protected Menus menus;

    private D activeDocument = null;
    protected final Set<D> documents = new HashSet<>();

    //Handler for MayClose requests
    private interface MayCloseHandler {

        boolean mayClose( final Integer originalHashCode,
                          final Integer currentHashCode );

    }

    //The default implementation delegates to the HashCode comparison in BaseEditor
    private final MayCloseHandler DEFAULT_MAY_CLOSE_HANDLER = ( originalHashCode, currentHashCode ) -> KieMultipleDocumentEditor.this.doMayClose( originalHashCode,
                                                                                                                                                  currentHashCode );

    //This implementation always permits closure as something went wrong loading the Editor's content
    private final MayCloseHandler EXCEPTION_MAY_CLOSE_HANDLER = ( originalHashCode, currentHashCode ) -> true;

    private MayCloseHandler mayCloseHandler = DEFAULT_MAY_CLOSE_HANDLER;

    @SuppressWarnings("unused")
    KieMultipleDocumentEditor() {
        //Zero-parameter constructor for CDI proxy
    }

    public KieMultipleDocumentEditor( final KieEditorView editorView ) {
        this.editorView = editorView;
    }

    @PostConstruct
    void setupMenuBar() {
        makeMenuBar();
        kieEditorWrapperView.init( this );
    }

    @Inject
    @KieMultipleDocumentEditorQualifier
    void setKieEditorWrapperView( final KieMultipleDocumentEditorWrapperView kieEditorWrapperView ) {
        this.kieEditorWrapperView = kieEditorWrapperView;
        this.kieEditorWrapperView.setPresenter( this );
    }

    @Inject
    void setOverviewWidget( final OverviewWidgetPresenter overviewWidget ) {
        this.overviewWidget = overviewWidget;
    }

    @Inject
    void setImportsWidget( final ImportsWidgetPresenter importsWidget ) {
        this.importsWidget = importsWidget;
    }

    @Inject
    void setNotificationEvent( final Event<NotificationEvent> notificationEvent ) {
        this.notificationEvent = notificationEvent;
    }

    @Inject
    void setChangeTitleEvent( final Event<ChangeTitleWidgetEvent> changeTitleEvent ) {
        this.changeTitleEvent = changeTitleEvent;
    }

    @Inject
    void setWorkbenchContext( final ProjectContext workbenchContext ) {
        this.workbenchContext = workbenchContext;
    }

    @Inject
    void setVersionRecordManager( final VersionRecordManager versionRecordManager ) {
        this.versionRecordManager = versionRecordManager;
        this.versionRecordManager.setShowMoreCommand( () -> {
            kieEditorWrapperView.selectOverviewTab();
            overviewWidget.showVersionsTab();
        } );
    }

    @Inject
    void setFileMenuBuilder( final FileMenuBuilder fileMenuBuilder ) {
        this.fileMenuBuilder = fileMenuBuilder;
    }

    @Inject
    void setSaveAllMenuBuilder( final SaveAllMenuBuilder saveAllMenuBuilder ) {
        this.saveAllMenuBuilder = saveAllMenuBuilder;
    }

    @Inject
    void setRegisteredDocumentsMenuBuilder( final RegisteredDocumentsMenuBuilder registeredDocumentsMenuBuilder ) {
        this.registeredDocumentsMenuBuilder = registeredDocumentsMenuBuilder;
    }

    @Inject
    void setFileNameValidator( final DefaultFileNameValidator fileNameValidator ) {
        this.fileNameValidator = fileNameValidator;
    }

    @Override
    public void registerDocument( final D document ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );

        if ( documents.contains( document ) ) {
            return;
        }

        documents.add( document );
        registeredDocumentsMenuBuilder.registerDocument( document );

        //Setup concurrent modification handlers
        final ObservablePath path = document.getLatestPath();

        path.onRename( () -> refresh( document ) );
        path.onConcurrentRename( ( info ) -> doConcurrentRename( document,
                                                                 info ) );

        path.onDelete( () -> {
            enableMenus( false );
            removeDocument( document );
            deregisterDocument( document );
        } );
        path.onConcurrentDelete( ( info ) -> {
            doConcurrentDelete( document,
                                info );
        } );

        path.onConcurrentUpdate( ( eventInfo ) -> document.setConcurrentUpdateSessionInfo( eventInfo ) );
    }

    //Package protected to allow overriding for Unit Tests
    void doConcurrentRename( final D document,
                             final ObservablePath.OnConcurrentRenameEvent info ) {
        newConcurrentRename( info.getSource(),
                             info.getTarget(),
                             info.getIdentity(),
                             getConcurrentRenameOnIgnoreCommand(),
                             getConcurrentRenameOnReopenCommand( document ) ).show();
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentRenameOnIgnoreCommand() {
        return () -> enableMenus( false );
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentRenameOnReopenCommand( final D document ) {
        return () -> {
            document.setConcurrentUpdateSessionInfo( null );
            refresh( document );
        };
    }

    //Package protected to allow overriding for Unit Tests
    void doConcurrentDelete( final D document,
                             final ObservablePath.OnConcurrentDelete info ) {
        newConcurrentDelete( info.getPath(),
                             info.getIdentity(),
                             getConcurrentDeleteOnIgnoreCommand(),
                             getConcurrentDeleteOnClose( document ) ).show();
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentDeleteOnIgnoreCommand() {
        return () -> enableMenus( false );
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentDeleteOnClose( final D document ) {
        return () -> {
            enableMenus( false );
            removeDocument( document );
            deregisterDocument( document );
        };
    }

    @Override
    public void deregisterDocument( final D document ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );

        if ( !documents.contains( document ) ) {
            return;
        }

        registeredDocumentsMenuBuilder.deregisterDocument( document );
        document.getLatestPath().dispose();
        documents.remove( document );
    }

    private void refresh( final D document ) {
        final String documentTitle = getDocumentTitle( document );
        editorView.refreshTitle( documentTitle );
        editorView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        refreshDocument( document );

        final PlaceRequest placeRequest = document.getPlaceRequest();
        changeTitleEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                           documentTitle,
                                                           getTitleWidget( document ) ) );
    }

    @Override
    public void activateDocument( final D document,
                                  final Overview overview,
                                  final AsyncPackageDataModelOracle dmo,
                                  final Imports imports,
                                  final boolean isReadOnly ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );
        PortablePreconditions.checkNotNull( "overview",
                                            overview );
        PortablePreconditions.checkNotNull( "dmo",
                                            document );
        PortablePreconditions.checkNotNull( "imports",
                                            imports );

        if ( !documents.contains( document ) ) {
            throw new IllegalArgumentException( "Document has not been registered." );
        }

        activeDocument = document;
        registeredDocumentsMenuBuilder.activateDocument( document );

        initialiseVersionManager( document );
        initialiseKieEditorTabs( document,
                                 overview,
                                 dmo,
                                 imports,
                                 isReadOnly );
    }

    @Override
    public D getActiveDocument() {
        return activeDocument;
    }

    void initialiseVersionManager( final D document ) {
        final String version = document.getVersion();
        final ObservablePath path = document.getLatestPath();

        versionRecordManager.init( version,
                                   path,
                                   ( versionRecord ) -> {
                                       versionRecordManager.setVersion( versionRecord.id() );
                                       document.setVersion( versionRecord.id() );
                                       document.setCurrentPath( versionRecordManager.getCurrentPath() );
                                       document.setReadOnly( !versionRecordManager.isLatest( versionRecord ) );
                                       refreshDocument( document );
                                   } );
    }

    void initialiseKieEditorTabs( final D document,
                                  final Overview overview,
                                  final AsyncPackageDataModelOracle dmo,
                                  final Imports imports,
                                  final boolean isReadOnly ) {
        kieEditorWrapperView.clear();

        kieEditorWrapperView.addMainEditorPage( editorView );

        kieEditorWrapperView.addOverviewPage( overviewWidget,
                                              () -> overviewWidget.refresh( document.getVersion() ) );

        kieEditorWrapperView.addSourcePage( sourceWidget );

        kieEditorWrapperView.addImportsTab( importsWidget );

        overviewWidget.setContent( overview,
                                   document.getLatestPath() );
        importsWidget.setContent( dmo,
                                  imports,
                                  isReadOnly );
    }

    @Override
    public IsWidget getWidget() {
        return kieEditorWrapperView.asWidget();
    }

    @Override
    public IsWidget getTitleWidget( final D document ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );
        editorView.refreshTitle( getDocumentTitle( document ) );
        return editorView.getTitleWidget();
    }

    @Override
    public Menus getMenus() {
        return this.menus;
    }

    @Override
    public void onClose() {
        this.versionRecordManager.clear();
        this.registeredDocumentsMenuBuilder.dispose();
    }

    @Override
    public void onSourceTabSelected() {
        onSourceTabSelected( getActiveDocument() );
    }

    @Override
    public void updateSource( final String source ) {
        sourceWidget.setContent( source );
    }

    @Override
    public void onEditTabSelected() {
        //Nothing to do
    }

    @Override
    public void onEditTabUnselected() {
        //Nothing to do
    }

    @Override
    public void onOverviewSelected() {
        //Nothing to do
    }

    @Override
    public boolean mayClose( final Integer originalHashCode,
                             final Integer currentHashCode ) {
        return mayCloseHandler.mayClose( originalHashCode,
                                         currentHashCode );
    }

    private boolean doMayClose( final Integer originalHashCode,
                                final Integer currentHashCode ) {
        if ( this.isDirty( originalHashCode,
                           currentHashCode ) || overviewWidget.isDirty() ) {
            return this.editorView.confirmClose();
        } else {
            return true;
        }
    }

    private boolean isDirty( final Integer originalHashCode,
                             final Integer currentHashCode ) {
        if ( originalHashCode == null ) {
            return currentHashCode != null;
        } else {
            return !originalHashCode.equals( currentHashCode );
        }
    }

    /**
     * Construct the default Menus, consisting of "Save", "Copy", "Rename", "Delete",
     * "Validate" and "VersionRecordManager" drop-down. Subclasses can override this
     * to customize their Menus.
     */
    @Override
    public void makeMenuBar() {
        this.fileMenuBuilder.setLockSyncMenuStateHelper( new KieMultipleDocumentEditorLockSyncHelper( this ) );
        this.menus = fileMenuBuilder
                .addSave( getSaveMenuItem() )
                .addCopy( () -> getActiveDocument().getCurrentPath(),
                          fileNameValidator )
                .addRename( () -> getActiveDocument().getLatestPath(),
                            fileNameValidator )
                .addDelete( () -> getActiveDocument().getLatestPath() )
                .addValidate( () -> onValidate( getActiveDocument() ) )
                .addNewTopLevelMenu( getSaveAllMenuItem() )
                .addNewTopLevelMenu( getRegisteredDocumentsMenuItem() )
                .addNewTopLevelMenu( getVersionManagerMenuItem() )
                .build();
    }

    /**
     * Get the MenuItem that should be used for "Save".
     * @return
     */
    protected MenuItem getSaveMenuItem() {
        if ( saveMenuItem == null ) {
            saveMenuItem = versionRecordManager.newSaveMenuItem( this::doSave );
        }
        return saveMenuItem;
    }

    /**
     * Get the MenuItem that should be used for "Save all".
     * @return
     */
    protected MenuItem getSaveAllMenuItem() {
        if ( saveAllMenuItem == null ) {
            saveAllMenuItem = saveAllMenuBuilder.build();
            saveAllMenuBuilder.setSaveAllCommand( this::doSaveAll );
        }
        return saveAllMenuItem;
    }

    /**
     * Get the MenuItem that should be used for listing "(Registered) documents".
     * @return
     */
    protected MenuItem getRegisteredDocumentsMenuItem() {
        if ( registeredDocumentsMenuItem == null ) {
            registeredDocumentsMenuItem = registeredDocumentsMenuBuilder.build();
            registeredDocumentsMenuBuilder.setSaveDocumentsCommand( this::doSaveAll );
            registeredDocumentsMenuBuilder.setOpenDocumentCommand( this::openDocumentInEditor );
        }
        return registeredDocumentsMenuItem;
    }

    /**
     * Get the MenuItem that should be used for "VersionRecordManager" drop-down.
     * @return
     */
    protected MenuItem getVersionManagerMenuItem() {
        if ( versionMenuItem == null ) {
            versionMenuItem = versionRecordManager.buildMenu();
        }
        return versionMenuItem;
    }

    /**
     * Called by the "Save" MenuItem to save or restore the active document. If the active document
     * is read-only a check is made whether the active document is an older version; in which case
     * the active document is restored. If the active document is read-only and the latest version
     * the User is notified that the document is read-only and the save aborted. If the document
     * is not read-only a check is made for concurrent updates before persisting.
     */
    protected void doSave() {
        final D document = getActiveDocument();
        if ( document == null ) {
            return;
        }

        final boolean isReadOnly = document.isReadOnly();
        if ( isReadOnly ) {
            if ( versionRecordManager.isCurrentLatest() ) {
                editorView.alertReadOnly();
            } else {
                versionRecordManager.restoreToCurrentVersion();
            }
            return;
        }
        doSaveCheckForAndHandleConcurrentUpdate( document );
    }

    /**
     * Called by the "Save all" MenuItem to save all active document. If a document
     * is read-only saving of that particular document is skipped. If a document
     * is not read-only a check is made for concurrent updates before persisting.
     */
    protected void doSaveAll() {
        for ( D document : documents ) {
            if ( !( document.isReadOnly() ) ) {
                doSaveCheckForAndHandleConcurrentUpdate( document );
            }
        }
    }

    /**
     * Checks whether a document has experienced a concurrent update by another user. If a concurrent update
     * is detected a {@link ConcurrentChangePopup} is shown allowing the user to choose whether to abort the
     * save, force the save or refresh the view with the latest version. If no concurrent update is detected
     * the document is persisted.
     * @param document
     */
    protected void doSaveCheckForAndHandleConcurrentUpdate( final D document ) {
        final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = document.getConcurrentUpdateSessionInfo();
        if ( concurrentUpdateSessionInfo != null ) {
            showConcurrentUpdatePopup( document );
        } else {
            doSave( document );
        }
    }

    void showConcurrentUpdatePopup( final D document ) {
        final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = document.getConcurrentUpdateSessionInfo();
        newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                             concurrentUpdateSessionInfo.getIdentity(),
                             () -> doSave( document ),
                             () -> {/*nothing*/},
                             () -> {
                                 document.setConcurrentUpdateSessionInfo( null );
                                 refresh( document );
                             } ).show();
    }

    //Package protected to allow overriding for Unit Tests
    void doSave( final D document ) {
        saveOperationService.save( document.getCurrentPath(),
                                   getSaveCommand( document ) );
    }

    //Package protected to allow overriding for Unit Tests
    ParameterizedCommand<String> getSaveCommand( final D document ) {
        return ( commitMessage ) -> {
            editorView.showSaving();
            onSave( document,
                    commitMessage );
            document.setConcurrentUpdateSessionInfo( null );
        };
    }

    void onRestore( final @Observes RestoreEvent event ) {
        if ( event == null || event.getPath() == null ) {
            return;
        }
        if ( versionRecordManager.getCurrentPath() == null ) {
            return;
        }
        if ( versionRecordManager.getCurrentPath().equals( event.getPath() ) ) {
            activeDocument.setVersion( null );
            activeDocument.setLatestPath( versionRecordManager.getPathToLatest() );
            activeDocument.setCurrentPath( versionRecordManager.getPathToLatest() );
            initialiseVersionManager( activeDocument );
            refreshDocument( activeDocument );
            notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRestored() ) );
        }
    }

    void onRepositoryRemoved( final @Observes RepositoryRemovedEvent event ) {
        if ( event.getRepository() == null ) {
            return;
        }
        if ( workbenchContext == null ) {
            return;
        }
        if ( workbenchContext.getActiveRepository() == null ) {
            return;
        }
        if ( workbenchContext.getActiveRepository().equals( event.getRepository() ) ) {
            enableMenus( false );
        }
    }

    /**
     * Enable/disable all menus associated with the MDI container, consisting of "Save", "Copy",
     * "Rename", "Delete", "Validate" and "VersionRecordManager" drop-down. Subclasses can override
     * this to customize their Menus.
     * @param enabled
     */
    protected void enableMenus( final boolean enabled ) {
        getSaveMenuItem().setEnabled( enabled );
        getVersionManagerMenuItem().setEnabled( enabled );

        enableMenuItem( enabled,
                        MenuItems.COPY );
        enableMenuItem( enabled,
                        MenuItems.RENAME );
        enableMenuItem( enabled,
                        MenuItems.DELETE );
        enableMenuItem( enabled,
                        MenuItems.VALIDATE );
    }

    /**
     * Enable/disable a single menu associated with the MDI container.
     * @param enabled
     */
    protected void enableMenuItem( final boolean enabled,
                                   final MenuItems menuItem ) {
        if ( menus.getItemsMap().containsKey( menuItem ) ) {
            menus.getItemsMap().get( menuItem ).setEnabled( enabled );
        }
    }

    protected void openDocumentInEditor() {
        getAvailableDocumentPaths( ( allPaths ) -> {
            for ( D document : documents ) {
                allPaths.remove( document.getLatestPath() );
            }

            if ( allPaths.isEmpty() ) {
                kieEditorWrapperView.showNoAdditionalDocuments();
            } else {
                kieEditorWrapperView.showAdditionalDocuments( allPaths );
            }
        } );
    }

    /**
     * Default callback for when loading a document fails.
     * @return
     */
    protected CommandDrivenErrorCallback getNoSuchFileExceptionErrorCallback() {
        return new CommandDrivenErrorCallback( editorView,
                                               new CommandBuilder()
                                                       .addNoSuchFileException( editorView,
                                                                                kieEditorWrapperView.getMultiPage(),
                                                                                menus )
                                                       .addFileSystemNotFoundException( editorView,
                                                                                        kieEditorWrapperView.getMultiPage(),
                                                                                        menus )
                                                       .build()
        ) {
            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                mayCloseHandler = EXCEPTION_MAY_CLOSE_HANDLER;
                return super.error( message,
                                    throwable );
            }
        };
    }

    /**
     * Default callback for when retrieval of a document's source fails.
     * @return
     */
    protected CommandDrivenErrorCallback getCouldNotGenerateSourceErrorCallback() {
        return new CommandDrivenErrorCallback( editorView,
                                               new CommandBuilder()
                                                       .addSourceCodeGenerationFailedException( editorView,
                                                                                                sourceWidget )
                                                       .build()
        );
    }

    /**
     * Default callback for when a document has been saved. This should be used by implementations
     * of {@link #onSave(KieDocument, String)} to ensure the "isDirty" mechanism is correctly updated.
     * @param document
     * @param currentHashCode
     * @return
     */
    protected RemoteCallback<Path> getSaveSuccessCallback( final D document,
                                                           final int currentHashCode ) {
        return ( path ) -> {
            editorView.hideBusyIndicator();
            versionRecordManager.reloadVersions( path );
            notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            document.setOriginalHashCode( currentHashCode );
        };
    }

}
