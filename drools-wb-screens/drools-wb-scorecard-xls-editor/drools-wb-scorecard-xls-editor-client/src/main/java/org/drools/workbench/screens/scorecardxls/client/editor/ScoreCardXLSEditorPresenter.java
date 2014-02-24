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

package org.drools.workbench.screens.scorecardxls.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.drools.workbench.screens.scorecardxls.service.ScoreCardXLSService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

import static org.uberfire.client.common.ConcurrentChangePopup.*;

@Dependent
@WorkbenchEditor(identifier = "ScoreCardXLSEditor", supportedTypes = { ScoreCardXLSResourceType.class })
public class ScoreCardXLSEditorPresenter
        implements ScoreCardXLSEditorView.Presenter {

    @Inject
    private Caller<ScoreCardXLSService> scoreCardXLSService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ScoreCardXLSEditorView view;

    @Inject
    private MetadataWidget metadataWidget;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private ScoreCardXLSResourceType type;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Menus menus;

    private ObservablePath path;
    private PlaceRequest place;
    private boolean isReadOnly;
    private String version;

    @PostConstruct
    public void setup() {
        view.init( this );
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        this.path = path;
        this.place = place;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        this.version = place.getParameter( "version", null );

        this.path.onRename( new Command() {
            @Override
            public void execute() {
                changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitle(), null ) );
            }
        } );
        this.path.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                view.setConcurrentUpdateSessionInfo( eventInfo );
            }
        } );

        this.path.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentRenameEvent info ) {
                newConcurrentRename( info.getSource(),
                                     info.getTarget(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             reload();
                                         }
                                     }
                                   ).show();
            }
        } );

        this.path.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentDelete info ) {
                newConcurrentDelete( info.getPath(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             placeManager.closePlace( place );
                                         }
                                     }
                                   ).show();
            }
        } );

        makeMenuBar();

        multiPage.addWidget( view,
                             ScoreCardXLSEditorConstants.INSTANCE.ScoreCard() );

        multiPage.addPage( new Page( metadataWidget,
                                     CommonConstants.INSTANCE.MetadataTabTitle() ) {
            @Override
            public void onFocus() {
                if ( !metadataWidget.isAlreadyLoaded() ) {
                    metadataWidget.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                    metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                       isReadOnly ),
                                          new HasBusyIndicatorDefaultErrorCallback( metadataWidget )
                                        ).getMetadata( path );
                }
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        } );

        view.setPath( path );
        view.setReadOnly( isReadOnly );
    }

    public void reload() {
        changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitle(), null ) );
    }

    private void disableMenus() {
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.COPY ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.RENAME ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.DELETE ).setEnabled( false );
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.VALIDATE ).setEnabled( false );
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addCopy( path,
                              fileNameValidator )
                    .addRename( path,
                                fileNameValidator )
                    .addDelete( path )
                    .addValidate( onValidate() )
                    .build();
        }
    }

    private Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                scoreCardXLSService.call( new RemoteCallback<List<ValidationMessage>>() {
                    @Override
                    public void callback( final List<ValidationMessage> results ) {
                        if ( results == null || results.isEmpty() ) {
                            notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                                                      NotificationEvent.NotificationType.SUCCESS ) );
                        } else {
                            ValidationPopup.showMessages( results );
                        }
                    }
                }, new DefaultErrorCallback() ).validate( path,
                                                          path );
            }
        };
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        String fileName = FileNameUtil.removeExtension( path,
                                                        type );
        if ( version != null ) {
            fileName = fileName + " v" + version;
        }
        return ScoreCardXLSEditorConstants.INSTANCE.ScoreCardXLEditorTitle() + " [" + fileName + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
