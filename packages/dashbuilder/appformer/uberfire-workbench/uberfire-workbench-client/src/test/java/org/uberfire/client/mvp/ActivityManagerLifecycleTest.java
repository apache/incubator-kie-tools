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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
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

    @Test
    public void lookupShouldReturnNullWhenPlaceHasNoSplashScreen() throws Exception {
        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor(kansas);
        assertNull(splashScreenActivity);
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldStartSplashScreens() throws Exception {
        PlaceRequest oz = new DefaultPlaceRequest("oz");

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts(kansas);
        SplashScreenActivity nonExpectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts(oz);
        splashScreenList.add(expectedSplashScreenActivity);

        when(activityBeansCache.getSplashScreens()).thenReturn(splashScreenList);

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor(kansas);
        assertSame(expectedSplashScreenActivity,
                   splashScreenActivity);
        verify(splashScreenActivity,
               times(1)).onStartup(kansas);

        verify(nonExpectedSplashScreenActivity,
               never()).onStartup(any(PlaceRequest.class));
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldNotStartDisabledSplashScreens() throws Exception {
        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeSplashScreenThatIntercepts(kansas,
                                                                                           false);
        splashScreenList.add(expectedSplashScreenActivity);

        when(activityBeansCache.getSplashScreens()).thenReturn(splashScreenList);

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor(kansas);
        assertNull(splashScreenActivity);
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldStopSplashScreensWhenDestroyed() throws Exception {

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts(kansas);
        splashScreenList.add(expectedSplashScreenActivity);

        when(activityBeansCache.getSplashScreens()).thenReturn(splashScreenList);

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor(kansas);
        activityManager.destroyActivity(splashScreenActivity);
        verify(expectedSplashScreenActivity,
               times(1)).onShutdown();
        assertFalse(activityManager.isStarted(expectedSplashScreenActivity));

        // never try to destroy singleton beans!
        verify(iocManager,
               never()).destroyBean(expectedSplashScreenActivity);
    }

    @Test
    public void shouldNotGetConfusedAboutSplashScreensWithSamePlaceAsTheirScreen() throws Exception {

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts(kansas);
        splashScreenList.add(expectedSplashScreenActivity);

        when(activityBeansCache.getSplashScreens()).thenReturn(splashScreenList);

        // this loads the regular kansas activity (not the splash screen) into the activityBeansCache
        activityManager.getActivity(kansas);

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor(kansas);

        // this must not get confused even though expectedSplashScreenActivity and kansasActivity both have the same PlaceRequest
        activityManager.destroyActivity(splashScreenActivity);

        verify(expectedSplashScreenActivity,
               times(1)).onShutdown();
        assertFalse(activityManager.isStarted(expectedSplashScreenActivity));

        // never try to destroy singleton beans!
        verify(iocManager,
               never()).destroyBean(expectedSplashScreenActivity);
    }

    /**
     * At the time this test was made, splash screens were handled as special cases because they're ApplicationScoped rather than Dependent.
     */
    @Test
    public void shouldThrowExceptionWhenDoubleDestroyingSplashScreen() throws Exception {

        List<SplashScreenActivity> splashScreenList = new ArrayList<SplashScreenActivity>();
        SplashScreenActivity expectedSplashScreenActivity = makeEnabledSplashScreenThatIntercepts(kansas);
        splashScreenList.add(expectedSplashScreenActivity);

        when(activityBeansCache.getSplashScreens()).thenReturn(splashScreenList);

        SplashScreenActivity splashScreenActivity = activityManager.getSplashScreenInterceptor(kansas);
        activityManager.destroyActivity(splashScreenActivity);
        try {
            activityManager.destroyActivity(splashScreenActivity);
            fail("should have thrown exception on double destroy");
        } catch (IllegalStateException e) {
            // expected
        }

        verify(expectedSplashScreenActivity,
               times(1)).onShutdown();
        // never try to destroy singleton beans!
        verify(iocManager,
               never()).destroyBean(expectedSplashScreenActivity);
    }

    @Test
    public void shouldNotAttemptToDestroyRuntimeRegisteredSingletonActivities() throws Exception {
        abstract class MyPerspectiveActivity implements PerspectiveActivity {

        }
        ;
        final String myPerspectiveId = "myPerspectiveId";
        final MyPerspectiveActivity activity = mock(MyPerspectiveActivity.class);
        when(activity.getPlace()).thenReturn(new DefaultPlaceRequest(myPerspectiveId));

        // note that we're telling the bean manager this bean is of concrete type PerspectiveActivity.
        // this mirrors what the JavaScript runtime plugin API does.
        SyncBeanDef<PerspectiveActivity> perspectiveActivityBean = makeSingletonBean(PerspectiveActivity.class,
                                                                                     activity,
                                                                                     myPerspectiveId);

        when(activityBeansCache.getActivity(myPerspectiveId)).thenReturn((SyncBeanDef) perspectiveActivityBean);

        Activity retrievedActivity = activityManager.getActivity(Activity.class,
                                                                 new DefaultPlaceRequest(myPerspectiveId));
        activityManager.destroyActivity(retrievedActivity);

        // it's a singleton, so we should not try to destroy it.
        verify(iocManager,
               never()).destroyBean(activity);
    }

    private SplashScreenActivity makeEnabledSplashScreenThatIntercepts(final PlaceRequest place) {
        return makeSplashScreenThatIntercepts(place,
                                              true);
    }

    private SplashScreenActivity makeSplashScreenThatIntercepts(final PlaceRequest place,
                                                                final boolean enabled) {
        String splashActivityName = place.getIdentifier() + "!Splash";
        SplashScreenActivity splashScreenActivity = mock(SplashScreenActivity.class);
        when(splashScreenActivity.isEnabled()).thenReturn(enabled);
        when(splashScreenActivity.intercept(place)).thenReturn(true);
        makeSingletonBean(SplashScreenActivity.class,
                          splashScreenActivity);
        return splashScreenActivity;
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
     * Makes a singleton bean whose name is type.getSimpleName().
     */
    private <T> IOCBeanDef<T> makeSingletonBean(final Class<T> type,
                                                final T beanInstance) {
        return makeSingletonBean(type,
                                 beanInstance,
                                 type.getSimpleName());
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
