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

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.Color;

public class ScaledTextExample extends BaseExample implements Example {

    private Text[] texts;

    private int total = 4;

    public ScaledTextExample(final String title) {
        super(title);
        topPadding = 100;
    }

    @Override
    public void run() {
        texts = new Text[total];
        final int fontSize = 24;
        double scale[] = new double[4];
        double s = 1.0;
        for (int i = 1; i < total; i++) {
            scale[i] = s;
            s = s + 0.1;
        }

        for (int i = 0; i < scale.length; i++) {
            Text text = new Text("Scaled Fonts", "Verdana", 70);
            text.setShadow(new Shadow("black", 7, 7, 7)).setScale(scale[i], scale[i]).setDraggable(true)
                    .setFillColor(Color.getRandomHexColor()).setDraggable(true);
            layer.add(text);
            texts[i] = text;
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
        int x = width / 5;
        int y = height / 5;

        for (int i = 0; i < total; i++) {
            texts[i].setX(x).setY(y);
            y += 70;
        }
    }
}
