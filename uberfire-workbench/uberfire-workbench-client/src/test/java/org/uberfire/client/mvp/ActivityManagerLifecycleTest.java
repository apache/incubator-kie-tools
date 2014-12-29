package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.BeanProvider;
import org.jboss.errai.ioc.client.container.CreationalContext;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCDependentBean;
import org.jboss.errai.ioc.client.container.IOCSingletonBean;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivityManagerLifecycleTest {

    // things to inject into the activity manager
    @Mock
    SyncBeanManagerImpl iocManager;
    @Mock
    ActivityBeansCache activityBeansCache;
    @Mock
    AuthorizationManager authzManager;
    @Spy
    User dorothy = new UserImpl( "dorothy" );

    // the activity manager we're unit testing
    @InjectMocks
    ActivityManagerImpl activityManager;

    // things that are useful to individual tests
    PlaceRequest kansas;
    Activity kansasActivity = mock( Activity.class );

    @Before
    public void setup() {
        kansas = new DefaultPlaceRequest( "kansas" );
        when( kansasActivity.getPlace() ).thenReturn( kansas );

        IOCBeanDef<Activity> kansasIocBean = makeDependentBean( Activity.class, kansasActivity );
        when( activityBeansCache.getActivity( "kansas" ) ).thenReturn( kansasIocBean );

        when( authzManager.authorize( any( Resource.class ),
                                      eq( dorothy ) ) ).thenReturn( true );

    }

    @Test
    public void shouldCallOnStartupBeforeReturningNewActivity() throws Exception {
        Set<Activity> activities = activityManager.getActivities( kansas );

        assertEquals( 1, activities.size() );
        assertEquals( kansasActivity, activities.iterator().next() );

        verify( kansasActivity, times( 1 ) ).onStartup( kansas );
    }

    @Test
    public void shouldCallOnShutdownWhenDestroyingActivity() throws Exception {
        activityManager.getActivities( kansas );
        activityManager.destroyActivity( kansasActivity );

        verify( kansasActivity, times( 1 ) ).onShutdown();
        verify( iocManager, times( 1 ) ).destroyBean( kansasActivity );
    }

    @Test
    public void shouldThrowExceptionWhenDestroyingDestroyedActivity() throws Exception {
        activityManager.getActivities( kansas );
        activityManager.destroyActivity( kansasActivity );

        try {
            activityManager.destroyActivity( kansasActivity );
            fail( "second destroy should have thrown an exception" );
        } catch ( IllegalStateException e ) {
            // expected
        }

        verify( kansasActivity, times( 1 ) ).onShutdown();
        verify( iocManager, times( 1 ) ).destroyBean( kansasActivity );
    }

    @Test
    public void shouldNotSeeUnauthorizedActivities() throws Exception {
        when( authzManager.authorize( any( Resource.class ),
                                      eq( dorothy ) ) ).thenReturn( false );
        Set<Activity> activities = activityManager.getActivities( kansas );
        assertEquals( 0, activities.size() );
    }

    @Test
    public void shouldNotLeakUnauthorizedActivityInstances() throws Exception {
        when( authzManager.authorize( any( Resource.class ),
                                      eq( dorothy ) ) ).thenReturn( false );
        activityManager.getActivities( kansas );

        // this overspecified; all we care is that any activity that was created has also been destroyed.
        // it would be equally okay if the bean was never instantiated in the first place.
        verify( activityBeansCache ).getActivity( "kansas" );
        verify( iocManager ).destroyBean( kansasActivity );
    }

    @Test
    public void shouldNotStartUnauthorizedActivities() throws Exception {
        when( authzManager.authorize( any( Resource.class ),
                                      eq( dorothy ) ) ).thenReturn( false );
        activityManager.getActivities( kansas );
        verify( kansasActivity, never() ).onStartup( kansas );
    }

    @Test
    public void lookupShouldReturnNullWhenPlaceHasNoSplashScreen() throws Exception {
        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor( kansas );
        assertNull( splashScreenActivity );
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldStartSplashScreens() throws Exception {
        PlaceRequest oz = new DefaultPlaceRequest( "oz" );

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts( kansas );
        SplashScreenActivity nonExpectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts( oz );
        splashScreenList.add( expectedSplashScreenActivity );

        when( activityBeansCache.getSplashScreens() ).thenReturn( splashScreenList );

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor( kansas );
        assertSame( expectedSplashScreenActivity, splashScreenActivity );
        verify( splashScreenActivity, times( 1 ) ).onStartup( kansas );

        verify( nonExpectedSplashScreenActivity, never() ).onStartup( any( PlaceRequest.class ) );
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldNotStartDisabledSplashScreens() throws Exception {
        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeSplashScreenThatIntercepts( kansas, false );
        splashScreenList.add( expectedSplashScreenActivity );

        when( activityBeansCache.getSplashScreens() ).thenReturn( splashScreenList );

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor( kansas );
        assertNull( splashScreenActivity );
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldStopSplashScreensWhenDestroyed() throws Exception {

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts( kansas );
        splashScreenList.add( expectedSplashScreenActivity );

        when( activityBeansCache.getSplashScreens() ).thenReturn( splashScreenList );

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor( kansas );
        activityManager.destroyActivity( splashScreenActivity );
        verify( expectedSplashScreenActivity, times( 1 ) ).onShutdown();
        assertFalse( activityManager.isStarted( expectedSplashScreenActivity ) );

        // never try to destroy singleton beans!
        verify( iocManager, never() ).destroyBean( expectedSplashScreenActivity );
    }

    @Test
    public void shouldNotGetConfusedAboutSplashScreensWithSamePlaceAsTheirScreen() throws Exception {

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts( kansas );
        when( expectedSplashScreenActivity.getPlace() ).thenReturn( kansas );
        splashScreenList.add( expectedSplashScreenActivity );

        when( activityBeansCache.getSplashScreens() ).thenReturn( splashScreenList );

        // this loads the regular kansas activity (not the splash screen) into the activityBeansCache
        activityManager.getActivity( kansas );

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor( kansas );

        // this must not get confused even though expectedSplashScreenActivity and kansasActivity both have the same PlaceRequest
        activityManager.destroyActivity( splashScreenActivity );

        verify( expectedSplashScreenActivity, times( 1 ) ).onShutdown();
        assertFalse( activityManager.isStarted( expectedSplashScreenActivity ) );

        // never try to destroy singleton beans!
        verify( iocManager, never() ).destroyBean( expectedSplashScreenActivity );
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldThrowExceptionWhenDoubleDestroyingSplashScreen() throws Exception {

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts( kansas );
        splashScreenList.add( expectedSplashScreenActivity );

        when( activityBeansCache.getSplashScreens() ).thenReturn( splashScreenList );

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor( kansas );
        activityManager.destroyActivity( splashScreenActivity );
        try {
            activityManager.destroyActivity( splashScreenActivity );
            fail( "should have thrown exception on double destroy" );
        } catch ( IllegalStateException e ) {
            // expected
        }

        verify( expectedSplashScreenActivity, times( 1 ) ).onShutdown();
        // never try to destroy singleton beans!
        verify( iocManager, never() ).destroyBean( expectedSplashScreenActivity );
    }

    @Test
    public void shouldNotAttemptToDestroyRuntimeRegisteredSingletonActivities() throws Exception {
        abstract class MyPerspectiveActivity implements PerspectiveActivity {

        }
        ;
        final String myPerspectiveId = "myPerspectiveId";
        final MyPerspectiveActivity activity = mock( MyPerspectiveActivity.class );
        when( activity.getIdentifier() ).thenReturn( myPerspectiveId );
        when( activity.getPlace() ).thenReturn( new DefaultPlaceRequest( myPerspectiveId ) );

        // note that we're telling the bean manager this bean is of concrete type PerspectiveActivity.
        // this mirrors what the JavaScript runtime plugin API does.
        IOCBeanDef<PerspectiveActivity> perspectiveActivityBean = makeSingletonBean( PerspectiveActivity.class, activity, myPerspectiveId );

        when( activityBeansCache.getActivity( myPerspectiveId ) ).thenReturn( (IOCBeanDef) perspectiveActivityBean );

        Activity retrievedActivity = activityManager.getActivity( Activity.class, new DefaultPlaceRequest( myPerspectiveId ) );
        activityManager.destroyActivity( retrievedActivity );

        // it's a singleton, so we should not try to destroy it.
        verify( iocManager, never() ).destroyBean( activity );
    }

    private SplashScreenActivity makeEnabledSplashScreenThatIntercepts( final PlaceRequest place ) {
        return makeSplashScreenThatIntercepts( place, true );
    }

    private SplashScreenActivity makeSplashScreenThatIntercepts( final PlaceRequest place,
                                                                 final boolean enabled ) {
        String splashActivityName = place.getIdentifier() + "!Splash";
        SplashScreenActivity splashScreenActivity = mock( SplashScreenActivity.class );
        when( splashScreenActivity.isEnabled() ).thenReturn( enabled );
        when( splashScreenActivity.intercept( place ) ).thenReturn( true );
        when( splashScreenActivity.getPlace() ).thenReturn( new DefaultPlaceRequest( splashActivityName ) );
        makeSingletonBean( SplashScreenActivity.class, splashScreenActivity );
        return splashScreenActivity;
    }

    @SuppressWarnings("unchecked")
    private <T> IOCBeanDef<T> makeDependentBean( final Class<T> type,
                                                 final T beanInstance ) {
        IOCBeanDef<T> beanDef = IOCDependentBean.newBean( iocManager,
                                                          type,
                                                          beanInstance.getClass(),
                                                          null,
                                                          type.getSimpleName(),
                                                          true,
                                                          new BeanProvider<T>() {
                                                              @Override
                                                              public T getInstance( CreationalContext context ) {
                                                                  return beanInstance;
                                                              }
                                                          },
                                                          null );
        when( (IOCBeanDef<T>) iocManager.lookupBean( beanInstance.getClass() ) ).thenReturn( beanDef );
        return beanDef;
    }

    /**
     * Makes a singleton bean whose name is type.getSimpleName().
     */
    private <T> IOCBeanDef<T> makeSingletonBean( final Class<T> type,
                                                 final T beanInstance ) {
        return makeSingletonBean( type, beanInstance, type.getSimpleName() );
    }

    /**
     * Makes a singleton bean with the given name.
     */
    @SuppressWarnings("unchecked")
    private <T> IOCBeanDef<T> makeSingletonBean( final Class<T> type,
                                                 final T beanInstance,
                                                 String name ) {
        IOCBeanDef<T> beanDef = IOCSingletonBean.newBean( iocManager,
                                                          type,
                                                          beanInstance.getClass(),
                                                          null,
                                                          name,
                                                          true,
                                                          new BeanProvider<T>() {
                                                              @Override
                                                              public T getInstance( CreationalContext context ) {
                                                                  return beanInstance;
                                                              }
                                                          },
                                                          beanInstance,
                                                          null );
        when( (IOCBeanDef<T>) iocManager.lookupBean( beanInstance.getClass() ) ).thenReturn( beanDef );
        return beanDef;
    }

}
