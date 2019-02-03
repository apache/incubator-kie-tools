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

package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.AdHocSubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CallActivityPropertyWriter;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class CaseManagementPropertyWriterFactoryTest {

    private CaseManagementPropertyWriterFactory tested = new CaseManagementPropertyWriterFactory();

    @Test
    public void testOf_CallActivity() throws Exception {
        final CallActivityPropertyWriter propertyWriter = tested.of(bpmn2.createCallActivity());

        assertTrue(CaseManagementCallActivityPropertyWriter.class.isInstance(propertyWriter));
    }

    @Test
    public void testOf_AdHocSubProcess() throws Exception {
        final AdHocSubProcessPropertyWriter propertyWriter = tested.of(bpmn2.createAdHocSubProcess());

        assertTrue(CaseManagementAdHocSubProcessPropertyWriter.class.isInstance(propertyWriter));
    }
}