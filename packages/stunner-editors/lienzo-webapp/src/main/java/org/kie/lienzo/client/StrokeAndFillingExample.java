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
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.TextAlign;

public class StrokeAndFillingExample extends BaseExample implements Example {

    private Circle[] circles;
    private Text[] texts;

    private int total = 3;

    public StrokeAndFillingExample(final String title) {
        super(title);
    }

    @Override
    public void run() {
        final String strokeColor = Color.getRandomHexColor();
        final String fillColor = Color.getRandomHexColor();
        Text text;

        circles = new Circle[total];
        texts = new Text[total];

        for (int i = 0; i < total; i++) {

            final Circle circle = new Circle(60);
            circles[i] = circle;

            if (i == 0) {
                text = new Text("Stroke", "oblique normal bold", 24).setTextAlign(TextAlign.CENTER).setStrokeColor(strokeColor)
                        .setStrokeWidth(2);
                texts[i] = text;
                circle.setStrokeColor(strokeColor).setStrokeWidth(2);
            } else if (i == 1) {
                text = new Text("Fill", "oblique normal bold", 24).setTextAlign(TextAlign.CENTER).setFillColor(fillColor);
                texts[i] = text;
                circle.setFillColor(fillColor);
            } else if (i == 2) {
                text = new Text("Stroke & Fill", "oblique normal bold", 24).setTextAlign(TextAlign.CENTER)
                        .setStrokeColor(strokeColor).setStrokeWidth(2).setFillColor(fillColor);
                texts[i] = text;
                circle.setFillColor(fillColor).setStrokeColor(strokeColor).setStrokeWidth(2);
            } else {
                throw new RuntimeException();
            }
            layer.add(circle);
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
        int x = (int) (width * .25);
        int y = (int) (height * .50);

        for (int i = 0; i < circles.length; i++) {
            int xOffSet = x * (i + 1);
            Shape shape = circles[i];
            shape.setX(xOffSet).setY(y);

            final Text text = texts[i];
            text.setX(xOffSet).setY(y - 70);
        }
    }
}
