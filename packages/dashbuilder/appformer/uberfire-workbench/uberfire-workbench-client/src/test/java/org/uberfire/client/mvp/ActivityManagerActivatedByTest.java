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

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwtmockito.GwtMockitoTestRunner;

/**
 * Mock-based tests for how ActivityManager handles beans affected by Errai IOC's {@code @ActivatedBy} feature.
 */
@RunWith(GwtMockitoTestRunner.class)
public class ActivityManagerActivatedByTest {

    @SuppressWarnings("unchecked")
    private final SyncBeanDef<Activity> activatedActivityBean = mock(SyncBeanDef.class);
    @SuppressWarnings("unchecked")
    private final SyncBeanDef<Activity> nonActivatedActivityBean = mock(SyncBeanDef.class);
    /**
     * The thing we're unit testing
     */
    @InjectMocks
    private ActivityManagerImpl activityManager;
    @Mock
    private ActivityBeansCache activityBeansCache;
    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private ResourceTypeManagerCache resourceTypeManagerCache;

    private Activity activatedActivity;

    @Before
    public void setup() {

        activatedActivity = mock(Activity.class);
        when(activatedActivity.getIdentifier()).thenReturn("activated activity");

        when(activatedActivityBean.getInstance()).thenReturn(activatedActivity);
        when(activatedActivityBean.isActivated()).thenReturn(true);

        when(nonActivatedActivityBean.isActivated()).thenReturn(false);

        Collection<SyncBeanDef<Activity>> activityList = new ArrayList<SyncBeanDef<Activity>>();
        activityList.add(activatedActivityBean);
        activityList.add(nonActivatedActivityBean);

        // This covers the case where the activity manager goes directly to the Errai bean manager.
        // The list includes all beans, active or otherwise, and the activity manager has to filter them.
        when(iocManager.lookupBeans(Activity.class)).thenReturn(activityList);

        // And this covers the case where the activity manager does the lookup via the ActivityBeansCache.
        // We set this up assuming ActivityBeansCache is well-behaved, and hides the existence of inactive beans.
        // (of course this assumption is verified in a separate test)
        ActivityAndMetaInfo activatedActivityAndMetaInfo =
                new ActivityAndMetaInfo(iocManager,
                                        activatedActivityBean,
                                        0,
                                        Collections.<String>emptyList());
        when(resourceTypeManagerCache.getResourceActivities()).thenReturn(singletonList(activatedActivityAndMetaInfo));
        when(activityBeansCache.getActivity("activated activity")).thenReturn(activatedActivityBean);
    }

    @After
    public void runBlanketVerifications() {

        // no matter what else we're testing, the non-activated bean should never be instantiated
        verify(nonActivatedActivityBean,
               never()).getInstance();
        verify(nonActivatedActivityBean,
               never()).newInstance();
    }

    @Test
    public void getActivitiesByTypeShouldRespectBeanActivationStatus() throws Exception {
        Set<Activity> activities = activityManager.getActivities(Activity.class);

        assertEquals(1,
                     activities.size());
        assertSame(activatedActivity,
                   activities.iterator().next());
    }

    @Test
    public void getActivitiesForActivePlaceRequestShouldReturnActivity() throws Exception {
        Set<Activity> activities = activityManager.getActivities(new DefaultPlaceRequest("activated activity"));

        assertEquals(1,
                     activities.size());
        assertSame(activatedActivity,
                   activities.iterator().next());
    }

    @Test
    public void getActivitiesForInactivePlaceRequestShouldReturnEmptySet() throws Exception {
        Set<Activity> activities = activityManager.getActivities(new DefaultPlaceRequest("non-activated activity"));

        assertEquals(0,
                     activities.size());
    }

    @Test
    public void getActivityForActivePlaceRequestShouldReturnActivity() throws Exception {
        Activity activity = activityManager.getActivity(Activity.class,
                                                        new DefaultPlaceRequest("activated activity"));

        assertSame(activatedActivity,
                   activity);
    }

    @Test
    public void getActivityForInactivePlaceRequestShouldReturnNull() throws Exception {
        Activity activity = activityManager.getActivity(Activity.class,
                                                        new DefaultPlaceRequest("non-activated activity"));

        assertNull(activity);
    }
}
