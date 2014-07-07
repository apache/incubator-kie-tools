package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.uberfire.client.common.MultiPageEditor;
import org.kie.uberfire.client.common.Page;
import org.kie.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnSave;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

public class GuvnorTextEditorPresenter
        extends TextEditorPresenter {

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<DefaultEditorService> defaultEditorService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    protected FileMenuBuilder menuBuilder;
    protected Menus menus;

    @Inject
    private MetadataWidget metadataWidget;

    @Inject
    private PlaceManager placeManager;

    private boolean isReadOnly;
    private String version;
    protected ObservablePath path;

    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.onStartup( path );

        this.path = path;

        this.path.onDelete( new Command() {
            @Override
            public void execute() {
                placeManager.forceClosePlace( place );
            }
        } );

        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        this.version = place.getParameter( "version", null );

        makeMenuBar();

        if ( isReadOnly ) {
            view.makeReadOnly();
        }

        multiPage.addWidget( super.getWidget(),
                             CommonConstants.INSTANCE.EditTabTitle() );

        multiPage.addPage( new Page( metadataWidget,
                                     CommonConstants.INSTANCE.MetadataTabTitle() ) {
            @Override
            public void onFocus() {
                    metadataWidget.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                    metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                       isReadOnly ),
                                          new HasBusyIndicatorDefaultErrorCallback( metadataWidget )
                                        ).getMetadata( path );
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        } );
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addSave(
                            new Command() {
                                @Override
                                public void execute() {
                                    onSave();
                                }
                            }
                            )
                    .addCopy( path,
                              fileNameValidator )
                    .addRename( path,
                                fileNameValidator )
                    .addDelete( path )
                    .build();
        }
    }

    @OnSave
    public void onSave() {
        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 defaultEditorService.call( getSaveSuccessCallback(),
                                                                            new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).save( path,
                                                                                                                                                  view.getContent(),
                                                                                                                                                  metadataWidget.getContent(),
                                                                                                                                                  commitMessage );
                                             }
                                         }
                                       );
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                view.setDirty( false );
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        };
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        String fileName = path.getFileName();
        if ( version != null ) {
            fileName = fileName + " v" + version;
        }
        return "Text Editor [" + fileName + "]";
    }

    @IsDirty
    public boolean isDirty() {
        return super.isDirty() || metadataWidget.isDirty();
    }

    public IsWidget getWidget() {
        return multiPage;
    }

}