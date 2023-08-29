/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.TestingSimpleDomainObject;
import org.kie.workbench.common.stunner.core.registry.impl.DefaultDefinitionsCacheRegistry;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
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
        assertEquals(TestingSimpleDomainObject.NAME, nameIdentifier);
    }
}