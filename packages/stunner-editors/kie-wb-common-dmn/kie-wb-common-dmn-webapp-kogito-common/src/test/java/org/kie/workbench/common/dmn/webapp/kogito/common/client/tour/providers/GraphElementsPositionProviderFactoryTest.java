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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers;

import java.util.stream.Stream;

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMRect;
import elemental2.dom.HTMLElement;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider.PositionProviderFunction;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Rect;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GraphElementsPositionProviderFactoryTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private GuidedTourUtils guidedTourUtils;

    @Mock
    private Elemental2DomUtil elemental2DomUtil;

    private GraphElementsPositionProviderFactory utils;

    @Before
    public void init() {
        utils = spy(new GraphElementsPositionProviderFactory(dmnGraphUtils, guidedTourUtils, elemental2DomUtil));
    }

    @Test
    public void testGetPositionProviderFunction() {
        final PositionProviderFunction providerFunction = utils.createPositionProvider();
        final NodeImpl<View> decisionNode = makeNodeImpl("0000", 10, 10, 50, 100);
        final String decisionNodeName = "Decision-1";
        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final WiresCanvas canvas = mock(WiresCanvas.class);
        final WiresCanvasView wiresCanvasView = mock(WiresCanvasView.class);
        final Element deprecatedElement = mock(Element.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final DOMRect clientRect = makeClientRect(10, 10);
        final Rect expected = mock(Rect.class);

        when(guidedTourUtils.asNodeImpl(decisionNode)).thenReturn(decisionNode);
        when(guidedTourUtils.getName(decisionNode)).thenReturn(decisionNodeName);
        when(dmnGraphUtils.getNodeStream()).thenReturn(Stream.of(decisionNode));

        when(dmnGraphUtils.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(wiresCanvasView);
        when(wiresCanvasView.getElement()).thenReturn(deprecatedElement);
        when(elemental2DomUtil.asHTMLElement(deprecatedElement)).thenReturn(htmlElement);
        when(htmlElement.getBoundingClientRect()).thenReturn(clientRect);

        when(utils.makeRect(20, 20, 50, 100)).thenReturn(expected);

        final Rect actual = providerFunction.call("Decision-1");

        assertEquals(expected, actual);
    }

    @Test
    public void testGetPositionProviderFunctionWhenParentCanvasElementCannotBeFound() {
        final PositionProviderFunction providerFunction = utils.createPositionProvider();
        final NodeImpl<View> decisionNode = makeNodeImpl("0000", 10, 10, 50, 100);
        final String decisionNodeName = "Decision-1";
        final Rect expected = mock(Rect.class);

        when(guidedTourUtils.asNodeImpl(decisionNode)).thenReturn(decisionNode);
        when(guidedTourUtils.getName(decisionNode)).thenReturn(decisionNodeName);
        when(dmnGraphUtils.getNodeStream()).thenReturn(Stream.of(decisionNode));

        when(utils.makeRect(10, 10, 50, 100)).thenReturn(expected);

        final Rect actual = providerFunction.call("Decision-1");

        assertEquals(expected, actual);
    }

    private DOMRect makeClientRect(final double top,
                                   final double left) {
        final DOMRect clientRect = new DOMRect();
        clientRect.top = top;
        clientRect.left = left;
        return clientRect;
    }

    private NodeImpl<View> makeNodeImpl(final String uuid,
                                        final double y,
                                        final double x,
                                        final double height,
                                        final double width) {
        final NodeImpl<View> viewNode = spy(new NodeImpl<>(uuid));
        final View content = mock(View.class);
        final Bounds bounds = mock(Bounds.class);

        doReturn(content).when(viewNode).getContent();
        when(content.getBounds()).thenReturn(bounds);
        when(bounds.getY()).thenReturn(y);
        when(bounds.getX()).thenReturn(x);
        when(bounds.getHeight()).thenReturn(height);
        when(bounds.getWidth()).thenReturn(width);

        return viewNode;
    }
}
