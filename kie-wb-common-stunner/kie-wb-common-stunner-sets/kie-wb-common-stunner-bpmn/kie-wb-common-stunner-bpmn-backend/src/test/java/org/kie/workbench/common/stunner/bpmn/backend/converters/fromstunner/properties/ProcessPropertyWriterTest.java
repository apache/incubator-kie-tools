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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class ProcessPropertyWriterTest {

    ProcessPropertyWriter p = new ProcessPropertyWriter(
            bpmn2.createProcess(), new FlatVariableScope());

    @Test
    public void addChildShape() {
        BPMNShape bpmnShape = di.createBPMNShape();
        bpmnShape.setId("a");
        p.addChildShape(bpmnShape);
        assertThatThrownBy(() -> p.addChildShape(bpmnShape))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot add the same shape twice");
    }

    @Test
    public void addChildEdge() {
        BPMNEdge bpmnEdge = di.createBPMNEdge();
        bpmnEdge.setId("a");
        p.addChildEdge(bpmnEdge);
        assertThatThrownBy(() -> p.addChildEdge(bpmnEdge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot add the same edge twice");
    }
}