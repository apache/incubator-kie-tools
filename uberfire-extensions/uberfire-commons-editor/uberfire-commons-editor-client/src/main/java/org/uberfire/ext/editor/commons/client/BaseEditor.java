/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.*;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.*;

public abstract class BaseEditor {

    protected boolean isReadOnly;

    protected BaseEditorView baseView;

    protected ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    protected Menus menus;

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected VersionRecordManager versionRecordManager;

    @Inject
    @New
    protected BasicFileMenuBuilder menuBuilder;

    @Inject
    protected DefaultFileNameValidator fileNameValidator;

    protected Set<MenuItems> menuItems = new HashSet<MenuItems>();

    protected PlaceRequest place;
    protected ClientResourceType type;
    protected Integer originalHash;
    private boolean displayShowMoreVersions;

    protected BaseEditor() {
    }

    protected BaseEditor( final BaseEditorView baseView ) {
        this.baseView = baseView;
    }

    protected void init( final ObservablePath path,
                         final PlaceRequest place,
                         final ClientResourceType type,
                         final MenuItems... menuItems ) {
        init( path, place, type, true, false, menuItems );
    }

    protected void init( final ObservablePath path,
                         final PlaceRequest place,
                         final ClientResourceType type,
                         final boolean addFileChangeListeners,
                         final boolean displayShowMoreVersions,
                         final MenuItems... menuItems ) {
        this.place = place;
        this.type = type;
        this.menuItems.addAll( Arrays.asList( menuItems ) );
        this.displayShowMoreVersions = displayShowMoreVersions;

        baseView.showLoading();

        this.isReadOnly = this.place.getParameter( "readOnly", null ) == null ? false : true;

        versionRecordManager.init(
                this.place.getParameter( "version", null ),
                path,
                new Callback<VersionRecord>() {
                    @Override
                    public void callback( VersionRecord versionRecord ) {
                        selectVersion( versionRecord );
                    }
                } );

        if ( displayShowMoreVersions ) {
            versionRecordManager.setShowMoreCommand(
                    new Command() {
                        @Override
                        public void execute() {
                            showVersions();
                        }
                    } );
        }

        if ( addFileChangeListeners ) {
            addFileChangeListeners( path );
        }

        makeMenuBar();

        buildMenuBar();

        loadContent();

        concurrentUpdateSessionInfo = null;
    }

    private void buildMenuBar() {
        if ( menuBuilder != null ) {
            menus = menuBuilder.build();
        }
    }

    protected void showVersions() {

    }

    /**
     * If you want to customize the menu override this method.
     */
    protected void makeMenuBar() {
        if ( menuItems.contains( SAVE ) ) {
            menuBuilder.addSave( new Command() {
                @Override
                public void execute() {
                    onSave();
                }
            } );
        }

        if ( menuItems.contains( COPY ) ) {
            menuBuilder.addCopy( versionRecordManager.getCurrentPath(),
                                 getCopyValidator(),
                                 getCopyServiceCaller() );
        }
        if ( menuItems.contains( RENAME ) ) {
            menuBuilder.addRename( versionRecordManager.getPathToLatest(),
                                   getRenameValidator(),
                                   getRenameServiceCaller() );
        }
        if ( menuItems.contains( DELETE ) ) {
            menuBuilder.addDelete( versionRecordManager.getCurrentPath(), getDeleteServiceCaller() );
        }
        if ( menuItems.contains( VALIDATE ) ) {
            menuBuilder.addValidate( onValidate() );
        }
        if ( menuItems.contains( HISTORY ) ) {
            menuBuilder.addNewTopLevelMenu( versionRecordManager.buildMenu() );
        }
    }

    public Validator getRenameValidator() {
        return fileNameValidator;
    }

    public Validator getCopyValidator() {
        return fileNameValidator;
    }

    private void selectVersion( VersionRecord versionRecord ) {
        baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        isReadOnly = !versionRecordManager.isLatest( versionRecord );

        versionRecordManager.setVersion( versionRecord.id() );

        loadContent();
    }

