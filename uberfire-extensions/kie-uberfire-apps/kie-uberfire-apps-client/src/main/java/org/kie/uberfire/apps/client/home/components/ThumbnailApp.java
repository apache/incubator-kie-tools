package org.kie.uberfire.apps.client.home.components;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ThumbnailLink;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.apps.client.home.components.popup.NewDirectory;
import org.uberfire.mvp.ParameterizedCommand;

public class ThumbnailApp extends Composite {

    @UiField
    ThumbnailLink thumbLink;

    private NewDirectory newDirectory = new NewDirectory();

    interface ThumbnailBinder
            extends
            UiBinder<Widget, ThumbnailApp> {

    }

    private static ThumbnailBinder uiBinder = GWT.create( ThumbnailBinder.class );

    public ThumbnailApp( IconType iconType,
                         final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createIcon( iconType );
        addClickPopUpHandler( clickCommand );
    }

    public ThumbnailApp( String dirName,
                         IconType iconType,
                         final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createLabel( dirName );
        createIcon( iconType );
        addClickCommandHandler( clickCommand, dirName );
    }

    private void addClickCommandHandler( final ParameterizedCommand<String> clickCommand,
                                         final String dirName ) {
        thumbLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                clickCommand.execute( dirName );
            }
        } );
    }

    private void addClickPopUpHandler( final ParameterizedCommand<String> clickCommand ) {
        thumbLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                newDirectory.show( clickCommand );
            }
        } );
    }

    private void createLabel( String name ) {
        Label label = new Label( name );
        thumbLink.add( label );
    }

    private void createIcon( IconType iconType
                           ) {
        Icon icon = new Icon( iconType );
        icon.setIconSize( IconSize.LARGE );
        thumbLink.add( icon );
    }

}
