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


package org.kie.workbench.common.stunner.core.rule.ext.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.AbstractGraphDefinitionTypesTest;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.StatefulGraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectorParentsMatchContainmentHandlerTest extends AbstractGraphDefinitionTypesTest {

    public static final String DEF_EDGE_ID = "EdgeDef1";
    public static final String EDGE_UUID = "edge1";

    @Mock
    private RuleExtension ruleExtension;

    @Mock
    private NodeContainmentContext containmentContext;

    @Mock
    private Object connectorDef;

    private ConnectorParentsMatchContainmentHandler tested;
    private Collection candidates;

    private Edge connector;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        candidates = Collections.singletonList(nodeA);
        when(graphHandler.getDefinitionAdapter().getId(eq(connectorDef))).thenReturn(DefinitionId.build(DEF_EDGE_ID));
        this.connector = graphHandler.newEdge(EDGE_UUID,
                                              Optional.of(connectorDef));
        GraphEvaluationState state = new StatefulGraphEvaluationState(graphHandler.graph);
        when(containmentContext.getState()).thenReturn(state);
        when(ruleExtension.getId()).thenReturn(DEF_EDGE_ID);
        when(ruleExtension.getArguments()).thenReturn(new String[]{"violation1"});
        tested = new ConnectorParentsMatchContainmentHandler(graphHandler.getDefinitionManager(),
                                                             new TreeWalkTraverseProcessorImpl());
    }

    @Test
    public void testTypes() {
        assertEquals(ConnectorParentsMatchContainmentHandler.class,
                     tested.getExtensionType());
        assertEquals(NodeContainmentContext.class,
                     tested.getContextType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAccepts() {
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        when(containmentContext.getCandidates()).thenReturn(candidates);
        when(containmentContext.getParent()).thenReturn(parentNode);
        graphHandler.addEdge(connector,
                             nodeA)
                .connectTo(connector, nodeB);
        assertTrue(tested.accepts(ruleExtension,
                                  containmentContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotAccepts() {
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{DefinitionC.class});
        when(containmentContext.getCandidates()).thenReturn(candidates);
        when(containmentContext.getParent()).thenReturn(parentNode);
        assertFalse(tested.accepts(ruleExtension,
                                   containmentContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateParentSuccess() {
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        when(containmentContext.getCandidates()).thenReturn(candidates);
        when(containmentContext.getParent()).thenReturn(parentNode);
        graphHandler.addEdge(connector,
                             nodeA)
                .connectTo(connector, nodeB);
        final RuleViolations violations = tested.evaluate(ruleExtension,
                                                          containmentContext);
        assertNotNull(violations);
        assertViolations(violations, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateParentFailed() {
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        when(containmentContext.getCandidates()).thenReturn(candidates);
        when(containmentContext.getParent()).thenReturn(parentNode);
        graphHandler.addEdge(connector,
                             nodeA)
                .connectTo(connector, nodeC);
        final RuleViolations violations = tested.evaluate(ruleExtension,
                                                          containmentContext);
        assertNotNull(violations);
        assertViolations(violations, false);
    }
}
