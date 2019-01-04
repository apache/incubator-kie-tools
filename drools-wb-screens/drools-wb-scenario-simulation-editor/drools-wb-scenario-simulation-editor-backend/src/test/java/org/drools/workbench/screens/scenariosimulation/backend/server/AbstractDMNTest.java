/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractDMNTest {

    @Mock
    protected DMNModel dmnModelMock;

    protected static final String SIMPLE_TYPE_NAME = "SIMPLE_TYPE_NAME";
    protected static final String SIMPLE_DECISION_TYPE_NAME = "SIMPLE_DECISION_TYPE_NAME";
    protected static final String COMPLEX_DECISION_TYPE_NAME = "COMPLEX_DECISION_TYPE_NAME";
    protected static final String BASE_TYPE = "BASE_TYPE";
    protected static final String COMPLEX_TYPE = "COMPLEX_TYPE";

    protected DMNType simpleTypeMock;
    protected DMNType complexTypeMock;
    protected DMNType nestedComplexTypeMock;
    protected Map<String, DMNType> complexFields;
    protected Map<String, DMNType> nestedComplexFields;
    protected Set<InputDataNode> inputDataNodes;
    protected Set<DecisionNode> decisionNodes;

    protected void init() {
        inputDataNodes = new HashSet<>();

        InputDataNode inputDataNodeSimpleMock = mock(InputDataNode.class);
        DMNType simpleTypeMock = mock(DMNType.class);
        when(simpleTypeMock.isComposite()).thenReturn(false);
        when(simpleTypeMock.getName()).thenReturn(BASE_TYPE);
        when(inputDataNodeSimpleMock.getType()).thenReturn(simpleTypeMock);
        when(inputDataNodeSimpleMock.getName()).thenReturn(SIMPLE_TYPE_NAME);

        inputDataNodes.add(inputDataNodeSimpleMock);
        when(dmnModelMock.getInputs()).thenReturn(inputDataNodes);

        decisionNodes = new HashSet<>();

        DecisionNode decisionNodeSimpleMock = mock(DecisionNode.class);
        when(decisionNodeSimpleMock.getResultType()).thenReturn(simpleTypeMock);
        when(decisionNodeSimpleMock.getName()).thenReturn(SIMPLE_DECISION_TYPE_NAME);
        decisionNodes.add(decisionNodeSimpleMock);

        DecisionNode decisionNodeComplexMock = mock(DecisionNode.class);

        DMNType complexTypeMock = mock(DMNType.class);
        when(complexTypeMock.isComposite()).thenReturn(true);
        when(complexTypeMock.getName()).thenReturn(COMPLEX_TYPE);

        Map<String, DMNType> complexFields = new HashMap<>();
        complexFields.put(SIMPLE_DECISION_TYPE_NAME, simpleTypeMock);
        when(complexTypeMock.getFields()).thenReturn(complexFields);

        DMNType nestedComplexTypeMock = mock(DMNType.class);
        when(nestedComplexTypeMock.isComposite()).thenReturn(true);
        when(nestedComplexTypeMock.getName()).thenReturn(COMPLEX_TYPE);
        complexFields.put(COMPLEX_DECISION_TYPE_NAME, nestedComplexTypeMock);

        Map<String, DMNType> nestedComplexFields = new HashMap<>();
        nestedComplexFields.put(SIMPLE_DECISION_TYPE_NAME, simpleTypeMock);
        when(nestedComplexTypeMock.getFields()).thenReturn(nestedComplexFields);

        when(decisionNodeComplexMock.getResultType()).thenReturn(complexTypeMock);
        when(decisionNodeComplexMock.getName()).thenReturn(COMPLEX_DECISION_TYPE_NAME);
        decisionNodes.add(decisionNodeComplexMock);
        when(dmnModelMock.getDecisions()).thenReturn(decisionNodes);
    }
}