    public void setOriginalHash( Integer originalHash ) {
        this.originalHash = originalHash;
    }

    private void addFileChangeListeners( final ObservablePath path ) {
        path.onRename( new Command() {
            @Override
            public void execute() {
                onRename();

            }
        } );
        path.onDelete( new Command() {
            @Override
            public void execute() {
                onDelete();
            }
        } );

        path.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                concurrentUpdateSessionInfo = eventInfo;
            }
        } );

        path.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
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

        path.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
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
    }

    private void onDelete() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                placeManager.forceClosePlace( place );
            }
        } );
    }

    /**
     * Effectively the same as reload() but don't reset concurrentUpdateSessionInfo
     */
    protected void onRename() {
        refreshTitle();
        baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        loadContent();
        changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitleText(), getTitle() ) );
    }

    /**
     * Override this method and use @WorkbenchPartTitleDecoration
     * @return The widget for the title
     */
    protected IsWidget getTitle() {
        refreshTitle();
        return baseView.getTitleWidget();
    }

    public String getTitleText() {
        return versionRecordManager.getCurrentPath().getFileName() + " - " + type.getDescription();
    }

    private void refreshTitle() {
        baseView.refreshTitle( getTitleText() );
    }

    protected void onSave() {

        if ( isReadOnly && versionRecordManager.isCurrentLatest() ) {
            baseView.alertReadOnly();
            return;
        } else if ( isReadOnly && !versionRecordManager.isCurrentLatest() ) {
            versionRecordManager.restoreToCurrentVersion();
            return;
        }

        if ( concurrentUpdateSessionInfo != null ) {
            showConcurrentUpdatePopup();
        } else {
            save();
        }
    }

    protected void showConcurrentUpdatePopup() {
        newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                             concurrentUpdateSessionInfo.getIdentity(),
                             new Command() {
                                 @Override
                                 public void execute() {
                                     save();
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
                                     reload();
                                 }
                             }
                           ).show();
    }

    protected RemoteCallback<Path> getSaveSuccessCallback( final int newHash ) {
        return new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                baseView.hideBusyIndicator();
                versionRecordManager.reloadVersions( path );
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
                setOriginalHash( newHash );
            }
        };
    }

    public void onRestore( @Observes RestoreEvent restore ) {
        if ( versionRecordManager.getCurrentPath() == null || restore == null || restore.getPath() == null ) {
            return;
        }
        if ( versionRecordManager.getCurrentPath().equals( restore.getPath() ) ) {
            //when a version is restored we don't want to add the concurrency listeners again -> false
            init( versionRecordManager.getPathToLatest(), place, type, false, displayShowMoreVersions );
            notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRestored() ) );
        }
    }

    public void reload() {
        concurrentUpdateSessionInfo = null;
        refreshTitle();
        baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        loadContent();
        changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitleText(), getTitle() ) );
    }

    private void disableMenus() {
        disableMenuItem( COPY );
        disableMenuItem( MenuItems.RENAME );
        disableMenuItem( MenuItems.DELETE );
        disableMenuItem( MenuItems.VALIDATE );
    }

    private void disableMenuItem( final MenuItems menuItem ) {
        if ( menus.getItemsMap().containsKey( menuItem ) ) {
            menus.getItemsMap().get( menuItem ).setEnabled( false );
        }
    }

    /**
     * If your editor has validation, overwrite this.
     * @return The validation command
     */
    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                // Default is that nothing happens.
            }
        };
    }

    protected abstract void loadContent();

    /**
     * Needs to be overwritten for save to work
     */
    protected void save() {

    }

    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return null;
    }

    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return null;
    }

    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return null;
    }

    public boolean mayClose( Integer currentHash ) {
        if ( isDirty( currentHash ) ) {
            return baseView.confirmClose();
        } else {
            return true;
        }
    }

    public boolean isDirty( Integer currentHash ) {
        if ( originalHash == null ) {
            return currentHash != null;
        } else {
            return !originalHash.equals( currentHash );
        }
    }

    public VersionRecordManager getVersionRecordManager() {
        return this.versionRecordManager;
    }
}

