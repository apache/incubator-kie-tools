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


package org.kie.workbench.common.stunner.sw.client.shapes.icons;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;

import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_SHAPE_HEIGHT;
import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_SHAPE_WIDTH;

public class BottomDepiction extends Group {

    private final HandlerRegistration mouseClickHandler;

    public BottomDepiction(String icon) {
        setLocation(new Point2D(STATE_SHAPE_WIDTH / 2 - 8, STATE_SHAPE_HEIGHT - 20));
        Rectangle border = new Rectangle(20, 20)
                .setFillColor("white")
                .setFillAlpha(0.001)
                .setStrokeAlpha(0.001)
                .setStrokeColor("white")
                .setCornerRadius(9)
                .setListening(true);
        add(border);
        setListening(true);

        MultiPath multiPath = new MultiPath(icon)
                .setStrokeWidth(1)
                .setStrokeColor(((ColorTheme) StunnerTheme.getTheme()).getStaticIconFillColor())
                .setFillColor(((ColorTheme) StunnerTheme.getTheme()).getStaticIconFillColor())
                .setListening(false);
        add(multiPath);

        mouseClickHandler = border.addNodeMouseClickHandler(
                event -> this.getParent().asGroup().getChildren().get(0).fireEvent(event)
        );
    }

    @Override
    public void destroy() {
        super.destroy();
        mouseClickHandler.removeHandler();
    }
}
