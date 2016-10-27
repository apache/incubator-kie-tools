/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectDiagramEditorMenuItemsBuilder {

    public MenuItem newClearSelectionItem( final Command command ) {
        return buildItem( buildClearSelectionItem( command ) );
    }

    private IsWidget buildClearSelectionItem( final Command command ) {
        return new Button() {{
            setIconSize( IconSize.NONE );
            setIcon( IconType.BAN );
            setTitle( "Clear selection" );
            addClickHandler( clickEvent -> command.execute() );
        }};
    }

    public MenuItem newVisitGraphItem( final Command command ) {
        return buildItem( buildVisitGraphItem( command ) );
    }

    private IsWidget buildVisitGraphItem( final Command command ) {
        return new Button() {{
            setIconSize( IconSize.NONE );
            setIcon( IconType.AUTOMOBILE );
            setTitle( "Visit graph" );
            addClickHandler( clickEvent -> command.execute() );
        }};
    }

    public MenuItem newSwitchGridItem( final Command command ) {
        return buildItem( buildSwitchGridItem( command ) );
    }

    private IsWidget buildSwitchGridItem( final Command command ) {
        return new Button() {{
            setIconSize( IconSize.NONE );
            setIcon( IconType.TH );
            setTitle( "Switch grid" );
            addClickHandler( clickEvent -> command.execute() );
        }};
    }

    public MenuItem newClearItem( final Command command ) {
        return buildItem( buildClearItem( command ) );
    }

    private IsWidget buildClearItem( final Command command ) {
        return new Button() {{
            setIconSize( IconSize.NONE );
            setIcon( IconType.ERASER );
            setTitle( "Clear" );
            addClickHandler( clickEvent -> command.execute() );
        }};
    }

    public MenuItem newDeleteSelectionItem( final Command command ) {
        return buildItem( buildDeleteSelectionItem( command ) );
    }

    private IsWidget buildDeleteSelectionItem( final Command command ) {
        return new Button() {{
            setIconSize( IconSize.NONE );
            setIcon( IconType.TRASH_O );
            setTitle( "Delete selected" );
            addClickHandler( clickEvent -> command.execute() );
        }};
    }

    public MenuItem newUndoItem( final Command command ) {
        return buildItem( buildUndoItem( command ) );
    }

    private IsWidget buildUndoItem( final Command command ) {
        return new Button() {{
            setIconSize( IconSize.NONE );
            setIcon( IconType.UNDO );
            setTitle( "Undo" );
            addClickHandler( clickEvent -> command.execute() );
        }};
    }

    public MenuItem newValidateItem( final Command command ) {
        return buildItem( buildValidateItem( command ) );
    }

    private IsWidget buildValidateItem( final Command command ) {
        return new Button() {{
            setIconSize( IconSize.NONE );
            setIcon( IconType.CHECK );
            setTitle( "Validate" );
            addClickHandler( clickEvent -> command.execute() );
        }};
    }

    public MenuItem newDevItems( final Command switchLogLevelCommand,
                                 final Command logGraphCommand,
                                 final Command logCommandHistoryCommand,
                                 final Command logSessionCommand ) {
        return buildItem( buildDevItems( switchLogLevelCommand, logGraphCommand,
                logCommandHistoryCommand, logSessionCommand ) );
    }

    private IsWidget buildDevItems( final Command switchLogLevelCommand,
                                    final Command logGraphCommand,
                                    final Command logCommandHistoryCommand,
                                    final Command logSessionCommand ) {
        final AnchorListItem switchLogLevelItem = new AnchorListItem( "Switch log level" ) {{
            setIcon( IconType.REFRESH );
            addClickHandler( event -> switchLogLevelCommand.execute() );
        }};
        final AnchorListItem logSessionItem = new AnchorListItem( "Log session" ) {{
            setIcon( IconType.PRINT );
            addClickHandler( event -> logSessionCommand.execute() );
        }};
        final AnchorListItem logGraphItem = new AnchorListItem( "Log Graph" ) {{
            setIcon( IconType.PRINT );
            addClickHandler( event -> logGraphCommand.execute() );
        }};
        final AnchorListItem logCommandHistoryItem = new AnchorListItem( "Log Command History" ) {{
            setIcon( IconType.HISTORY );
            addClickHandler( event -> logCommandHistoryCommand.execute() );
        }};
        return new ButtonGroup() {{
            add( new Button() {{
                setToggleCaret( false );
                setDataToggle( Toggle.DROPDOWN );
                setIcon( IconType.COG );
                setSize( ButtonSize.SMALL );
                setTitle( "Development" );
            }} );
            add( new DropDownMenu() {{
                addStyleName( "pull-right" );
                add( switchLogLevelItem );
                add( logSessionItem );
                add( logGraphItem );
                add( logCommandHistoryItem );
            }} );
        }};
    }

    private MenuItem buildItem( final IsWidget widget ) {
        return new MenuFactory.CustomMenuBuilder() {
            @Override
            public void push( MenuFactory.CustomMenuBuilder element ) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom() {
                    @Override
                    public IsWidget build() {
                        return widget;
                    }
                };
            }
        }.build();
    }

}
