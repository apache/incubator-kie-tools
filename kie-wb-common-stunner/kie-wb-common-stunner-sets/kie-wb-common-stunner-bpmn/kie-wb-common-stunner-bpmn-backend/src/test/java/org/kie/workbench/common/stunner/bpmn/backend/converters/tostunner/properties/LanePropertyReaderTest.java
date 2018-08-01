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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.eclipse.bpmn2.Lane;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TestDefinitionsWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.LanePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public class LanePropertyReaderTest {

    @Test
    public void JBPM_7523_shouldPreserveNameChars() {
        PropertyReaderFactory factory = new PropertyReaderFactory(
                new TestDefinitionsWriter().getDefinitionResolver());
        Lane lane = bpmn2.createLane();

        PropertyWriterFactory writerFactory = new PropertyWriterFactory();
        LanePropertyWriter w = writerFactory.of(lane);

        String aWeirdName = "   XXX  !!@@ <><> ";
        String aWeirdDoc = "   XXX  !!@@ <><> Docs ";
        w.setName(aWeirdName);
        w.setDocumentation(aWeirdDoc);

        LanePropertyReader r = factory.of(lane);
        assertThat(r.getName()).isEqualTo(asCData(aWeirdName));
        assertThat(r.getDocumentation()).isEqualTo(asCData(aWeirdDoc));
    }
}