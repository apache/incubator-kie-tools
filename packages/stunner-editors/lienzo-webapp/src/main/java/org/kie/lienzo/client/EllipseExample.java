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

import com.ait.lienzo.client.core.shape.Ellipse;
import com.ait.lienzo.shared.core.types.Color;

public class EllipseExample extends BaseShapesExample<Ellipse> implements Example {

    public EllipseExample(String title) {
        super(title);
        this.setPaddings(20, 20, 30, 100);
        numberOfShapes = 40;
        shapes = new Ellipse[numberOfShapes];
    }

    @Override
    public void run() {
        final int strokeWidth = 1;
        for (int i = 0; i < numberOfShapes; i++) {
            shapes[i] = new Ellipse(Math.random() * 160, Math.random() * 80);
            shapes[i].setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            layer.add(shapes[i]);
        }
        setLocation();
    }
}
