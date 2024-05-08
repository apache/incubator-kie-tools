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
import com.ait.lienzo.shared.core.types.Color;

public class ColorsAndTransparencyExample extends BaseExample implements Example {

    private Shape[] shapes;

    private int total = 10;

    public ColorsAndTransparencyExample(final String title) {
        super(title);
    }

    @Override
    public void run() {
        shapes = new Shape[total];

        // create the rectangles
        for (int i = 0; i < total; i++) {
            final Rectangle rectangle = new Rectangle(120, 60);
            rectangle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(1).setFillColor(Color.getRandomHexColor())
                    .setAlpha(Math.random() * 1).setDraggable(true);
            layer.add(rectangle);
            shapes[i] = rectangle;
        }

        setLocation();
    }

    @Override
    public void onResize() {
        super.onResize();

        setLocation();

        layer.batch();
    }

    private void setLocation() {
        int xOffSet = 40;
        int yOffSet = 40;

        int x = width / 2 - (xOffSet * total / 2);
        int y = height / 2 - (yOffSet * total / 2);

        for (int j = 0; j < shapes.length; j++) {
            final Shape shape = shapes[j];
            shape.setX(x).setY(y);
            x += xOffSet;
            y += yOffSet;
        }
    }
}
