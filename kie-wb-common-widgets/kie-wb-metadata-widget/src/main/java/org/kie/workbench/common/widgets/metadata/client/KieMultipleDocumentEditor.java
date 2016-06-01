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
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.java.nio.base.version.VersionRecord;
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
 * @param <P>
 */
public abstract class KieMultipleDocumentEditor<P extends KieMultipleDocumentEditor.KieDocument> implements KieEditorWrapperView.KieEditorWrapperPresenter {

    //Injected
    private KieEditorWrapperView kieEditorWrapperView;
    private OverviewWidgetPresenter overviewWidget;
    private ImportsWidgetPresenter importsWidget;
    private Event<NotificationEvent> notificationEvent;
    private Event<ChangeTitleWidgetEvent> changeTitleEvent;
    private ProjectContext workbenchContext;

    protected VersionRecordManager versionRecordManager;
    protected FileMenuBuilder fileMenuBuilder;
    protected DefaultFileNameValidator fileNameValidator;

    //Constructed
    private BaseEditorView editorView;
    protected ViewDRLSourceWidget sourceWidget = GWT.create( ViewDRLSourceWidget.class );
    private SaveOperationService saveOperationService = new SaveOperationService();
    private MenuItem versionMenuItem;
    private MenuItem saveMenuItem;

    protected Menus menus;

    private P activeDocument = null;
    protected final Set<P> documents = new HashSet<>();

    /**
     * Definition of a document that can hosted in KIE's Multiple Document Editor
     */
    public interface KieDocument {

        /**
         * Returns the current version of the document. Can be null if it is the "latest" version.
         * The version is identical to that used by {@link VersionRecordManager}.
         * @return
         */
        String getVersion();

        /**
         * Sets the current version of the document. This is called automatically by
         * {@link KieMultipleDocumentEditor} in response to changes to the document
         * version from the {@link VersionRecordManager} .
         * @param version
         */
        void setVersion( final String version );

        /**
         * Returns the "latest" path for the document. Latest is the tip/head version.
         * @return Can be null.
         */
        ObservablePath getLatestPath();

        /**
         * Sets the "latest" path for the document. This is called automatically by
         * {@link KieMultipleDocumentEditor} when an older version of a document is
         * restored and hence the latest version changes.
         * @param latestPath Can be null.
         */
        void setLatestPath( final ObservablePath latestPath );

        /**
         * Returns the "current" path for the document reflecting
         * the version selected in {@link VersionRecordManager}
         * @return Cannot be null.
         */
        ObservablePath getCurrentPath();

        /**
         * Sets the "current" path for the document. This is called automatically by
         * {@link VersionRecordManager} in response to a different version of the document
         * being selected; and in response to restoration of an older version of the document.
         * @param currentPath Cannot be null.
         */
        void setCurrentPath( final ObservablePath currentPath );

        /**
         * Returns the original {@link PlaceRequest} associated with the {@link KieMultipleDocumentEditor}
         * when first initialised. The {@link PlaceRequest} is used to support changes to the Editor title.
         * Subclasses may also need to use this to support different {@link LockManager} configurations.
         * @return
         */
        PlaceRequest getPlaceRequest();

        /**
         * Returns whether the document is read-only; normally when {@link KieDocument#getCurrentPath()}
         * points to version that is not the "latest" version however can also be set by subclasses for
         * example should attempts to lock the document for editing fail.
         * @return
         */
        boolean isReadOnly();

        /**
         * Sets whether the document is read-only. This is called automatically by {@link KieMultipleDocumentEditor}
         * in response to Users selecting a version of the document that is not the lastest.
         * @param isReadOnly
         */
        void setReadOnly( final boolean isReadOnly );

        /**
         * Returns the original hashCode of the model represented by the document. This is used by the hashCode-based
         * "is dirty" mechanism; where by a document is considered dirty should the hashCode when the document was
         * loaded differ to the document's current hashCode. This method should be used in conjunction with
         * {@link KieMultipleDocumentEditor#mayClose(Integer, Integer)}
         * @return
         */
        Integer getOriginalHashCode();

        /**
         * Sets the "original" hashCode. This is called automatically by {@link KieMultipleDocumentEditor#getSaveSuccessCallback(KieDocument, int)}
         * when a document has been successfully saved; effectively resetting the "is dirty" mechansism. Subclasses may also call this to set
         * the documents original hashCode after the document has been loaded. However by default this will be null and operate identically
         * to if it had been set by subclasses.
         * @param originalHashCode
         */
        void setOriginalHashCode( final Integer originalHashCode );

