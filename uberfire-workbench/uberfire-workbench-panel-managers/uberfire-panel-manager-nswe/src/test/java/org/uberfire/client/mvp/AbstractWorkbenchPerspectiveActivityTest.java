package org.uberfire.client.mvp;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractWorkbenchPerspectiveActivityTest extends BasePanelManagerTest {


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

        verify( perspectiveActivity, never() ).onStartup( any( PlaceRequest.class ) );
        verify( perspectiveActivity ).onOpen();
    }

}
