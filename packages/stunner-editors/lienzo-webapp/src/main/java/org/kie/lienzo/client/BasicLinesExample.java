/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.lienzo.client;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.shape.AbstractMultiPointShape;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.PolyMorphicLine;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.Direction;
import com.google.gwt.dom.client.Style;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.Node;
import org.kie.lienzo.client.util.WiresUtils;

import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.OFFSET;
import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.POINT;

public class BasicLinesExample extends BaseExample implements Example {

    private static final String LINE_POLYLINE = "polyline";
    private static final String LINE_ORTHOGONAL = "orthogonal";
    private static final String LINE_POLYMORPHIC = "polymorphic";

    private IDirectionalMultiPointShape line;
    private Group pointsContainer;
    private HTMLTextAreaElement pointsElement;
    private HTMLSelectElement lineTypeElement;
    private HTMLSelectElement headDirectionElement;
    private HTMLSelectElement tailDirectionElement;
    private HTMLButtonElement refreshButton;
    private HTMLButtonElement logPointsButton;
    private HTMLButtonElement showHandlesButton;
    private HTMLButtonElement showPointsButton;
    private HTMLButtonElement hideCPButton;
    private HTMLButtonElement upButton;
    private HTMLButtonElement downButton;
    private HTMLButtonElement leftButton;
    private HTMLButtonElement rightButton;

    public BasicLinesExample(final String title) {
        super(title);
    }

    private static final int TYPE_POLYLINE = 0;
    private static final int TYPE_ORTHOGONAL = 1;
    private static final int TYPE_POLYMORPHIC = 2;

    @Override
    public void init(LienzoPanel panel,
                     HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Style.Display.INLINE.getCssName();

        pointsElement = createText("50 50\n100 50", this::onPointsTextChanged);
        // pointsElement = createText("50 50\n100 50\n100 100\n150 100", this::onPointsTextChanged);
        // pointsElement = createText("90 88\n100 88\n100 100\n110 100", this::onPointsTextChanged);
        // pointsElement = createText("50 50\n100 50\n100 100\n150 100\n150 150\n200 150", this::onPointsTextChanged);
        // pointsElement = createText("50 50\n100 100\n150 50\n200 50\n200 100", this::onPointsTextChanged);
        pointsElement.rows = 6;
        addToHeader(pointsElement);

        lineTypeElement = createSelect(lineTypesMap(), this::onLineTypeChanged);
        lineTypeElement.selectedIndex = TYPE_POLYMORPHIC;
        addToHeader(lineTypeElement);

        headDirectionElement = createSelect(directionsMap(), this::onHeadDirectionChanged);
        headDirectionElement.selectedIndex = 3;
        addToHeader(headDirectionElement);

        tailDirectionElement = createSelect(directionsMap(), this::onTailDirectionChanged);
        tailDirectionElement.selectedIndex = 4;
        addToHeader(tailDirectionElement);

        refreshButton = createButton("Refresh", this::onRefreshButtonClick);
        addToHeader(refreshButton);

        logPointsButton = createButton("Log", this::onLogPointsButtonClick);
        addToHeader(logPointsButton);

        showPointsButton = createButton("POINTS", this::onShowPointsButton);
        addToHeader(showPointsButton);

        showHandlesButton = createButton("OFFSET", this::onShowOffsetHandlesButton);
        addToHeader(showHandlesButton);

        hideCPButton = createButton("Hide CPs", this::onHidePointHandlesButton);
        addToHeader(hideCPButton);

        upButton = createButton("Up", this::onUpButtonClick);
        addToHeader(upButton);

        downButton = createButton("Down", this::onDownButtonClick);
        addToHeader(downButton);

        leftButton = createButton("Left", this::onLeftButtonClick);
        addToHeader(leftButton);

        rightButton = createButton("Right", this::onRightButtonClick);
        addToHeader(rightButton);

        pointsContainer = new Group();
        layer.add(pointsContainer);
    }

    private static boolean SHOW_HEADER = true;

    private void addToHeader(Node newChild) {
        if (SHOW_HEADER) {
            topDiv.appendChild(newChild);
        }
    }

    @Override
    public void run() {
        refresh();
    }

    private void onUpButtonClick() {
        testUpdatePx(0, -1);
    }

    private void onDownButtonClick() {
        testUpdatePx(0, +1);
    }

    private void onLeftButtonClick() {
        testUpdatePx(-1, 0);
    }

    private void onRightButtonClick() {
        testUpdatePx(1, 0);
    }

    private void testUpdatePx(double dx, double dy) {
        if (line instanceof AbstractMultiPointShape) {
            int lastIndex = ((AbstractMultiPointShape) line).getPoints().size() - 1;
            Point2D p = ((AbstractMultiPointShape) line).getPoints().get(lastIndex);
            ((AbstractMultiPointShape) line).updatePointAtIndex(lastIndex, p.getX() + dx, p.getY() + dy);
            layer.draw();
            onLogPointsButtonClick();
        }
    }

