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
 */

package org.kie.workbench.common.stunner.core.util;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.TestingSimpleDomainObject;
import org.kie.workbench.common.stunner.core.registry.impl.DefaultDefinitionsCacheRegistry;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionUtilsTest {

    private DefinitionUtils tested;
    private TestingGraphMockHandler graphMockHandler;
    private TestingSimpleDomainObject domainObject;

    @Before
    public void setUp() throws Exception {
        graphMockHandler = new TestingGraphMockHandler();
        domainObject = new TestingSimpleDomainObject(graphMockHandler);
        tested = new DefinitionUtils(graphMockHandler.getDefinitionManager(),
                                     new DefaultDefinitionsCacheRegistry(graphMockHandler.getFactoryManager(),
                                                                         graphMockHandler.getAdapterManager()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getNameFromField() {
        when(graphMockHandler.getPropertyAdapter().getValue(domainObject.getNameProperty())).thenReturn("nameValue");
        final String name = tested.getName(domainObject);
        assertEquals("nameValue", name);
    }

    @Test
    public void getNameIdentifier() {
        final String nameIdentifier = tested.getNameIdentifier(domainObject);
        assertEquals(TestingSimpleDomainObject.NAME_FIELD, nameIdentifier);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getNameIdentifierFromMetadata() {
        when(graphMockHandler.getDefinitionAdapter().getNameField(eq(domainObject))).thenReturn(Optional.empty());
        final String nameIdentifier = tested.getNameIdentifier(domainObject);
        assertEquals(TestingSimpleDomainObject.NAME, nameIdentifier);
    }
}