        /**
         * Returns the concurrent modification meta-data associated with the document. This is called automatically by the
         * concurrent modification handlers configured by {@link KieMultipleDocumentEditor#registerDocument(KieDocument)}.
         * to check for and handle concurrent modifications. It should not need to be called by subclasses.
         * @return
         */
        ObservablePath.OnConcurrentUpdateEvent getConcurrentUpdateSessionInfo();

        /**
         * Sets the concurrent modification meta-data. This is called automatically by the concurrent modification handlers
         * configured by {@link KieMultipleDocumentEditor#registerDocument(KieDocument)}. It should not need to be called
         * by subclasses directly.
         * @param concurrentUpdateSessionInfo
         */
        void setConcurrentUpdateSessionInfo( final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo );

    }

    //Handler for MayClose requests
    private interface MayCloseHandler {

        boolean mayClose( final Integer originalHashCode,
                          final Integer currentHashCode );

    }

    //The default implementation delegates to the HashCode comparison in BaseEditor
    private final MayCloseHandler DEFAULT_MAY_CLOSE_HANDLER = new MayCloseHandler() {

        @Override
        public boolean mayClose( final Integer originalHashCode,
                                 final Integer currentHashCode ) {
            return KieMultipleDocumentEditor.this.doMayClose( originalHashCode,
                                                              currentHashCode );
        }

    };

