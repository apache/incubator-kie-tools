/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.dnd;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;

/**
 * The Group representing a column or row during the drag operation of the column or row being moved.
 */
public class GridWidgetDnDProxy extends Group {

    private Rectangle r = new Rectangle(0,
                                        0)
            .setFillColor(ColorName.DARKGRAY)
            .setListening(false)
            .setAlpha(0.5);

    public GridWidgetDnDProxy() {
        add(r);
    }

    public GridWidgetDnDProxy setWidth(final double width) {
        r.setWidth(width);
        return this;
    }

    public GridWidgetDnDProxy setHeight(final double height) {
        r.setHeight(height);
        return this;
    }
}
