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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.util.GWTEditorNativeRegister;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.workbench.category.Category;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivityBeansCacheTest {

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;

    @Mock
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEvent;

    @Mock
    private CategoriesManagerCache categoriesManagerCache;

    private ResourceTypeManagerCache resourceTypeManagerCache;

    @Mock
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    @Mock
    private GWTEditorNativeRegister gwtEditorNativeRegister;

    @InjectMocks
    ActivityBeansCache cache;

    @Before
    public void setUp() {
        resourceTypeManagerCache = new ResourceTypeManagerCache(categoriesManagerCache);

        cache = spy(new ActivityBeansCache(iocManager,
                                       newPerspectiveEventEvent,
                                       newWorkbenchScreenEvent,
                                       resourceTypeManagerCache,
                                       experimentalActivitiesAuthorizationManager,
                                       gwtEditorNativeRegister));

        doNothing().when(cache).registerGwtEditorProvider();
    }

    @Test
    public void initShouldCacheSplashScreen() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();

        cache.init();

        assertEquals(cache.getMockDef(),
                     cache.getActivity(cache.getIdMock()));
        assertTrue(cache.getSplashScreens().contains(cache.getActivity()));
    }

    @Test
    public void initShouldCacheClientEditors() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = spy(new ActivityBeansCacheUnitTestWrapper());
        cache.mockClientEditorBehaviour();

        cache.init();

        verify(cache).registerGwtClientBean(eq("mockDef1"), any());

        assertEquals(cache.getMockDef(),
                     cache.getActivity(cache.getIdMock()));
        assertTrue(cache.getActivitiesById().contains(cache.getActivity().getIdentifier()));
    }

    @Test
    public void initShouldCacheActivityById() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();

        cache.init();

        assertEquals(cache.getMockDef(),
                     cache.getActivity(cache.getIdMock()));
    }

    @Test(expected = RuntimeException.class)
    public void initShouldNotAllowTwoIdenticalsActivities() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();
        cache.duplicateActivity();

        cache.init();
    }

    @Test
    public void initShouldCacheOtherActivities() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockActivityBehaviour();
        cache.init();

        assertFalse(cache.getResourceTypeManagerCache().getResourceActivities().isEmpty());
    }

    @Test
    public void initShouldOrderActivityByPriority() throws Exception {

        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();

        int priorityActivityOne = Integer.MIN_VALUE;
        int priorityActivityTwo = Integer.MAX_VALUE;

        cache.createActivitiesAndMetaInfo(priorityActivityOne,
                                          priorityActivityTwo);

        ActivityAndMetaInfo firstActivityOnList = cache.getResourceTypeManagerCache().getResourceActivities().get(0);
        ActivityAndMetaInfo secondActivityOnList = cache.getResourceTypeManagerCache().getResourceActivities().get(1);

        assertEquals(priorityActivityOne,
                     firstActivityOnList.getPriority());
        assertEquals(priorityActivityTwo,
                     secondActivityOnList.getPriority());

        cache.getResourceTypeManagerCache().sortResourceActivitiesByPriority();

        firstActivityOnList = cache.getResourceTypeManagerCache().getResourceActivities().get(0);
        secondActivityOnList = cache.getResourceTypeManagerCache().getResourceActivities().get(1);

        assertEquals(priorityActivityTwo,
                     firstActivityOnList.getPriority());
        assertEquals(priorityActivityOne,
                     secondActivityOnList.getPriority());
    }

    @Test
    public void activityAndMetaInfoShouldLookupResourceTypesOnRuntime() {
        ClientResourceType clientResourceType = mock(ClientResourceType.class);
        SyncBeanDef<ClientResourceType> syncBeanDef = mock(SyncBeanDef.class);
        when(syncBeanDef.getInstance()).thenReturn(clientResourceType);
        Collection<SyncBeanDef> resourceTypeBeans = Arrays.asList(syncBeanDef);
        when(iocManager.lookupBeans("resource1")).thenReturn(resourceTypeBeans);

        ActivityAndMetaInfo activatedActivityAndMetaInfo =
                new ActivityAndMetaInfo(iocManager,
                                        mock(SyncBeanDef.class),
                                        0,
                                        Arrays.asList("resource1"));
        assertNull(activatedActivityAndMetaInfo.resourceTypes);
        assertTrue(!activatedActivityAndMetaInfo.resourceTypesNames.isEmpty());

        activatedActivityAndMetaInfo.getResourceTypes();

        assertTrue(activatedActivityAndMetaInfo.resourceTypes.length > 0);
    }

    @Test(expected = RuntimeException.class)
    public void dynamicLookupOfResourceTypeShouldFailWhenThereIsNoResource() {
        Collection<SyncBeanDef> resourceTypeBeans = new ArrayList<>();
        when(iocManager.lookupBeans("resource1")).thenReturn(resourceTypeBeans);

        ActivityAndMetaInfo activatedActivityAndMetaInfo =
                new ActivityAndMetaInfo(iocManager,
                                        mock(SyncBeanDef.class),
                                        0,
                                        Arrays.asList("resource1"));

        activatedActivityAndMetaInfo.getResourceTypes();
    }

    @Test
    public void addEditorActivityShouldSortResourcesByPriority() {
        String higherPriority = "20000";
        String lowerPriority = "1";

        Collection<SyncBeanDef> resourceTypeBeans = createResourceType("MODEL");
        when(iocManager.lookupBeans(eq("resource"))).thenReturn(resourceTypeBeans);

        Collection<SyncBeanDef> resourceTypeBeans1 = createResourceType("MODEL");
        when(iocManager.lookupBeans(eq("resource1"))).thenReturn(resourceTypeBeans1);

        Collection<SyncBeanDef> resourceTypeBeans2 = createResourceType("MODEL");
        when(iocManager.lookupBeans(eq("resource2"))).thenReturn(resourceTypeBeans2);

        SyncBeanDef mock = mock(SyncBeanDef.class);
        when(mock.getName()).thenReturn("resource1");
        cache.addNewEditorActivity(mock,
                                   lowerPriority,
                                   "resource");
        SyncBeanDef mock1 = mock(SyncBeanDef.class);
        when(mock1.getName()).thenReturn("resource2");
        cache.addNewEditorActivity(mock1,
                                   higherPriority,
                                   "resource1");
        List<ActivityAndMetaInfo> resourceActivities = this.resourceTypeManagerCache.getResourceActivities();

        assertEquals(resourceActivities.get(0).getPriority(),
                     Integer.valueOf(higherPriority).intValue());
    }

    private Collection<SyncBeanDef> createResourceType(String type) {
        Category model = mock(Category.class);
        when(model.getName()).thenReturn(type);
        ClientResourceType clientResourceType = mock(ClientResourceType.class);
        when(clientResourceType.getCategory()).thenReturn(model);
        SyncBeanDef<ClientResourceType> syncBeanDef = mock(SyncBeanDef.class);
        when(syncBeanDef.getInstance()).thenReturn(clientResourceType);
        return Arrays.asList(syncBeanDef);
    }

    @Test
    public void addEditorActivityShouldAddToActivitiesByID() {
        String resource = "resource";

        Collection<SyncBeanDef> resourceTypeBeans = createResourceType("MODEL");
        when(iocManager.lookupBeans(eq(resource))).thenReturn(resourceTypeBeans);

        SyncBeanDef mock = mock(SyncBeanDef.class);
        when(mock.getName()).thenReturn(resource);
        cache.addNewEditorActivity(mock,
                                   "1",
                                   resource);

        assertTrue(cache.hasActivity(resource));
    }

    @Test
    public void getPerspectiveActivities() {
        SyncBeanDef mock1 = mock(SyncBeanDef.class);
        when(mock1.getName()).thenReturn("perspective2");
        when(mock1.isAssignableTo(PerspectiveActivity.class)).thenReturn(true);

        SyncBeanDef mock2 = mock(SyncBeanDef.class);
        when(mock2.getName()).thenReturn("screen");
        when(mock2.isAssignableTo(PerspectiveActivity.class)).thenReturn(false);

        cache.addNewPerspectiveActivity(mock1);
        cache.addNewPerspectiveActivity(mock2);

        List<SyncBeanDef<Activity>> perspectiveActivities = cache.getPerspectiveActivities();
        assertEquals(perspectiveActivities.size(),
                     1);
    }

    @Test
    public void getActivitiesNull() {
        assertNull(cache.getActivity((String) null));
    }

}
