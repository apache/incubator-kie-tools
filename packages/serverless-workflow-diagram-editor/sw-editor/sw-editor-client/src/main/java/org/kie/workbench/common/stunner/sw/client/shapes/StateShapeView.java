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

import java.util.Map;

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
    public final static String SHAPE_TITLE_FONT_COLOR = "#929292";
    public final static double SHAPE_TITLE_FONT_SIZE = 12.00;
    public final static String SHAPE_TITLE_FONT_FAMILY = "Open Sans";
    private Text title;

    public StateShapeView(String name) {
        super(new MultiPath()
                      .rect(0, 0, STATE_SHAPE_WIDTH, STATE_SHAPE_HEIGHT)
                      .setCornerRadius(STATE_CORNER_RADIUS));
        initialize(name);
        initializeTitle(name);
    }

    private void initializeTitle(String title) {
        setTitle(title);
        setTitlePosition(VerticalAlignment.MIDDLE, HorizontalAlignment.CENTER, ReferencePosition.INSIDE, Orientation.HORIZONTAL);
        setTitleFontColor(SHAPE_TITLE_FONT_COLOR);
        setTitleFontFamily(SHAPE_TITLE_FONT_FAMILY);
        setTitleFontSize(SHAPE_TITLE_FONT_SIZE);
        setTitleStrokeWidth(0);
        setTitleStrokeAlpha(0);
    }

    @Override
    public StateShapeView setTitle(String name) {
        title.setText(name);
        title.setY(40 - title.getBoundingBox().getHeight() / 2);
        return this;
    }

    @Override
    public StateShapeView setTitleAlpha(double alpha) {
        title.setAlpha(alpha);
        return this;
    }

    @Override
    public double getMarginX() {
        return 80d;
    }

    private void initialize(String name) {
        title = new Text(name)
                .setX(80)
                .setY(35)
                .setStrokeWidth(0)
                .setFillColor("#383B3D")
                .setFontFamily("Open Sans")
                .setTextAlign(CENTER)
                .setTextBaseLine(MIDDLE)
                .setFontSize(12)
                .setListening(false);
        TextLineBreakTruncateWrapper textWrapper = new TextLineBreakTruncateWrapper(title, BoundingBox.fromDoubles(0, 0, 150, 90));

        title.setWrapper(textWrapper);
        addChild(title);
        Group icon = newGroup()
                .setX(45.00)
                .setY(46.00);
        addChild(icon);

        backgroundCircle = newCircle(STATE_SHAPE_ICON_RADIUS)
                .setStrokeColor("#d5d5d5");
        icon.add(backgroundCircle);

        iconImage = newGroup();
        icon.add(iconImage);
    }

    public void setText(String name) {
        title.setText(name);
        title.setVisible(true);
    }

    public void setIconPicture(Picture picture) {
        backgroundCircle.setFillColor("#FFF");

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
                              .setFillColor("#fff")
                              .setStrokeColor("#fff")
                              .setStrokeWidth(2.00));
    }

    public String getIconBackgroundColor() {
        return backgroundCircle.getFillColor();
    }

    public boolean isIconEmpty() {
        return iconImage.getChildNodes().isEmpty();
    }

    // TODO
    @Override
    public StateShapeView setTitleSizeConstraints(Size sizeConstraints) {
        return this;
    }

    // TODO
    @Override
    public StateShapeView setTitlePosition(VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment, ReferencePosition referencePosition, Orientation orientation) {
        return this;
    }

    // TODO
    @Override
    public StateShapeView setMargins(Map<Enum, Double> margins) {
        return this;
    }

    // TODO
    @Override
    public StateShapeView setTitleXOffsetPosition(Double xOffset) {
        return this;
    }

    // TODO
    @Override
    public StateShapeView setTitleYOffsetPosition(Double yOffset) {
        return this;
    }

    @Override
    public StateShapeView setTitleRotation(double degrees) {
        title.setRotationDegrees(degrees);
        return this;
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

    // TODO
    @Override
    public String getTitlePosition() {
        return null;
    }

    // TODO
    @Override
    public String getOrientation() {
        return null;
    }

    // TODO
    @Override
    public String getFontPosition() {
        return null;
    }

    // TODO
    @Override
    public String getFontAlignment() {
        return null;
    }

    @Override
    public StateShapeView moveTitleToTop() {
        title.moveToTop();
        return this;
    }

    @Override
    public void batch() {
        title.refresh();
        title.getLayer().batch();
    }
}
