/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.codecompletion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.Candidate;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.Variable;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoFEELSuggestionsTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private FEELLanguageService feelLanguageService;

    @Captor
    private ArgumentCaptor<List<Variable>> variablesArgumentCaptor;

    private MonacoFEELSuggestions monacoFEELSuggestions;

    @Before
    public void setup() {
        monacoFEELSuggestions = spy(new MonacoFEELSuggestions(dmnGraphUtils, feelLanguageService));
    }

    @Test
    public void getSuggestions() {

        final String expectedNodeName1 = "Decision-1";
        final String expectedNodeName2 = "Decision-2";
        final String expectedNodeName3 = "Decision-3";
        final String expectedNodeName4 = "Decision-4";
        final String expectedNodeName5 = "Decision-5";
        final String expectedNodeName6 = "Decision-6";
        final String expression = "1 +";
        final Position position = new Position(1, 1);
        final List<Candidate> expectedCandidates = new ArrayList<>();
        final Stream<Node> nodeStream = Stream.of(
                makeDecisionNode(expectedNodeName1, BuiltInType.NUMBER),
                makeDecisionNode(expectedNodeName2, BuiltInType.STRING),
                makeDecisionNode(expectedNodeName3, BuiltInType.DATE),
                makeDecisionNode(expectedNodeName4, BuiltInType.BOOLEAN),
                makeDecisionNode(expectedNodeName5, BuiltInType.CONTEXT),
                makeDecisionNode(expectedNodeName6, null));

        when(dmnGraphUtils.getNodeStream()).thenReturn(nodeStream);
        when(feelLanguageService.getCandidates(any(), any(), any())).thenReturn(expectedCandidates);

        final List<Candidate> actualSuggestions = monacoFEELSuggestions.getCandidates(expression, position);
        verify(feelLanguageService).getCandidates(eq(expression), variablesArgumentCaptor.capture(), eq(position));
        final List<Variable> actualVariables = variablesArgumentCaptor.getValue();

        assertSame(expectedCandidates, actualSuggestions);
        assertEquals(6, actualVariables.size());

        assertEquals(expectedNodeName1, actualVariables.get(0).getName());
        assertEquals(expectedNodeName2, actualVariables.get(1).getName());
        assertEquals(expectedNodeName3, actualVariables.get(2).getName());
        assertEquals(expectedNodeName4, actualVariables.get(3).getName());
        assertEquals(expectedNodeName5, actualVariables.get(4).getName());
        assertEquals(expectedNodeName6, actualVariables.get(5).getName());

        assertEquals(org.kie.dmn.feel.lang.types.BuiltInType.NUMBER, actualVariables.get(0).getType());
        assertEquals(org.kie.dmn.feel.lang.types.BuiltInType.STRING, actualVariables.get(1).getType());
        assertEquals(org.kie.dmn.feel.lang.types.BuiltInType.DATE, actualVariables.get(2).getType());
        assertEquals(org.kie.dmn.feel.lang.types.BuiltInType.BOOLEAN, actualVariables.get(3).getType());
        assertEquals(org.kie.dmn.feel.lang.types.BuiltInType.CONTEXT, actualVariables.get(4).getType());
        assertEquals(org.kie.dmn.feel.lang.types.BuiltInType.UNKNOWN, actualVariables.get(5).getType());
    }

    private NodeImpl<Definition<Decision>> makeDecisionNode(final String name,
                                                            final BuiltInType builtInType) {

        final NodeImpl<Definition<Decision>> node = makeNodeImpl();
        final Definition<Decision> definition = makeDefinition();
        final Decision decision = spy(new Decision());
        final InformationItemPrimary informationItemPrimary = mock(InformationItemPrimary.class);
        final QName qName = builtInType == null ? null : new QName(builtInType);

        when(informationItemPrimary.getTypeRef()).thenReturn(qName);
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(decision);

        decision.setName(new Name(name));
        decision.setVariable(informationItemPrimary);

        return node;
    }

    @Test
    public void getSuggestionsWhenFEELLanguageServiceFails() {

        when(feelLanguageService.getCandidates(any(), any(), any())).thenThrow(new RuntimeException());
        doNothing().when(monacoFEELSuggestions).warn(any());

        final List<Candidate> candidates = monacoFEELSuggestions.getCandidates(null, null);

        verify(monacoFEELSuggestions).warn("[FEELLanguageService] Error: Candidates could not be processed.");
        assertTrue(candidates.isEmpty());
    }

    @SuppressWarnings("unchecked")
    private NodeImpl<Definition<Decision>> makeNodeImpl() {
        return mock(NodeImpl.class);
    }

    @SuppressWarnings("unchecked")
    private Definition<Decision> makeDefinition() {
        return mock(Definition.class);
    }
}
