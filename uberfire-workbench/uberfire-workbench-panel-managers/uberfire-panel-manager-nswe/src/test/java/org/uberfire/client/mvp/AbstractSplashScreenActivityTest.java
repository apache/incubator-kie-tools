package org.uberfire.client.mvp;

import static java.util.Collections.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractSplashScreenActivityTest extends BasePanelManagerTest {


    @Test
    public void testSplashScreenActivityShouldNotLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractSplashScreenActivity activity = mock( AbstractSplashScreenActivity.class );
        when( activityManager.getActivities( somewhere ) ).thenReturn( singleton( (Activity) activity ) );

        placeManager = new PlaceManagerImplUnitTestWrapper( activity, panelManager );

        placeManager.goTo( somewhere );

        verify( activity, never() ).onStartup( eq( somewhere ) );
        verify( activity, never() ).onOpen();
    }


    @Test
    public void testSplashScreenActivityLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractSplashScreenActivity splashScreenActivity = mock( AbstractSplashScreenActivity.class );


        final AbstractWorkbenchPerspectiveActivity perspectiveActivity = mock( AbstractWorkbenchPerspectiveActivity.class );

        HashSet<Activity> activities = new HashSet<Activity>( Arrays.asList( perspectiveActivity,
                                                                             splashScreenActivity ));

        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( perspectiveActivity, panelManager, splashScreenActivity );

        placeManager.goTo( somewhere );

        verify( splashScreenActivity, never() ).onStartup( eq( somewhere ) );
        verify( splashScreenActivity ).onOpen();

    }

}
