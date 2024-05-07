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

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.shared.core.types.Color;

public class DragCirclesExample extends BaseExample implements Example {

    private Circle[] circles;
    int total = 1000;

    public DragCirclesExample(final String title) {
        super(title);
    }

    @Override
    public void run() {
        this.circles = new Circle[total];

        for (int i = 0; i < total; i++) {

            final Circle circle = new Circle(10);
            circles[i] = circle;
            circle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            setRandomLocation(circle);

            layer.add(circle);
        }
    }

    @Override
    public void onResize() {
        super.onResize();

        for (int i = 0; i < total; i++) {

            final Circle circle = circles[i];
            setRandomLocation(circle);
        }

        layer.batch();
    }
}
