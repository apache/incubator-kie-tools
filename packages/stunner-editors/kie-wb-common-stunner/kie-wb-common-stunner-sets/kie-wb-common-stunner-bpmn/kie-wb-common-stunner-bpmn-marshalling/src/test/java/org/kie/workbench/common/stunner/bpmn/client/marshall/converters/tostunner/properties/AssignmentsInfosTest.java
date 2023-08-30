/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Collections;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.ParsedAssignmentsInfo;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

@RunWith(MockitoJUnitRunner.class)
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
        assertFalse(result.getOutputs().getDeclarations().isEmpty());
    }
}