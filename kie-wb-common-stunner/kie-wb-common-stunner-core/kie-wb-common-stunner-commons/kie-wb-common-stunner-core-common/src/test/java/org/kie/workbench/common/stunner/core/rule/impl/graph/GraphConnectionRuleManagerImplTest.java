/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.impl.graph;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.model.ModelConnectionRuleManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GraphConnectionRuleManagerImplTest extends AbstractGraphRuleManagerTest {

    private static final String EDGE_ID = "edgeId1";
    private static final String OUT_CANDIDATE_ID = "ocId1";
    private static final String OUT_CANDIDATE_ROLE1 = "ocId1Role1";
    private static final String OUT_CANDIDATE_ROLE2 = "ocId1Role2";
    private static final Set<String> OUT_CANDIDATE_LABELS = new HashSet<String>(2) {{
        add(OUT_CANDIDATE_ROLE1);
        add(OUT_CANDIDATE_ROLE2);
    }};
    private static final String IN_CANDIDATE_ID = "icId1";
    private static final String IN_CANDIDATE_ROLE1 = "icId1Role1";
    private static final String IN_CANDIDATE_ROLE2 = "icId1Role2";
    private static final Set<String> IN_CANDIDATE_LABELS = new HashSet<String>(2) {{
        add(IN_CANDIDATE_ROLE1);
        add(IN_CANDIDATE_ROLE2);
    }};

    @Mock
    ModelConnectionRuleManager modelConnectionRuleManager;

    private GraphConnectionRuleManagerImpl tested;
    private Edge edge;
    private Node inCandidate;
    private Node outCandidate;

    @Before
    public void setup() {
        super.setup();
        this.edge = mockEdge(EDGE_ID,
                             new HashSet<>(0));
        this.inCandidate = mockNode(IN_CANDIDATE_ID,
                                    IN_CANDIDATE_LABELS);
        this.outCandidate = mockNode(OUT_CANDIDATE_ID,
                                     OUT_CANDIDATE_LABELS);
        this.tested = new GraphConnectionRuleManagerImpl(definitionManager,
                                                         modelConnectionRuleManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateAccept() {
        RuleViolations violations = mockNoViolations();
        when(modelConnectionRuleManager
                     .evaluate(eq(EDGE_ID),
                               eq(OUT_CANDIDATE_LABELS),
                               eq(IN_CANDIDATE_LABELS)))
                .thenReturn(violations);
        final RuleViolations result = tested.evaluate(edge,
                                                      outCandidate,
                                                      inCandidate);
        assertNotNull(result);
        assertFalse(result.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateDeny() {
        RuleViolations violations = mockWithViolations();
        when(modelConnectionRuleManager
                     .evaluate(eq(EDGE_ID),
                               eq(OUT_CANDIDATE_LABELS),
                               eq(IN_CANDIDATE_LABELS)))
                .thenReturn(violations);
        final RuleViolations result = tested.evaluate(edge,
                                                      outCandidate,
                                                      inCandidate);
        assertNotNull(result);
        assertTrue(result.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
