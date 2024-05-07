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
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;

public class HorizontalTextAlignmentExample extends BaseExample implements Example {

    private Rectangle[] markers;
    private Text[] texts;

    private int total = TextAlign.values().length;

    public HorizontalTextAlignmentExample(final String title) {
        super(title);
        topPadding = 100;
    }

    @Override
    public void run() {

        markers = new Rectangle[total];
        texts = new Text[total];
        final int fontSize = 24;

        int i = 0;
        for (TextAlign align : TextAlign.values()) {

            Rectangle marker = new Rectangle(7, 7);
            marker.setFillColor(ColorName.RED.getValue());
            markers[i] = marker;

            Text text = new Text(align.getValue().toUpperCase(), "oblique normal bold", fontSize);
            text.setFillColor(ColorName.CORNFLOWERBLUE.getValue()).setTextAlign(align);
            texts[i] = text;

            layer.add(marker);
            layer.add(text);

            i++;
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
        int x = width / 2;
        int y = height / 4;
        int fontSize = 24;

        for (int i = 0; i < total; i++) {
            Rectangle marker = markers[i];
            Text text = texts[i];

            marker.setX(x).setY(y);
            text.setX(x).setY(y);

            y += fontSize * 2;
        }
    }
}
