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

package com.ait.lienzo.client.core.shape.wires.layout.direction;

import com.ait.lienzo.client.core.shape.wires.layout.AbstractContainerLayoutTest;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.HorizontalAlignment;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Orientation;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.ReferencePosition;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.VerticalAlignment;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DirectionContainerLayoutTest extends AbstractContainerLayoutTest<DirectionLayout, DirectionContainerLayout> {

    private BoundingBox parentBoundingBox;

    private BoundingBox childBoundingBox;

    @Before
    public void setUp() {
        super.setUp();
        when(child.getBoundingBox()).thenReturn(BoundingBox.fromDoubles(0, 0, 0, 0));

        currentLayout = new DirectionLayout.Builder().build();
        parentBoundingBox = parent.getBoundingBox();
        childBoundingBox = child.getBoundingBox();
    }

    //Outside
    @Test
    public void add1() {

        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.OUTSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();
        tested.add(child, currentLayout);

        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX() - childBoundingBox.getWidth());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY());
    }

    @Test
    public void add2() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.MIDDLE)
                .referencePosition(ReferencePosition.OUTSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);

        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX() - childBoundingBox.getWidth());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getHeight() / 2 - childBoundingBox.getHeight() / 2);
    }

    @Test
    public void add3() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.BOTTOM)
                .referencePosition(ReferencePosition.OUTSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);

        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX() - childBoundingBox.getWidth());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getHeight());
    }

    @Test
    public void add4() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.OUTSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getWidth() / 2 - childBoundingBox.getWidth() / 2);
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY());
    }

    @Test
    public void add6() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.RIGHT)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.OUTSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getWidth());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY());
    }

    //Inside

    @Test
    public void add7() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.INSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY());
    }

    @Test
    public void add8() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.MIDDLE)
                .referencePosition(ReferencePosition.INSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getHeight() / 2 - childBoundingBox.getHeight() / 2);
    }

    @Test
    public void add9() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.BOTTOM)
                .referencePosition(ReferencePosition.INSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getHeight() - childBoundingBox.getHeight());
    }

    @Test
    public void add10() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.INSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getWidth() / 2 - childBoundingBox.getWidth() / 2);
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY());
    }

    @Test
    public void add11() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.RIGHT)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.INSIDE)
                .orientation(Orientation.HORIZONTAL)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getWidth() - childBoundingBox.getWidth());
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY());
    }

    //Margins Inside
    @Test
    public void add12() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.INSIDE)
                .orientation(Orientation.HORIZONTAL)
                .margin(VerticalAlignment.TOP, 5.0)
                .margin(HorizontalAlignment.LEFT, 5.0)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX() + 5.0);
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY() + 5.0);
    }

    @Test
    public void add13() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.RIGHT)
                .verticalAlignment(VerticalAlignment.BOTTOM)
                .referencePosition(ReferencePosition.INSIDE)
                .orientation(Orientation.HORIZONTAL)
                .margin(VerticalAlignment.BOTTOM, 5.0)
                .margin(HorizontalAlignment.RIGHT, 5.0)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getWidth() - childBoundingBox.getWidth() - 5.0);
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getHeight() - childBoundingBox.getHeight() - 5.0);
    }

    //Margins outside
    @Test
    public void add14() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.RIGHT)
                .verticalAlignment(VerticalAlignment.BOTTOM)
                .referencePosition(ReferencePosition.OUTSIDE)
                .orientation(Orientation.HORIZONTAL)
                .margin(VerticalAlignment.BOTTOM, 5.0)
                .margin(HorizontalAlignment.RIGHT, 5.0)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getWidth() + 5.0);
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getHeight() + 5.0);
    }

    @Test
    public void add15() {
        currentLayout = new DirectionLayout.Builder()
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .referencePosition(ReferencePosition.OUTSIDE)
                .orientation(Orientation.HORIZONTAL)
                .margin(VerticalAlignment.TOP, 5.0)
                .margin(HorizontalAlignment.LEFT, 5.0)
                .build();

        tested.add(child, currentLayout);
        verify(tested).setHorizontalAlignment(child, parentBoundingBox.getX() - childBoundingBox.getWidth() - 5.0);
        verify(tested).setVerticalAlignment(child, parentBoundingBox.getY() - childBoundingBox.getHeight() - 5.0);
    }

    @Override
    protected DirectionContainerLayout createInstance() {
        return new DirectionContainerLayout(parent);
    }

    @Override
    protected DirectionLayout getDefaultLayoutForTest() {
        return new DirectionLayout.Builder().build();
    }
}