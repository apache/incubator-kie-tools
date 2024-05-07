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

package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Slice;
import com.ait.lienzo.shared.core.types.ColorName;

public class SliceGroupExample extends BaseShapesExample<Slice> implements Example {

    private SliceGroup sliceGroup;

    public SliceGroupExample(String title) {
        super(title);
        this.setPaddings(20, 20, 30, 100);
        numberOfShapes = 3;
        shapes = new Slice[numberOfShapes];
    }

    @Override
    public void run() {
        final int w = width / 2;
        final int h = height / 2;

        sliceGroup = new SliceGroup(w);
        sliceGroup.setRotation(-Math.PI / 2);
        sliceGroup.setX(w - w / 2).setY(h + w / 2);

        layer.add(sliceGroup);
    }

    @Override
    protected void setLocation() {
        final int w = width / 2;
        final int h = height / 2;

        sliceGroup.resize(w);
        sliceGroup.setX(w - w / 2).setY(h + w / 2);
    }

    private class SliceGroup extends Group {

        private Rectangle rectangle;
        private double width;

        public SliceGroup(double width) {

            this.width = width;
            rectangle = new Rectangle(width, width).setStrokeColor(ColorName.BLACK.getValue());
            add(rectangle);

            final double radius = width / 4;

            shapes[0] = new Slice(radius, 0, Math.PI / 2, true);
            shapes[0].setX(radius).setY(radius);
            shapes[0].setFillColor(ColorName.RED.getValue());
            shapes[0].setAlpha(0.5);
            shapes[0].setDraggable(true);
            add(shapes[0]);

            shapes[1] = new Slice(radius, 0.75 * Math.PI, 3 * Math.PI / 2, true);
            shapes[1].setX(3 * radius).setY(radius);
            shapes[1].setScale(0.5);
            shapes[1].setFillColor(ColorName.GREEN.getValue());
            shapes[1].setAlpha(0.5);
            shapes[1].setDraggable(true);
            add(shapes[1]);

            shapes[2] = new Slice(radius, 0, Math.PI);
            shapes[2].setX(radius).setY(3 * radius);
            shapes[2].setRotation(Math.PI / 4);
            shapes[2].setFillColor(ColorName.BLUE.getValue());
            shapes[2].setAlpha(0.5);
            shapes[2].setDraggable(true);
            add(shapes[2]);
        }

        public void resize(int width) {
            console.log("Resizing slice group");
            this.width = width;
            setSizeAndLocation();
        }

        public void setSizeAndLocation() {
            final double radius = width / 4;
            rectangle.setWidth(width);
            rectangle.setHeight(width);

            ((Slice) shapes[0]).setRadius(radius).setStartAngle(0).setEndAngle(Math.PI / 2).setCounterClockwise(true)
                    .setX(radius).setY(radius);

            ((Slice) shapes[1]).setRadius(radius).setStartAngle(0.75 * Math.PI).setEndAngle(3 * Math.PI / 2).setCounterClockwise(true)
                    .setX(3 * radius).setY(radius);

            ((Slice) shapes[2]).setRadius(radius).setStartAngle(0).setEndAngle(Math.PI)
                    .setX(radius).setY(3 * radius);
        }
    }
}
