package org.kie.uberfire.apps.client.home.components;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.apps.client.home.components.popup.NewDirectory;
import org.kie.uberfire.apps.client.resources.WebAppResource;
import org.uberfire.mvp.ParameterizedCommand;

public class ThumbnailApp extends Composite {

    @UiField
    Icon icon;

    @UiField
    Label label;

    @UiField
    FocusPanel clickTile;

    @UiField
    FlowPanel tilePanel;

    private NewDirectory newDirectory = new NewDirectory();

    private static WebAppResource APP_CSS = GWT.create( WebAppResource.class );

    interface ThumbnailBinder
            extends
            UiBinder<Widget, ThumbnailApp> {

    }

    private static ThumbnailBinder uiBinder = GWT.create( ThumbnailBinder.class );

    public ThumbnailApp( TYPE type,
                         final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        defineTileColor( type );
        createIcon( type );
        displayNoneOnLabel();
        addClickPopUpHandler( clickCommand );
    }

    public ThumbnailApp( String componentName,
                         TYPE type,
                         final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        defineTileColor( type );
        createIcon( type );
        createLabel( componentName );
        addClickCommandHandler( clickCommand, componentName );
    }

    public ThumbnailApp( String dirName,
                         String dirURI,
                         TYPE type,
                         final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        defineTileColor( type );
        createIcon( type );
        createLabel( dirName );
        addClickCommandHandler( clickCommand, dirURI );
    }

    private void defineTileColor( TYPE type ) {
        tilePanel.addStyleName( type.tileColor() );
    }

    private void displayNoneOnLabel() {
        label.getElement().getStyle().setProperty( "display", "none" );
    }

    private void addClickCommandHandler( final ParameterizedCommand<String> clickCommand,
                                         final String parameter ) {
        clickTile.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                clickCommand.execute( parameter );
            }
        } );
    }

    private void addClickPopUpHandler( final ParameterizedCommand<String> clickCommand ) {
        clickTile.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                newDirectory.show( clickCommand );
            }
        } );
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
