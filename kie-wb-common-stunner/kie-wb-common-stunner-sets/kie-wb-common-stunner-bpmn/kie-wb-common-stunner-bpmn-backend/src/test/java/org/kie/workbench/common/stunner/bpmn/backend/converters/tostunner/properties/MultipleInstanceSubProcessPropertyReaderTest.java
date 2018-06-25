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

import java.util.Collections;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class MultipleInstanceSubProcessPropertyReaderTest {

    @Test
    public void defaultReturnValues() {
        MultipleInstanceSubProcessPropertyReader p =
                new MultipleInstanceSubProcessPropertyReader(bpmn2.createSubProcess(), null, new DummyDefinitionResolver());
        assertNull(p.getCollectionInput());
        assertNull(p.getCollectionOutput());
        assertEquals("", p.getDataInput());
        assertEquals("", p.getDataOutput());
        assertEquals("", p.getCompletionCondition());
    }

    // internal mock
    static class DummyDefinitionResolver extends DefinitionResolver {

        DummyDefinitionResolver() {
            super(makeDefinitions(), Collections.emptyList());
        }

        public BPMNShape getShape(String elementId) {
            return null;
        }

        static Definitions makeDefinitions() {
            Definitions d = bpmn2.createDefinitions();
            BPMNPlane bpmnPlane = di.createBPMNPlane();
            BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
            bpmnDiagram.setPlane(bpmnPlane);
            d.getDiagrams().add(bpmnDiagram);
            d.getRootElements().add(bpmn2.createProcess());
            return d;
        }
    }
}