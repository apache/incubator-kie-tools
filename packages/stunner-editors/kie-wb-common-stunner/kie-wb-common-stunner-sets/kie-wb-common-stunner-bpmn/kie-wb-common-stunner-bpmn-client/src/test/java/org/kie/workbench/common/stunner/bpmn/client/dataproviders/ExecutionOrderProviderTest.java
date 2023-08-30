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
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ExecutionOrder;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExecutionOrderProviderTest {

    private static final String PARALLEL_LABEL = "PARALLEL";

    private static final String SEQUENTIAL_LABEL = "SEQUENTIAL";

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private FormRenderingContext context;

    private ExecutionOrderProvider provider;

    @Before
    public void setUp() {
        final String PARALLEL_KEY = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ExecutionOrderProvider.parallel";
        final String SEQUENTIAL_KEY = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ExecutionOrderProvider.sequential";

        when(translationService.getValue(PARALLEL_KEY)).thenReturn(PARALLEL_LABEL);
        when(translationService.getValue(SEQUENTIAL_KEY)).thenReturn(SEQUENTIAL_LABEL);
        provider = new ExecutionOrderProvider(translationService);
        provider.init();
    }

    @Test
    public void testGetSelectorData() {
        SelectorData selectorData = provider.getSelectorData(context);
        assertNotNull(selectorData.getValues());
        assertEquals(2,
                     selectorData.getValues().size());
        assertEquals(ExecutionOrder.SEQUENTIAL.value(),
                     selectorData.getSelectedValue());
        assertEquals(SEQUENTIAL_LABEL,
                     selectorData.getValues().get(ExecutionOrder.SEQUENTIAL.value()));
        assertEquals(PARALLEL_LABEL,
                     selectorData.getValues().get(ExecutionOrder.PARALLEL.value()));
    }
}
