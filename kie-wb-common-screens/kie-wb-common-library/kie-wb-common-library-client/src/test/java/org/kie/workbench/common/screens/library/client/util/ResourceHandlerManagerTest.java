/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.screens.projectimportsscreen.type.ProjectImportsResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Model;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.uberfire.workbench.category.Others;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ResourceHandlerManagerTest {

    private ResourceHandlerManager resourceHandlerManager;

    @Before
    public void setUp() {
        resourceHandlerManager = spy(new ResourceHandlerManager());
    }

    @Test
    public void testResourceHandlers() {

        NewResourceHandler rh1 = mock(NewResourceHandler.class);
        NewResourceHandler rh2 = mock(NewResourceHandler.class);

        when(rh1.getResourceType()).thenReturn(new JavaResourceTypeDefinition(new Model()));
        when(rh2.getResourceType()).thenReturn(new ProjectImportsResourceTypeDefinition(new Others()));

        doReturn(Arrays.asList(rh1,
                               rh2)).when(this.resourceHandlerManager).getNewResourceHandlers();

        List<NewResourceHandler> handlers = this.resourceHandlerManager.getResourceHandlers(resourceHandler -> !resourceHandler.getResourceType().getCategory().equals(new Model()));

        assertTrue(handlers.size() == 1);
        assertEquals(rh1,
                     handlers.get(0));
    }
}