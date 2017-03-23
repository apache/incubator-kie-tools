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

package org.kie.workbench.common.screens.library.client.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ResourceUtilsTest {

    @Mock
    private Classifier classifier;

    @Mock
    private ManagedInstance<NewResourceHandler> newResourceHandlers;

    private ResourceUtils resourceUtils;

    @Before
    public void setup() {
        resourceUtils = spy(new ResourceUtils(classifier,
                                              newResourceHandlers));

        ClientResourceType clientResourceType = mock(ClientResourceType.class);
        doReturn("java").when(clientResourceType).getSuffix();

        doReturn(clientResourceType).when(classifier).findResourceType(any(Path.class));
        doReturn(getNewResourceHandlers()).when(resourceUtils).getNewResourceHandlers();
    }

    @Test
    public void getBaseFileNameTest() {
        assertEquals("MyClass",
                     resourceUtils.getBaseFileName(getPath("MyClass.java")));
        assertEquals("MyClass.txt",
                     resourceUtils.getBaseFileName(getPath("MyClass.txt.java")));
    }

    private Path getPath(final String fileName) {
        final Path path = mock(Path.class);
        doReturn(fileName).when(path).getFileName();

        return path;
    }

    @Test
    public void getOrderedNewResourceHandlersTest() {
        final List<NewResourceHandler> orderedNewResourceHandlers = resourceUtils.getOrderedNewResourceHandlers();

        assertEquals("B",
                     orderedNewResourceHandlers.get(0).getDescription());
        assertEquals("A",
                     orderedNewResourceHandlers.get(1).getDescription());
        assertEquals("b",
                     orderedNewResourceHandlers.get(2).getDescription());
        assertEquals("c",
                     orderedNewResourceHandlers.get(3).getDescription());
        assertEquals("C",
                     orderedNewResourceHandlers.get(4).getDescription());
        assertEquals("a",
                     orderedNewResourceHandlers.get(5).getDescription());
    }

    @Test
    public void getAlphabeticallyOrderedNewResourceHandlersTest() {
        final List<NewResourceHandler> alphabeticallyOrderedNewResourceHandlers = resourceUtils.getAlphabeticallyOrderedNewResourceHandlers();

        assertEquals("a",
                     alphabeticallyOrderedNewResourceHandlers.get(0).getDescription());
        assertEquals("A",
                     alphabeticallyOrderedNewResourceHandlers.get(1).getDescription());
        assertEquals("b",
                     alphabeticallyOrderedNewResourceHandlers.get(2).getDescription());
        assertEquals("B",
                     alphabeticallyOrderedNewResourceHandlers.get(3).getDescription());
        assertEquals("c",
                     alphabeticallyOrderedNewResourceHandlers.get(4).getDescription());
        assertEquals("C",
                     alphabeticallyOrderedNewResourceHandlers.get(5).getDescription());
    }

    private List<NewResourceHandler> getNewResourceHandlers() {
        List<NewResourceHandler> newResourceHandlers = new ArrayList<>();

        newResourceHandlers.add(createNewResourceHandler("b",
                                                         0));
        newResourceHandlers.add(createNewResourceHandler("c",
                                                         0));
        newResourceHandlers.add(createNewResourceHandler("B",
                                                         -10));
        newResourceHandlers.add(createNewResourceHandler("a",
                                                         10));
        newResourceHandlers.add(createNewResourceHandler("A",
                                                         0));
        newResourceHandlers.add(createNewResourceHandler("C",
                                                         0));

        return newResourceHandlers;
    }

    private NewResourceHandler createNewResourceHandler(String description,
                                                        int order) {
        NewResourceHandler newResourceHandler = mock(NewResourceHandler.class);

        doReturn(description).when(newResourceHandler).getDescription();
        doReturn(order).when(newResourceHandler).order();

        return newResourceHandler;
    }
}
