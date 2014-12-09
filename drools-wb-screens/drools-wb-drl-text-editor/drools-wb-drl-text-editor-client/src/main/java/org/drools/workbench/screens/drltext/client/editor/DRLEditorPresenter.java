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

package org.drools.workbench.screens.drltext.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.screens.drltext.client.type.DRLResourceType;
import org.drools.workbench.screens.drltext.client.type.DSLRResourceType;
import org.drools.workbench.screens.drltext.model.DrlModelContent;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 * This is the default rule editor widget (just text editor based).
 */
@Dependent
@WorkbenchEditor(identifier = "DRLEditor", supportedTypes = { DRLResourceType.class, DSLRResourceType.class })
public class DRLEditorPresenter
        extends KieEditor {

    @Inject
    private Caller<DRLTextEditorService> drlTextEditorService;

    @Inject
    private Event<NotificationEvent> notification;

    private DRLEditorView view;

    @Inject
    private DRLResourceType resourceTypeDRL;

    @Inject
    private DSLRResourceType resourceTypeDSLR;

    private boolean isDSLR;

    @Inject
    public DRLEditorPresenter( final DRLEditorView view ) {
        super( view );
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.init( path,
                    place,
                    getResourceType( path ) );
        this.isDSLR = resourceTypeDSLR.accept( path );
    }

    protected void loadContent() {
        view.showLoading();
        drlTextEditorService.call( getLoadContentSuccessCallback(),
                                   getNoSuchFileExceptionErrorCallback() ).loadContent( versionRecordManager.getCurrentPath() );
    }

    private RemoteCallback<DrlModelContent> getLoadContentSuccessCallback() {
        return new RemoteCallback<DrlModelContent>() {

            @Override
            public void callback( final DrlModelContent content ) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                resetEditorPages( content.getOverview() );
                addSourcePage();

                final String drl = assertContent( content.getDrl() );
                final List<String> fullyQualifiedClassNames = content.getFullyQualifiedClassNames();
                final List<DSLSentence> dslConditions = content.getDslConditions();
                final List<DSLSentence> dslActions = content.getDslActions();

                //Populate view
                if ( isDSLR ) {
                    view.setContent( drl,
                                     fullyQualifiedClassNames,
                                     dslConditions,
                                     dslActions );
                } else {
                    view.setContent( drl,
                                     fullyQualifiedClassNames );
                }
                view.hideBusyIndicator();
                setOriginalHash(view.getContent().hashCode());
            }

            private String assertContent( final String drl ) {
                if ( drl == null || drl.isEmpty() ) {
                    return "";
                }
                return drl;
            }

        };
    }

    public void loadClassFields( final String fullyQualifiedClassName,
                                 final Callback<List<String>> callback ) {
        drlTextEditorService.call( getLoadClassFieldsSuccessCallback( callback ),
                                   new HasBusyIndicatorDefaultErrorCallback( view ) ).loadClassFields( versionRecordManager.getCurrentPath(),
                                                                                                       fullyQualifiedClassName );

    }

    private RemoteCallback<List<String>> getLoadClassFieldsSuccessCallback( final Callback<List<String>> callback ) {
        return new RemoteCallback<List<String>>() {

            @Override
            public void callback( final List<String> fields ) {
                callback.callback( fields );
            }
        };
    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                drlTextEditorService.call( new RemoteCallback<List<ValidationMessage>>() {
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
                                                 drlTextEditorService.call( getSaveSuccessCallback(view.getContent().hashCode()),
                                                                            new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                                                                                 view.getContent(),
                                                                                                                                 metadata,
                                                                                                                                 commitMessage );
                                             }
                                         }
                                       );
        concurrentUpdateSessionInfo = null;
    }

    @OnClose
    public void onClose() {
        versionRecordManager.clear();
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(view.getContent().hashCode());
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @Override
    protected void onSourceTabSelected() {
        updateSource( view.getContent() );
    }

    private ClientResourceType getResourceType( Path path ) {
        if ( resourceTypeDRL.accept( path ) ) {
            return resourceTypeDRL;
        } else {
            return resourceTypeDSLR;
        }
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
