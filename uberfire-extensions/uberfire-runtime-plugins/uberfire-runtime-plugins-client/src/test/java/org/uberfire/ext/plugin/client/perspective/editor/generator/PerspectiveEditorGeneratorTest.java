/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PerspectiveEditorGeneratorTest {

    @Mock
    LayoutGenerator layoutGenerator;

    @Mock
    ActivityBeansCache activityBeansCache;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PerspectiveServices perspectiveServices;

    @Mock
    SyncBeanDef activityBeanDef;

    PerspectiveEditorGenerator generator;

    @Before
    public void setUp() {
        when(beanManager.lookupBeans(anyString())).thenReturn(Collections.singleton(activityBeanDef));
        when(beanManager.lookupBeans(SyncBeanDef.class)).thenReturn(Collections.singleton(activityBeanDef));
        generator = new PerspectiveEditorGenerator(beanManager,
                activityBeansCache,
                layoutGenerator,
                new CallerMock<>(perspectiveServices));
    }

    @Test
    public void testGeneratedActivitiesIdentifier() {
        LayoutTemplate layoutTemplate = new LayoutTemplate("test");
        PerspectiveEditorActivity perspectiveActivity = generator.generatePerspective(layoutTemplate);
        PerspectiveEditorScreenActivity screenActivity = perspectiveActivity.getScreen();

        assertNotNull(perspectiveActivity);
        assertNotNull(screenActivity);
        assertEquals(perspectiveActivity.getIdentifier(), "test");
        assertEquals(screenActivity.getIdentifier(), "test [Screen]");

    }
}
