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

package org.kie.workbench.common.stunner.bpmn.backend.workitem.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionRemoteService.WorkItemsHolder;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionRemoteServiceTest {

    private static final String URL = "http://url";
    private static final String WD1_NAME = "wd1Name";
    private static final String WD2_NAME = "wd2Name";

    @Mock
    private Function<String, WorkItemsHolder> lookupService;

    private WorkItemDefinitionRemoteService tested;
    private WorkDefinitionImpl wd1;
    private WorkDefinitionImpl wd2;

    @Before
    public void init() {
        wd1 = new WorkDefinitionImpl();
        wd1.setName(WD1_NAME);
        wd2 = new WorkDefinitionImpl();
        wd2.setName(WD2_NAME);
        when(lookupService.apply(eq(URL)))
                .thenReturn(new WorkItemsHolder(new HashMap<String, WorkDefinitionImpl>(2) {{
                    put(WD1_NAME, wd1);
                    put(WD2_NAME, wd2);
                }}));
        tested = new WorkItemDefinitionRemoteService(lookupService);
    }

    @Test
    public void testExecute() {
        Collection<WorkItemDefinition> result =
                tested.execute(WorkItemDefinitionRemoteRequest.build(URL,
                                                                     new String[]{WD1_NAME, WD2_NAME}));

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(w -> WD1_NAME.equals(w.getName())));
        assertTrue(result.stream().anyMatch(w -> WD2_NAME.equals(w.getName())));
    }

    @Test
    public void testExecuteFiltered1() {
        Collection<WorkItemDefinition> result =
                tested.execute(WorkItemDefinitionRemoteRequest.build(URL,
                                                                     new String[]{WD1_NAME}));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(w -> WD1_NAME.equals(w.getName())));
    }

    @Test
    public void testExecuteFiltered2() {
        Collection<WorkItemDefinition> result =
                tested.execute(WorkItemDefinitionRemoteRequest.build(URL,
                                                                     new String[]{WD2_NAME}));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(w -> WD2_NAME.equals(w.getName())));
    }
}
