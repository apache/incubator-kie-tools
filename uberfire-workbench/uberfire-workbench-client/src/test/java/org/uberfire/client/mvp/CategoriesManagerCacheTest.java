/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.uberfire.client.mvp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.util.GWTEditorNativeRegister;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Undefined;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoriesManagerCacheTest {

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;

    @Mock
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEvent;

    private CategoriesManagerCache categoriesManagerCache;

    @Mock
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    @Mock
    private GWTEditorNativeRegister gwtEditorNativeRegister;

    private ResourceTypeManagerCache resourceTypeManagerCache;

    private ActivityBeansCache activityBeansCache;

    private Undefined undefinedCategory;


    @Before
    public void setUp() {
        categoriesManagerCache = new CategoriesManagerCache(undefinedCategory);
        resourceTypeManagerCache = new ResourceTypeManagerCache(categoriesManagerCache);
        activityBeansCache = new ActivityBeansCache(iocManager,
                                                    newPerspectiveEventEvent,
                                                    newWorkbenchScreenEvent,
                                                    resourceTypeManagerCache,
                                                    experimentalActivitiesAuthorizationManager,
                                                    gwtEditorNativeRegister);
    }

    @Test
    public void testAvailableCategories() {

        Category process = mock(Category.class);
        Category model = mock(Category.class);
        when(process.getName()).thenReturn("PROCESS");
        when(model.getName()).thenReturn("MODEL");

        ClientResourceType clientResourceType = mock(ClientResourceType.class);
        when(clientResourceType.getCategory()).thenReturn(model);
        SyncBeanDef<ClientResourceType> syncBeanDef = mock(SyncBeanDef.class);
        when(syncBeanDef.getInstance()).thenReturn(clientResourceType);
        Collection<SyncBeanDef> resourceTypeBeans = Arrays.asList(syncBeanDef);
        when(iocManager.lookupBeans(eq("java"))).thenReturn(resourceTypeBeans);

        SyncBeanDef mock = mock(SyncBeanDef.class);
        when(mock.getName()).thenReturn("java");

        activityBeansCache.addNewEditorActivity(mock,
                                                "1",
                                                "java");

        Set<Category> categories = this.categoriesManagerCache.getCategories();

        assertTrue(categories.stream().anyMatch(category -> category.equals(model)));
        assertTrue(categories.stream().noneMatch(category -> category.equals(process)));
    }
}