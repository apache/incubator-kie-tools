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
package org.kie.workbench.common.dmn.api.rules;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SingleConnectorPerTypeGraphRuleTest extends BaseGraphRuleTest<SingleConnectorPerTypeGraphRule> {

    @Override
    protected SingleConnectorPerTypeGraphRule getRule() {
        return new SingleConnectorPerTypeGraphRule();
    }

    @Override
    protected Class getExpectedExtensionType() {
        return SingleConnectorPerTypeGraphRule.class;
    }

    @Override
    protected Class getExpectedContextType() {
        return GraphConnectionContext.class;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkMissingConnectionNodesDoesNotTriggerCheck() {
        when(context.getSource()).thenReturn(Optional.empty());
        when(context.getTarget()).thenReturn(Optional.empty());
        when(context.getConnector()).thenReturn(connector);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
        verify(check,
               never()).isConnectionAlreadyFormed(any(Node.class),
                                                  any(Node.class),
                                                  any(Edge.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkMissingConnectionTargetNodeDoesNotTriggerCheck() {
        final Node source = mock(Node.class);
        when(context.getSource()).thenReturn(Optional.of(source));
        when(context.getTarget()).thenReturn(Optional.empty());
        when(context.getConnector()).thenReturn(connector);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
        verify(check,
               never()).isConnectionAlreadyFormed(any(Node.class),
                                                  any(Node.class),
                                                  any(Edge.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkCompleteConnectionDefinitionTriggersCheck() {
        final Node source = mock(Node.class);
        final Node target = mock(Node.class);
        when(context.getSource()).thenReturn(Optional.of(source));
        when(context.getTarget()).thenReturn(Optional.of(target));
        when(context.getConnector()).thenReturn(connector);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
        verify(check).isConnectionAlreadyFormed(any(Node.class),
                                                any(Node.class),
                                                any(Edge.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkHasExistingConnection() {
        final Node node1 = new NodeImpl<>("node1");
        final Node node2 = new NodeImpl<>("node2");
        final Edge existingConnector = new EdgeImpl<>("edge1");
        final ViewConnector existingConnectorView = mock(ViewConnector.class);
        existingConnector.setContent(existingConnectorView);
        when(existingConnectorView.getDefinition()).thenReturn(new Definition());

        node1.getOutEdges().add(existingConnector);
        node2.getInEdges().add(existingConnector);
        existingConnector.setSourceNode(node1);
        existingConnector.setTargetNode(node2);

        graph.addNode(node1);
        graph.addNode(node2);

        when(context.getSource()).thenReturn(Optional.of(node1));
        when(context.getTarget()).thenReturn(Optional.of(node2));
        when(context.getConnector()).thenReturn(connector);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertTrue(result.violations().iterator().hasNext());
        final RuleViolation violation = result.violations().iterator().next();
        assertNotNull(violation);
        assertTrue(violation.getArguments().isPresent());
        assertEquals(1,
                     violation.getArguments().get().length);
        assertEquals(SingleConnectorPerTypeGraphRule.ERROR_MESSAGE,
                     violation.getArguments().get()[0]);
    }
}
