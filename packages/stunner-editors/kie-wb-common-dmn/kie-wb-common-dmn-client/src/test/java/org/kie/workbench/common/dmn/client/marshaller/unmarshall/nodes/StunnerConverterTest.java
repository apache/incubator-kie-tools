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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.property.styling.BgColour;
import org.kie.workbench.common.dmn.api.property.styling.BorderColour;
import org.kie.workbench.common.dmn.api.property.styling.FontColour;
import org.kie.workbench.common.dmn.api.property.styling.FontSize;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class StunnerConverterTest {
    @Mock
    private FactoryManager factoryManager;

    @Mock
    private DMNDiagramsSession diagramsSession;

    @Captor
    private ArgumentCaptor<BgColour> bgColourArgumentCaptor;

    @Captor
    private ArgumentCaptor<BorderColour> borderColourArgumentCaptor;

    @Captor
    private ArgumentCaptor<FontColour> fontColourArgumentCaptor;

    @Captor
    private ArgumentCaptor<FontSize> fontSizeArgumentCaptor;

    private StunnerConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = spy(new StunnerConverter(factoryManager, diagramsSession));
    }

    @Test
    public void testDecision() {
        final StylingSet decisionStylingSet = mock(StylingSet.class);

        final Decision decision = spy(new Decision());
        when(decision.getStylingSet()).thenReturn(decisionStylingSet);

        final JSITDecision jsitDecision = mock(JSITDecision.class);
        when(jsitDecision.getTYPE_NAME()).thenReturn(JSITDecision.TYPE);

        final JSIStyle style = mock(JSIStyle.class);
        final JSIDMNShape shape = mock(JSIDMNShape.class);
        final JSIDMNStyle dmnStyleOfDrgShape = mock(JSIDMNStyle.class);
        when(shape.getStyle()).thenReturn(style);
        doReturn(style).when(converter).getUnwrappedJSIStyle(style);
        doReturn(true).when(converter).isJSIDMNStyle(style);
        doReturn(dmnStyleOfDrgShape).when(converter).getJSIDmnStyle(style);

        final NodeEntry nodeEntry = mock(NodeEntry.class);
        when(nodeEntry.getDmnElement()).thenReturn(jsitDecision);
        when(nodeEntry.getId()).thenReturn("_id");
        when(nodeEntry.getDmnShape()).thenReturn(shape);

        final Element graphElement = mock(Element.class);
        final Node graphNode = mock(Node.class);
        final View content = mock(View.class);
        when(factoryManager.newElement(anyString(), anyString())).thenReturn(graphElement);
        when(graphElement.asNode()).thenReturn(graphNode);
        when(graphNode.getContent()).thenReturn(content);

        when(content.getDefinition()).thenReturn(decision);

        final JSIColor randomColor = mock(JSIColor.class);
        when(randomColor.getRed()).thenReturn(12);
        when(randomColor.getGreen()).thenReturn(34);
        when(randomColor.getBlue()).thenReturn(56);

        when(dmnStyleOfDrgShape.getFontColor()).thenReturn(randomColor);
        when(dmnStyleOfDrgShape.getStrokeColor()).thenReturn(randomColor);
        when(dmnStyleOfDrgShape.getFillColor()).thenReturn(randomColor);
        when(dmnStyleOfDrgShape.getFontSize()).thenReturn(11d);

        converter.make(nodeEntry);

        verify(decisionStylingSet).setFontSize(fontSizeArgumentCaptor.capture());
        Assertions.assertThat(fontSizeArgumentCaptor.getValue().getValue()).isEqualTo(11d);

        verify(decisionStylingSet).setBorderColour(borderColourArgumentCaptor.capture());
        Assertions.assertThat(borderColourArgumentCaptor.getValue().getValue()).isEqualTo("#0c2238");

        verify(decisionStylingSet).setBgColour(bgColourArgumentCaptor.capture());
        Assertions.assertThat(bgColourArgumentCaptor.getValue().getValue()).isEqualTo("#0c2238");

        verify(decisionStylingSet).setFontColour(fontColourArgumentCaptor.capture());
        Assertions.assertThat(fontColourArgumentCaptor.getValue().getValue()).isEqualTo("#0c2238");
    }
}
