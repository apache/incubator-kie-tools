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

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;

public class PanAndZoomExample extends BaseExample implements Example {

    private Shape[] shapes;

    private int total = 50;

    public PanAndZoomExample(final String title) {
        super(title);
        topPadding = 100;
    }

    @Override
    public void run() {

        shapes = new Shape[total];
        for (int i = 0; i < total; i++) {

            final int strokeWidth = 1;

            final Rectangle rectangle = new Rectangle(Math.random() * 220, Math.random() * 220);
            rectangle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            layer.add(rectangle);
            shapes[i] = rectangle;

            Text text = new Text("Press Shift and use your mouse wheel to zoom in and out!");
            text.setX(5);
            text.setY(40);
            text.setFontFamily("Verdana");
            text.setFontSize(24);
            text.setFillColor(ColorName.BLACK);
            layer.add(text);

            text = new Text("Press Cmd(Meta) and use your mouse button to pan!");
            text.setX(5);
            text.setY(75);
            text.setFontFamily("Verdana");
            text.setFontSize(24);
            text.setFillColor(ColorName.BLACK);
            layer.add(text);
        }

        setLocation();

        layer.draw();
    }

    @Override
    public void onResize() {
        super.onResize();

        setLocation();

        layer.batch();
    }

    private void setLocation() {
        for (int i = 0; i < shapes.length; i++) {
            final Shape shape = shapes[i];
            setRandomLocation(shape);
        }
    }
}
