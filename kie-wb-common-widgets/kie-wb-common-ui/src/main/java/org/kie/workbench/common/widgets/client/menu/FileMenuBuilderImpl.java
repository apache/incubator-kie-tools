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

package org.kie.workbench.common.widgets.client.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.workbench.common.services.shared.validation.Validator;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithFileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CopyPopup;
import org.kie.workbench.common.widgets.client.popups.file.DeletePopup;
import org.kie.workbench.common.widgets.client.popups.file.FileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.RenamePopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

/**
 *
 */
@Dependent
public class FileMenuBuilderImpl
        implements FileMenuBuilder {

    @Inject
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    @Inject
    private Caller<DeleteService> deleteService;

    @Inject
    private Caller<RenameService> renameService;

    @Inject
    private Caller<CopyService> copyService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private Command saveCommand = null;
    private Command deleteCommand = null;
    private Command renameCommand = null;
    private Command copyCommand = null;
    private Command validateCommand = null;
    private Command restoreCommand = null;
    private List<Pair<String, Command>> otherCommands = new ArrayList<Pair<String, Command>>();
    private List<MenuItem> topLevelMenus = new ArrayList<MenuItem>();

    @Override
    public FileMenuBuilder addSave( final Command command ) {
        this.saveCommand = command;
        return this;
    }

    @Override
    public FileMenuBuilder addDelete( final Command command ) {
        this.deleteCommand = command;
        return this;
    }

    @Override
    public FileMenuBuilder addDelete( final Path path ) {
        this.deleteCommand = new Command() {
            @Override
            public void execute() {
                final DeletePopup popup = new DeletePopup( new CommandWithCommitMessage() {
                    @Override
                    public void execute( final String comment ) {
                        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                        deleteService.call( getDeleteSuccessCallback( path ),
                                            new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( path,
                                                                                                                    comment );
                    }
                } );

                popup.show();
            }
        };
        return this;
    }

    private RemoteCallback<Void> getDeleteSuccessCallback( final Path path ) {
        return new RemoteCallback<Void>() {

            @Override
            public void callback( final Void response ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
            }
        };
    }

    @Override
    public FileMenuBuilder addRename( final Command command ) {
        this.renameCommand = command;
        return this;
    }

    @Override
    public FileMenuBuilder addRename( final Path path ) {
        this.renameCommand = new Command() {
            @Override
            public void execute() {
                final RenamePopup popup = new RenamePopup( path,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   renameService.call( getRenameSuccessCallback(),
                                                                                       new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                               details.getNewFileName(),
                                                                                                                                                               details.getCommitMessage() );
                                                               }
                                                           } );

                popup.show();
            }
        };

        return this;
    }

    @Override
    public FileMenuBuilder addRename( final Path path,
                                      final Validator validator ) {
        this.renameCommand = new Command() {
            @Override
            public void execute() {
                final RenamePopup popup = new RenamePopup( path,
                                                           validator,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   renameService.call( getRenameSuccessCallback(),
                                                                                       new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                               details.getNewFileName(),
                                                                                                                                                               details.getCommitMessage() );
                                                               }
                                                           } );

                popup.show();
            }
        };

        return this;
    }

    private RemoteCallback<Path> getRenameSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
            }
        };
    }

    @Override
    public FileMenuBuilder addCopy( final Command command ) {
        this.copyCommand = command;
        return this;
    }

    @Override
    public FileMenuBuilder addCopy( final Path path ) {
        this.copyCommand = new Command() {
            @Override
            public void execute() {
                final CopyPopup popup = new CopyPopup( path,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               copyService.call( getCopySuccessCallback(),
                                                                                 new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( path,
                                                                                                                                                       details.getNewFileName(),
                                                                                                                                                       details.getCommitMessage() );
                                                           }
                                                       } );
                popup.show();
            }
        };

        return this;
    }

    @Override
    public FileMenuBuilder addCopy( final Path path,
                                    final Validator validator ) {
        this.copyCommand = new Command() {
            @Override
            public void execute() {
                final CopyPopup popup = new CopyPopup( path,
                                                       validator,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               copyService.call( getCopySuccessCallback(),
                                                                                 new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( path,
                                                                                                                                                       details.getNewFileName(),
                                                                                                                                                       details.getCommitMessage() );
                                                           }
                                                       } );
                popup.show();
            }
        };

        return this;
    }

    private RemoteCallback<Path> getCopySuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
            }
        };
    }

    @Override
    public FileMenuBuilder addValidate( final Command validateCommand ) {
        this.validateCommand = validateCommand;
        return this;
    }

    @Override
    public FileMenuBuilder addRestoreVersion( final Path path ) {
        this.restoreCommand = restoreVersionCommandProvider.getCommand( path );
        return this;
    }

    @Override
    public FileMenuBuilder addCommand( final String caption,
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
        }

        if ( deleteCommand != null ) {
            menuItems.put( MenuItems.DELETE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Delete() )
                    .respondsWith( deleteCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        }

        if ( renameCommand != null ) {
            menuItems.put( MenuItems.RENAME, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Rename() )
                    .respondsWith( renameCommand )
                    .endMenu().build().getItems().get( 0 ) );
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
            menuItems.put( MenuItems.RESTORE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Restore() )
                    .respondsWith( restoreCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
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

        for (MenuItem menuItem : topLevelMenus) {
            menuItems.put(menuItem, menuItem);
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
        };
    }

    @Override
    public FileMenuBuilder addNewTopLevelMenu(MenuItem menu) {
        topLevelMenus.add(menu);
        return this;
    }
}