    private void testLogPoints() {
        Point2DArray points = line.getPoint2DArray();
        DomGlobal.console.log("POINTS [" + points + "]");
    }

    private IControlHandleList pointHandles;

    private void onShowOffsetHandlesButton() {
        onShowPointHandlesButton(OFFSET);
    }

    private void onShowPointsButton() {
        onShowPointHandlesButton(POINT);
    }

    private void onShowPointHandlesButton(IControlHandle.ControlHandleType type) {
        clearPointsHandles();
        pointHandles = line.getControlHandles(type).get(type);
        pointHandles.show();
    }

    private void onHidePointHandlesButton() {
        clearPointsHandles();
        layer.batch();
    }

    private void clearPointsHandles() {
        pointsContainer.removeAll();
        if (null != pointHandles) {
            pointHandles.destroy();
            pointHandles = null;
        }
    }

    private void onLogPointsButtonClick() {
        pointsContainer.removeAll();
        testLogPoints();
        layer.draw();
    }

    private void onRefreshButtonClick() {
        refresh();
    }

    private void onPointsTextChanged() {
        refresh();
    }

    private void onLineTypeChanged(String value) {
        refresh();
    }

    private void onHeadDirectionChanged(String value) {
        refresh();
    }

    private void onTailDirectionChanged(String value) {
        refresh();
    }

    private void refresh() {
        layer.removeAll();
        layer.clear();
        layer.add(pointsContainer);
        if (LINE_ORTHOGONAL.equals(lineTypeElement.value)) {
            refreshOrthogonalLine();
        } else if (LINE_POLYMORPHIC.equals(lineTypeElement.value)) {
            refreshPolymorphicLine();
        } else if (LINE_POLYLINE.equals(lineTypeElement.value)) {
            refreshPolyLine();
        }
        layer.draw();
        onLogPointsButtonClick();
    }

    private void refreshPolyLine() {
        Point2DArray points = parsePoints();
        Direction headDirection = parseHeadDirection();
        Direction tailDirection = parseTailDirection();
        PolyLine line = WiresUtils.createPolyline(points);
        this.line = line;
        line.setHeadDirection(headDirection);
        line.setTailDirection(tailDirection);
        layer.add(line);
    }

    private void refreshOrthogonalLine() {
        Point2DArray points = parsePoints();
        Direction headDirection = parseHeadDirection();
        Direction tailDirection = parseTailDirection();
        OrthogonalPolyLine line = WiresUtils.createOrthogonalPolyline(points);
        this.line = line;
        line.setHeadDirection(headDirection);
        line.setTailDirection(tailDirection);
        layer.add(line);
    }

    private void refreshPolymorphicLine() {
        Point2DArray points = parsePoints();
        Direction headDirection = parseHeadDirection();
        Direction tailDirection = parseTailDirection();
        PolyMorphicLine line = WiresUtils.createPolymorphicLine(points);
        this.line = line;
        line.setHeadDirection(headDirection);
        line.setTailDirection(tailDirection);
        layer.add(line);
    }

    private Direction parseHeadDirection() {
        String value = headDirectionElement.value;
        return Direction.lookup(value);
    }

    private Direction parseTailDirection() {
        String value = tailDirectionElement.value;
        return Direction.lookup(value);
    }

    private Point2DArray parsePoints() {
        Point2DArray points = new Point2DArray();
        String[] lines = pointsElement.value.split("\n");
        for (String line : lines) {
            String[] point = line.split("\\s");
            if (point.length == 2) {
                double x = Double.parseDouble(point[0]);
                double y = Double.parseDouble(point[1]);
                points.push(new Point2D(x, y));
            }
        }
        return points;
    }

    private void fillPoints() {
        String raw = "";
        Point2DArray points = null;
        for (int i = 0; i < points.size(); i++) {
            Point2D point = points.get(i);
            String x = Double.valueOf(point.getX()).toString();
            String y = Double.valueOf(point.getY()).toString();
            raw += x + " " + y + "\n";
        }
        pointsElement.textContent = raw;
        onPointsTextChanged();
    }

    private static Map<String, String> directionsMap() {
        Map<String, String> options = new HashMap<>();
        options.put(Direction.NONE.getValue(), Direction.NONE.getValue());
        options.put(Direction.NORTH.getValue(), Direction.NORTH.getValue());
        options.put(Direction.SOUTH.getValue(), Direction.SOUTH.getValue());
        options.put(Direction.EAST.getValue(), Direction.EAST.getValue());
        options.put(Direction.WEST.getValue(), Direction.WEST.getValue());
        return options;
    }

    private static Map<String, String> lineTypesMap() {
        Map<String, String> options = new HashMap<>();
        options.put(TYPE_POLYLINE + "_" + LINE_POLYLINE, LINE_POLYLINE);
        options.put(TYPE_ORTHOGONAL + "_" + LINE_ORTHOGONAL, LINE_ORTHOGONAL);
        options.put(TYPE_POLYMORPHIC + "_" + LINE_POLYMORPHIC, LINE_POLYMORPHIC);
        return options;
    }
}