    //This implementation always permits closure as something went wrong loading the Editor's content
    private final MayCloseHandler EXCEPTION_MAY_CLOSE_HANDLER = new MayCloseHandler() {

        @Override
        public boolean mayClose( final Integer originalHashCode,
                                 final Integer currentHashCode ) {
            return true;
        }
    };

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
    }

    @Inject
    void setKieEditorWrapperView( final KieEditorWrapperView kieEditorWrapperView ) {
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
        this.versionRecordManager.setShowMoreCommand( new Command() {
            @Override
            public void execute() {
                kieEditorWrapperView.selectOverviewTab();
                overviewWidget.showVersionsTab();
            }
        } );
    }

    @Inject
    void setFileMenuBuilder( final FileMenuBuilder fileMenuBuilder ) {
        this.fileMenuBuilder = fileMenuBuilder;
    }

    @Inject
    void setFileNameValidator( final DefaultFileNameValidator fileNameValidator ) {
        this.fileNameValidator = fileNameValidator;
    }

    /**
     * Register a new document in the MDI container. The document's Path is configured with concurrent lock handlers.
     * @param document The document to register. Cannot be null.
     */
    protected void registerDocument( final P document ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );

        if ( documents.contains( document ) ) {
            return;
        }

        documents.add( document );

        //Setup concurrent modification handlers
        final ObservablePath path = document.getLatestPath();

        path.onRename( new Command() {
            @Override
            public void execute() {
                refresh( document );

            }
        } );
        path.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentRenameEvent info ) {
                doConcurrentRename( document,
                                    info );
            }
        } );

        path.onDelete( new Command() {
            @Override
            public void execute() {
                enableMenus( false );
                removeDocument( document );
            }
        } );
        path.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentDelete info ) {
                doConcurrentDelete( document,
                                    info );
            }
        } );

        path.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                document.setConcurrentUpdateSessionInfo( eventInfo );
            }
        } );
    }

    //Package protected to allow overriding for Unit Tests
    void doConcurrentRename( final P document,
                             final ObservablePath.OnConcurrentRenameEvent info ) {
        newConcurrentRename( info.getSource(),
                             info.getTarget(),
                             info.getIdentity(),
                             getConcurrentRenameOnIgnoreCommand(),
                             getConcurrentRenameOnReopenCommand( document ) ).show();
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentRenameOnIgnoreCommand() {
        return new Command() {
            @Override
            public void execute() {
                enableMenus( false );
            }
        };
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentRenameOnReopenCommand( final P document ) {
        return new Command() {
            @Override
            public void execute() {
                document.setConcurrentUpdateSessionInfo( null );
                refresh( document );
            }
        };
    }

    //Package protected to allow overriding for Unit Tests
    void doConcurrentDelete( final P document,
                             final ObservablePath.OnConcurrentDelete info ) {
        newConcurrentDelete( info.getPath(),
                             info.getIdentity(),
                             getConcurrentDeleteOnIgnoreCommand(),
                             getConcurrentDeleteOnClose( document ) ).show();
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentDeleteOnIgnoreCommand() {
        return new Command() {
            @Override
            public void execute() {
                enableMenus( false );
            }
        };
    }

    //Package protected to allow overriding for Unit Tests
    Command getConcurrentDeleteOnClose( final P document ) {
        return new Command() {
            @Override
            public void execute() {
                enableMenus( false );
                removeDocument( document );
            }
        };
    }

    /**
     * Deregister an existing document from the MDI container.
     * @param document The document to deregister. Cannot be null.
     */
    public void deregisterDocument( final P document ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );

        if ( !documents.contains( document ) ) {
            return;
        }

        document.getLatestPath().dispose();
        documents.remove( document );
    }

    private void refresh( final P document ) {
        final String documentTitle = getDocumentTitle( document );
        editorView.refreshTitle( documentTitle );
        editorView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        refreshDocument( document );

        final PlaceRequest placeRequest = document.getPlaceRequest();
        changeTitleEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                           documentTitle,
                                                           getTitleWidget( document ) ) );
    }

    /**
     * Load a document. Once a document has been loaded it should be registered to the MDI container.
     * @param path Provided by Uberfire to the @WorkbenchEditor's @OnStartup method.
     * @param placeRequest Provided by Uberfire to the @WorkbenchEditor's @OnStartup method.
     */
    protected abstract void loadDocument( final ObservablePath path,
                                          final PlaceRequest placeRequest );

    /**
     * Refresh a document due to a change in the selected version.
     * @param document
     */
    protected abstract void refreshDocument( final P document );

    /**
     * Remove a document from the MDI container.
     * @param document
     */
    protected abstract void removeDocument( final P document );

    /**
     * Get a title for the document to show in the WorkbenchPart's title widget.
     * @param document
     * @return
     */
    protected abstract String getDocumentTitle( final P document );

    /**
     * The "View Source" tab has been selected. Subclasses should generate the source for the document
     * and update the "View Source" widget's content with {@link #updateSource(String)} .
     * @param document
     * @return
     */
    protected abstract void onSourceTabSelected( final P document );

    /**
     * The "Validate" MenuItem has been selected. Subclasses should perform validation of the document.
     * @param document
     * @return
     */
    protected abstract void onValidate( final P document );

    /**
     * The "Save" MenuItem has been selected. Subclasses should save the document.
     * The {@link #getSaveSuccessCallback(KieDocument, int)} should be used to ensure the "isDirty"
     * mechanism is correctly updated.
     * @param document
     * @param commitMessage
     * @return
     */
    protected abstract void onSave( final P document,
                                    final String commitMessage );

    /**
     * Activate a document. Activation initialises the VersionRecordManager drop-down and Editor tabs
     * with the content of the document. Subclasses could call this, for example, when a document
     * has been selected.
     * @param document The document to activate. Cannot be null.
     * @param overview The {@link Overview} associated with the document. Cannot be null.
     * @param dmo The {@link AsyncPackageDataModelOracle} associated with the document. Cannot be null.
     * @param imports The {@link Imports} associated with the document. Cannot be null.
     * @param isReadOnly true if the document is read-only.
     */
    protected void activateDocument( final P document,
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

        initialiseVersionManager( document );
        initialiseKieEditorTabs( document,
                                 overview,
                                 dmo,
                                 imports,
                                 isReadOnly );
    }

    /**
     * Get the active document. Can return null.
     * @return
     */
    protected P getActiveDocument() {
        return activeDocument;
    }

    void initialiseVersionManager( final P document ) {
        final String version = document.getVersion();
        final ObservablePath path = document.getLatestPath();

        versionRecordManager.init( version,
                                   path,
                                   new Callback<VersionRecord>() {
                                       @Override
                                       public void callback( final VersionRecord versionRecord ) {
                                           versionRecordManager.setVersion( versionRecord.id() );
                                           document.setVersion( versionRecord.id() );
                                           document.setCurrentPath( versionRecordManager.getCurrentPath() );
                                           document.setReadOnly( !versionRecordManager.isLatest( versionRecord ) );
                                           refreshDocument( document );
                                       }
                                   } );
    }

    void initialiseKieEditorTabs( final P document,
                                  final Overview overview,
                                  final AsyncPackageDataModelOracle dmo,
                                  final Imports imports,
                                  final boolean isReadOnly ) {
        kieEditorWrapperView.clear();

        kieEditorWrapperView.addMainEditorPage( editorView );

        kieEditorWrapperView.addOverviewPage( overviewWidget,
                                              new com.google.gwt.user.client.Command() {
                                                  @Override
                                                  public void execute() {
                                                      overviewWidget.refresh( document.getVersion() );
                                                  }
                                              } );

        kieEditorWrapperView.addSourcePage( sourceWidget );

        kieEditorWrapperView.addImportsTab( importsWidget );

        overviewWidget.setContent( overview,
                                   document.getLatestPath() );
        importsWidget.setContent( dmo,
                                  imports,
                                  isReadOnly );
    }

    /**
     * The Widget for this Editor. To be returned by subclasses @WorkbenchPartView method.
     * @return
     */
    protected IsWidget getWidget() {
        return kieEditorWrapperView.asWidget();
    }

    /**
     * The title decoration for this Editor. To be returned by subclasses @WorkbenchPartTitleDecoration method.
     * @param document The document for which to get the title widget. Cannot be null.
     * @return
     */
    protected IsWidget getTitleWidget( final P document ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );
        editorView.refreshTitle( getDocumentTitle( document ) );
        return editorView.getTitleWidget();
    }

    /**
     * The Menus for this Editor. To be returned by subclasses @WorkbenchMenu method.
     * @return
     */
    protected Menus getMenus() {
        return this.menus;
    }

    /**
     * Check whether a document can be closed. The original hashCode can be retrieved from the document.
     * The current hashCode should be retrieved from the document's model. To be used in conjunction with
     * subclasses @OnMayClose method.
     * @param originalHashCode The document's model original hashCode.
     * @param currentHashCode The document's model current hashCode.
     * @return
     */
    protected boolean mayClose( final Integer originalHashCode,
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
     * Ensure VersionRecordManager is released correctly. To be used by subclasses @OnClose method.
     */
    protected void onClose() {
        this.versionRecordManager.clear();
    }

    /**
     * Update the "View Source" widget's content.
     * @param source
     */
    protected void updateSource( final String source ) {
        sourceWidget.setContent( source );
    }

    @Override
    public void onSourceTabSelected() {
        onSourceTabSelected( getActiveDocument() );
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

    protected Command onValidate() {
        return () -> onValidate( getActiveDocument() );
    }

    /**
     * Construct the default Menus, consisting of "Save", "Copy", "Rename", "Delete",
     * "Validate" and "VersionRecordManager" drop-down. Subclasses can override this
     * to customize their Menus.
     */
    protected void makeMenuBar() {
        this.menus = fileMenuBuilder
                .addSave( getSaveMenuItem() )
                .addCopy( () -> getActiveDocument().getCurrentPath(),
                          fileNameValidator )
                .addRename( () -> getActiveDocument().getLatestPath(),
                            fileNameValidator )
                .addDelete( () -> getActiveDocument().getLatestPath() )
                .addValidate( onValidate() )
                .addNewTopLevelMenu( getVersionManagerMenuItem() )
                .build();
    }

    /**
     * Get the MenuItem that should be used for "Save".
     * @return
     */
    protected MenuItem getSaveMenuItem() {
        if ( saveMenuItem == null ) {
            saveMenuItem = versionRecordManager.newSaveMenuItem( new Command() {
                @Override
                public void execute() {
                    onSave();
                }
            } );
        }
        return saveMenuItem;
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

    private void onSave() {
        final P document = getActiveDocument();
        final boolean isReadOnly = document.isReadOnly();
        if ( isReadOnly ) {
            if ( versionRecordManager.isCurrentLatest() ) {
                editorView.alertReadOnly();
            } else {
                versionRecordManager.restoreToCurrentVersion();
            }
            return;
        }

        final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = document.getConcurrentUpdateSessionInfo();
        if ( concurrentUpdateSessionInfo != null ) {
            showConcurrentUpdatePopup( document );
        } else {
            save( document );
        }
    }

    private void showConcurrentUpdatePopup( final P document ) {
        final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = document.getConcurrentUpdateSessionInfo();
        newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                             concurrentUpdateSessionInfo.getIdentity(),
                             new Command() {
                                 @Override
                                 public void execute() {
                                     save( document );
                                 }
                             },
                             new Command() {
                                 @Override
                                 public void execute() {
                                     //cancel?
                                 }
                             },
                             new Command() {
                                 @Override
                                 public void execute() {
                                     document.setConcurrentUpdateSessionInfo( null );
                                     refresh( document );
                                 }
                             }
                           ).show();
    }

    private void save( final P document ) {
        doSave( document );
    }

    //Package protected to allow overriding for Unit Tests
    void doSave( final P document ) {
        saveOperationService.save( document.getCurrentPath(),
                                   getSaveCommand( document ) );
    }

    //Package protected to allow overriding for Unit Tests
    ParameterizedCommand<String> getSaveCommand( final P document ) {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( final String commitMessage ) {
                editorView.showSaving();
                onSave( document,
                        commitMessage );
                document.setConcurrentUpdateSessionInfo( null );
            }
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
    protected RemoteCallback<Path> getSaveSuccessCallback( final P document,
                                                           final int currentHashCode ) {
        return new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                editorView.hideBusyIndicator();
                versionRecordManager.reloadVersions( path );
                notificationEvent.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
                document.setOriginalHashCode( currentHashCode );
            }
        };
    }

}
