/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.exporter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.screen.JSNativeScreen;
import org.uberfire.client.screen.JSWorkbenchScreenActivity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JSExporterUtilsTest {

    private SyncBeanManager beanManager;

    private JSWorkbenchScreenActivity screenActivity;

    private JSNativeScreen screen;

    @Before
    public void setup() {
        beanManager = mock(SyncBeanManager.class);
        screen = mock(JSNativeScreen.class);
        screenActivity = new JSWorkbenchScreenActivity(screen,
                                                       mock(PlaceManager.class));
        when(screen.getId()).thenReturn("id");
    }

    @Test
    public void testUpdateExistentActivity() {
        List<SyncBeanDef> activities = new ArrayList<SyncBeanDef>();
        activities.add(createActivityBeanDef(screenActivity));
        when(beanManager.lookupBeans(any(String.class))).thenReturn(activities);

        JSWorkbenchScreenActivity activity = JSExporterUtils.findActivityIfExists(beanManager,
                                                                                  screen.getId(),
                                                                                  JSWorkbenchScreenActivity.class);

        assertNotNull(activity);
    }

    @Test
    public void testTryUpdatingUnexistentActivity() {
        List<SyncBeanDef> activities = new ArrayList<SyncBeanDef>();
        when(beanManager.lookupBeans(any(String.class))).thenReturn(activities);

        JSWorkbenchScreenActivity activity = JSExporterUtils.findActivityIfExists(beanManager,
                                                                                  screen.getId(),
                                                                                  JSWorkbenchScreenActivity.class);

        assertNull(activity);
    }

    private SyncBeanDef createActivityBeanDef(Activity activity) {
        return new SyncBeanDef() {
            @Override
            public Object getInstance() {
                return activity;
            }

            @Override
            public Object newInstance() {
                return null;
            }

            @Override
            public boolean isAssignableTo(final Class aClass) {
                return false;
            }

            @Override
            public Class getType() {
                return null;
            }

            @Override
            public Class<?> getBeanClass() {
                return null;
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return null;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                return null;
            }

            @Override
            public boolean matches(final Set set) {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public boolean isActivated() {
                return false;
            }
        };
    }
}
