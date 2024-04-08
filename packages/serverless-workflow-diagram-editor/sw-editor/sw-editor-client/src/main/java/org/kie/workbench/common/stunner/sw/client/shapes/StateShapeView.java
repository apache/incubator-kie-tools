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

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.PathPartListPathClipper;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextLineBreakTruncateWrapper;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;

import static com.ait.lienzo.shared.core.types.TextAlign.CENTER;
import static com.ait.lienzo.shared.core.types.TextBaseLine.MIDDLE;

public class StateShapeView
        extends ServerlessWorkflowShapeView<StateShapeView>
        implements HasTitle<StateShapeView> {

    private Group iconImage;
    private Circle backgroundCircle;
    public final static double STATE_SHAPE_WIDTH = 250;
    public final static double STATE_SHAPE_HEIGHT = 90;
    public final static double STATE_SHAPE_ICON_RADIUS = 25;
    public final static double STATE_CORNER_RADIUS = 5.00;
    private final static double LABEL_WIDTH = 150;
    private final static double LABEL_POSITION_X = 80;
    private final static double LABEL_POSITION_Y = 5;
    private final static double FONT_SIZE = 12;
    private Text title;

    public StateShapeView(String name) {
        super(new MultiPath()
                      .rect(0, 0, STATE_SHAPE_WIDTH, STATE_SHAPE_HEIGHT)
                      .setCornerRadius(STATE_CORNER_RADIUS));
        initialize(name);
    }

    @Override
    public StateShapeView setTitle(String name) {
        title.setText(name);
        // Text widget can't be centered vertically, so we need to manually set Y position
        title.setY(calculateTitleCenterY());
        return this;
    }

    private double calculateTitleCenterY() {
        return (STATE_SHAPE_HEIGHT / 2) - (FONT_SIZE / 2) - (title.getBoundingBox().getHeight() / 2);
    }

    @Override
    public StateShapeView setTitleAlpha(double alpha) {
        title.setAlpha(alpha);
        return this;
    }

    private void initialize(String name) {
        title = new Text(name)
                .setX(LABEL_POSITION_X)
                // Y is set based on the size of the Text widget after it is initialized, see calculateTitleCenterY()
                .setStrokeWidth(0)
                .setFillColor(((ColorTheme) StunnerTheme.getTheme()).getShapeTextColor())
                .setFontFamily("Open Sans")
                .setTextAlign(CENTER)
                .setTextBaseLine(MIDDLE)
                .setFontSize(FONT_SIZE)
                .setListening(false);
        TextLineBreakTruncateWrapper textWrapper = new TextLineBreakTruncateWrapper(title, BoundingBox.fromDoubles(0, 0, LABEL_WIDTH, STATE_SHAPE_HEIGHT));

        title.setWrapper(textWrapper);
        addChild(title);
        Group icon = newGroup()
                .setX(45.00)
                .setY(46.00);
        addChild(icon);

        backgroundCircle = newCircle(STATE_SHAPE_ICON_RADIUS)
                .setStrokeColor(((ColorTheme) StunnerTheme.getTheme()).getBackgroundIconCircleStrokeColor());
        icon.add(backgroundCircle);

        iconImage = newGroup();
        icon.add(iconImage);
    }

    public void setIconPicture(Picture picture) {
        backgroundCircle.setFillColor(((ColorTheme) StunnerTheme.getTheme()).getBackgroundIconCircleFillColor());

        picture.setX(-STATE_SHAPE_ICON_RADIUS); // PathClipper is a circle with center at [0.0], we need to compensate radius
        picture.setY(-STATE_SHAPE_ICON_RADIUS); // on both X and Y axis.

        IPathClipper pathClipper = new PathPartListPathClipper(newCircle(24.4)); // icon radius -0.6 to compensate Stroke
        pathClipper.setActive(true);
        iconImage.setPathClipper(pathClipper);
        iconImage.add(picture);
    }

    public void setSvgIcon(String backgroundColor, String path) {
        backgroundCircle.setFillColor(backgroundColor);

        iconImage.add(newMultiPath(path)
                              .setScale(0.35)
                              .setX(-11)
                              .setY(-11)
                              .setFillColor(((ColorTheme) StunnerTheme.getTheme()).getIconPictureFillColor())
                              .setStrokeColor(((ColorTheme) StunnerTheme.getTheme()).getIconPictureStrokeColor())
                              .setStrokeWidth(1.00));
    }

    public String getIconBackgroundColor() {
        return backgroundCircle.getFillColor();
    }

    public boolean isIconEmpty() {
        return iconImage.getChildNodes().isEmpty();
    }

    @Override
    public StateShapeView setTitleFontFamily(String fontFamily) {
        title.setFontFamily(fontFamily);
        return this;
    }

    @Override
    public StateShapeView setTitleFontSize(double fontSize) {
        title.setFontSize(fontSize);
        return this;
    }

    @Override
    public StateShapeView setTitleFontColor(String fillColor) {
        title.setFillColor(fillColor);
        return this;
    }

    @Override
    public StateShapeView setTitleStrokeWidth(double strokeWidth) {
        title.setStrokeWidth(strokeWidth);
        return this;
    }

    @Override
    public double getTextboxWidth() {
        return LABEL_WIDTH;
    }

    @Override
    public double getTextboxHeight() {
        return STATE_SHAPE_HEIGHT;
    }

    @Override
    public StateShapeView setTitleStrokeColor(String color) {
        title.setStrokeColor(color);
        return this;
    }

    @Override
    public String getTitleFontFamily() {
        return title.getFontFamily();
    }

    @Override
    public double getTitleFontSize() {
        return title.getFontSize();
    }

    @Override
    public Point2D getTitlePosition() {
        return new Point2D(LABEL_POSITION_X, LABEL_POSITION_Y);
    }

    @Override
    public StateShapeView moveTitleToTop() {
        title.moveToTop();
        return this;
    }

    @Override
    public void batch() {
        title.refresh();
        if (title.getLayer() != null) {
            title.getLayer().batch();
        }
    }
}
