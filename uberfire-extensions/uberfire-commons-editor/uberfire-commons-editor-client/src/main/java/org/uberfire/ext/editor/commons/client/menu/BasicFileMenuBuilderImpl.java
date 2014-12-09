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

package org.uberfire.ext.editor.commons.client.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

public class BasicFileMenuBuilderImpl<T> implements BasicFileMenuBuilder<T> {

    @Inject
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private Command saveCommand = null;
    private MenuItem saveMenuItem;
    private Command deleteCommand = null;
    private Command renameCommand = null;
    private Command copyCommand = null;
    private Command validateCommand = null;
    private Command restoreCommand = null;
    private List<Pair<String, Command>> otherCommands = new ArrayList<Pair<String, Command>>();
    private List<MenuItem> topLevelMenus = new ArrayList<MenuItem>();

    @Override
    public T addSave( MenuItem menuItem ) {
        saveMenuItem = menuItem;
        return (T) this;
    }

    @Override
    public T addSave( final Command command ) {
        this.saveCommand = command;
        return (T) this;
    }

    @Override
    public T addDelete( final Command command ) {
        this.deleteCommand = command;
        return (T) this;
    }

    @Override
    public T addRename( final Command command ) {
        this.renameCommand = command;
        return (T) this;
    }

    @Override
    public T addCopy( final Command command ) {
        this.copyCommand = command;
        return (T) this;
    }

    @Override
    public T addValidate( final Command validateCommand ) {
        this.validateCommand = validateCommand;
        return (T) this;
    }

    @Override
    public T addRestoreVersion( final Path path ) {
        this.restoreCommand = restoreVersionCommandProvider.getCommand( path );
        return (T) this;
    }

    @Override
    public T addCommand( final String caption,
                                            final Command command ) {
        this.otherCommands.add( new Pair<String, Command>( caption,
                                                           command ) );
        return (T) this;
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
        };
    }

    @Override
    public T addNewTopLevelMenu( MenuItem menu ) {
        topLevelMenus.add( menu );
        return (T) this;
    }
}
