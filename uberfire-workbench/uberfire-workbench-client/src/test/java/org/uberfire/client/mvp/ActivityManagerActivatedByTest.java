package org.uberfire.client.mvp;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.jboss.errai.ioc.client.container.CreationalContext;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache.ActivityAndMetaInfo;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import com.google.gwtmockito.GwtMockitoTestRunner;

/**
 * Mock-based tests for how ActivityManager handles beans affected by Errai IOC's {@code @ActivatedBy} feature.
 */
@RunWith( GwtMockitoTestRunner.class )
public class ActivityManagerActivatedByTest {

    /** The thing we're unit testing */
    @InjectMocks
    ActivityManagerImpl activityManager;

    @Mock
    ActivityBeansCache activityBeansCache;

    @Mock
    SyncBeanManager iocManager;

    @Mock
    AuthorizationManager authzManager;


    private Activity activatedActivity;

    @SuppressWarnings("unchecked")
    private final IOCBeanDef<Activity> activatedActivityBean = mock( IOCBeanDef.class );

    @SuppressWarnings("unchecked")
    private final IOCBeanDef<Activity> nonActivatedActivityBean = mock( IOCBeanDef.class );

    @Before
    public void setup() {
        when( authzManager.authorize( any( Resource.class ), any( User.class) ) ).thenReturn( true );

        activatedActivity = mock( Activity.class );
        when( activatedActivity.getSignatureId() ).thenReturn( "activated activity" );

        when( activatedActivityBean.getInstance() ).thenReturn( activatedActivity );
        when( activatedActivityBean.isActivated() ).thenReturn( true );

        when( nonActivatedActivityBean.isActivated() ).thenReturn( false );

        Collection<IOCBeanDef<Activity>> activityList = new ArrayList<IOCBeanDef<Activity>>();
        activityList.add( activatedActivityBean );
        activityList.add( nonActivatedActivityBean );

        // This covers the case where the activity manager goes directly to the Errai bean manager.
        // The list includes all beans, active or otherwise, and the activity manager has to filter them.
        when( iocManager.lookupBeans( Activity.class ) ).thenReturn( activityList );

        // And this covers the case where the activity manager does the lookup via the ActivityBeansCache.
        // We set this up assuming ActivityBeansCache is well-behaved, and hides the existence of inactive beans.
        // (of course this assumption is verified in a separate test)
        ActivityAndMetaInfo activatedActivityAndMetaInfo =
                activityBeansCache.new ActivityAndMetaInfo( activatedActivityBean, 0, Collections.<Class<? extends ClientResourceType>>emptyList() );
        when( activityBeansCache.getResourceActivities() ).thenReturn( singletonList( activatedActivityAndMetaInfo ) );
        when( activityBeansCache.getActivity( "activated activity" ) ).thenReturn( activatedActivityBean );
    }

    @After
    public void runBlanketVerifications() {

        // no matter what else we're testing, the non-activated bean should never be instantiated
        verify( nonActivatedActivityBean, never() ).getInstance();
        verify( nonActivatedActivityBean, never() ).getInstance( any( CreationalContext.class) );
        verify( nonActivatedActivityBean, never() ).newInstance();
    }


    @Test
    public void getActivitiesByTypeShouldRespectBeanActivationStatus() throws Exception {
        Set<Activity> activities = activityManager.getActivities( Activity.class );

        assertEquals( 1, activities.size() );
        assertSame( activatedActivity, activities.iterator().next() );
    }

    @Test
    public void getActivitiesForActivePlaceRequestShouldReturnActivity() throws Exception {
        Set<Activity> activities = activityManager.getActivities( new DefaultPlaceRequest( "activated activity" )) ;

        assertEquals( 1, activities.size() );
        assertSame( activatedActivity, activities.iterator().next() );
    }

    @Test
    public void getActivitiesForInactivePlaceRequestShouldReturnEmptySet() throws Exception {
        Set<Activity> activities = activityManager.getActivities( new DefaultPlaceRequest( "non-activated activity" )) ;

        assertEquals( 0, activities.size() );
    }

    @Test
    public void getActivityForActivePlaceRequestShouldReturnActivity() throws Exception {
        Activity activity = activityManager.getActivity( Activity.class, new DefaultPlaceRequest( "activated activity" )) ;

        assertSame( activatedActivity, activity );
    }

    @Test
    public void getActivityForInactivePlaceRequestShouldReturnNull() throws Exception {
        Activity activity = activityManager.getActivity( Activity.class, new DefaultPlaceRequest( "non-activated activity" )) ;

        assertNull( activity );
    }

}
