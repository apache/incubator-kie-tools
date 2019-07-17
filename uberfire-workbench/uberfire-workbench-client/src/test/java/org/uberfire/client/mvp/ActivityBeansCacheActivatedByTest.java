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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.util.GWTEditorNativeRegister;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.category.Others;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests that {@link ActivityBeansCache} respects the active flag controlled by the {@code @ActivatedBy} annotation.
 */
@RunWith(GwtMockitoTestRunner.class)
@SuppressWarnings("rawtypes")
public class ActivityBeansCacheActivatedByTest {

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
            return (Class<? extends ClientResourceType>[]) new Class<?>[]{DotResourceType.class};
        }
    };

    private ActivityBeansCache activityBeansCache;
    @Mock
    private SyncBeanManager iocManager;

    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEventEvent;

    @Mock
    private CategoriesManagerCache categoriesManagerCache;

    private ResourceTypeManagerCache resourceTypeManagerCache;

    @Mock
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    @Mock
    private GWTEditorNativeRegister gwtEditorNativeRegister;

    private ActiveSplashScreenActivity activeSplashScreenActivity;
    private SyncBeanDef activeSplashScreenActivityBean;
    private SyncBeanDef nonActiveSplashScreenActivityBean;
    private ActiveRegularActivity activeRegularActivity;
    private SyncBeanDef activeRegularActivityBean;
    private SyncBeanDef nonActiveRegularActivityBean;
    private ActiveResourceActivity activeResourceActivity;
    private SyncBeanDef activeResourceActivityBean;
    private SyncBeanDef nonActiveResourceActivityBean;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {

        this.resourceTypeManagerCache = new ResourceTypeManagerCache(categoriesManagerCache);

        newPerspectiveEventEvent = new EventSourceMock<>();
        newWorkbenchScreenEventEvent = new EventSourceMock<>();
        activityBeansCache = new ActivityBeansCache(iocManager,
                                                    newPerspectiveEventEvent,
                                                    newWorkbenchScreenEventEvent,
                                                    resourceTypeManagerCache,
                                                    experimentalActivitiesAuthorizationManager,
                                                    gwtEditorNativeRegister);

        activeSplashScreenActivity = mock(ActiveSplashScreenActivity.class);
        activeSplashScreenActivityBean = mockSplashScreenActivityBean(ActiveSplashScreenActivity.class,
                                                                      activeSplashScreenActivity);

        nonActiveSplashScreenActivityBean = mockSplashScreenActivityBean(NonActiveSplashScreenActivity.class,
                                                                         null);

        activeRegularActivity = mock(ActiveRegularActivity.class);
        activeRegularActivityBean = mockRegularBean(ActiveRegularActivity.class,
                                                    activeRegularActivity);

        nonActiveRegularActivityBean = mockRegularBean(NonActiveRegularActivity.class,
                                                       null);

        activeResourceActivity = mock(ActiveResourceActivity.class);
        activeResourceActivityBean = mockResourceActivityBean(ActiveResourceActivity.class,
                                                              activeResourceActivity);
        mockRegularBean(DotResourceType.class,
                        new DotResourceType(new Others()));

        nonActiveResourceActivityBean = mockResourceActivityBean(NonActiveResourceActivity.class,
                                                                 null);

        Collection<SyncBeanDef<SplashScreenActivity>> splashScreenBeans = new ArrayList<SyncBeanDef<SplashScreenActivity>>();
        splashScreenBeans.add(activeSplashScreenActivityBean);
        splashScreenBeans.add(nonActiveSplashScreenActivityBean);

        // all activity beans, including splash screens
        Collection<SyncBeanDef<Activity>> allActivityBeans = new ArrayList<SyncBeanDef<Activity>>();
        allActivityBeans.add(activeSplashScreenActivityBean);
        allActivityBeans.add(nonActiveSplashScreenActivityBean);
        allActivityBeans.add(activeRegularActivityBean);
        allActivityBeans.add(nonActiveRegularActivityBean);
        allActivityBeans.add(activeResourceActivityBean);
        allActivityBeans.add(nonActiveResourceActivityBean);

        when(iocManager.lookupBeans(SplashScreenActivity.class)).thenReturn(splashScreenBeans);
        when(iocManager.lookupBeans(Activity.class)).thenReturn(allActivityBeans);
    }

    @Test
    public void shouldNotReturnInactiveBeansFromGetSplashScreens() throws Exception {
        activityBeansCache.init();
        List<SplashScreenActivity> splashScreens = activityBeansCache.getSplashScreens();

        assertEquals(1,
                     splashScreens.size());
        assertSame(activeSplashScreenActivity,
                   splashScreens.iterator().next());
    }

    @Test
    public void cacheShouldNotReturnInactiveBeansFromGetResourceActivities() throws Exception {
        activityBeansCache.init();
        List<ActivityAndMetaInfo> activityBeans = this.resourceTypeManagerCache.getResourceActivities();

        assertEquals(1,
                     activityBeans.size());
        assertSame(activeResourceActivityBean,
                   activityBeans.get(0).getActivityBean());
    }

    @Test
    public void cacheShouldNotReturnInactiveBeansByName() throws Exception {
        activityBeansCache.init();

        assertSame(activeSplashScreenActivityBean,
                   activityBeansCache.getActivity("ActiveSplashScreenActivity"));
        assertSame(activeResourceActivityBean,
                   activityBeansCache.getActivity("ActiveResourceActivity"));
        assertSame(activeRegularActivityBean,
                   activityBeansCache.getActivity("ActiveRegularActivity"));
        assertNull(activityBeansCache.getActivity("NonActiveSplashScreenActivity"));
        assertNull(activityBeansCache.getActivity("NonActiveResourceActivity"));
        assertNull(activityBeansCache.getActivity("NonActiveRegularActivity"));
    }

    @SuppressWarnings("unchecked")
    private <T> SyncBeanDef mockRegularBean(Class<T> type,
                                            T instance) {
        SyncBeanDef<T> beanDef = mock(SyncBeanDef.class);
        when(iocManager.lookupBeans(type.getName())).thenReturn(Collections.singleton(beanDef));
        when(beanDef.getInstance()).thenReturn(instance);
        when(beanDef.getBeanClass()).thenReturn((Class) type);
        when(beanDef.isActivated()).thenReturn(instance != null);
        when(beanDef.getName()).thenReturn(type.getSimpleName());
        return beanDef;
    }

    private <T extends SplashScreenActivity> SyncBeanDef mockSplashScreenActivityBean(Class<T> type,
                                                                                      T instance) {
        SyncBeanDef beanDef = mockRegularBean(type,
                                              instance);
        when(beanDef.getQualifiers()).thenReturn(Collections.singleton(IS_SPLASH_SCREEN));
        return beanDef;
    }

    private <T extends Activity> SyncBeanDef mockResourceActivityBean(Class<T> type,
                                                                      T instance) {
        SyncBeanDef beanDef = mockRegularBean(type,
                                              instance);
        when(beanDef.getQualifiers()).thenReturn(Collections.singleton(ASSOCIATED_RESOURCES));
        return beanDef;
    }

    interface ActiveSplashScreenActivity extends SplashScreenActivity {

    }

    interface NonActiveSplashScreenActivity extends SplashScreenActivity {

    }

    interface ActiveRegularActivity extends Activity {

    }

    interface NonActiveRegularActivity extends Activity {

    }

    interface ActiveResourceActivity extends Activity {

    }

    interface NonActiveResourceActivity extends Activity {

    }
}
