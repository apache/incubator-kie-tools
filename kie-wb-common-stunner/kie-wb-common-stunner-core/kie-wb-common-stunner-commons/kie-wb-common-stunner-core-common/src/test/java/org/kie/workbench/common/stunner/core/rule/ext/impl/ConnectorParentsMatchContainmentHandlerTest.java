/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.ext.impl;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.AbstractGraphDefinitionTypesTest;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorParentsMatchContainmentHandlerTest extends AbstractGraphDefinitionTypesTest {

    public static final String DEF_EDGE_ID = "EdgeDef1";
    public static final String EDGE_UUID = "edge1";

    @Mock
    private RuleExtension ruleExtension;

    @Mock
    private NodeContainmentContext containmentContext;

    @Mock
    private Object connectorDef;

    @Mock
    private Edge edge;

    @Mock
    private ViewConnector viewConnector;

    private ConnectorParentsMatchContainmentHandler tested;
    private Edge connector;
    private Graph graph;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        this.graph = graphHandler.graph;
        when(graphHandler.definitionAdapter.getId(eq(connectorDef))).thenReturn(DEF_EDGE_ID);
        this.connector = graphHandler.newEdge(EDGE_UUID,
                                              Optional.of(connectorDef));
        when(containmentContext.getGraph()).thenReturn(graph);
        when(ruleExtension.getId()).thenReturn(DEF_EDGE_ID);
        when(ruleExtension.getArguments()).thenReturn(new String[]{"violation1"});
        tested = new ConnectorParentsMatchContainmentHandler(graphHandler.definitionManager,
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
        when(containmentContext.getCandidate()).thenReturn(nodeA);
        when(containmentContext.getParent()).thenReturn(parentNode);

        nodeA.getInEdges().add(edge);
        when(edge.getContent()).thenReturn(viewConnector);

        assertTrue(tested.accepts(ruleExtension,
                                  containmentContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotAccepts() {
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{DefinitionC.class});
        when(containmentContext.getCandidate()).thenReturn(nodeA);
        when(containmentContext.getParent()).thenReturn(parentNode);
        assertFalse(tested.accepts(ruleExtension,
                                   containmentContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateParentSuccess() {
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        when(containmentContext.getCandidate()).thenReturn(nodeA);
        when(containmentContext.getParent()).thenReturn(parentNode);
        graphHandler.addEdge(connector,
                             nodeA)
                .connectTo(connector, nodeB);
        final RuleViolations violations = tested.evaluate(ruleExtension,
                                                          containmentContext);
        assertNotNull(violations);
        assertFalse(violations.violations(Violation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateParentFailed() {
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        when(containmentContext.getCandidate()).thenReturn(nodeA);
        when(containmentContext.getParent()).thenReturn(parentNode);
        graphHandler.addEdge(connector,
                             nodeA)
                .connectTo(connector, nodeC);
        final RuleViolations violations = tested.evaluate(ruleExtension,
                                                          containmentContext);
        assertNotNull(violations);
        assertTrue(violations.violations(Violation.Type.ERROR).iterator().hasNext());
    }
}
