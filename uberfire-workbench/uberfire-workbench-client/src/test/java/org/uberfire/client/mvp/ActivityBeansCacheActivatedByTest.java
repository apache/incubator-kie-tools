package org.uberfire.client.mvp;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache.ActivityAndMetaInfo;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.DotResourceType;

import com.google.gwtmockito.GwtMockitoTestRunner;


/**
 * Tests that {@link ActivityBeansCache} respects the active flag controlled by the {@code @ActivatedBy} annotation.
 */
@RunWith( GwtMockitoTestRunner.class )
@SuppressWarnings("rawtypes")
public class ActivityBeansCacheActivatedByTest {

    @InjectMocks
    ActivityBeansCache activityBeansCache;

    @Mock
    SyncBeanManager iocManager;

    private ActiveSplashScreenActivity activeSplashScreenActivity;
    private IOCBeanDef activeSplashScreenActivityBean;
    private IOCBeanDef nonActiveSplashScreenActivityBean;
    private ActiveRegularActivity activeRegularActivity;
    private IOCBeanDef activeRegularActivityBean;
    private IOCBeanDef nonActiveRegularActivityBean;
    private ActiveResourceActivity activeResourceActivity;
    private IOCBeanDef activeResourceActivityBean;
    private IOCBeanDef nonActiveResourceActivityBean;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        activeSplashScreenActivity = mock( ActiveSplashScreenActivity.class );
        activeSplashScreenActivityBean = mockSplashScreenActivityBean(ActiveSplashScreenActivity.class, activeSplashScreenActivity);

        nonActiveSplashScreenActivityBean = mockSplashScreenActivityBean( NonActiveSplashScreenActivity.class, null );

        activeRegularActivity = mock( ActiveRegularActivity.class );
        activeRegularActivityBean = mockRegularBean(ActiveRegularActivity.class, activeRegularActivity);

        nonActiveRegularActivityBean = mockRegularBean( NonActiveRegularActivity.class, null );

        activeResourceActivity = mock( ActiveResourceActivity.class );
        activeResourceActivityBean = mockResourceActivityBean(ActiveResourceActivity.class, activeResourceActivity);
        mockRegularBean( DotResourceType.class, new DotResourceType() );

        nonActiveResourceActivityBean = mockResourceActivityBean( NonActiveResourceActivity.class, null );

        Collection<IOCBeanDef<SplashScreenActivity>> splashScreenBeans = new ArrayList<IOCBeanDef<SplashScreenActivity>>();
        splashScreenBeans.add( activeSplashScreenActivityBean );
        splashScreenBeans.add( nonActiveSplashScreenActivityBean );

        // all activity beans, including splash screens
        Collection<IOCBeanDef<Activity>> allActivityBeans = new ArrayList<IOCBeanDef<Activity>>();
        allActivityBeans.add( activeSplashScreenActivityBean );
        allActivityBeans.add( nonActiveSplashScreenActivityBean );
        allActivityBeans.add( activeRegularActivityBean );
        allActivityBeans.add( nonActiveRegularActivityBean );
        allActivityBeans.add( activeResourceActivityBean );
        allActivityBeans.add( nonActiveResourceActivityBean );

        when( iocManager.lookupBeans( SplashScreenActivity.class ) ).thenReturn( splashScreenBeans );
        when( iocManager.lookupBeans( Activity.class ) ).thenReturn( allActivityBeans );
    }

    @Test
    public void shouldNotReturnInactiveBeansFromGetSplashScreens() throws Exception {
        activityBeansCache.init();
        List<SplashScreenActivity> splashScreens = activityBeansCache.getSplashScreens();

        assertEquals( 1, splashScreens.size() );
        assertSame( activeSplashScreenActivity, splashScreens.iterator().next() );
    }

    @Test
    public void cacheShouldNotReturnInactiveBeansFromGetResourceActivities() throws Exception {
        activityBeansCache.init();
        List<ActivityAndMetaInfo> activityBeans = activityBeansCache.getResourceActivities();

        assertEquals( 1, activityBeans.size() );
        assertSame( activeResourceActivityBean, activityBeans.get(0).getActivityBean() );
    }

    @Test
    public void cacheShouldNotReturnInactiveBeansByName() throws Exception {
        activityBeansCache.init();

        assertSame( activeSplashScreenActivityBean, activityBeansCache.getActivity( "ActiveSplashScreenActivity" ) );
        assertSame( activeResourceActivityBean, activityBeansCache.getActivity( "ActiveResourceActivity" ) );
        assertSame( activeRegularActivityBean, activityBeansCache.getActivity( "ActiveRegularActivity" ) );
        assertNull( activityBeansCache.getActivity( "NonActiveSplashScreenActivity" ) );
        assertNull( activityBeansCache.getActivity( "NonActiveResourceActivity" ) );
        assertNull( activityBeansCache.getActivity( "NonActiveRegularActivity" ) );
    }

    interface ActiveSplashScreenActivity extends SplashScreenActivity {}
    interface NonActiveSplashScreenActivity extends SplashScreenActivity {}
    interface ActiveRegularActivity extends Activity {}
    interface NonActiveRegularActivity extends Activity {}
    interface ActiveResourceActivity extends Activity {}
    interface NonActiveResourceActivity extends Activity {}

    private static final IsSplashScreen IS_SPLASH_SCREEN = new IsSplashScreen() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return IsSplashScreen.class;
        }
    };

    private static final AssociatedResources ASSOCIATED_RESOURCES = new AssociatedResources() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return AssociatedResources.class;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<? extends ClientResourceType>[] value() {
            return (Class<? extends ClientResourceType>[]) new Class<?>[] { DotResourceType.class };
        }
    };

    @SuppressWarnings("unchecked")
    private <T> IOCBeanDef mockRegularBean(Class<T> type, T instance) {
        IOCBeanDef<T> beanDef = mock( IOCBeanDef.class );
        when( iocManager.lookupBean( type ) ).thenReturn( beanDef );
        when( beanDef.getInstance() ).thenReturn( instance );
        when( beanDef.getBeanClass() ).thenReturn( (Class) type );
        when( beanDef.isActivated() ).thenReturn( instance != null );
        when( beanDef.getName() ).thenReturn( type.getSimpleName() );
        return beanDef;
    }

    private <T extends SplashScreenActivity> IOCBeanDef mockSplashScreenActivityBean(Class<T> type, T instance) {
        IOCBeanDef beanDef = mockRegularBean( type, instance );
        when( beanDef.getQualifiers() ).thenReturn( Collections.singleton( IS_SPLASH_SCREEN ) );
        return beanDef;
    }

    private <T extends Activity> IOCBeanDef mockResourceActivityBean(Class<T> type, T instance) {
        IOCBeanDef beanDef = mockRegularBean( type, instance );
        when( beanDef.getQualifiers() ).thenReturn( Collections.singleton( ASSOCIATED_RESOURCES ) );
        return beanDef;
    }

}
