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


package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;

public class StartShapeView extends ServerlessWorkflowShapeView<StartShapeView> {

    private final static double START_SHAPE_RADIUS = 25;

    public StartShapeView(String title) {
        super(new MultiPath()
                      .circle(START_SHAPE_RADIUS)
                      .setID("start"));
        addChild(new Text(title)
                         .setX(7)
                         .setY(29)
                         .setStrokeWidth(0)
                         .setFillColor("#929292")
                         .setFontFamily("Open Sans")
                         .setFontSize(12)
                         .setListening(false));
    }
}
