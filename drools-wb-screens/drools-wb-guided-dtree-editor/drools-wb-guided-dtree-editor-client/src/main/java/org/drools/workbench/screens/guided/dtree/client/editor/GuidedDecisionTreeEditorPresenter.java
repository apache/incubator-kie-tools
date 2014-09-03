/*
* Copyright 2014 JBoss Inc
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
package org.drools.workbench.screens.guided.dtree.client.editor;

import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.screens.guided.dtree.client.type.GuidedDTreeResourceType;
import org.drools.workbench.screens.guided.dtree.model.GuidedDecisionTreeEditorContent;
import org.drools.workbench.screens.guided.dtree.service.GuidedDecisionTreeEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
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
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Guided Decision Tree Editor Presenter
 */
@WorkbenchEditor(identifier = "GuidedDecisionTreeEditorPresenter", supportedTypes = { GuidedDTreeResourceType.class }, priority = 101)
public class GuidedDecisionTreeEditorPresenter
        extends KieEditor {

    @Inject
    private ImportsWidgetPresenter importsWidget;

    @Inject
    private Caller<GuidedDecisionTreeEditorService> service;

    @Inject
    private Caller<RuleNamesService> ruleNameService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private GuidedDTreeResourceType type;

    @Inject
    private AsyncPackageDataModelOracleFactory oracleFactory;

    private GuidedDecisionTree model;
    private AsyncPackageDataModelOracle oracle;
    private GuidedDecisionTreeEditorContent content;

    private GuidedDecisionTreeEditorView view;

    public GuidedDecisionTreeEditorPresenter() {
    }

    @Inject
    public GuidedDecisionTreeEditorPresenter( final GuidedDecisionTreeEditorView baseView ) {
        super( baseView );
        view = baseView;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.init( path,
                    place,
                    type );
    }

    protected void loadContent() {
        view.showLoading();
        service.call( getModelSuccessCallback(),
                      getNoSuchFileExceptionErrorCallback() ).loadContent( versionRecordManager.getCurrentPath() );
    }

    private RemoteCallback<GuidedDecisionTreeEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<GuidedDecisionTreeEditorContent>() {

            @Override
            public void callback( final GuidedDecisionTreeEditorContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                GuidedDecisionTreeEditorPresenter.this.content = content;

                model = content.getModel();
                metadata = content.getOverview().getMetadata();
                final PackageDataModelOracleBaselinePayload dataModel = content.getDataModel();
                oracle = oracleFactory.makeAsyncPackageDataModelOracle( versionRecordManager.getCurrentPath(),
                                                                        model,
                                                                        dataModel );

                resetEditorPages( content.getOverview() );

                addSourcePage();
                addImportsTab( importsWidget );
                importsWidget.setContent( oracle,
                                          model.getImports(),
                                          isReadOnly );

                view.hideBusyIndicator();
            }
        };
    }

    @Override
    protected void onEditTabSelected() {
        view.setContent( versionRecordManager.getCurrentPath(),
                         model,
                         oracle,
                         ruleNameService,
                         isReadOnly );
    }

    @Override
    protected void onSourceTabSelected() {
        service.call( new RemoteCallback<String>() {
            @Override
            public void callback( final String source ) {
                updateSource( source );
            }
        } ).toSource( versionRecordManager.getCurrentPath(),
                      model );
    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                service.call( new RemoteCallback<List<ValidationMessage>>() {
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
                                                 view.showSaving();
                                                 service.call( getSaveSuccessCallback(),
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
        return super.getWidget();
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

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
