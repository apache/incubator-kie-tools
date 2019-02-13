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

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class AssignmentsInfosTest {

    @Test
    public void JBPM_7447_shouldNotFilterOutDataOutputsWithEmptyType() {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setName("InputName");
        dataInput.setId("InputID");
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setName("OutputName");
        dataOutput.setId("OutputID");
        ParsedAssignmentsInfo result = AssignmentsInfos.parsed(
                Collections.singletonList(dataInput),
                Collections.emptyList(),
                Collections.singletonList(dataOutput),
                Collections.emptyList(),
                false
        );
        assertThat(result.getOutputs().getDeclarations()).isNotEmpty();
    }
}