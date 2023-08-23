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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGraphSubIndexTest {

    @Mock
    private DMNGraphUtils graphUtils;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    private EventSourceMock<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    @Mock
    private EventSourceMock<CanvasClearSelectionEvent> canvasClearSelectionEventEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private Node node1;

    @Mock
    private Node node2;

    @Mock
    private Node node3;

    @Mock
    private Node node4;

    @Mock
    private Node node5;

    @Mock
    private Node node6;

    @Mock
    private Definition definition1;

    @Mock
    private Definition definition2;

    @Mock
    private Definition definition3;

    @Mock
    private Definition definition4;

    @Mock
    private Definition definition5;

    @Mock
    private DRGElement drgElement1;

    @Mock
    private DRGElement drgElement2;

    @Mock
    private DRGElement drgElement3;

    @Mock
    private TextAnnotation textAnnotation1;

    @Mock
    private TextAnnotation textAnnotation2;

    @Mock
    private CanvasHandler canvasHandler;

    private String drgElement1String = "DRG Element 1";

    private String drgElement2String = "DRG Element 2";

    private String drgElement3String = "DRG Element 3";

    private String textAnnotation1String = "Text Annotation 1";

    private String textAnnotation2String = "Text Annotation 2";

    private String uuid1 = "1111-1111";

    private String uuid2 = "2222-2222";

    private String uuid3 = "3333-3333";

    private String uuid4 = "4444-4444";

    private String uuid5 = "5555-5555";

    private String uuid6 = "6666-6666";

    private DMNGraphSubIndex index;

    @Before
    public void setup() {

        index = new DMNGraphSubIndex(graphUtils, canvasSelectionEvent, canvasFocusedSelectionEvent, canvasClearSelectionEventEvent, domainObjectSelectionEvent);

        when(node1.getUUID()).thenReturn(uuid1);
        when(node2.getUUID()).thenReturn(uuid2);
        when(node3.getUUID()).thenReturn(uuid3);
        when(node4.getUUID()).thenReturn(uuid4);
        when(node5.getUUID()).thenReturn(uuid5);
        when(node6.getUUID()).thenReturn(uuid6);

        when(node1.getContent()).thenReturn(definition1);
        when(node2.getContent()).thenReturn(definition2);
        when(node3.getContent()).thenReturn(definition3);
        when(node4.getContent()).thenReturn(definition4);
        when(node5.getContent()).thenReturn(definition5);

        when(definition1.getDefinition()).thenReturn(drgElement1);
        when(definition2.getDefinition()).thenReturn(drgElement2);
        when(definition3.getDefinition()).thenReturn(drgElement3);
        when(definition4.getDefinition()).thenReturn(textAnnotation1);
        when(definition5.getDefinition()).thenReturn(textAnnotation2);

        when(drgElement1.getName()).thenReturn(new Name(drgElement1String));
        when(drgElement2.getName()).thenReturn(new Name(drgElement2String));
        when(drgElement3.getName()).thenReturn(new Name(drgElement3String));
        when(textAnnotation1.getText()).thenReturn(new Text(textAnnotation1String));
        when(textAnnotation2.getText()).thenReturn(new Text(textAnnotation2String));

        when(graphUtils.getNodeStream()).thenReturn(Stream.of(node1, node2, node3, node4, node5, node6));

        when(graphUtils.getCanvasHandler()).thenReturn(canvasHandler);
    }

    @Test
    public void testGetSearchableElements() {

        final List<DMNSearchableElement> elements = index
                .getSearchableElements()
                .stream()
                .sorted(Comparator.comparing(DMNSearchableElement::getText))
                .collect(Collectors.toList());

        assertEquals(5, elements.size());

        // Text values
        assertEquals(drgElement1String, elements.get(0).getText());
        assertEquals(drgElement2String, elements.get(1).getText());
        assertEquals(drgElement3String, elements.get(2).getText());
        assertEquals(textAnnotation1String, elements.get(3).getText());
        assertEquals(textAnnotation2String, elements.get(4).getText());

        // Text values
        elements.get(0).onFound().execute();
        elements.get(1).onFound().execute();
        elements.get(2).onFound().execute();
        elements.get(3).onFound().execute();
        elements.get(4).onFound().execute();

        verify(canvasSelectionEvent, times(5)).fire(any(CanvasSelectionEvent.class));
        verify(canvasFocusedSelectionEvent, times(5)).fire(any(CanvasFocusedShapeEvent.class));
    }

    @Test
    public void testOnNoResultsFound() {

        index.onNoResultsFound();

        verify(canvasClearSelectionEventEvent).fire(any(CanvasClearSelectionEvent.class));
        verify(domainObjectSelectionEvent).fire(any(DomainObjectSelectionEvent.class));
    }
}
