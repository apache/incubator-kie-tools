package org.uberfire.wbtest.client.breakable;

import static java.util.Arrays.*;
import static org.uberfire.debug.Debug.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;
import org.uberfire.wbtest.client.api.PlaceButton;
import org.uberfire.wbtest.client.main.DefaultPerspectiveActivity;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

@Dependent
@Named( "org.uberfire.wbtest.client.breakable.BreakableMenuScreen" )
public class BreakableMenuScreen extends AbstractTestScreenActivity {

    private final VerticalPanel panel = new VerticalPanel();

    @Inject
    public BreakableMenuScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void setup() {

        // makes this screen detectable to automated tests
        panel.getElement().setId( shortName( getClass() ) );

        Map<String, String> params = new HashMap<String, String>();
        for ( Class<?> activityClass : asList( BreakableScreen.class, BreakablePerspective.class ) ) {
            for ( LifecyclePhase phase : LifecyclePhase.values() ) {
                params.put( "broken", phase.toString() );
                panel.add( new PlaceButton( placeManager,
                                            new DefaultPlaceRequest( activityClass.getName(),
                                                                     params ) ) );
            }
        }

        panel.add( new PlaceButton( placeManager,
                                    new DefaultPlaceRequest( DefaultPerspectiveActivity.class.getName() ) ) );
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
