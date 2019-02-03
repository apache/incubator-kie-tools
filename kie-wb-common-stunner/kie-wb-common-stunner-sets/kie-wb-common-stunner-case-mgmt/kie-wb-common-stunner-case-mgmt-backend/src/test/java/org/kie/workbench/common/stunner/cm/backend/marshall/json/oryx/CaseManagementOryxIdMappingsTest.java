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
 */

package org.kie.workbench.common.stunner.cm.backend.marshall.json.oryx;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxIdMappings;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CaseManagementOryxIdMappingsTest {

    @Mock
    private DefinitionManager definitionManager;

    private OryxIdMappings oryxIdMappings;

    @Before
    public void setup() {
        this.oryxIdMappings = new CaseManagementOryxIdMappings(definitionManager);
        this.oryxIdMappings.init(Collections.emptyList());
    }

    @Test
    public void checkSkippedProperties() {
        final Map<Class<?>, Set<String>> skippedProperties = oryxIdMappings.getSkippedProperties();
        final Set<String> cmSkippedProperties = skippedProperties.get(CaseManagementDiagram.class);
        assertNotNull(cmSkippedProperties);
    }

    @Test
    public void checkGetDefinitionMappingsForDiagram() {
        assertDefinitionMappings(CaseManagementDiagram.class);
    }

    @Test
    public void checkGetDefinitionMappings() {
        assertDefinitionMappings(CaseManagementDiagram.class);
        assertDefinitionMappings(CaseReusableSubprocess.class);
        assertDefinitionMappings(ProcessReusableSubprocess.class);
        assertDefinitionMappings(AdHocSubprocess.class);
        assertDefinitionMappings(UserTask.class);
    }

    private void assertDefinitionMappings(final Class cmClass) {
        final Map<Class<?>, Map<Class<?>, String>> definitionMappings = oryxIdMappings.getDefinitionMappings();
        final Map<Class<?>, String> cmDefinitionMappings = definitionMappings.get(cmClass);
        assertNotNull(cmDefinitionMappings);
    }

    @Test
    public void testGetDefinition() throws Exception {
        assertGetDefinistion("AdHocSubprocess", AdHocSubprocess.class);
        assertGetDefinistion("CaseReusableSubprocess", CaseReusableSubprocess.class);
        assertGetDefinistion("ProcessReusableSubprocess", ProcessReusableSubprocess.class);
        assertGetDefinistion("Task", UserTask.class);
    }

    private void assertGetDefinistion(final String oryxId, final Class<?> expectedResult) {
        final Class<?> result = oryxIdMappings.getDefinition(oryxId);
        assertEquals(result, expectedResult);
    }

    @Test
    public void testGetOryxDefinitionId() throws Exception {
        assertGetOryxDefinitionId(new AdHocSubprocess(), "AdHocSubprocess");
        assertGetOryxDefinitionId(new CaseReusableSubprocess(), "ReusableSubprocess");
        assertGetOryxDefinitionId(new ProcessReusableSubprocess(), "ReusableSubprocess");
        assertGetOryxDefinitionId(new UserTask(), "Task");
    }

    private void assertGetOryxDefinitionId(final Object def, final String expectedId) {
        final String result = oryxIdMappings.getOryxDefinitionId(def);
        assertEquals(result, expectedId);
    }
}
