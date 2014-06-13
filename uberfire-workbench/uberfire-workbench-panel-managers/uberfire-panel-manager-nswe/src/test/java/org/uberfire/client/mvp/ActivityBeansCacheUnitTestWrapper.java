package org.uberfire.client.mvp;

import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCDependentBean;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

@IsSplashScreen
public class ActivityBeansCacheUnitTestWrapper extends ActivityBeansCache {

    private String idMock;
    private IOCBeanDef mockDef;
    private SplashScreenActivity splashScreenActivity;
    private Collection<IOCBeanDef<Activity>> availableActivities = new HashSet<IOCBeanDef<Activity>>();
    private List<ActivityAndMetaInfo> activitiesAndMetaInfo = new ArrayList<ActivityAndMetaInfo>();
    private  Pair<Integer, List<Class<? extends ClientResourceType>>> metaInfo;
    private boolean mockSplashcreen = true;

    public ActivityBeansCacheUnitTestWrapper() {
        mockDef = mock( IOCDependentBean.class );
        idMock = "mockDef1";
        when( mockDef.getName() ).thenReturn( idMock );
        when( mockDef.getBeanClass() ).thenReturn( this.getClass() );
        availableActivities.add( mockDef );
    }

    public void mockSplashScreenBehaviour() {
        mockSplashcreen = true;

        Set<Annotation> annotations = new HashSet<Annotation>( Arrays.asList( ActivityBeansCacheUnitTestWrapper.class.getAnnotations() ) );
        when( mockDef.getQualifiers() ).thenReturn( annotations );

        splashScreenActivity = mock( AbstractSplashScreenActivity.class );
        when( mockDef.getInstance() ).thenReturn( splashScreenActivity );

    }

    public void createActivitiesAndMetaInfo( int priority1,
                                             int priority2 ) {
        activitiesAndMetaInfo.add( new ActivityAndMetaInfo( null, priority1, new ArrayList() ) );
        activitiesAndMetaInfo.add( new ActivityAndMetaInfo( null, priority2, new ArrayList() ) );
    }

    Collection<IOCBeanDef<Activity>> getAvailableActivities() {
        return availableActivities;
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
        IOCBeanDef duplicateMockDef = mock( IOCDependentBean.class );
        when( duplicateMockDef.getName() ).thenReturn( idMock );
        availableActivities.add( duplicateMockDef );
    }

    @Override
    List<ActivityAndMetaInfo> getResourceActivities() {
        if(mockSplashcreen)  return activitiesAndMetaInfo;

        return super.getResourceActivities();
    }

    Pair<Integer, List<Class<? extends ClientResourceType>>> generateActivityMetaInfo( IOCBeanDef<Activity> activityBean ) {
        return metaInfo;
    }

    public void mockActivityBehaviour() {
        mockSplashcreen=false;

        metaInfo = mock(Pair.class);
        when( metaInfo.getK1() ).thenReturn( new Integer(1) );
        when( metaInfo.getK2() ).thenReturn( new ArrayList<Class<? extends ClientResourceType>>(  ) );
    }
}
