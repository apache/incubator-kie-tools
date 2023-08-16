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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractGraphRuleHandlerTest {

    protected static final String DEFINITION_ID = "defId1";
    protected static final String DEFINITION_ROLE1 = "defId1Role1";
    protected static final String DEFINITION_ROLE2 = "defId1Role2";
    protected static final String[] DEFINITION_LABELS_ARRAY = new String[]{DEFINITION_ID, DEFINITION_ROLE1, DEFINITION_ROLE2};
    protected static final Set<String> DEFINITION_LABELS = new HashSet<>(Arrays.asList(DEFINITION_LABELS_ARRAY));

    protected static final String PARENT_ID = "pId1";
    protected static final String[] PARENT_LABELS_ARRAY = new String[]{PARENT_ID, "theParent"};
    protected static final Set<String> PARENT_LABELS = new HashSet<>(Arrays.asList(PARENT_LABELS_ARRAY));

    protected static final String CANDIDATE_ID = "cId1";
    protected static final String CANDIDATE_ROLE1 = "cId1Role1";
    protected static final String CANDIDATE_ROLE2 = "cId1Role2";
    protected static final String[] CANDIDATE_LABELS_ARRAY = new String[]{CANDIDATE_ID, CANDIDATE_ROLE1, CANDIDATE_ROLE2};
    protected static final Set<String> CANDIDATE_LABELS = new HashSet<>(Arrays.asList(CANDIDATE_LABELS_ARRAY));

    @Mock
    protected DefinitionManager definitionManager;
    @Mock
    protected AdapterManager adapterManager;
    @Mock
    protected DefinitionAdapter<Object> definitionAdapter;

    @Mock
    protected Element element;
    @Mock
    protected View elementContent;
    @Mock
    protected Object elementDefinition;
    @Mock
    protected Node candidate;
    @Mock
    protected View candidateContent;
    @Mock
    protected Object candidateDefinition;
    @Mock
    protected Node parent;
    @Mock
    protected View parentContent;
    @Mock
    protected Object parentDefinition;

    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(element.getContent()).thenReturn(elementContent);
        when(element.getLabels()).thenReturn(DEFINITION_LABELS);
        when(elementContent.getDefinition()).thenReturn(elementDefinition);
        when(definitionAdapter.getId(eq(elementDefinition))).thenReturn(DefinitionId.build(DEFINITION_ID));
        when(definitionAdapter.getLabels(eq(elementDefinition))).thenReturn(DEFINITION_LABELS_ARRAY);
        when(candidate.getContent()).thenReturn(candidateContent);
        when(candidate.getLabels()).thenReturn(CANDIDATE_LABELS);
        when(candidateContent.getDefinition()).thenReturn(candidateDefinition);
        when(parent.getContent()).thenReturn(parentContent);
        when(parent.getLabels()).thenReturn(PARENT_LABELS);
        when(parentContent.getDefinition()).thenReturn(parentDefinition);
        when(definitionAdapter.getId(eq(candidateDefinition))).thenReturn(DefinitionId.build(CANDIDATE_ID));
        when(definitionAdapter.getLabels(eq(candidateDefinition))).thenReturn(CANDIDATE_LABELS_ARRAY);
        when(definitionAdapter.getId(eq(parentDefinition))).thenReturn(DefinitionId.build(PARENT_ID));
        when(definitionAdapter.getLabels(eq(parentDefinition))).thenReturn(PARENT_LABELS_ARRAY);
    }

    @SuppressWarnings("unchecked")
    protected Element<View<?>> mockElement(String id,
                                           Set<String> labels) {
        Element<View<?>> e = mock(Element.class);
        View v = mock(View.class);
        Object d = mock(Object.class);
        when(e.getContent()).thenReturn(v);
        when(v.getDefinition()).thenReturn(d);
        when(definitionAdapter.getId(eq(d))).thenReturn(DefinitionId.build(id));
        when(definitionAdapter.getLabels(eq(d))).thenReturn(labels.toArray(new String[labels.size()]));
        when(e.getLabels()).thenReturn(labels);
        return e;
    }

    @SuppressWarnings("unchecked")
    protected Node mockNode(String id,
                            Set<String> labels) {
        Node e = mock(Node.class);
        View v = mock(View.class);
        Object d = mock(Object.class);
        when(e.getContent()).thenReturn(v);
        when(v.getDefinition()).thenReturn(d);
        when(definitionAdapter.getId(eq(d))).thenReturn(DefinitionId.build(id));
        when(definitionAdapter.getLabels(eq(d))).thenReturn(labels.toArray(new String[labels.size()]));
        when(e.getLabels()).thenReturn(labels);
        return e;
    }

    @SuppressWarnings("unchecked")
    protected Edge mockEdge(String id,
                            Set<String> labels) {
        Edge e = mock(Edge.class);
        View v = mock(View.class);
        Object d = mock(Object.class);
        when(e.getContent()).thenReturn(v);
        when(v.getDefinition()).thenReturn(d);
        when(definitionAdapter.getId(eq(d))).thenReturn(DefinitionId.build(id));
        when(definitionAdapter.getLabels(eq(d))).thenReturn(labels.toArray(new String[labels.size()]));
        when(e.getLabels()).thenReturn(labels);
        return e;
    }

    protected RuleViolations mockNoViolations() {
        RuleViolations violations = mock(RuleViolations.class);
        List<RuleViolation> result = new ArrayList<>(0);
        when(violations.violations(eq(RuleViolation.Type.ERROR))).thenReturn(result);
        return violations;
    }

    protected RuleViolations mockWithViolations() {
        RuleViolation v1 = mock(RuleViolation.class);
        when(v1.getViolationType()).thenReturn(RuleViolation.Type.ERROR);
        RuleViolations violations = mock(RuleViolations.class);
        List<RuleViolation> result = new ArrayList<RuleViolation>(1) {{
            add(v1);
        }};
        when(violations.violations(eq(RuleViolation.Type.ERROR))).thenReturn(result);
        return violations;
    }
}
