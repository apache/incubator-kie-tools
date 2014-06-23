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
public class AbstractPopupActivityTest  extends BasePanelManagerTest {


    @Test
    public void testPopUpLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractPopupActivity activity = mock( AbstractPopupActivity.class );

        when( activityManager.getActivities( somewhere ) ).thenReturn( singleton( (Activity) activity ) );

        placeManager = new PlaceManagerImplUnitTestWrapper( activity, panelManager );

        placeManager.goTo( somewhere );

        verify( activity, never() ).onStartup( any( PlaceRequest.class ) );
        verify( activity ).onOpen();
    }

}
