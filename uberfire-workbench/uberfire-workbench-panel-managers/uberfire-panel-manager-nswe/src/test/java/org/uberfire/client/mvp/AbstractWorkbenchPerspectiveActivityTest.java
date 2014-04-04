package org.uberfire.client.mvp;

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AbstractWorkbenchPerspectiveActivityTest extends BaseWorkbenchTest {


    @Test
    public void testAbstractWorkbenchPerspectiveActivityLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractWorkbenchPerspectiveActivity perspectiveActivity = mock( AbstractWorkbenchPerspectiveActivity.class );

        HashSet<Activity> activities = new HashSet<Activity>( 1 ) {{
            add( perspectiveActivity );
        }};

        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( perspectiveActivity, panelManager );

        placeManager.goTo( somewhere );

        verify( perspectiveActivity ).launch( eq( somewhere ), any( Command.class ) );

    }

}
