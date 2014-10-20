package org.kie.uberfire.apps.client.home.components;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.apps.client.home.components.popup.NewDirectory;
import org.kie.uberfire.apps.client.resources.WebAppResource;
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

    private NewDirectory newDirectory = new NewDirectory();

    private Icon deleteIcon;

    private static WebAppResource APP_CSS = GWT.create( WebAppResource.class );

    interface TilesBinder
            extends
            UiBinder<Widget, TilesApp> {

    }

    private static TilesBinder uiBinder = GWT.create( TilesBinder.class );

    public static TilesApp createDirTiles( TYPE type,
                                           final ParameterizedCommand<String> clickCommand ) {
        return new TilesApp( type, clickCommand );
    }

    private TilesApp( TYPE type,
                      final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        defineTileColor( type );
        createIcon( type );
        displayNoneOnLabel();
        addClickPopUpHandler( clickCommand );
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
        createIcon( type );
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
        createIcon( type );
        createLabel( dirName );
        addClickCommandHandler( clickCommand, dirURI );
        createDeleteIcon( deleteCommand, dirURI );
    }

    private void createDeleteIcon( final ParameterizedCommand<String> deleteCommand,
                                   final String dirURI ) {
        deleteIcon = new Icon( IconType.REMOVE_CIRCLE );
        deleteIcon.setIconSize( IconSize.DEFAULT );
        deleteIcon.addStyleName( APP_CSS.CSS().deleteIcon() );
        deleteIcon.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                deleteCommand.execute( dirURI );
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

    private void addClickPopUpHandler( final ParameterizedCommand<String> clickCommand ) {
        tilePanel.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                newDirectory.show( clickCommand );
            }
        }, ClickEvent.getType() );
    }

    private void createLabel( String name ) {
        label.setText( name );
    }

    private void createIcon( TYPE type
                           ) {
        icon.setIconSize( IconSize.LARGE );
        icon.setType( type.icon() );
    }

    public enum TYPE {

        DIR( IconType.FOLDER_OPEN, APP_CSS.CSS().blueTile() ), ADD( IconType.PLUS_SIGN, APP_CSS.CSS().redTile() ), COMPONENT( IconType.FILE, APP_CSS.CSS().greenTile() );

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
