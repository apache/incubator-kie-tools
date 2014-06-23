package org.uberfire.client.mvp;

import static java.util.Collections.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
        when( activityManager.getActivities( somewhere ) ).thenReturn( singleton( (Activity) perspectiveActivity ) );

        placeManager = new PlaceManagerImplUnitTestWrapper( perspectiveActivity, panelManager );

        placeManager.goTo( somewhere );

        verify( perspectiveActivity, never() ).onStartup( any( PlaceRequest.class ) );
        verify( perspectiveActivity ).onOpen();
    }

}
