/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.mvp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

@IsSplashScreen
public class ActivityBeansCacheUnitTestWrapper extends ActivityBeansCache {

    private String idMock;
    private SyncBeanDef mockDef;
    private SplashScreenActivity splashScreenActivity;
    private Collection<SyncBeanDef<Activity>> availableActivities = new HashSet<SyncBeanDef<Activity>>();
    private List<ActivityAndMetaInfo> activitiesAndMetaInfo = new ArrayList<ActivityAndMetaInfo>();
    private  Pair<Integer, List<Class<? extends ClientResourceType>>> metaInfo;
    private boolean mockSplashcreen = true;

    public ActivityBeansCacheUnitTestWrapper() {
        mockDef = mock( SyncBeanDef.class );
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

    @Override
    Collection<SyncBeanDef<Activity>> getAvailableActivities() {
        return availableActivities;
    }

    public SyncBeanDef getMockDef() {
        return mockDef;
    }

    public SplashScreenActivity getSplashScreenActivity() {
        return splashScreenActivity;
    }

    public String getIdMock() {
        return idMock;
    }

    public void duplicateActivity() {
        SyncBeanDef duplicateMockDef = mock( SyncBeanDef.class );
        when( duplicateMockDef.getName() ).thenReturn( idMock );
        availableActivities.add( duplicateMockDef );
    }

    @Override
    List<ActivityAndMetaInfo> getResourceActivities() {
        if(mockSplashcreen)  return activitiesAndMetaInfo;

        return super.getResourceActivities();
    }

    @Override
    Pair<Integer, List<Class<? extends ClientResourceType>>> generateActivityMetaInfo( SyncBeanDef<Activity> activityBean ) {
        return metaInfo;
    }

    public void mockActivityBehaviour() {
        mockSplashcreen=false;

        metaInfo = mock(Pair.class);
        when( metaInfo.getK1() ).thenReturn( new Integer(1) );
        when( metaInfo.getK2() ).thenReturn( new ArrayList<Class<? extends ClientResourceType>>(  ) );
    }
}
