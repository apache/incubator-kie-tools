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

import org.eclipse.bpmn2.SubProcess;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class SubProcessPropertyWriterTest {

    SubProcessPropertyWriter p ;
    FlatVariableScope variableScope;

    @Before
    public void before() {
        this.variableScope = new FlatVariableScope();
        this.p = new SubProcessPropertyWriter(
                bpmn2.createSubProcess(), variableScope);
    }

    @Test
    public void addChildElement() {
        SubProcess process = (SubProcess) p.getElement();

        BoundaryEventPropertyWriter boundaryEventPropertyWriter =
                new BoundaryEventPropertyWriter(bpmn2.createBoundaryEvent(), variableScope);

        UserTaskPropertyWriter userTaskPropertyWriter =
                new UserTaskPropertyWriter(bpmn2.createUserTask(), variableScope);

        p.addChildElement(boundaryEventPropertyWriter);
        p.addChildElement(userTaskPropertyWriter);

        // boundary event should always occur after other nodes (compat with old marshallers)
        assertThat(process.getFlowElements().get(0)).isEqualTo(userTaskPropertyWriter.getFlowElement());
        assertThat(process.getFlowElements().get(1)).isEqualTo(boundaryEventPropertyWriter.getFlowElement());
    }

}