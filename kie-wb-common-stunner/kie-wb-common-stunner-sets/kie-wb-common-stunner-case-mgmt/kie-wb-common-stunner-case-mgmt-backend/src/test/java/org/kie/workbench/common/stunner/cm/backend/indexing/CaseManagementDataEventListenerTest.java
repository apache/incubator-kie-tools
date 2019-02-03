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

package org.kie.workbench.common.stunner.cm.backend.indexing;

import java.util.Collection;
import java.util.UUID;

import org.jbpm.process.core.Process;
import org.jbpm.process.core.impl.ProcessImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaseManagementDataEventListenerTest {

    private CaseManagementDataEventListener tested = new CaseManagementDataEventListener();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetProcessIdResourceType() throws Exception {
        assertEquals(tested.getProcessIdResourceType(), ResourceType.BPMN_CM);
    }

    @Test
    public void testGetProcessDescriptorName() throws Exception {
        assertEquals(tested.getProcessNameResourceType(), ResourceType.BPMN_CM_NAME);
    }

    @Test
    public void testOnProcessAdded() throws Exception {
        final String processId = UUID.randomUUID().toString();
        final String processName = UUID.randomUUID().toString();

        final Process process = new ProcessImpl();
        process.setId(processId);
        process.setName(processName);

        tested.onProcessAdded(process);

        assertEquals(tested, process.getMetaData().get(tested.getProcessDescriptorName()));
        final Collection<Resource> resources = tested.getResources();
        assertTrue(resources.stream().allMatch(
                r -> (processId.equals(r.getResourceFQN()) && tested.getProcessIdResourceType().equals(r.getResourceType()))
                        || (processName.equals(r.getResourceFQN()) && tested.getProcessNameResourceType().equals(r.getResourceType()))));
    }
}