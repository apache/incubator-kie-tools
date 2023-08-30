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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.HashSet;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.SubProcess;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class MultipleInstanceSubProcessPropertyWriterTest {

    private MultipleInstanceSubProcessPropertyWriter tested =
            new MultipleInstanceSubProcessPropertyWriter(bpmn2.createSubProcess(),
                                                         new FlatVariableScope(),
                                                         new HashSet<>());

    @Test
    public void nullInputOutputsShouldNotThrow() {
        SubProcess subProcess = bpmn2.createSubProcess();
        MultipleInstanceSubProcessPropertyWriter p =
                new MultipleInstanceSubProcessPropertyWriter(
                        subProcess, new FlatVariableScope(), new HashSet<>());

        assertThatCode(() -> {
            p.setInput(null);
            p.setOutput(null);
            p.createDataInput(null, null);
            p.createDataOutput(null, null);
            p.setCompletionCondition(null);
        }).doesNotThrowAnyException();
    }

    @Test
    public void completionConditionMustBeWrappedInCdata() {
        String expression = "x<1";
        String expected = "<![CDATA[" + expression + "]]>";
        SubProcess subProcess = bpmn2.createSubProcess();
        MultipleInstanceSubProcessPropertyWriter p =
                new MultipleInstanceSubProcessPropertyWriter(
                        subProcess, new FlatVariableScope(), new HashSet<>());
        p.setCompletionCondition(expression);
        MultiInstanceLoopCharacteristics loopCharacteristics =
                (MultiInstanceLoopCharacteristics) subProcess.getLoopCharacteristics();
        FormalExpression completionCondition =
                (FormalExpression) loopCharacteristics.getCompletionCondition();

        assertThat(expected).isEqualTo(FormalExpressionBodyHandler.of(completionCondition).getBody());
    }

    @Test
    public void testSetIsAsync() {
        tested.setAsync(Boolean.TRUE);
        assertTrue(CustomElement.async.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetSlaDueDate() {
        String slaDueDate = "12/25/1983";
        tested.setSlaDueDate(new SLADueDate(slaDueDate));

        assertTrue(CustomElement.slaDueDate.of(tested.getFlowElement()).get().contains(slaDueDate));
    }
}