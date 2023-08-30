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


package org.kie.workbench.common.stunner.bpmn.client.documentation;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.documentation.BPMNDocumentationService;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNDocumentationViewTest {

    private BPMNDocumentationView tested;

    @Mock
    private BPMNDocumentationService documentationService;

    @Mock
    private ClientTranslationService clientTranslationService;

    @Mock
    private Diagram diagram;

    @Mock
    private DocumentationOutput output;

    @Mock
    private Button printButton;

    @Mock
    private HTMLElement div;

    @Mock
    private PrintHelper printHelper;

    private String value = "doc";

    @Mock
    private Graph graph;

    @Mock
    private Node node;

    @Before
    public void setUp() throws Exception {
        when(documentationService.generate(diagram)).thenReturn(output);
        when(output.getValue()).thenReturn(value);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getNode("uuid")).thenReturn(node);

        tested = spy(new BPMNDocumentationView(documentationService, clientTranslationService, printHelper, div, printButton));
    }

    @Test
    public void initializeAndRefresh() {
        tested.initialize(diagram);
        verify(documentationService).generate(diagram);
        verify(output).getValue();

        tested.refresh();
        verify(documentationService, times(2)).generate(diagram);
        verify(output, times(2)).getValue();
        assertEquals(div.innerHTML, value);
    }

    @Test
    public void onFormFieldChanged() {
        tested.setIsSelected(() -> true);
        tested.onFormFieldChanged(new FormFieldChanged(null, null, "uuid"));
        verify(tested).refresh();
    }

    @Test
    public void onFormFieldChangedNotExists() {
        tested.onFormFieldChanged(new FormFieldChanged(null, null, "uuid2"));
        verify(tested, never()).refresh();
    }

    @Test
    public void onFormFieldChangedNotActive() {
        tested.setIsSelected(() -> false);
        tested.onFormFieldChanged(new FormFieldChanged(null, null, "uuid"));
        verify(tested, never()).refresh();
    }

    @Test
    public void isEnabled() {
        assertTrue(tested.isEnabled());
    }

    @Test
    public void print() {
        tested.print();
        verify(printHelper).print(div);
    }
}