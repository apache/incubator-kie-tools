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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.apps.client.home.components.popup.NewDirectory;
import org.uberfire.mvp.ParameterizedCommand;

public class ThumbnailApp extends Composite {

    @UiField
    ThumbnailLink thumbLink;

    @UiField
    Label label;

    @UiField
    Icon icon;

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
        displayNoneOnLabel();
        addClickPopUpHandler( clickCommand );
    }

    private void displayNoneOnLabel() {
        label.getElement().getStyle().setProperty("display", "none");
    }

    public ThumbnailApp( String componentName,
                         IconType iconType,
                         final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createIcon( iconType );
        createLabel( componentName );
        addClickCommandHandler( clickCommand, componentName );
    }

    public ThumbnailApp( String dirName,
                         String dirURI,
                         IconType iconType,
                         final ParameterizedCommand<String> clickCommand ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        createIcon( iconType );
        createLabel( dirName );
        addClickCommandHandler( clickCommand, dirURI );
    }

    private void addClickCommandHandler( final ParameterizedCommand<String> clickCommand,
                                         final String parameter ) {
        thumbLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                clickCommand.execute( parameter );
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
        label.setText( name );
    }

    private void createIcon( IconType iconType
                           ) {
        icon.setIconSize( IconSize.LARGE );
        icon.setType( iconType );
    }

}
