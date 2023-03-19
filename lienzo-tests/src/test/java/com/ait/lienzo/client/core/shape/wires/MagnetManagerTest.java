/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator;
import com.ait.lienzo.client.core.shape.wires.decorator.MagnetDecorator;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastStringMap;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.core.JsArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
@WithClassesToStub(JsArray.class)
public class MagnetManagerTest {

    @Mock
    private MagnetManager.Magnets magnets;

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private NFastStringMap<WiresShape> shapesColors;

    @Mock
    private NFastStringMap<WiresMagnet> magnetsColors;

    @Mock
    private MagnetDecorator magnetDecorator;

    private MagnetManager tested;

    @Before
    public void setUp() {
        tested = spy(new MagnetManager());
    }

    @Test
    public void testDrawMagnetsToBack() {
        // ScratchPad and it's context preparations
        final Context2D context = mock(Context2D.class);
        when(scratchPad.getContext()).thenReturn(context);
        when(scratchPad.getWidth()).thenReturn(121);
        when(scratchPad.getHeight()).thenReturn(132);

        // Magnets preparations. We will use them to check that all magnets are drawn
        final WiresMagnet magnet1 = mock(WiresMagnet.class);
        final WiresMagnet magnet2 = mock(WiresMagnet.class);
        final WiresMagnet magnet3 = mock(WiresMagnet.class);
        when(magnets.size()).thenReturn(3);
        when(magnets.getMagnet(0)).thenReturn(magnet1);
        when(magnets.getMagnet(1)).thenReturn(magnet2);
        when(magnets.getMagnet(2)).thenReturn(magnet3);

        // We are not able to mock static method, so let's prepare all staff used by

        // Test starts here
        doNothing().when(tested).drawShapeToBacking(magnets, shapesColors, context);
        doNothing().when(tested).drawMagnet(eq(magnetsColors), eq(context), any(WiresMagnet.class));
        tested.drawMagnetsToBack(magnets, shapesColors, magnetsColors, scratchPad);

        verify(magnetsColors).clear();

        verify(tested).drawMagnet(magnetsColors, context, magnet1);
        verify(tested).drawMagnet(magnetsColors, context, magnet2);
        verify(tested).drawMagnet(magnetsColors, context, magnet3);

        verify(context).getImageData(0, 0, 121, 132);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testDrawMagnet() {
        final Context2D context = mock(Context2D.class);
        final WiresMagnet magnet = mock(WiresMagnet.class);
        final IPrimitive primitive = mock(IPrimitive.class);
        when(magnet.getControl()).thenReturn(primitive);

        // Test starts here
        tested.drawMagnet(magnetsColors, context, magnet);

        // Magnet is registered in colors map
        verify(magnetsColors).put(anyString(), eq(magnet));

        // Magnet position is used during drawing
        verify(primitive).getX();
        verify(primitive).getY();

        // Magnet is drawn
        verify(context).beginPath();
        verify(context).stroke();
    }

    @Test
    public void testCreateMagnets() {
        WiresShape shape = new WiresShape(new MultiPath().rect(0, 0, 10, 10));
        tested.setMagnetDecorator(magnetDecorator);
        tested.createMagnets(shape);
        verify(magnetDecorator, times(9)).decorate(any(Circle.class),
                                                   eq(IShapeDecorator.ShapeState.VALID));
    }
}