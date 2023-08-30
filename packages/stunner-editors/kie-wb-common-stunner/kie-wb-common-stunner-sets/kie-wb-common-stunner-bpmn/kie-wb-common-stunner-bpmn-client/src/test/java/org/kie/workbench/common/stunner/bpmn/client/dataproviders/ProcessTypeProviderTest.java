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


package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ProcessTypeProviderTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private FormRenderingContext context;

    private ProcessTypeProvider tested;

    @Before
    public void setup() {
        tested = new ProcessTypeProvider(sessionManager);
    }

    @Test
    public void testGetProviderName() {
        assertEquals(tested.getClass().getSimpleName(), tested.getProviderName());
    }

    @Test
    public void testGetValues() {
        SelectorData selectorData = tested.getSelectorData(context);
        assertEquals(2, selectorData.getValues().size());
        assertEquals("Public", selectorData.getSelectedValue());
        assertTrue(selectorData.getValues().containsValue("Public"));
        assertTrue(selectorData.getValues().containsValue("Private"));
    }

    @Test
    public void testGetFilter() {
        assertTrue(tested.getFilter().test(new NodeImpl("uuid_1")));
    }

    @Test
    public void testGetMapper() {
        assertNull(tested.getMapper());
    }
}
