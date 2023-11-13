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

package org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CanvasInlineTextEditorControlTest extends AbstractCanvasInlineTextEditorControlTest<CanvasInlineTextEditorControl> {

    @Override
    protected CanvasInlineTextEditorControl getControl() {
        return new CanvasInlineTextEditorControl(floatingView, textEditorBox);
    }

    @Test
    public void testInitParameters() {
        CanvasInlineTextEditorControl tested = mock(CanvasInlineTextEditorControl.class);
        doCallRealMethod().when(tested).initParameters();

        tested.initParameters();

        assertTrue(tested.isMultiline);
        assertEquals(2d, tested.borderOffsetX, 0.001);
        assertEquals(2d, tested.borderOffsetY, 0.001);
        assertEquals(2d, tested.underBoxOffset, 0.001);
        assertEquals(-2d, tested.topBorderOffset, 0.001);
        assertEquals(4d, tested.fontSizeCorrection, 0.001);
        assertEquals(190d, tested.maxInnerLeftBoxWidth, 0.001);
        assertEquals(190d, tested.maxInnerLeftBoxHeight, 0.001);
        assertEquals(13d, tested.scrollBarOffset, 0.001);
        assertEquals(0d, tested.paletteOffsetX, 0.001);
        assertEquals(190d, tested.maxInnerTopBoxWidth, 0.001);
        assertEquals(190d, tested.maxInnerTopBoxHeight, 0.001);
        assertEquals(-1.1d, tested.innerBoxOffsetY, 0.001);
    }
}
