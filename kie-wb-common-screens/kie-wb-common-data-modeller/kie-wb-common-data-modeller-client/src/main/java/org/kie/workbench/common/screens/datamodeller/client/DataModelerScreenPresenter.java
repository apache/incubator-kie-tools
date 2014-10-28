/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.events.PublishBaseEvent;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;
import org.kie.uberfire.client.common.Page;
import org.kie.uberfire.client.common.popups.YesNoCancelPopup;
import org.kie.uberfire.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.JavaFileNameValidator;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.ShowUsagesPopup;
import org.kie.workbench.common.screens.datamodeller.events.DataModelSaved;
import org.kie.workbench.common.screens.datamodeller.events.DataModelStatusChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.JavaTypeInfoTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.model.TypeInfoResult;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.javaeditor.client.type.JavaResourceType;
import org.kie.workbench.common.screens.javaeditor.client.widget.EditJavaSourceWidget;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithFileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CopyPopup;
import org.kie.workbench.common.widgets.client.popups.file.DeletePopup;
import org.kie.workbench.common.widgets.client.popups.file.FileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.RenamePopup;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "DataModelerEditor",
                 supportedTypes = { JavaResourceType.class },
                 priority = Integer.MAX_VALUE)
public class DataModelerScreenPresenter
    extends KieEditor {

    public interface DataModelerScreenView
            extends
            KieEditorView {

        void setContext( DataModelerContext context );

        boolean confirmClose();

        void refreshTypeLists( boolean keepCurrentSelection );
    }

    private DataModelerScreenView view;

    @Inject
    private EditJavaSourceWidget javaSourceEditor;

    @Inject
    private Event<DataModelerEvent> dataModelerEvent;

    @Inject
    private Event<UnpublishMessagesEvent> unpublishMessagesEvent;

    @Inject
    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    @Inject
    private Caller<DataModelerService> modelerService;

    @Inject
    private ValidatorService validatorService;

    @Inject
    private JavaFileNameValidator javaFileNameValidator;

    @Inject
    private JavaResourceType resourceType;

    private DataModelerContext context;

    private boolean open = false;

    private boolean uiStarted = false;

    @Inject
    private SessionInfo sessionInfo;

    private String currentMessageType;

    private static final int EDITABLE_SOURCE_TAB = 2;

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Inject
    public DataModelerScreenPresenter(DataModelerScreenView baseView) {
        super( baseView );
        view = baseView;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        init(path, place, resourceType);

        initContext( path );
        open = true;

        currentMessageType = "DataModeler" + path.toURI();
        cleanSystemMessages(getCurrentMessageType());

        javaSourceEditor.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                if (getContext() != null) {
                    getContext().setEditionStatus(DataModelerContext.EditionStatus.SOURCE_CHANGED);
                    getContext().setDirty(true);
                }
            }
        });
    }

    @IsDirty
    public boolean isDirty() {
        return getContext() != null && getContext().isDirty();
    }

    @OnMayClose
    public boolean onMayClose() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @OnClose
    public void OnClose() {
        open = false;
        versionRecordManager.clear();
        cleanSystemMessages( getCurrentMessageType() );
        clearContext();
        super.OnClose();
    }

    private void onSafeDelete() {

        if ( getContext().getEditorModelContent().getOriginalClassName() != null ) {
            //if we are about to delete a .java file that could be parsed without errors, and we can calculate the
            //className we can check for class usages prior to deletion.

            final String className = getContext().getEditorModelContent().getOriginalClassName();
            modelerService.call( new RemoteCallback<List<Path>>() {

                @Override
                public void callback( List<Path> paths ) {

                    if ( paths != null && paths.size() > 0 ) {
                        //If usages for this class were detected in project assets
                        //show the confirmation message to the user.

                        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForDeletion(
                                Constants.INSTANCE.modelEditor_confirm_deletion_of_used_class( className ),
                                paths,
                                new Command() {
                                    @Override
                                    public void execute() {
                                        onDelete( versionRecordManager.getPathToLatest() );
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                    }
                                }
                            );

                        showUsagesPopup.setCloseVisible( false );
                        showUsagesPopup.show();

                    } else {
                        //no usages, just proceed with the deletion.
                        onDelete( versionRecordManager.getPathToLatest() );
                    }
                }
            } ).findClassUsages( className );
        } else {
            //we couldn't parse the class, so no check can be done. Just proceed with the standard
            //file deletion procedure.
            onDelete( versionRecordManager.getPathToLatest() );
        }
    }

    private void onDelete( final Path path ) {
        final DeletePopup popup = new DeletePopup( new CommandWithCommitMessage() {
            @Override
            public void execute( final String comment ) {
                view.showBusyIndicator( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Deleting() );
                modelerService.call( getDeleteSuccessCallback( path ), new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_deleting_error() ) ).delete( path,
                        comment );
            }
        } );
        popup.show();
    }

    private void onCopy() {
        final CopyPopup popup = new CopyPopup( versionRecordManager.getCurrentPath(),
                                               javaFileNameValidator,
                                               new CommandWithFileNameAndCommitMessage() {
                                                   @Override
                                                   public void execute( final FileNameAndCommitMessage details ) {
                                                       view.showBusyIndicator( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Copying() );
                                                       modelerService.call( getCopySuccessCallback( ),
                                                                            new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_copying_error() ) ).copy( versionRecordManager.getCurrentPath(),
                                                               details.getNewFileName(),
                                                               details.getCommitMessage(),
                                                               true );
                                                   }
                                               } );
        popup.show();
    }

    private void onSafeRename() {

        if ( getContext().getEditorModelContent().getOriginalClassName() != null ) {
            //if we are about to rename a .java file that could be parsed without errors, and we can calculate the
            //className we can check for class usages prior to renaming and we can also suggest to perform an automatic
            // class renaming.

            final String className = getContext().getEditorModelContent().getOriginalClassName();

            modelerService.call( new RemoteCallback<List<Path>>() {

                @Override
                public void callback( List<Path> paths ) {

                    if ( paths != null && paths.size() > 0 ) {
                        //If usages for this class were detected in project assets
                        //show the confirmation message to the user.

                        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForRenaming(
                                Constants.INSTANCE.modelEditor_confirm_renaming_of_used_class( className ),
                                paths,
                                new Command() {
                                    @Override
                                    public void execute() {
                                        rename();
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                    }
                                }
                                                                                                   );

                        showUsagesPopup.setCloseVisible( false );
                        showUsagesPopup.show();

                    } else {
                        //no usages, just proceed with the deletion.
                        rename();
                    }
                }
            } ).findClassUsages( className );
        } else {
            //we couldn't parse the class, so no check can be done. Just proceed with the standard
            //file renaming procedure.
            rename();
        }
    }

    protected void rename() {
        if ( getContext().isDirty() ) {
            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                                                                                      Constants.INSTANCE.modelEditor_confirm_save_before_rename(),
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              rename( true );
                                                                                          }
                                                                                      },
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              rename( false );
                                                                                          }
                                                                                      },
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              //do nothing.
                                                                                          }
                                                                                      }
                                                                                    );
            yesNoCancelPopup.setCloseVisible( false );
            yesNoCancelPopup.show();
        } else {
            //just rename.
            rename( false );
        }
    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {

                //at validation time we must do the same calculation as if we were about to save.
                final DataObjectTO[] modifiedDataObject = new DataObjectTO[1];
                if (getContext().isDirty()) {
                    if (getContext().isEditorChanged()) {

                        //at save time the source has always priority over the model.
                        //If the source was properly parsed and the editor has changes, we need to send the DataObject
                        //to the server in order to let the source to be updated prior to save.
                        modifiedDataObject[0] = getContext().getDataObject();
                    } else {
                        //if the source has changes, no update form the UI to the source will be performed.
                        //instead the parsed DataObject must be returned from the server.
                        modifiedDataObject[0] = null;
                    }
                }

                modelerService.call(new RemoteCallback<List<ValidationMessage>>() {
                    @Override
                    public void callback(final List<ValidationMessage> results) {
                        if (results == null || results.isEmpty()) {
                            notification.fire(new NotificationEvent(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                    NotificationEvent.NotificationType.SUCCESS));
                        } else {
                            ValidationPopup.showMessages(results);
                        }
                    }
                }, new DefaultErrorCallback()).validate( getSource(), versionRecordManager.getCurrentPath(), modifiedDataObject[ 0 ] );
            }
        };
    }

    private RemoteCallback<Path> getCopySuccessCallback( ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path response ) {
                view.hideBusyIndicator();
                notification.fire( new NotificationEvent( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
            }
        };
    }

    private RemoteCallback<Path> getDeleteSuccessCallback( final Path path ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path response ) {
                view.hideBusyIndicator();
                notification.fire( new NotificationEvent( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
            }
        };
    }

    private RemoteCallback<Path> getRenameSuccessCallback( ) {
        return new RemoteCallback<Path>() {
            @Override
            public void callback( final Path targetPath ) {
                view.hideBusyIndicator();
            }
        };
    }

    protected void save() {

        final JavaTypeInfoTO newTypeInfo = new JavaTypeInfoTO(  );
        if ( getContext().isDirty() ) {
            if ( getContext().isEditorChanged() ) {
                newTypeInfo.setPackageName( getContext().getDataObject().getPackageName() );
                newTypeInfo.setName( getContext().getDataObject().getName() );
                save( newTypeInfo );
            } else {
                view.showLoading();
                modelerService.call( new RemoteCallback<TypeInfoResult>() {
                    @Override
                    public void callback( TypeInfoResult typeInfoResult ) {
                        view.hideBusyIndicator();
                        if ( !typeInfoResult.hasErrors() && typeInfoResult.getJavaTypeInfo() != null ) {
                            newTypeInfo.setPackageName( typeInfoResult.getJavaTypeInfo().getPackageName() );
                            newTypeInfo.setName( typeInfoResult.getJavaTypeInfo().getName() );
                        }
                        save( newTypeInfo );
                    }
                } ).loadJavaTypeInfo( getSource() );
            }
        } else {
            save( null );
        }
    }

    private void save( final JavaTypeInfoTO newTypeInfo ) {

        String currentFileName = DataModelerUtils.extractSimpleFileName( versionRecordManager.getPathToLatest() );

        if ( newTypeInfo != null && newTypeInfo.getPackageName() != null && !newTypeInfo.getPackageName().equals( getContext().getEditorModelContent().getOriginalPackageName() ) ) {

            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                    Constants.INSTANCE.modelEditor_confirm_file_package_refactoring( newTypeInfo.getPackageName() ),
                    new Command() {
                        @Override
                        public void execute() {
                            new SaveOperationService().save( versionRecordManager.getPathToLatest(), getSaveCommand( newTypeInfo, versionRecordManager.getPathToLatest() ) );
                        }
                    },
                    Constants.INSTANCE.modelEditor_action_yes_refactor_directory(),
                    ButtonType.PRIMARY,
                    new Command() {
                        @Override
                        public void execute() {
                            new SaveOperationService().save( versionRecordManager.getPathToLatest(), getSaveCommand( null, versionRecordManager.getPathToLatest() ) );
                        }
                    },
                    Constants.INSTANCE.modelEditor_action_no_dont_refactor_directory(),
                    ButtonType.DANGER,
                    new Command() {
                        @Override
                        public void execute() {
                            //do nothing
                        }
                    },
                    null,
                    null
            );

            yesNoCancelPopup.setCloseVisible( false );
            yesNoCancelPopup.show();



        } else if ( currentFileName != null && newTypeInfo != null && !currentFileName.equals( newTypeInfo.getName() ) ) {

            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                                                                                      Constants.INSTANCE.modelEditor_confirm_file_name_refactoring( newTypeInfo.getName() ),
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              new SaveOperationService().save( versionRecordManager.getPathToLatest(), getSaveCommand( newTypeInfo, versionRecordManager.getPathToLatest() ) );
                                                                                          }
                                                                                      },
                                                                                      Constants.INSTANCE.modelEditor_action_yes_refactor_file_name(),
                                                                                      ButtonType.PRIMARY,
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              new SaveOperationService().save( versionRecordManager.getPathToLatest(), getSaveCommand( null, versionRecordManager.getPathToLatest() ) );
                                                                                          }
                                                                                      },
                                                                                      Constants.INSTANCE.modelEditor_action_no_dont_refactor_file_name(),
                                                                                      ButtonType.DANGER,
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              //do nothing
                                                                                          }
                                                                                      },
                                                                                      null,
                                                                                      null
                                                                                    );

            yesNoCancelPopup.setCloseVisible( false );
            yesNoCancelPopup.show();

        } else {
            new SaveOperationService().save( versionRecordManager.getPathToLatest(), getSaveCommand( null, versionRecordManager.getPathToLatest() ) );
        }
    }

    private CommandWithCommitMessage getSaveCommand( final JavaTypeInfoTO newTypeInfo, final Path path ) {
        return new CommandWithCommitMessage() {
            @Override
            public void execute( final String commitMessage ) {

                final DataObjectTO[] modifiedDataObject = new DataObjectTO[ 1 ];
                if ( getContext().isDirty() ) {
                    if ( getContext().isEditorChanged() ) {

                        //at save time the source has always priority over the model.
                        //If the source was properly parsed and the editor has changes, we need to send the DataObject
                        //to the server in order to let the source to be updated prior to save.
                        modifiedDataObject[ 0 ] = getContext().getDataObject();
                    } else {
                        //if the source has changes, no update form the UI to the source will be performed.
                        //instead the parsed DataObject must be returned from the server.
                        modifiedDataObject[ 0 ] = null;
                    }
                }
                view.showSaving();

                if ( newTypeInfo != null ) {
                    modelerService.call( getSaveSuccessCallback( newTypeInfo , path ),
                                     new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_saving_error() ) ).saveSource(
                            getSource(),
                            path,
                            modifiedDataObject[ 0 ],
                            metadata, commitMessage,
                            newTypeInfo.getPackageName(), newTypeInfo.getName() );
                } else {
                    modelerService.call( getSaveSuccessCallback( newTypeInfo , path ),
                            new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_saving_error() ) ).saveSource(
                            getSource(),
                            path,
                            modifiedDataObject[ 0 ],
                            metadata, commitMessage );
                }

            }
        };
    }

    private RemoteCallback<GenerationResult> getSaveSuccessCallback( final JavaTypeInfoTO newTypeInfo, final Path currentPath ) {
        return new RemoteCallback<GenerationResult>() {

            @Override
            public void callback( GenerationResult result ) {

                view.hideBusyIndicator();

                if ( newTypeInfo == null ) {

                    if ( result.hasErrors() ) {
                        getContext().setParseStatus( DataModelerContext.ParseStatus.PARSE_ERRORS );
                        updateEditorView( null );
                        getContext().setDataObject( null );

                        if ( isEditorTabSelected() ) {
                            //un common case

                            showParseErrorsDialog( Constants.INSTANCE.modelEditor_message_file_parsing_errors(),
                                                   true,
                                                   result.getErrors(),
                                                   new Command() {
                                                       @Override
                                                       public void execute() {
                                                           //return to the source tab
                                                           setSelectedTab( EDITABLE_SOURCE_TAB );
                                                       }
                                                   } );

                        }
                    } else {
                        getContext().setParseStatus( DataModelerContext.ParseStatus.PARSED );
                        if ( getContext().isSourceChanged() ) {
                            updateEditorView( result.getDataObject() );
                            getContext().setDataObject( result.getDataObject() );
                        }
                        cleanSystemMessages( getCurrentMessageType() );
                    }

                    setSource( result.getSource() );

                    Boolean oldDirtyStatus = getContext().isDirty();
                    getContext().setDirty( false );
                    setSourceDirty( false );
                    getContext().setEditionStatus( DataModelerContext.EditionStatus.NO_CHANGES );

                    notification.fire( new NotificationEvent( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemSavedSuccessfully()) );
                    dataModelerEvent.fire( new DataModelStatusChangeEvent( DataModelerEvent.DATA_MODEL_BROWSER,
                                                                           getDataModel(),
                                                                           oldDirtyStatus,
                                                                           getContext().isDirty() ) );

                    dataModelerEvent.fire( new DataModelSaved( null, getDataModel() ) );

                    versionRecordManager.reloadVersions( currentPath );

                } else {
                    //If the file was renamed as part of the file saving, don't do anything.
                    //A rename event will arrive, the same as for the "Rename" case.
                    //and the file will be automatically reloaded.
                }
            }
        };
    }

    @Override
    protected void loadContent() {

        modelerService.call( new RemoteCallback<Map<String, AnnotationDefinitionTO>>() {
            @Override
            public void callback( final Map<String, AnnotationDefinitionTO> defs ) {

                context.setAnnotationDefinitions( defs );

                modelerService.call( getLoadModelSuccessCallback(),
                                     new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_loading_error() ) ).loadContent( versionRecordManager.getCurrentPath() );
            }
        }, new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_annotationDef_loading_error() )
                           ).getAnnotationDefinitions();
    }

    private RemoteCallback<EditorModelContent> getLoadModelSuccessCallback() {
        return new RemoteCallback<EditorModelContent>() {

            @Override
            public void callback( EditorModelContent content) {

                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                javaSourceEditor.setReadonly( isReadOnly );
                getContext().setDirty( false );
                getContext().setReadonly( isReadOnly );
                getContext().setEditionStatus( DataModelerContext.EditionStatus.NO_CHANGES );
                getContext().setEditorModelContent(content);
                setModel(content);

                resetEditorPages( content.getOverview() );

                addPage(new Page(javaSourceEditor,
                        org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.SourceTabTitle()) {
                    @Override
                    public void onFocus() {
                        if (uiStarted) {
                            onSourceTabSelected();
                        }
                    }

                    @Override
                    public void onLostFocus() {
                    }
                });

                view.hideBusyIndicator();

                if ( content.hasErrors() ) {
                    publishSystemMessages( getCurrentMessageType(), true, content.getErrors() );
                }
                if ( content.getDataObject() != null ) {
                    setSelectedTab( EDITOR_TAB_INDEX );
                    uiStarted = true;
                } else {
                    showParseErrorsDialog(Constants.INSTANCE.modelEditor_message_file_parsing_errors(),
                            false,
                            getContext().getEditorModelContent().getErrors(),
                            new Command() {
                                @Override
                                public void execute() {
                                    //we need to go directly to the sources tab
                                    uiStarted = true;
                                    //onSourceTabSelected();
                                    setSelectedTab( EDITABLE_SOURCE_TAB );
                                }
                    });
                }
            }
        };
    }

    private void rename( final boolean saveCurrentChanges ) {

        final DataObjectTO[] modifiedDataObject = new DataObjectTO[ 1 ];
        if ( saveCurrentChanges ) {
            if ( getContext().isDirty() ) {
                if ( getContext().isEditorChanged() ) {
                    //at save time the source has always priority over the model.
                    //If the source was properly parsed and the editor has changes, we need to send the DataObject
                    //to the server in order to let the source to be updated prior to save.
                    modifiedDataObject[ 0 ] = getContext().getDataObject();
                } else {
                    //if the source has changes, no update form the UI to the source will be performed.
                    //instead the parsed DataObject must be returned from the server.
                    modifiedDataObject[ 0 ] = null;
                }
            }
        }

        final RenamePopup popup = new RenamePopup( versionRecordManager.getPathToLatest(),
                                                   javaFileNameValidator,
                                                   new CommandWithFileNameAndCommitMessage() {
                                                       @Override
                                                       public void execute( final FileNameAndCommitMessage details ) {
                                                           view.showBusyIndicator( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Renaming() );

                                                           modelerService.call( getRenameSuccessCallback( ),
                                                                                new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_renaming_error() ) ).rename( versionRecordManager.getPathToLatest(),
                                                                   details.getNewFileName(),
                                                                   details.getCommitMessage(),
                                                                   true,
                                                                   saveCurrentChanges,
                                                                   getSource(), modifiedDataObject[ 0 ], metadata );
                                                       }
                                                   } );
        popup.show();
    }

    public DataModelTO getDataModel() {
        return context.getDataModel();
    }

    public DataModelerContext getContext() {
        return context;
    }

    public String getSource() {
        return javaSourceEditor.getContent();
    }

    public void setSource( String source ) {
        javaSourceEditor.setContent( source );
    }

    private void setSourceDirty( boolean dirty ) {
        javaSourceEditor.setDirty( dirty );
    }

    private boolean isSourceTabSelected() {
        return getSelectedTabIndex() == EDITABLE_SOURCE_TAB;
    }

    private void setModel( EditorModelContent model ) {

        view.setContext( context );
        setSource( model.getSource() );

        if ( model.getDataObject() != null ) {
            getContext().setParseStatus( DataModelerContext.ParseStatus.PARSED );
            dataModelerEvent.fire( new DataObjectSelectedEvent( DataModelerEvent.DATA_MODEL_BROWSER, getDataModel(), model.getDataObject() ) );
        } else {
            getContext().setParseStatus( DataModelerContext.ParseStatus.PARSE_ERRORS );
            dataModelerEvent.fire( new DataObjectSelectedEvent( DataModelerEvent.DATA_MODEL_BROWSER, getDataModel(), null ) );
        }
    }

    protected void onSourceTabSelected() {

        if ( getContext().isParsed() && getContext().isEditorChanged() ) {

            //If there are changes in the ui the source must be regenerated on server side.
            view.showLoading();
            modelerService.call( new RemoteCallback<GenerationResult>() {
                @Override
                public void callback( GenerationResult result ) {
                    view.hideBusyIndicator();
                    setSource( result.getSource() );
                    updateSource(result.getSource());
                    getContext().setEditionStatus( DataModelerContext.EditionStatus.NO_CHANGES );
                }
            }, new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_loading_error() ) ).updateSource( getSource(), versionRecordManager.getCurrentPath(), getContext().getDataObject() );
        } else {
            if ( !isOverviewTabSelected() ) {
                getContext().setEditionStatus( DataModelerContext.EditionStatus.NO_CHANGES );
            }
            updateSource(javaSourceEditor.getContent());
        }
    }

    @Override
    protected void updateSource( String source ) {
        setSource( source );
    }

    private void updateEditorView( DataObjectTO dataObjectTO ) {
        //here we need to check if data object name, or package, changed, etc.
        //if this is the likely we can show an alert to the user, etc.
        //also the file should be renamed.

        if ( getContext().getDataObject() != null ) {
            getContext().getDataModel().removeDataObject( getContext().getDataObject().getClassName() );
        }
        if ( dataObjectTO != null ) {
            getContext().getDataModel().removeDataObject( dataObjectTO.getClassName() );
            getContext().getDataModel().getDataObjects().add( dataObjectTO );
        }
        dataModelerEvent.fire( new DataObjectSelectedEvent( DataModelerEvent.DATA_MODEL_BROWSER, getDataModel(), dataObjectTO ) );
    }

    @Override
    protected void onEditTabSelected()  {

        boolean doParsing = false;
        if ( getContext().isSourceChanged() ) {
            //if there has been changes in the source we should try to parse the file and build the data object again.
            doParsing = true;
        } else if ( getContext().isNotParsed() ) {
            //uncommon case, the file wasn't parsed yet.
            doParsing = true;
        }

        if ( doParsing ) {

            view.showLoading();

            //If there are changes in the source, we must try to parse the file.
            modelerService.call( new RemoteCallback<GenerationResult>() {
                @Override
                public void callback( GenerationResult result ) {
                    view.hideBusyIndicator();
                    if ( result.hasErrors() ) {

                        showParseErrorsDialog( Constants.INSTANCE.modelEditor_message_file_parsing_errors(),
                                               true,
                                               result.getErrors(),
                                               new Command() {
                                                   @Override
                                                   public void execute() {
                                                       //return to the source tab
                                                       setSelectedTab(EDITABLE_SOURCE_TAB);
                                                       getContext().setParseStatus( DataModelerContext.ParseStatus.PARSE_ERRORS );
                                                       updateEditorView( null );
                                                       getContext().setDataObject( null );
                                                   }
                                               } );

                    } else {
                        //ok, we can reload the editor tab.
                        getContext().setParseStatus( DataModelerContext.ParseStatus.PARSED );
                        updateEditorView( result.getDataObject() );
                        setSourceDirty( false );
                        getContext().setEditionStatus( DataModelerContext.EditionStatus.NO_CHANGES );
                        getContext().setDataObject( result.getDataObject() );
                        cleanSystemMessages( getCurrentMessageType() );
                    }
                }
            }, new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_loading_error() ) ).updateDataObject( getContext().getDataObject(), getSource(), versionRecordManager.getCurrentPath() );
        } else {
            //no changes in the source tab
            if ( !isOverviewTabSelected() ) {
                getContext().setEditionStatus( DataModelerContext.EditionStatus.NO_CHANGES );
            }

            if ( getContext().isParseErrors() ) {
                //there are parse errors, the editor tab couldn't be loaded.  (errors are already published)
                showParseErrorsDialog( Constants.INSTANCE.modelEditor_message_file_parsing_errors(),
                                       false,
                                       null,
                                       new Command() {
                                           @Override
                                           public void execute() {
                                               setSelectedTab(EDITABLE_SOURCE_TAB);
                                           }
                                       } );
            }
        }
    }

    private void showParseErrorsDialog( final String message,
                                        final boolean publishErrors,
                                        final List<DataModelerError> errors,
                                        final Command command ) {

        if ( publishErrors && errors != null && !errors.isEmpty() ) {
            publishSystemMessages( getCurrentMessageType(), true, errors );
        }

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                                                                                  message,
                                                                                  new Command() {
                                                                                      @Override
                                                                                      public void execute() {
                                                                                          command.execute();
                                                                                      }
                                                                                  },
                                                                                  CommonConstants.INSTANCE.OK(),
                                                                                  null,
                                                                                  null,
                                                                                  null,
                                                                                  null
                                                                                );
        yesNoCancelPopup.setCloseVisible( false );
        yesNoCancelPopup.show();
    }

    private boolean isOpen() {
        return open;
    }

    private void onDataObjectDeleted( @Observes DataObjectDeletedEvent event ) {
        if ( getContext() != null &&
                event.isFrom( getContext().getCurrentProject() ) &&
                event.getCurrentDataObject() != null &&
                getContext().isParsed() &&
                isEditorTabSelected() &&
                getContext().getDataObject() != null &&
                !getContext().getDataObject().getClassName().equals( event.getCurrentDataObject().getClassName() ) ) {

            //check deleted object is referenced by current data object.
            if ( validatorService.isReferencedByCurrentObject( event.getCurrentDataObject(), getContext().getDataObject() ) ) {
                notification.fire( new NotificationEvent( Constants.INSTANCE.modelEditor_notification_dataObject_referenced_has_been_deleted( event.getCurrentDataObject().getClassName(), getContext().getDataObject().getClassName() ) ) );
            } else if ( !getDataModel().isExternal( event.getCurrentDataObject().getClassName() ) ) {
                getDataModel().removeDataObject( event.getCurrentDataObject().getClassName() );
                view.refreshTypeLists( true );
            }
        }
    }

    private void onDataObjectCreated( @Observes DataObjectCreatedEvent event ) {
        if ( getContext() != null &&
                event.isFrom( getContext().getCurrentProject() ) &&
                event.getCurrentDataObject() != null &&
                getDataModel() != null &&
                getDataModel().getDataObjectByClassName( event.getCurrentDataObject().getClassName() ) == null ) {

            getDataModel().getDataObjects().add( event.getCurrentDataObject() );
            view.refreshTypeLists( true );
        }
    }

    private void cleanSystemMessages( String currentMessageType ) {
        UnpublishMessagesEvent unpublishMessage = new UnpublishMessagesEvent();
        unpublishMessage.setShowSystemConsole( false );
        unpublishMessage.setMessageType( currentMessageType );
        unpublishMessage.setUserId( ( sessionInfo != null && sessionInfo.getIdentity() != null ) ? sessionInfo.getIdentity().getIdentifier() : null );
        unpublishMessagesEvent.fire( unpublishMessage );
    }

    private void publishSystemMessages( String messageType,
                                        boolean cleanExisting,
                                        List<DataModelerError> errors ) {
        PublishBatchMessagesEvent publishMessage = new PublishBatchMessagesEvent();
        publishMessage.setCleanExisting( cleanExisting );
        publishMessage.setMessageType( messageType );
        publishMessage.setUserId( ( sessionInfo != null && sessionInfo.getIdentity() != null ) ? sessionInfo.getIdentity().getIdentifier() : null );
        publishMessage.setPlace( PublishBaseEvent.Place.TOP );
        SystemMessage systemMessage;
        for ( DataModelerError error : errors ) {
            systemMessage = new SystemMessage();
            systemMessage.setMessageType( messageType );
            systemMessage.setId( error.getId() );
            systemMessage.setText( error.getMessage() );
            systemMessage.setPath( error.getFile() );
            systemMessage.setLevel( error.getLevel() );
            systemMessage.setLine( error.getLine() );
            systemMessage.setColumn( error.getColumn() );
            publishMessage.getMessagesToPublish().add( systemMessage );
        }
        publishBatchMessagesEvent.fire( publishMessage );
    }

    protected void makeMenuBar() {

        menus = menuBuilder
                .addSave( versionRecordManager.newSaveMenuItem( new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                } ) )
                .addCopy( new Command() {
                    @Override
                    public void execute() {
                        onCopy();
                    }
                } )
                .addRename( new Command() {
                    @Override
                    public void execute() {
                        onSafeRename();
                    }
                } )
                .addDelete( new Command() {
                    @Override
                    public void execute() {
                        onSafeDelete();
                    }
                } )
                .addValidate(
                        onValidate()
                )
                .addNewTopLevelMenu( versionRecordManager.buildMenu() )
                .build();

    }

    private void initContext( final ObservablePath path ) {
        context = new DataModelerContext();

        modelerService.call(
                new RemoteCallback<List<PropertyTypeTO>>() {
                    @Override
                    public void callback( List<PropertyTypeTO> baseTypes ) {
                        context.init( baseTypes );
                    }
                },
                new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_propertyType_loading_error() )
        ).getBasePropertyTypes();
        /*
        modelerService.call(
                new RemoteCallback<Set<Package>>() {
                    @Override
                    public void callback( Set<Package> packages ) {
                        context.appendPackages( packages );
                    }
                },
                new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_loading_error() )
        ).resolvePackages( path );
        */
    }

    private void clearContext() {
        context.clear();
    }

    private String getCurrentMessageType() {
        return currentMessageType;
    }

    private static String printSessionInfo( SessionInfo sessionInfo ) {
        if ( sessionInfo != null ) {
            return " [id: " + sessionInfo.getId() + ", identity: " + ( sessionInfo.getIdentity() != null ? sessionInfo.getIdentity().getIdentifier() : null ) + " ]";
        }
        return null;
    }

    private static String getSessionInfoIdentity( SessionInfo sessionInfo ) {
        return sessionInfo != null && sessionInfo.getIdentity() != null ? sessionInfo.getIdentity().getIdentifier() : null;
    }

}