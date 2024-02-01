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
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.TextAlign;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;

import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_SHAPE_HEIGHT;
import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_SHAPE_WIDTH;

public class BottomText extends Group {

    public BottomText(String text) {
        setLocation(new Point2D(STATE_SHAPE_WIDTH / 2, STATE_SHAPE_HEIGHT - 4));
        Rectangle border = new Rectangle(60, 10)
                .setFillColor("white")
                .setFillAlpha(0.001)
                .setStrokeAlpha(0.001)
                .setStrokeColor("white")
                .setCornerRadius(9)
                .setListening(true);
        add(border);
        setListening(true);

        Text textElement = new Text(text)
                .setFontSize(11)
                .setStrokeWidth(0)
                .setFillColor(((ColorTheme) StunnerTheme.getTheme()).getBottomTextFillColor())
                .setTextAlign(TextAlign.CENTER)
                .setListening(false);
        add(textElement);
    }
}
