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

public class EndShapeView extends ServerlessWorkflowShapeView<EndShapeView> {

    private final static int END_SHAPE_SIZE = 46;

    public EndShapeView(String title) {
        super(new MultiPath()
                      .rect(0, 0, END_SHAPE_SIZE, END_SHAPE_SIZE)
                      .setID("end"));
        addChild(new Text(title)
                         .setX(9)
                         .setY(29)
                         .setStrokeWidth(0)
                         .setFillColor("#929292")
                         .setFontFamily("Open Sans")
                         .setFontSize(12)
                         .setListening(false));
    }
}
