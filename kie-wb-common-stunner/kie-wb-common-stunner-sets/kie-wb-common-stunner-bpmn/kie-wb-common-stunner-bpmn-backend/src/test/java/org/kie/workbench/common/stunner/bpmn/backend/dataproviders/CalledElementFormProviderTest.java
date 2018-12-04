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

package org.kie.workbench.common.stunner.bpmn.backend.dataproviders;

import org.junit.Test;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.stunner.bpmn.backend.query.FindBpmnProcessIdsQuery;

import static org.junit.Assert.assertEquals;

public class CalledElementFormProviderTest {

    private CalledElementFormProvider tested = new CalledElementFormProvider();

    @Test
    public void testGetProcessIdResourceType() throws Exception {
        assertEquals(tested.getProcessIdResourceType(), ResourceType.BPMN2);
    }

    @Test
    public void testGetQueryName() throws Exception {
        assertEquals(tested.getQueryName(), FindBpmnProcessIdsQuery.NAME);
    }
}