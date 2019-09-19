/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeViewExtTest extends AbstractWiresShapeViewText {

    private static ViewEventType[] viewEventTypes = {};
    private final static MultiPath PATH = new MultiPath();

    @Before
    public void setup() throws Exception {
        super.setUp();
    }

    @Override
    public WiresShapeViewExt createInstance() {
        return new WiresShapeViewExt<>(viewEventTypes,
                                       PATH);
    }

    @Test
    public void testTextWrapBoundariesUpdates() {
        tested.refresh();
        verify(textDecorator).update();
    }

    @Test
    public void testSetTitleXOffsetPosition() {
        tested.setTitleXOffsetPosition(10.0);

        verify(textDecorator).setTitleXOffsetPosition(10.0);
    }

    @Test
    public void testSetTitleYOffsetPosition() {
        tested.setTitleYOffsetPosition(10.0);

        verify(textDecorator).setTitleYOffsetPosition(10.0);
    }

    @Test
    public void testSetTextWrapperBounds() {
        testSetTextWrapperStrategy(TextWrapperStrategy.BOUNDS);
    }

    @Test
    public void testSetTextWrapperBoundsAndLineBreaks() {
        testSetTextWrapperStrategy(TextWrapperStrategy.BOUNDS_AND_LINE_BREAKS);
    }

    @Test
    public void testSetTextWrapperLineBreak() {
        testSetTextWrapperStrategy(TextWrapperStrategy.LINE_BREAK);
    }

    @Test
    public void testSetTextWrapperNoWrap() {
        testSetTextWrapperStrategy(TextWrapperStrategy.NO_WRAP);
    }

    @Test
    public void testSetTextWrapperTruncate() {
        testSetTextWrapperStrategy(TextWrapperStrategy.TRUNCATE);
    }

    @Test
    public void testSetTextWrapperTruncateWithLineBreak() {
        testSetTextWrapperStrategy(TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK);
    }

    @Test
    public void testTextNotIncludedInBoundBox() {
        tested.addChild(new Circle(43));

        final BoundingBox startingBB = tested.getBoundingBox();
        //Adding a Tittle will add a label, but will not be included in bounding box
        tested.setTitle("Some Title");
        final BoundingBox endingBB = tested.getBoundingBox();

        assertTrue(startingBB.getMinX() == endingBB.getMinX());
        assertTrue(startingBB.getMinY() == endingBB.getMinY());

        assertTrue(startingBB.getMaxX() == endingBB.getMaxX());
        assertTrue(startingBB.getMaxY() == endingBB.getMaxY());
    }

    private void testSetTextWrapperStrategy(final TextWrapperStrategy wrapperStrategy) {
        tested.setTextWrapper(wrapperStrategy);
        verify(textDecorator).setTextWrapper(wrapperStrategy);
    }
}
