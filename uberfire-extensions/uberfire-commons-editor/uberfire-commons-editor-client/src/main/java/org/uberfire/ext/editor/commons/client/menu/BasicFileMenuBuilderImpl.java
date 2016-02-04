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

package org.uberfire.ext.editor.commons.client.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

public class BasicFileMenuBuilderImpl implements BasicFileMenuBuilder {

    @Inject
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private Command saveCommand = null;
    private MenuItem saveMenuItem;
    private Command deleteCommand = null;
    private MenuItem deleteMenuItem;
    private Command renameCommand = null;
    private MenuItem renameMenuItem;
    private Command copyCommand = null;
    private Command validateCommand = null;
    private Command restoreCommand = null;
    private MenuItem restoreMenuItem;
    private List<Pair<String, Command>> otherCommands = new ArrayList<Pair<String, Command>>();
    private List<MenuItem> topLevelMenus = new ArrayList<MenuItem>();
    private List<MenuItem> menuItemsSyncedWithLockState = new ArrayList<MenuItem>();

    @Override
    public BasicFileMenuBuilder addSave( final MenuItem menuItem ) {
        saveMenuItem = menuItem;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addSave( final Command command ) {
        this.saveCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addDelete( final Path path,
                                           final Caller<? extends SupportsDelete> deleteCaller ) {
        return addDelete( new Command() {
            @Override
            public void execute() {
                final DeletePopup popup = new DeletePopup( new ParameterizedCommand<String>() {
                    @Override
                    public void execute( final String comment ) {
                        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                        deleteCaller.call( getDeleteSuccessCallback(),
                                           new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( path,
                                                                                                                   comment );
                    }
                } );

                popup.show();
            }
        } );
    }

    private RemoteCallback<Void> getDeleteSuccessCallback() {
        return new RemoteCallback<Void>() {

            @Override
            public void callback( final Void response ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addDelete( final Command command ) {
        this.deleteCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRename( final Command command ) {
        this.renameCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRename( final Path path,
                                           final Caller<? extends SupportsRename> renameCaller ) {
        return addRename( new Command() {
            @Override
            public void execute() {
                final RenamePopupView renamePopupView = RenamePopup.getDefaultView();
                final RenamePopup popup = new RenamePopup( path,
                                                           getRenamePopupCommand( renameCaller, path, renamePopupView ), renamePopupView );

                popup.show();
            }
        } );
    }

    @Override
    public BasicFileMenuBuilder addRename( final Path path,
                                           final Validator validator,
                                           final Caller<? extends SupportsRename> renameCaller ) {
        return addRename( new Command() {
            @Override
            public void execute() {
                final RenamePopupView renamePopupView = RenamePopup.getDefaultView();
                final RenamePopup popup = new RenamePopup( path,
                                                           validator,
                                                           getRenamePopupCommand( renameCaller, path, renamePopupView ), renamePopupView );

                popup.show();
            }
        } );
    }

    private CommandWithFileNameAndCommitMessage getRenamePopupCommand( final Caller<? extends SupportsRename> renameCaller,
                                                                       final Path path,
                                                                       final RenamePopupView renamePopupView ) {
        return new CommandWithFileNameAndCommitMessage() {
            @Override
            public void execute( final FileNameAndCommitMessage details ) {
                busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                renameCaller.call( getRenameSuccessCallback( renamePopupView ),
                                   getRenameErrorCallback( renamePopupView, busyIndicatorView ) ).rename( path,
                                                                                                          details.getNewFileName(),
                                                                                                          details.getCommitMessage() );
            }
        };
    }

    private RemoteCallback<Path> getRenameSuccessCallback( final RenamePopupView renamePopupView ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                renamePopupView.hide();
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
            }
        };
    }

    private HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback( final RenamePopupView renamePopupView,
                                                                         BusyIndicatorView busyIndicatorView ) {
        return new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                if ( fileAlreadyExists( throwable ) ) {
                    hideBusyIndicator();
                    renamePopupView.handleDuplicatedFileName();
                    return false;
                }

                renamePopupView.hide();
                return super.error( message, throwable );
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addCopy( final Command command ) {
        this.copyCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addCopy( final Path path,
                                         final Caller<? extends SupportsCopy> copyCaller ) {
        return addCopy( new Command() {
            @Override
            public void execute() {
                final CopyPopupView copyPopupView = CopyPopup.getDefaultView();
                final CopyPopup popup = new CopyPopup( path,
                                                       getCopyPopupCommand( copyCaller, path, copyPopupView ), copyPopupView );
                popup.show();
            }
        } );
    }

    @Override
    public BasicFileMenuBuilder addCopy( final Path path,
                                         final Validator validator,
                                         final Caller<? extends SupportsCopy> copyCaller ) {
        return addCopy( new Command() {
            @Override
            public void execute() {
                final CopyPopupView copyPopupView = CopyPopup.getDefaultView();
                final CopyPopup popup = new CopyPopup( path,
                                                       validator,
                                                       getCopyPopupCommand( copyCaller, path, copyPopupView ), copyPopupView );
                popup.show();
            }
        } );
    }

    private CommandWithFileNameAndCommitMessage getCopyPopupCommand( final Caller<? extends SupportsCopy> copyCaller,
                                                                     final Path path,
                                                                     final CopyPopupView copyPopupView ) {
        return new CommandWithFileNameAndCommitMessage() {
            @Override
            public void execute( final FileNameAndCommitMessage details ) {
                busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                copyCaller.call( getCopySuccessCallback( copyPopupView ),
                                 getCopyErrorCallback( copyPopupView, busyIndicatorView ) ).copy( path,
                                                                                                  details.getNewFileName(),
                                                                                                  details.getCommitMessage() );
            }
        };
    }

    private RemoteCallback<Path> getCopySuccessCallback( final CopyPopupView copyPopupView ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                copyPopupView.hide();
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
            }
        };
    }

    public HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback( final CopyPopupView copyPopupView,
                                                                      BusyIndicatorView busyIndicatorView ) {
        return new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                if ( fileAlreadyExists( throwable ) ) {
                    hideBusyIndicator();
                    copyPopupView.handleDuplicatedFileName();
                    return false;
                }

                copyPopupView.hide();
                return super.error( message, throwable );
            }
        };
    }

    private boolean fileAlreadyExists( final Throwable throwable ) {
        return throwable != null && throwable.getMessage() != null && throwable.getMessage().contains( "FileAlreadyExistsException" );
    }

    @Override
    public BasicFileMenuBuilder addValidate( final Command validateCommand ) {
        this.validateCommand = validateCommand;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRestoreVersion( final Path path ) {
        this.restoreCommand = restoreVersionCommandProvider.getCommand( path );
        return this;
    }

    @Override
    public BasicFileMenuBuilder addCommand( final String caption,
                                            final Command command ) {
        this.otherCommands.add( new Pair<String, Command>( caption,
                                                           command ) );
        return this;
    }

    @Override
    public Menus build() {
        final Map<Object, MenuItem> menuItems = new LinkedHashMap<Object, MenuItem>();
        if ( saveCommand != null ) {
            menuItems.put( MenuItems.SAVE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Save() )
                    .respondsWith( saveCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        } else if ( saveMenuItem != null ) {
            menuItems.put( MenuItems.SAVE, saveMenuItem );
            menuItemsSyncedWithLockState.add( saveMenuItem );
        }

        if ( deleteCommand != null ) {
            if ( deleteMenuItem == null ) {
                deleteMenuItem = MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Delete() )
                        .respondsWith( deleteCommand )
                        .endMenu()
                        .build().getItems().get( 0 );
            }
            menuItems.put( MenuItems.DELETE, deleteMenuItem );
            menuItemsSyncedWithLockState.add( deleteMenuItem );
        }

        if ( renameCommand != null ) {
            if ( renameMenuItem == null ) {
                renameMenuItem = MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Rename() )
                        .respondsWith( renameCommand )
                        .endMenu()
                        .build().getItems().get( 0 );
            }
            menuItems.put( MenuItems.RENAME, renameMenuItem );
            menuItemsSyncedWithLockState.add( renameMenuItem );
        }

        if ( copyCommand != null ) {
            menuItems.put( MenuItems.COPY, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Copy() )
                    .respondsWith( copyCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        }

        if ( validateCommand != null ) {
            menuItems.put( MenuItems.VALIDATE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Validate() )
                    .respondsWith( validateCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        }

        if ( restoreCommand != null ) {
            if ( restoreMenuItem == null ) {
                restoreMenuItem = MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Restore() )
                        .respondsWith( restoreCommand )
                        .endMenu()
                        .build().getItems().get( 0 );
            }
            menuItemsSyncedWithLockState.add( restoreMenuItem );
        }

        if ( !( otherCommands == null || otherCommands.isEmpty() ) ) {
            final List<MenuItem> otherMenuItems = new ArrayList<MenuItem>();
            for ( Pair<String, Command> other : otherCommands ) {
                otherMenuItems.add( newSimpleItem( other.getK1() )
                                            .respondsWith( other.getK2() )
                                            .endMenu().build().getItems().get( 0 ) );
            }
            final MenuItem item = MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Other() )
                    .withItems( otherMenuItems )
                    .endMenu()
                    .build().getItems().get( 0 );
            menuItems.put( item, item );
        }

        for ( MenuItem menuItem : topLevelMenus ) {
            menuItems.put( menuItem, menuItem );
        }

        return new Menus() {

            @Override
            public List<MenuItem> getItems() {
                return new ArrayList<MenuItem>() {{
                    for ( final MenuItem menuItem : menuItems.values() ) {
                        add( menuItem );
                    }
                }};
            }

            @Override
            public Map<Object, MenuItem> getItemsMap() {
                return menuItems;
            }

            @Override
            public void accept( MenuVisitor visitor ) {
                if ( visitor.visitEnter( this ) ) {
                    for ( final MenuItem item : menuItems.values() ) {
                        item.accept( visitor );
                    }
                    visitor.visitLeave( this );
                }
            }

            @Override
            public int getOrder() {
                return 0;
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addNewTopLevelMenu( MenuItem menu ) {
        topLevelMenus.add( menu );
        return this;
    }

    private void onEditorLockInfo( @Observes UpdatedLockStatusEvent lockInfo ) {
        boolean enabled = ( !lockInfo.isLocked() || lockInfo.isLockedByCurrentUser() );
        for ( MenuItem menuItem : menuItemsSyncedWithLockState ) {
            menuItem.setEnabled( enabled );
        }
    }
}
