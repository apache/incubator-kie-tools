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

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.LineCap;

public class LinesCapExample extends BaseShapesExample<Line> implements Example {

    public LinesCapExample(String title) {
        super(title);
        numberOfShapes = 3;
        shapes = new Line[numberOfShapes];
    }

    @Override
    public void run() {
        shapes[0] = new Line();
        shapes[0].setStrokeWidth(30).setFillColor(Color.getRandomHexColor()).setStrokeColor(Color.getRandomHexColor()).setLineCap(LineCap.BUTT);
        layer.add(shapes[0]);

        shapes[1] = new Line();
        shapes[1].setStrokeWidth(30).setFillColor(Color.getRandomHexColor()).setStrokeColor(Color.getRandomHexColor()).setLineCap(LineCap.ROUND);
        layer.add(shapes[1]);

        shapes[2] = new Line();
        shapes[2].setStrokeWidth(30).setFillColor(Color.getRandomHexColor()).setStrokeColor(Color.getRandomHexColor()).setLineCap(LineCap.SQUARE);
        layer.add(shapes[2]);
        setLocation();
    }

    @Override
    protected void setLocation() {
        console.log("Setting Location for LinesCap ..>>");

        final int middleX = width / 2;
        final int middleY = height / 2;

        ((Line) shapes[0]).setPoint2DArray(Point2DArray.fromArrayOfPoint2D(new Point2D(middleX - middleX / 2, middleY - 100), new Point2D(middleX + middleX / 2, middleY - 100)));
        ((Line) shapes[1]).setPoint2DArray(Point2DArray.fromArrayOfPoint2D(new Point2D(middleX - middleX / 2, middleY), new Point2D(middleX + middleX / 2, middleY)));
        ((Line) shapes[2]).setPoint2DArray(Point2DArray.fromArrayOfPoint2D(new Point2D(middleX - middleX / 2, middleY + 100), new Point2D(middleX + middleX / 2, middleY + 100)));
    }
}
