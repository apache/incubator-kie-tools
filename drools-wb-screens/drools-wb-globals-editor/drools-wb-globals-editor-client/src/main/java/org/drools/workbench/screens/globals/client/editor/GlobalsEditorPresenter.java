package org.drools.workbench.screens.globals.client.editor;

import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.drools.workbench.screens.globals.client.type.GlobalResourceType;
import org.drools.workbench.screens.globals.model.GlobalsEditorContent;
import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.MultiPageEditor;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.editor.KieEditor;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

/**
 * Globals Editor Presenter
 */
@WorkbenchEditor(identifier = "org.kie.guvnor.globals", supportedTypes = { GlobalResourceType.class }, priority = 101)
public class GlobalsEditorPresenter
    extends KieEditor {

    @Inject
    private Caller<GlobalsEditorService> globalsEditorService;

    private GlobalsEditorView view;

    @Inject
    private OverviewWidgetPresenter overview;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private GlobalResourceType type;

    private GlobalsModel model;

    private Metadata metadata;

    public GlobalsEditorPresenter() {
    }

    @Inject
    public GlobalsEditorPresenter(GlobalsEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.init(path, place);
    }

    protected void loadContent() {
        globalsEditorService.call( getModelSuccessCallback(),
                                   new CommandDrivenErrorCallback( view,
                                                                   new CommandBuilder().addNoSuchFileException( view,
                                                                                                                multiPage,
                                                                                                                menus ).build()
                                   ) ).loadContent( versionRecordManager.getCurrentPath() );
    }

    private RemoteCallback<GlobalsEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<GlobalsEditorContent>() {

            @Override
            public void callback( final GlobalsEditorContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                multiPage.clear();

                multiPage.addWidget(overview,
                        CommonConstants.INSTANCE.Overview());
                overview.setContent(content.getOverview(), versionRecordManager.getCurrentPath());

                versionRecordManager.setVersions(content.getOverview().getMetadata().getVersion());

                multiPage.addWidget(view,
                        CommonConstants.INSTANCE.EditTabTitle());


                metadata = content.getOverview().getMetadata();
                model = content.getModel();
                final List<String> fullyQualifiedClassNames = content.getFullyQualifiedClassNames();

                view.setContent( content.getModel().getGlobals(),
                                 fullyQualifiedClassNames,
                                 isReadOnly );

                view.hideBusyIndicator();
            }
        };
    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                globalsEditorService.call( new RemoteCallback<List<ValidationMessage>>() {
                    @Override
                    public void callback( final List<ValidationMessage> results ) {
                        if ( results == null || results.isEmpty() ) {
                            notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                                                      NotificationEvent.NotificationType.SUCCESS ) );
                        } else {
                            ValidationPopup.showMessages( results );
                        }
                    }
                }, new DefaultErrorCallback() ).validate( versionRecordManager.getCurrentPath(),
                                                          model );
            }
        };
    }

    protected void save() {
        new SaveOperationService().save( versionRecordManager.getCurrentPath(),
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {
                                                 view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 globalsEditorService.call( getSaveSuccessCallback(),
                                                                            new HasBusyIndicatorDefaultErrorCallback( view ) ).save( versionRecordManager.getCurrentPath(),
                                                                                                                                     model,
                                                                                                                                     metadata,
                                                                                                                                     comment );
                                             }
                                         }
                                       );
        concurrentUpdateSessionInfo = null;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
    }

    @IsDirty
    public boolean isDirty() {
        if ( isReadOnly ) {
            return false;
        }
        return ( view.isDirty() );
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        String fileName = FileNameUtil.removeExtension( versionRecordManager.getCurrentPath(),
                                                        type );
        if ( versionRecordManager.getVersion() != null ) {
            fileName = fileName + " v" + versionRecordManager.getVersion();
        }

        if ( isReadOnly ) {
            return GlobalsEditorConstants.INSTANCE.globalsEditorReadOnlyTitle0( fileName );
        }
        return GlobalsEditorConstants.INSTANCE.globalsEditorTitle0( fileName );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
