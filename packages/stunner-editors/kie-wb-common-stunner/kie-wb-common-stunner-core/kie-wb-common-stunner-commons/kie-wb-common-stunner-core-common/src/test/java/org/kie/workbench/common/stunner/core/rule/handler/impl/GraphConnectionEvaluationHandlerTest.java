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


package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.StatefulGraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GraphConnectionEvaluationHandlerTest extends AbstractGraphRuleHandlerTest {

    private final static String EDGE_ID = "eId";
    private final static String[] P1 = new String[]{"theParent", CANDIDATE_ROLE1};
    private final static String[] P2 = new String[]{"theParent", CANDIDATE_ROLE2};
    private final static List<CanConnect.PermittedConnection> PCS =
            new ArrayList<CanConnect.PermittedConnection>(2) {{
                add(new CanConnect.PermittedConnection(P1[0],
                                                       P1[1]));
                add(new CanConnect.PermittedConnection(P2[0],
                                                       P2[1]));
            }};
    private final static CanConnect RULE = new CanConnect("r1",
                                                          EDGE_ID,
                                                          PCS);

    @Mock
    GraphConnectionContext context;

    @Mock
    Edge edge;

    @Mock
    Definition edgeContent;

    @Mock
    Object edgeDefinition;

    private GraphConnectionEvaluationHandler tested;
    private static final ConnectionEvaluationHandler HANDLER = new ConnectionEvaluationHandler();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        final Set<String> edgeLabels = Collections.singleton(EDGE_ID);
        when(edge.getContent()).thenReturn(edgeContent);
        when(edge.getLabels()).thenReturn(edgeLabels);
        when(edgeContent.getDefinition()).thenReturn(edgeDefinition);
        when(definitionAdapter.getId(eq(edgeDefinition))).thenReturn(DefinitionId.build(EDGE_ID));
        when(context.getConnector()).thenReturn(edge);
        when(context.getSource()).thenReturn(Optional.of(parent));
        when(context.getTarget()).thenReturn(Optional.of(candidate));
        StatefulGraphEvaluationState state = new StatefulGraphEvaluationState(GraphImpl.build("graphUUID"));
        when(context.getState()).thenReturn(state);
        tested = new GraphConnectionEvaluationHandler(definitionManager,
                                                      HANDLER);
    }

    @Test
    public void testEvaluateSuccess1() {
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateFailed1() {
        when(context.getSource()).thenReturn(Optional.of(candidate));
        when(context.getTarget()).thenReturn(Optional.of(parent));
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
