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

import com.ait.lienzo.client.core.shape.RegularPolygon;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.LineJoin;
import org.kie.lienzo.client.util.Util;

public class PolygonsExample extends BaseShapesExample<RegularPolygon> implements Example {

    public PolygonsExample(String title) {
        super(title);
        this.setPaddings(20, 20, 30, 100);
        numberOfShapes = 40;
        shapes = new RegularPolygon[numberOfShapes];
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfShapes; i++) {
            final int strokeWidth = Util.randomNumber(2, 10);

            shapes[i] = new RegularPolygon(8, 60);
            shapes[i].setShadow(new Shadow("black", 6, 6, 6)).setFillColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth)
                    .setStrokeColor(Color.getRandomHexColor()).setLineJoin(LineJoin.ROUND)
                    .setDraggable(true);
            layer.add(shapes[i]);
        }
        setLocation();
    }
}
