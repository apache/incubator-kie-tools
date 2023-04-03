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

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.util.MockIOCBeanDef;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityManagerLifecycleTest {

    public static final String PATH_PLACE_ID = "id";
    // things to inject into the activity manager
    @Mock
    SyncBeanManagerImpl iocManager;
    @Mock
    ActivityBeansCache activityBeansCache;

    // the activity manager we're unit testing
    @InjectMocks
    ActivityManagerImpl activityManager = new ActivityManagerImpl();

    // things that are useful to individual tests
    PlaceRequest kansas;
    Activity kansasActivity = mock(Activity.class);

    Path path = mock(Path.class);
    PlaceRequest pathPlace;
    Activity pathPlaceActivity = mock(Activity.class);

    private SyncBeanDef<Activity> pathIocBeanSpy;

    @Before
    public void setup() {
        kansas = new DefaultPlaceRequest("kansas");
        when(kansasActivity.getPlace()).thenReturn(kansas);

        SyncBeanDef<Activity> kansasIocBean = makeDependentBean(Activity.class,
                                                                kansasActivity);
        when(activityBeansCache.getActivity("kansas")).thenReturn(kansasIocBean);

        pathPlace = new PathPlaceRequest(path) {
            @Override
            protected ObservablePath createObservablePath(Path path) {
                return mock(ObservablePath.class);
            }
        };

        when(pathPlaceActivity.getPlace()).thenReturn(pathPlace);
        when(pathPlaceActivity.getIdentifier()).thenReturn(PATH_PLACE_ID);
        SyncBeanDef<Activity> pathIocBean = makeDependentBean(Activity.class,
                                                              pathPlaceActivity);
        pathIocBeanSpy = spy(pathIocBean);
        when(activityBeansCache.getActivity(pathPlace.getIdentifier())).thenReturn(pathIocBeanSpy);
    }

    @Test
    public void shouldCallOnStartupBeforeReturningNewActivity() throws Exception {
        Set<Activity> activities = activityManager.getActivities(kansas);

        assertEquals(1,
                     activities.size());
        assertEquals(kansasActivity,
                     activities.iterator().next());

        verify(kansasActivity,
               times(1)).onStartup(kansas);
    }

    @Test
    public void shouldResolvePlaceIdentifierForPathPlaceRequestOnGetActivity() throws Exception {

        assertEquals(PathPlaceRequest.NULL,
                     pathPlace.getIdentifier());
        Activity activity = activityManager.getActivity(pathPlace);
        assertNotNull(activity);
        assertEquals(PATH_PLACE_ID,
                     pathPlace.getIdentifier());
        assertEquals(pathPlaceActivity,
                     activity);
    }

    @Test
    public void shouldResolvePlaceIdentifierForPathPlaceRequestsOnGetActivities() throws Exception {

        assertEquals(PathPlaceRequest.NULL,
                     pathPlace.getIdentifier());

        Set<Activity> activities = activityManager.getActivities(pathPlace);
        assertEquals(1,
                     activities.size());
        assertEquals(PATH_PLACE_ID,
                     activities.iterator().next().getPlace().getIdentifier());
    }

    @Test
    public void activityBeanShouldBeCreatedOnlyOnceOnGetActivities() throws Exception {
        activityManager.getActivities(pathPlace);
        verify(pathIocBeanSpy,
               times(1)).getInstance();
    }

    @Test
    public void activityBeanShouldBeCreatedOnlyOnceOnGetActivity() throws Exception {
        activityManager.getActivity(pathPlace);
        activityManager.getActivity(pathPlace);
        verify(pathIocBeanSpy,
               times(1)).getInstance();
    }

    @Test
    public void shouldResolveIdentifier() throws Exception {
        Set<Activity> activities = activityManager.getActivities(kansas);

        assertEquals(1,
                     activities.size());
        assertEquals(kansasActivity,
                     activities.iterator().next());

        verify(kansasActivity,
               times(1)).onStartup(kansas);
    }

    @Test
    public void shouldCallOnShutdownWhenDestroyingActivity() throws Exception {
        activityManager.getActivities(kansas);
        activityManager.destroyActivity(kansasActivity);

        verify(kansasActivity,
               times(1)).onShutdown();
        verify(iocManager,
               times(1)).destroyBean(kansasActivity);
    }

    @Test
    public void shouldThrowExceptionWhenDestroyingDestroyedActivity() throws Exception {
        activityManager.getActivities(kansas);
        activityManager.destroyActivity(kansasActivity);

        try {
            activityManager.destroyActivity(kansasActivity);
            fail("second destroy should have thrown an exception");
        } catch (IllegalStateException e) {
            // expected
        }

        verify(kansasActivity,
               times(1)).onShutdown();
        verify(iocManager,
               times(1)).destroyBean(kansasActivity);
    }



    @SuppressWarnings("unchecked")
    private <T> SyncBeanDef<T> makeDependentBean(final Class<T> type,
                                                 final T beanInstance) {
        final SyncBeanDef<T> beanDef = new MockIOCBeanDef<T, T>(beanInstance,
                                                                type,
                                                                Dependent.class,
                                                                null,
                                                                beanInstance.getClass().getSimpleName(),
                                                                true);
        when((IOCBeanDef<T>) iocManager.lookupBean(beanInstance.getClass())).thenReturn(beanDef);
        return beanDef;
    }

    /**
     * Makes a singleton bean with the given name.
     */
    @SuppressWarnings("unchecked")
    private <T> SyncBeanDef<T> makeSingletonBean(final Class<T> type,
                                                 final T beanInstance,
                                                 final String name) {
        SyncBeanDef<T> beanDef = new MockIOCBeanDef<T, T>(beanInstance,
                                                          type,
                                                          ApplicationScoped.class,
                                                          null,
                                                          name,
                                                          true);

        when((IOCBeanDef<T>) iocManager.lookupBean(beanInstance.getClass())).thenReturn(beanDef);
        return beanDef;
    }
}
