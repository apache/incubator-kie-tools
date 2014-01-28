package org.uberfire.client.mvp;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCDependentBean;

import static org.mockito.Mockito.*;

@IsSplashScreen
public class ActivityBeansCacheUnitTestWrapper extends ActivityBeansCache {

    private String idMock;
    private IOCBeanDef mockDef;
    private SplashScreenActivity splashScreenActivity;
    private Collection<IOCBeanDef<Activity>> activities = new HashSet<IOCBeanDef<Activity>>();

    public ActivityBeansCacheUnitTestWrapper() {
        mockDef = mock( IOCDependentBean.class );
        idMock = "mockDef1";
        when( mockDef.getName() ).thenReturn( idMock );
        when (mockDef.getBeanClass()).thenReturn(this.getClass() );
        activities.add( mockDef );
    }

    public void mockSplashScreenBehaviour() {
        Set<Annotation> annotations = new HashSet<Annotation>( Arrays.asList( ActivityBeansCacheUnitTestWrapper.class.getAnnotations() ) );
        ;
        when( mockDef.getQualifiers() ).thenReturn( annotations );

        splashScreenActivity = mock( AbstractSplashScreenActivity.class );
        when( mockDef.getInstance() ).thenReturn( splashScreenActivity );
    }

    Collection<IOCBeanDef<Activity>> getAvailableActivities() {
        return activities;
    }

    IOCBeanDef<Activity> reLookupBean( IOCBeanDef<Activity> baseBean ) {
        return mockDef;
    }

    public IOCBeanDef getMockDef() {
        return mockDef;
    }

    public SplashScreenActivity getSplashScreenActivity() {
        return splashScreenActivity;
    }

    public String getIdMock() {
        return idMock;
    }

    public void duplicateActivity() {
        IOCBeanDef  duplicateMockDef = mock( IOCDependentBean.class );
        when( duplicateMockDef.getName() ).thenReturn( idMock );
        activities.add( duplicateMockDef );
    }
}
