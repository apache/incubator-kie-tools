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
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.PathPartListPathClipper;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextLineBreakTruncateWrapper;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;

public class StateShapeView extends ServerlessWorkflowBasicShape<StateShapeView> {

    private Group iconImage;
    private Circle backgroundCircle;

    private final static double STATE_SHAPE_WIDTH = 250;
    private final static double STATE_SHAPE_HEIGHT = 90;
    public final static double STATE_SHAPE_ICON_RADIUS = 25;

    public StateShapeView(String name) {
        super(new MultiPath()
                      .rect(0, 0, STATE_SHAPE_WIDTH, STATE_SHAPE_HEIGHT)
                      .setCornerRadius(5.00));
        initialize(name);
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
        Group icon = newGroup()
                .setX(45.00)
                .setY(46.00);
        icon.setEventPropagationMode(EventPropagationMode.LAST_ANCESTOR);
        addChild(icon);

        backgroundCircle = newCircle(STATE_SHAPE_ICON_RADIUS)
                .setStrokeColor("#d5d5d5");
        icon.add(backgroundCircle);

        iconImage = newGroup();
        icon.add(iconImage);
    }

    public void setIconPicture(Picture picture) {
        picture.setX(-STATE_SHAPE_ICON_RADIUS); // PathClipper is a circle with center at [0.0], we need to compensate radius
        picture.setY(-STATE_SHAPE_ICON_RADIUS); // on both X and Y axis.

        IPathClipper pathClipper = new PathPartListPathClipper(newCircle(24.4)); // icon radius -0.6 to compensate Stroke
        pathClipper.setActive(true);
        iconImage.setPathClipper(pathClipper);
        iconImage.add(picture);
    }

    public void setSvgIcon(String backgroundColor, String path) {
        backgroundCircle.setFillColor(backgroundColor);

        iconImage.setScale(0.35, 0.35)
                .setOffset(-17.00); // to compensate existing SVG logo paths settings
        iconImage.add(newMultiPath(path)
                              .setFillColor("#fff")
                              .setStrokeColor("#fff")
                              .setStrokeWidth(2.00));
    }

    public boolean isIconEmpty() {
        return iconImage.getChildNodes().isEmpty();
    }
}
