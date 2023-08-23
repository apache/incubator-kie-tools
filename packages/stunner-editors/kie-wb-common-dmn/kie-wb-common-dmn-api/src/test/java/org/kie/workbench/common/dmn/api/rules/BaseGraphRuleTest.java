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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.StatelessGraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class BaseGraphRuleTest<T extends RuleExtensionHandler> {

    @Mock
    protected RuleExtension rule;

    @Mock
    protected GraphConnectionContext context;

    @Mock
    protected EdgeImpl connector;

    @Mock
    protected ViewConnector connectorView;

    @Mock
    private TreeWalkTraverseProcessor walker;

    protected GraphImpl graph = new GraphImpl("uuid",
                                              new GraphNodeStoreImpl());

    protected T check;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.check = spy(getRule());
        StatelessGraphEvaluationState evaluationState = new StatelessGraphEvaluationState(graph);
        when(context.getState()).thenReturn(evaluationState);
        when(context.getConnector()).thenReturn(connector);
        when(connector.getContent()).thenReturn(connectorView);
        when(connectorView.getDefinition()).thenReturn(new Definition());
    }

    protected abstract T getRule();

    protected abstract Class getExpectedExtensionType();

    protected abstract Class getExpectedContextType();

    @Test
    public void assertExtensionType() {
        assertEquals(getExpectedExtensionType(),
                     check.getExtensionType());
    }

    @Test
    public void assertContextType() {
        assertEquals(getExpectedContextType(),
                     check.getContextType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRuleAcceptsAnnotatedConnectorType() {
        when(rule.getTypeArguments()).thenReturn(new Class[]{Definition.class});

        assertTrue(check.accepts(rule,
                                 context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRuleDoesNotAcceptDifferentAnnotatedConnectorType() {
        when(rule.getTypeArguments()).thenReturn(new Class[]{String.class});

        assertFalse(check.accepts(rule,
                                  context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkNoViolationsWhenNoExistingConnections() {
        final Node node1 = new NodeImpl<>("node1");
        final Node node2 = new NodeImpl<>("node2");
        graph.addNode(node1);
        graph.addNode(node2);
        when(context.getSource()).thenReturn(Optional.of(node1));
        when(context.getTarget()).thenReturn(Optional.of(node2));
        when(context.getConnector()).thenReturn(connector);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
    }

    protected static class Definition {

    }
}
