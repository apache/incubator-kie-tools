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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadata;

import static org.kie.workbench.common.stunner.bpmn.client.util.EqualsAndHashCodeTestUtils.TestCaseBuilder;
import static org.mockito.Mockito.mock;

public class VariableMetadataTest {

    @Test
    public void testEqualsAndHashCode() {
        TypeMetadata typeMetadata1 = mock(TypeMetadata.class);
        TypeMetadata typeMetadata2 = mock(TypeMetadata.class);

        TestCaseBuilder.newTestCase()
                .addTrueCase(new VariableMetadata(null, null, null), new VariableMetadata(null, null, null))
                .addTrueCase(new VariableMetadata("name1", null, null), new VariableMetadata("name1", null, null))
                .addTrueCase(new VariableMetadata("name1", "type1", null), new VariableMetadata("name1", "type1", null))
                .addTrueCase(new VariableMetadata("name1", "type1", typeMetadata1), new VariableMetadata("name1", "type1", typeMetadata1))
                .addFalseCase(new VariableMetadata("name1", null, null), new VariableMetadata(null, null, null))
                .addFalseCase(new VariableMetadata("name1", "type1", null), new VariableMetadata("name2", null, null))
                .addFalseCase(new VariableMetadata("name1", "type1", null), new VariableMetadata("name1", "type2", null))
                .addFalseCase(new VariableMetadata("name1", "type1", typeMetadata1), new VariableMetadata("name1", "type1", null))
                .addFalseCase(new VariableMetadata("name1", "type1", typeMetadata1), new VariableMetadata("name1", "type", typeMetadata2))
                .test();
    }
}
