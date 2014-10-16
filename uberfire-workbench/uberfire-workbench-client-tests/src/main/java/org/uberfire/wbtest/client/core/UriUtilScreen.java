package org.uberfire.wbtest.client.core;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.debug.Debug;
import org.uberfire.util.URIUtil;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

@ApplicationScoped
@Named("org.uberfire.wbtest.client.core.UriUtilScreen")
public class UriUtilScreen extends AbstractTestScreenActivity {

    private final Panel panel = new VerticalPanel();

    @Inject
    public UriUtilScreen( PlaceManager placeManager ) {
        super( placeManager );

        final Label resultLabel = new Label();
        resultLabel.getElement().setId( Debug.shortName( getClass() ) + "-resultLabel" );

        final TextBox uriCheckerBox = new TextBox();
        uriCheckerBox.getElement().setId( Debug.shortName( getClass() ) + "-uriCheckerBox" );

        uriCheckerBox.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( URIUtil.isValid( uriCheckerBox.getText() ) ) {
                    resultLabel.setText( "Not valid." );
                } else {
                    resultLabel.setText( "Valid. Encoded form is <" + URIUtil.encode( uriCheckerBox.getText() ) + ">" );
                }
            }
        } );

        panel.add( new Label("Type URIs into this box to see if they're valid:") );
        panel.add( uriCheckerBox );
        panel.add( resultLabel );
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
