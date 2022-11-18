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

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextLineBreakTruncateWrapper;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;

public class StateShapeView extends ServerlessWorkflowBasicShape<StateShapeView> {

    private Group iconCircle;

    private Circle backgroundCircle;

    public StateShapeView(String name) {
        this(new MultiPath()
                     .rect(0, 0, 250, 90)
                     .setCornerRadius(5.00)
                     .setDraggable(false)
                     .setAlpha(1.00)
                     .setListening(true)
                     .setScale(1.00, 1.00)
                     .setOffset(0.00, 0.00)
                     .setStrokeWidth(2.00)
                     .setFillColor("#fff")
                     .setStrokeColor("#ccc")
                     .setStrokeWidth(2.00)
                     .setStrokeAlpha(1)
                     .moveToBottom()
                     .setListening(true), name);
    }

    public StateShapeView(MultiPath path, String name) {
        super(path);
        initialize(name);
    }

    public void addIconElement(String path) {
        iconCircle.add(new MultiPath(path)
                               .setDraggable(false)
                               .setX(0.00)
                               .setY(0.00)
                               .setAlpha(1.00)
                               .setListening(false)
                               .setScale(1.00, 1.00)
                               .setOffset(0.00, 0.00)
                               .setFillColor("#fff")
                               .setStrokeColor("#fff")
                               .setStrokeWidth(2.00));
    }

    public void setIconBackground(String color) {
        backgroundCircle.setFillColor(color);
    }

    private void initialize(String name) {
        Text title = new Text(name)
                .setX(90)
                .setY(35)
                .setStrokeWidth(0)
                .setFillColor("#383B3D")
                .setFontFamily("Open Sans")
                .setTextAlign(TextAlign.CENTER)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setEventPropagationMode(EventPropagationMode.LAST_ANCESTOR)
                .setFontSize(12)
                .setListening(false);
        TextLineBreakTruncateWrapper textWrapper = new TextLineBreakTruncateWrapper(title, BoundingBox.fromDoubles(0, 0, 160, 44));

        title.setWrapper(textWrapper);

        addChild(title);
        addChild(createIconCircle());
    }

    private Group createIconCircle() {
        Group icon = new Group();
        backgroundCircle = new Circle(25.00)
                .setDraggable(false)
                .setX(45.00)
                .setY(46.00)
                .setAlpha(1.00)
                .setListening(false)
                .setScale(1.00, 1.00)
                .setOffset(0.00, 0.00)
                .setStrokeColor("#d5d5d5");
        icon.add(backgroundCircle);
        iconCircle = new Group()
                .setDraggable(false)
                .setX(0.00)
                .setY(0.00)
                .setAlpha(1.00)
                .setListening(false)
                .setScale(0.35, 0.35)
                .setOffset(52.00, 53.00)
                .setListening(false);

        icon.add(iconCircle);
        icon.setEventPropagationMode(EventPropagationMode.LAST_ANCESTOR);

        return icon;
    }
}
