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

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MorphNodeCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "uuid";

    private static final String CURRENT_DEFINITION = "current-definition";

    private static final String CURRENT_DEFINITION_ID = "current-definition-id";

    private static final String NEW_DEFINITION = "new-definition";

    private static final String NEW_DEFINITION_ID = "new-definition-id";

    private static final String NEW_DEFINITION_LABEL = "new-definition-label";

    @Mock
    private Node<Definition, Edge> candidate;

    @Mock
    private Definition content;

    @Mock
    private MorphDefinition morphDefinition;

    @Mock
    private MorphAdapter morphAdaptor;

    private MorphNodeCommand tested;
    private Set<String> labels = new HashSet<>();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        when(candidate.getUUID()).thenReturn(UUID);
        when(candidate.getLabels()).thenReturn(labels);
        when(candidate.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(CURRENT_DEFINITION);
        when(adapterRegistry.getMorphAdapter(any(Class.class))).thenReturn(morphAdaptor);
        when(graphIndex.get(eq(UUID))).thenReturn(candidate);
        when(graphIndex.getNode(eq(UUID))).thenReturn(candidate);
        when(definitionAdapter.getId(CURRENT_DEFINITION)).thenReturn(DefinitionId.build(CURRENT_DEFINITION_ID));
        when(definitionAdapter.getLabels(CURRENT_DEFINITION)).thenReturn(new String[0]);
        when(morphAdaptor.morph(CURRENT_DEFINITION, morphDefinition, CURRENT_DEFINITION_ID)).thenReturn(NEW_DEFINITION);
        when(definitionAdapter.getId(NEW_DEFINITION)).thenReturn(DefinitionId.build(NEW_DEFINITION_ID));
        when(definitionAdapter.getLabels(NEW_DEFINITION)).thenReturn(new String[]{NEW_DEFINITION_LABEL});
        when(morphAdaptor.morph(NEW_DEFINITION, morphDefinition, CURRENT_DEFINITION_ID)).thenReturn(CURRENT_DEFINITION);
        tested = new MorphNodeCommand(candidate, morphDefinition, CURRENT_DEFINITION_ID);
    }

    @Test
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());

        verify(content).setDefinition(eq(NEW_DEFINITION));
        assertEquals(2, labels.size());
        assertTrue(labels.contains(NEW_DEFINITION_ID));
        assertTrue(labels.contains(NEW_DEFINITION_LABEL));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo() {
        //Execute command and mock new state
        tested.execute(graphCommandExecutionContext);
        reset(content);
        when(content.getDefinition()).thenReturn(NEW_DEFINITION);
        CommandResult<RuleViolation> result = tested.undo(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());

        verify(content).setDefinition(eq(CURRENT_DEFINITION));
        assertEquals(1, labels.size());
        assertTrue(labels.contains(CURRENT_DEFINITION_ID));
    }
}
