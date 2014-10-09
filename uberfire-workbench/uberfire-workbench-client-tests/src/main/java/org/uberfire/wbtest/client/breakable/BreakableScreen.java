package org.uberfire.wbtest.client.breakable;

import static org.uberfire.debug.Debug.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

@Dependent
@Named( "org.uberfire.wbtest.client.breakable.BreakableScreen" )
public class BreakableScreen extends AbstractTestScreenActivity {

    private LifecyclePhase brokenLifecycle;

    private final Panel panel = new VerticalPanel();
    private final Label label = new Label( "Not started" );
    private final Button closeButton = new Button( "Close this screen" );

    @Inject
    public BreakableScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        panel.getElement().setId( shortName( getClass() ) );
        String brokenParam = place.getParameter( "broken", null );
        if ( brokenParam != null && brokenParam.length() > 0 ) {
            brokenLifecycle = LifecyclePhase.valueOf( brokenParam );
        }

        if ( brokenParam == null ) {
            label.setText( "Screen with no broken methods" );
        } else {
            label.setText( "Screen with broken " + brokenLifecycle + " method" );
        }

        closeButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                placeManager.closePlace( getPlace() );
            }
        } );

        panel.add( label );
        panel.add( closeButton );

        if ( brokenLifecycle == LifecyclePhase.STARTUP ) {
            throw new RuntimeException( "This screen has a broken startup callback" );
        }
    }

    @Override
    public void onOpen() {
        super.onOpen();
        if ( brokenLifecycle == LifecyclePhase.OPEN ) {
            throw new RuntimeException( "This screen has a broken open callback" );
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if ( brokenLifecycle == LifecyclePhase.CLOSE ) {
            throw new RuntimeException( "This screen has a broken close callback" );
        }
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        if ( brokenLifecycle == LifecyclePhase.SHUTDOWN ) {
            throw new RuntimeException( "This screen has a broken shutdown callback" );
        }
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }
}
