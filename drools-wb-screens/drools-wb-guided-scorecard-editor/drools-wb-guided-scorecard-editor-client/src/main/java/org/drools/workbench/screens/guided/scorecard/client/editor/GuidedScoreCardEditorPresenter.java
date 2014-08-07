/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.scorecard.client.editor;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.client.resources.i18n.GuidedScoreCardConstants;
import org.drools.workbench.screens.guided.scorecard.client.type.GuidedScoreCardResourceType;
import org.drools.workbench.screens.guided.scorecard.model.ScoreCardModelContent;
import org.drools.workbench.screens.guided.scorecard.service.GuidedScoreCardEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.MultiPageEditor;
import org.kie.uberfire.client.common.Page;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.datamodel.ImportAddedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportRemovedEvent;
import org.kie.workbench.common.widgets.client.editor.GuvnorEditor;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.widgets.viewsource.client.callbacks.ViewSourceSuccessCallback;
import org.kie.workbench.common.widgets.viewsource.client.screen.ViewSourceView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

@Dependent
@WorkbenchEditor(identifier = "GuidedScoreCardEditor", supportedTypes = { GuidedScoreCardResourceType.class })
public class GuidedScoreCardEditorPresenter
    extends GuvnorEditor {

    @Inject
    private Caller<GuidedScoreCardEditorService> scoreCardEditorService;

    @Inject
    private OverviewWidgetPresenter overview;

    private GuidedScoreCardEditorView view;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private GuidedScoreCardResourceType type;

    @Inject
    private AsyncPackageDataModelOracleFactory oracleFactory;


    private ScoreCardModel model;
    private AsyncPackageDataModelOracle oracle;

    @Inject
    private ImportsWidgetPresenter importsWidget;

    private Metadata metadata;

    @Inject
    public GuidedScoreCardEditorPresenter(GuidedScoreCardEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.init(path, place);
    }

    protected void loadContent() {
        scoreCardEditorService.call( getModelSuccessCallback(),
                                     new CommandDrivenErrorCallback( view,
                                                                     new CommandBuilder().addNoSuchFileException( view,
                                                                                                                  multiPage,
                                                                                                                  menus ).build()
                                     ) ).loadContent( versionRecordManager.getCurrentPath() );
    }

    private RemoteCallback<ScoreCardModelContent> getModelSuccessCallback() {
        return new RemoteCallback<ScoreCardModelContent>() {

            @Override
            public void callback( final ScoreCardModelContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                metadata = content.getOverview().getMetadata();

                multiPage.clear();
                multiPage.addWidget(overview,
                        CommonConstants.INSTANCE.Overview());
                overview.setContent(content.getOverview(), versionRecordManager.getCurrentPath());

                versionRecordManager.setVersions(content.getOverview().getMetadata().getVersion());

                multiPage.addWidget(view,
                        CommonConstants.INSTANCE.EditTabTitle());

                multiPage.addWidget(importsWidget,
                        CommonConstants.INSTANCE.ConfigTabTitle());

                model = content.getModel();
                final PackageDataModelOracleBaselinePayload dataModel = content.getDataModel();
                oracle = oracleFactory.makeAsyncPackageDataModelOracle( versionRecordManager.getCurrentPath(),
                                                                        model,
                                                                        dataModel );

                view.setContent( model,
                                 oracle );
                importsWidget.setContent( oracle,
                                          model.getImports(),
                                          isReadOnly );

                view.hideBusyIndicator();
            }
        };
    }

    public void handleImportAddedEvent( @Observes ImportAddedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        view.refreshFactTypes();
    }

    public void handleImportRemovedEvent( @Observes ImportRemovedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        view.refreshFactTypes();
    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                scoreCardEditorService.call( new RemoteCallback<List<ValidationMessage>>() {
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
                                                          view.getModel() );
            }
        };
    }

    protected void save() {
        new SaveOperationService().save( versionRecordManager.getCurrentPath(),
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {
                                                 view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 scoreCardEditorService.call( getSaveSuccessCallback(),
                                                                              new HasBusyIndicatorDefaultErrorCallback( view ) ).save( versionRecordManager.getCurrentPath(),
                                                                                                                                       view.getModel(),
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
        this.oracleFactory.destroy( oracle );
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
            return "Read Only Score Card Viewer [" + fileName + "]";
        }
        return GuidedScoreCardConstants.INSTANCE.ScoreCardEditorTitle() + " [" + fileName + "]";
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
