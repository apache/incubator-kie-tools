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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.ConnectionContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionEvaluationHandlerTest {

    private final static String EDGE_ID = "eId";
    private final static String[] P1 = new String[]{"r1", "o1"};
    private final static String[] P2 = new String[]{"r2", "o2"};
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
    ConnectionContext context;

    private ConnectionEvaluationHandler tested;

    @Before
    public void setup() throws Exception {
        tested = new ConnectionEvaluationHandler();
    }

    @Test
    public void testAccepts() {
        when(context.getConnectorRole()).thenReturn(EDGE_ID);
        assertTrue(tested.accepts(RULE,
                                  context));
        when(context.getConnectorRole()).thenReturn("anotherEdgeId");
        assertFalse(tested.accepts(RULE,
                                   context));
    }

    @Test
    public void testEvaluateSuccess1() {
        final Set<String> sourceRoles = new HashSet<String>(1) {{
            add("r1");
        }};
        final Set<String> targetRoles = new HashSet<String>(1) {{
            add("o1");
        }};
        when(context.getConnectorRole()).thenReturn(EDGE_ID);
        when(context.getSourceRoles()).thenReturn(Optional.of(sourceRoles));
        when(context.getTargetRoles()).thenReturn(Optional.of(targetRoles));
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateFailed1() {
        final Set<String> sourceRoles = new HashSet<String>(1) {{
            add("r1");
        }};
        final Set<String> targetRoles = new HashSet<String>(1) {{
            add("o2");
        }};
        when(context.getConnectorRole()).thenReturn(EDGE_ID);
        when(context.getSourceRoles()).thenReturn(Optional.of(sourceRoles));
        when(context.getTargetRoles()).thenReturn(Optional.of(targetRoles));
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateSuccess2() {
        final Set<String> sourceRoles = new HashSet<String>(1) {{
            add("r2");
        }};
        final Set<String> targetRoles = new HashSet<String>(1) {{
            add("o2");
        }};
        when(context.getConnectorRole()).thenReturn(EDGE_ID);
        when(context.getSourceRoles()).thenReturn(Optional.of(sourceRoles));
        when(context.getTargetRoles()).thenReturn(Optional.of(targetRoles));
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateFailed2() {
        final Set<String> sourceRoles = new HashSet<String>(1) {{
            add("r2");
        }};
        final Set<String> targetRoles = new HashSet<String>(1) {{
            add("o1");
        }};
        when(context.getConnectorRole()).thenReturn(EDGE_ID);
        when(context.getSourceRoles()).thenReturn(Optional.of(sourceRoles));
        when(context.getTargetRoles()).thenReturn(Optional.of(targetRoles));
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
