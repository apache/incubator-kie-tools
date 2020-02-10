/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoFEELVariableSuggestionsTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Test
    public void getSuggestions() {

        final MonacoFEELVariableSuggestions variableSuggestions = new MonacoFEELVariableSuggestions(dmnGraphUtils);
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final Node node4 = mock(Node.class);
        final Node node5 = mock(Node.class);
        final Node node6 = mock(Node.class);
        final Definition definition1 = mock(Definition.class);
        final Definition definition2 = mock(Definition.class);
        final Definition definition3 = mock(Definition.class);
        final Definition definition4 = mock(Definition.class);
        final Definition definition5 = mock(Definition.class);
        final Definition definition6 = mock(Definition.class);
        final NamedElement namedElement1 = mock(NamedElement.class);
        final NamedElement namedElement2 = mock(NamedElement.class);
        final NamedElement namedElement3 = mock(NamedElement.class);
        final DMNDiagram dmnDiagram4 = mock(DMNDiagram.class);
        final DMNDiagram dmnDiagram5 = mock(DMNDiagram.class);
        final Definitions dmnDiagram4Definitions = mock(Definitions.class);
        final Definitions dmnDiagram5Definitions = mock(Definitions.class);
        final ItemDefinition itemDefinition41 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition42 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition43 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition51 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition52 = mock(ItemDefinition.class);
        final Name name1 = mock(Name.class);
        final Name name2 = mock(Name.class);
        final Name name3 = mock(Name.class);
        final Name name4 = mock(Name.class);
        final Name name5 = mock(Name.class);
        final Name name6 = mock(Name.class);
        final Name name7 = mock(Name.class);
        final Name name8 = mock(Name.class);
        final String expectedNameValue1 = "Decision-1";
        final String expectedNameValue2 = "Decision-2";
        final String expectedNameValue3 = "Decision-3";
        final String expectedNameValue4 = "Decision-4";
        final String expectedNameValue5 = "Decision-5";
        final String expectedNameValue6 = "";
        final String expectedNameValue7 = "Decision-6";
        final String expectedNameValue8 = "Decision-6";

        when(node1.getContent()).thenReturn(definition1);
        when(node2.getContent()).thenReturn(definition2);
        when(node3.getContent()).thenReturn(definition3);
        when(node4.getContent()).thenReturn(definition4);
        when(node5.getContent()).thenReturn(definition5);
        when(node6.getContent()).thenReturn(definition6);

        when(definition1.getDefinition()).thenReturn(namedElement1);
        when(definition2.getDefinition()).thenReturn(namedElement2);
        when(definition3.getDefinition()).thenReturn(namedElement3);
        when(definition4.getDefinition()).thenReturn(dmnDiagram4);
        when(definition5.getDefinition()).thenReturn(dmnDiagram5);
        when(definition6.getDefinition()).thenReturn(new Object());

        when(dmnDiagram4.getDefinitions()).thenReturn(dmnDiagram4Definitions);
        when(dmnDiagram5.getDefinitions()).thenReturn(dmnDiagram5Definitions);

        when(dmnDiagram4Definitions.getItemDefinition()).thenReturn(asList(itemDefinition41, itemDefinition42, itemDefinition43));
        when(dmnDiagram5Definitions.getItemDefinition()).thenReturn(asList(itemDefinition51, itemDefinition52));

        when(namedElement1.getName()).thenReturn(name1);
        when(namedElement2.getName()).thenReturn(name2);
        when(namedElement3.getName()).thenReturn(name3);

        when(itemDefinition41.getName()).thenReturn(name4);
        when(itemDefinition42.getName()).thenReturn(name5);
        when(itemDefinition43.getName()).thenReturn(name6);
        when(itemDefinition51.getName()).thenReturn(name7);
        when(itemDefinition52.getName()).thenReturn(name8);

        when(name1.getValue()).thenReturn(expectedNameValue1);
        when(name2.getValue()).thenReturn(expectedNameValue2);
        when(name3.getValue()).thenReturn(expectedNameValue3);
        when(name4.getValue()).thenReturn(expectedNameValue4);
        when(name5.getValue()).thenReturn(expectedNameValue5);
        when(name6.getValue()).thenReturn(expectedNameValue6);
        when(name7.getValue()).thenReturn(expectedNameValue7);
        when(name8.getValue()).thenReturn(expectedNameValue8);

        when(dmnGraphUtils.getNodeStream()).thenReturn(Stream.of(node1, node2, node3, node4, node5, node6));
        when(node1.getContent()).thenReturn(definition1);
        when(node2.getContent()).thenReturn(definition2);
        when(node3.getContent()).thenReturn(definition3);
        when(node4.getContent()).thenReturn(definition4);
        when(node5.getContent()).thenReturn(definition5);

        final List<String> suggestions = variableSuggestions.getSuggestions();

        assertEquals(6, suggestions.size());
        assertEquals(expectedNameValue1, suggestions.get(0));
        assertEquals(expectedNameValue2, suggestions.get(1));
        assertEquals(expectedNameValue3, suggestions.get(2));
        assertEquals(expectedNameValue4, suggestions.get(3));
        assertEquals(expectedNameValue5, suggestions.get(4));
        assertEquals(expectedNameValue7, suggestions.get(5));
    }
}
