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

package org.uberfire.ext.apps.client.home.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.apps.client.home.components.popup.NewDirectoryPopup;
import org.uberfire.ext.apps.client.resources.WebAppResource;
import org.uberfire.ext.apps.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.ParameterizedCommand;

public class TilesApp extends Composite {

    @UiField
    Icon icon;

    @UiField
    Label label;

    @UiField
    FlowPanel outerPanel;

    @UiField
    FlowPanel tilePanel;

    @UiField
    FlowPanel deletePanel;

    private NewDirectoryPopup newDirectoryPopup;

    private Icon deleteIcon;

    private static WebAppResource APP_CSS = GWT.create( WebAppResource.class );

    interface TilesBinder
            extends
            UiBinder<Widget, TilesApp> {

    }

    private static TilesBinder uiBinder = GWT.create( TilesBinder.class );

    public static TilesApp createDirTiles( TYPE type,
                                           final ParameterizedCommand<String> clickCommand,
                                           Directory currentDirectory ) {
        return new TilesApp( type, clickCommand, currentDirectory );
    }

    private TilesApp( TYPE type,
                      final ParameterizedCommand<String> clickCommand,
                      Directory currentDirectory ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        defineTileColor( type );
        createIcon( type, CommonConstants.INSTANCE.CreateDir() );
        displayNoneOnLabel();
        addClickPopUpHandler( clickCommand, currentDirectory );
    }

    public static TilesApp componentTiles( String componentName,
                                           TYPE type,
                                           final ParameterizedCommand<String> clickCommand ) {
        return new TilesApp( componentName, type, clickCommand );
    }

    private TilesApp( String componentName,
                      TYPE type,
                      final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        defineTileColor( type );
        createIcon( type, CommonConstants.INSTANCE.GotoComponent() );
        createLabel( componentName );
        addClickCommandHandler( clickCommand, componentName );
    }

    public static TilesApp directoryTiles( String dirName,
                                           String dirURI,
                                           TYPE type,
                                           final ParameterizedCommand<String> clickCommand,
                                           ParameterizedCommand<String> deleteCommand ) {
        return new TilesApp( dirName, dirURI, type, clickCommand, deleteCommand );
    }

    private TilesApp( String dirName,
                      String dirURI,
                      TYPE type,
                      final ParameterizedCommand<String> clickCommand,
                      final ParameterizedCommand<String> deleteCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        defineTileColor( type );
        createIcon( type, CommonConstants.INSTANCE.OpenDir() );
        createLabel( dirName );
        addClickCommandHandler( clickCommand, dirURI );
        createDeleteIcon( deleteCommand, dirURI );
    }

    private void createDeleteIcon( final ParameterizedCommand<String> deleteCommand,
                                   final String dirURI ) {
        deleteIcon = new Icon( IconType.REMOVE );
        deleteIcon.setTitle( CommonConstants.INSTANCE.DeleteDir() );
        deleteIcon.addStyleName( APP_CSS.CSS().deleteIcon() );
        deleteIcon.addStyleName( "fa" );
        deleteIcon.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( Window.confirm( CommonConstants.INSTANCE.DeleteAppPrompt() ) ) {
                    deleteCommand.execute( dirURI );
                }
            }
        }, ClickEvent.getType() );
        outerPanel.addDomHandler( new MouseOverHandler() {
            @Override
            public void onMouseOver( MouseOverEvent event ) {
                deletePanel.add( deleteIcon );
            }
        }, MouseOverEvent.getType() );
        outerPanel.addDomHandler( new MouseOutHandler() {
            @Override
            public void onMouseOut( MouseOutEvent event ) {
                deletePanel.remove( deleteIcon );
            }
        }, MouseOutEvent.getType() );
    }

    private void defineTileColor( TYPE type ) {
        tilePanel.addStyleName( type.tileColor() );
        deletePanel.addStyleName( type.tileColor() );
    }

    private void displayNoneOnLabel() {
        label.getElement().getStyle().setProperty( "display", "none" );
    }

    private void addClickCommandHandler( final ParameterizedCommand<String> clickCommand,
                                         final String parameter ) {
        tilePanel.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                clickCommand.execute( parameter );
            }
        }, ClickEvent.getType() );
    }

    private void addClickPopUpHandler( final ParameterizedCommand<String> clickCommand,
                                       final Directory currentDirectory ) {
        tilePanel.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                newDirectoryPopup = new NewDirectoryPopup( currentDirectory );
                newDirectoryPopup.show( clickCommand );
            }
        }, ClickEvent.getType() );
    }

    private void createLabel( String name ) {
        label.setText( name );
    }

    private void createIcon( TYPE type,
                             String tooltip ) {
        icon.setTitle( tooltip );
        icon.setType( type.icon() );
    }

    public enum TYPE {

        DIR( IconType.FOLDER_OPEN, APP_CSS.CSS().blueTile() ), ADD( IconType.PLUS, APP_CSS.CSS().redTile() ), COMPONENT( IconType.FILE, APP_CSS.CSS().greenTile() );

        private IconType iconType;
        private String tile;

        TYPE( IconType iconType,
              String tile ) {
            this.iconType = iconType;
            this.tile = tile;
        }

        IconType icon() {
            return iconType;
        }

        String tileColor() {
            return tile;
        }

    }

}
