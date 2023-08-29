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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UpdateElementPropertyValueCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "testUUID";
    private static final String DEF_ID = "defId";
    private static final String PROPERTY_ID = "pId";
    private static final String PROPERTY_VALUE = "testValue1";
    private static final String PROPERTY_OLD_VALUE = "testOldValue1";

    @Mock
    private Node candidate;
    private View content;
    @Mock
    private Object definition;
    private Object property = new PropertyStub(PROPERTY_ID);
    private UpdateElementPropertyValueCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        content = mockView(10,
                           10,
                           50,
                           50);
        when(candidate.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);

        when(definitionAdapter.getPropertyFields(eq(definition))).thenReturn(new String[]{PROPERTY_ID});
        when(definitionAdapter.getProperty(eq(definition), eq(PROPERTY_ID))).thenReturn(Optional.of(property));
        when(definitionAdapter.getId(eq(definition))).thenReturn(DefinitionId.build(DEF_ID));
        when(propertyAdapter.getId(eq(property))).thenReturn(PROPERTY_ID);
        when(propertyAdapter.getValue(eq(property))).thenReturn(PROPERTY_OLD_VALUE);
        when(graphIndex.getNode(eq(UUID))).thenReturn(candidate);
        when(graphIndex.get(eq(UUID))).thenReturn(candidate);
        this.tested = new UpdateElementPropertyValueCommand(UUID,
                                                            PROPERTY_ID,
                                                            PROPERTY_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test(expected = BadCommandArgumentsException.class)
    public void testAllowElementNotFound() {
        when(graphIndex.get(eq(UUID))).thenReturn(null);
        tested.allow(graphCommandExecutionContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<Bounds> bounds = ArgumentCaptor.forClass(Bounds.class);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        assertEquals(PROPERTY_OLD_VALUE,
                     tested.getOldValue());
        verify(propertyAdapter,
               times(1)).getValue(eq(property));
        verify(propertyAdapter,
               times(1)).setValue(eq(property),
                                  eq(PROPERTY_VALUE));
    }

    @Test(expected = BadCommandArgumentsException.class)
    public void testExecuteNodeNotFound() {
        when(graphIndex.get(eq(UUID))).thenReturn(null);
        tested.execute(graphCommandExecutionContext);
    }

    private class PropertyStub {

        private final String uuid;

        private PropertyStub(String uuid) {
            this.uuid = uuid;
        }
    }
}
