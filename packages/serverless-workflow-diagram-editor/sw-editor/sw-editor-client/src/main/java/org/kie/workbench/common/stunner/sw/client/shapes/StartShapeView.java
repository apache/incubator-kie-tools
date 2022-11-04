/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.shape.MultiPath;

public class StartShapeView extends ServerlessWorkflowBasicShape<StartShapeView> {

    public StartShapeView() {
        super(new MultiPath()
                      .circle(25)
                      .setDraggable(false)
                      .setID("start")
                      .setAlpha(1.00)
                      .setListening(true)
                      .setOffset(0.00, 0.00)
                      .setFillColor("#ffffff")
                      .setStrokeColor("#ccc")
                      .setStrokeWidth(2.00));
        setTitle("Start");
        setTitleXOffsetPosition(2.0);
        setTitleYOffsetPosition(17.0);
        setTitlePosition(VerticalAlignment.MIDDLE, HorizontalAlignment.CENTER, ReferencePosition.INSIDE, Orientation.HORIZONTAL);
        setTitleFontColor("#929292");
        setTitleFontFamily("Open Sans");
        setTitleFontSize(12);
        setTitleStrokeWidth(0);
        setTitleStrokeAlpha(0);
        setTitleStrokeColor("#929292");
        isTitleListening(false);
    }
}
