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


package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import java.util.Optional;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.util.WiresConnectorLabel;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorViewExtTest {

    private MultiPathDecorator HEAD_DECORATOR;
    private MultiPathDecorator TAIL_DECORATOR;
    private Point2DArray POINTS;
    private PolyLine line;
    private Layer layer;

    @Mock
    private WiresConnectorLabel label;

    @Mock
    private Text labelText;

    @Mock
    private WiresConnectorControl connectorControl;

    private WiresConnectorViewExt tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        HEAD_DECORATOR = new MultiPathDecorator(new MultiPath().rect(0, 0, 10, 10));
        TAIL_DECORATOR = new MultiPathDecorator(new MultiPath().rect(0, 0, 10, 10));
        POINTS = Point2DArray.fromArrayOfPoint2D(new Point2D(0, 10),
                                                 new Point2D(10, 10),
                                                 new Point2D(20, 20),
                                                 new Point2D(30, 30),
                                                 new Point2D(40, 40));
        line = new PolyLine(POINTS);
        layer = spy(new Layer());
        doAnswer(invocation -> {
            ((Consumer) invocation.getArguments()[0]).accept(labelText);
            return label;
        }).when(label).configure(any(Consumer.class));
        tested = spy(new WiresConnectorViewExt(ShapeViewSupportedEvents.DESKTOP_CONNECTOR_EVENT_TYPES,
                                               line,
                                               HEAD_DECORATOR,
                                               TAIL_DECORATOR) {
            @Override
            protected Optional<WiresConnectorLabel> createLabel(String title) {
                return Optional.of(WiresConnectorViewExtTest.this.label);
            }
        });
        tested.setControl(connectorControl);
        layer.add(tested.getGroup());
    }

    @Test
    public void testLabel() {
        assertNotNull(tested.label);
        assertTrue(tested.label.isPresent());
        tested.setTitle("some label");
        verify(labelText, times(1)).setText(eq("some label"));
        tested.setTitleAlpha(0.1d);
        verify(labelText, times(1)).setAlpha(eq(0.1d));
        tested.setTitleFontFamily("family1");
        verify(labelText, times(1)).setFontFamily(eq("family1"));
        tested.setTitleStrokeWidth(0.2);
        verify(labelText, times(1)).setStrokeWidth(eq(0.2d));
        tested.setTitleStrokeColor("color1");
        verify(labelText, times(1)).setStrokeColor(eq("color1"));
        tested.setTitleFontColor("color1");
        verify(labelText, times(1)).setFillColor(eq("color1"));
        tested.setTitleFontSize(0.3d);
        verify(labelText, times(1)).setFontSize(eq(0.3d));
        tested.moveTitleToTop();
        verify(labelText, times(1)).moveToTop();
        tested.batch();
        verify(labelText, times(1)).batch();
        assertNull(tested.getTitlePosition());
        assertNull(tested.getOrientation());
        assertEquals(0.0, tested.getMarginX(), 0.0001);
        assertNull(tested.getTitleFontFamily());
        assertEquals(0.0, tested.getTitleFontSize(), 0.0001);
        assertNull(tested.getFontPosition());
        assertNull(tested.getFontAlignment());

        tested.destroy();
        verify(label, times(1)).destroy();
        assertFalse(tested.label.isPresent());
        verify(connectorControl, times(1)).destroy();
    }

    @Test
    public void testLabelNotPresent() {
        tested = spy(new WiresConnectorViewExt(ShapeViewSupportedEvents.DESKTOP_CONNECTOR_EVENT_TYPES,
                                               line,
                                               HEAD_DECORATOR,
                                               TAIL_DECORATOR) {
            @Override
            protected Optional<WiresConnectorLabel> createLabel(String title) {
                return Optional.empty();
            }
        });

        assertNotNull(tested.label);
        assertFalse(tested.label.isPresent());
        tested.batch();
        verify(labelText, never()).batch();
    }
}
