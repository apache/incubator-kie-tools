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

package org.drools.workbench.screens.guided.template.client.editor;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.client.resources.i18n.GuidedTemplateEditorConstants;
import org.drools.workbench.screens.guided.template.client.type.GuidedRuleTemplateResourceType;
import org.drools.workbench.screens.guided.template.model.GuidedTemplateEditorContent;
import org.drools.workbench.screens.guided.template.service.GuidedRuleTemplateEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.datamodel.ImportAddedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportRemovedEvent;
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
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.Page;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "GuidedRuleTemplateEditor", supportedTypes = { GuidedRuleTemplateResourceType.class })
public class GuidedRuleTemplateEditorPresenter
        extends KieEditor {

    private GuidedRuleTemplateEditorView view;

    @Inject
    private GuidedRuleTemplateDataView dataView;

    @Inject
    private ImportsWidgetPresenter importsWidget;

    @Inject
    private Caller<GuidedRuleTemplateEditorService> service;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private GuidedRuleTemplateResourceType type;

    @Inject
    private AsyncPackageDataModelOracleFactory oracleFactory;

    private EventBus eventBus = new SimpleEventBus();

    private TemplateModel model;
    private AsyncPackageDataModelOracle oracle;

    @Inject
    private Caller<RuleNamesService> ruleNamesService;

    @Inject
    public GuidedRuleTemplateEditorPresenter( final GuidedRuleTemplateEditorView baseView ) {
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

    private RemoteCallback<GuidedTemplateEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<GuidedTemplateEditorContent>() {

            @Override
            public void callback( final GuidedTemplateEditorContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                resetEditorPages( content.getOverview() );
                addSourcePage();

                addPage( new Page( dataView,
                                   GuidedTemplateEditorConstants.INSTANCE.Data() ) {

                    @Override
                    public void onFocus() {
                        dataView.setContent( model,
                                             oracle,
                                             eventBus,
                                             isReadOnly );
                    }

                    @Override
                    public void onLostFocus() {
                        // Nothing to do here
                    }
                } );

                addImportsTab( importsWidget );

                model = content.getModel();
                final PackageDataModelOracleBaselinePayload dataModel = content.getDataModel();
                oracle = oracleFactory.makeAsyncPackageDataModelOracle( versionRecordManager.getCurrentPath(),
                                                                        model,
                                                                        dataModel );

                view.setContent( versionRecordManager.getCurrentPath(),
                                 model,
                                 oracle,
                                 ruleNamesService,
                                 eventBus,
                                 isReadOnly );
                importsWidget.setContent( oracle,
                                          model.getImports(),
                                          isReadOnly );

                setOriginalHash(model.hashCode());
                view.hideBusyIndicator();
            }
        };
    }

    public void handleImportAddedEvent( @Observes ImportAddedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        view.refresh();
    }

    public void handleImportRemovedEvent( @Observes ImportRemovedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        view.refresh();
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
                                                          view.getContent() );
            }
        };
    }

    protected void save() {
        new SaveOperationService().save( versionRecordManager.getCurrentPath(),
                                         new ParameterizedCommand<String>() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 view.showSaving();
                                                 service.call( getSaveSuccessCallback(model.hashCode()),
                                                               new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                                                                    view.getContent(),
                                                                                                                    metadata,
                                                                                                                    commitMessage );
                                             }
                                         }
                                       );
        concurrentUpdateSessionInfo = null;
    }

    @Override
    protected void onSourceTabSelected() {
        service.call( new RemoteCallback<String>() {
            @Override
            public void callback( String source ) {
                updateSource( source );
            }
        } ).toSource( versionRecordManager.getCurrentPath(), model );
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
        this.oracleFactory.destroy( oracle );
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(view.getContent().hashCode());
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
