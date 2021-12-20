/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.palette;

import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.uberfire.client.mvp.UberElement;

public interface BS3PaletteWidgetView extends UberElement<BS3PaletteWidget> {

    void setShapeGlyphDragHandler(ShapeGlyphDragHandler shapeGlyphDragHandler);

    void showDragProxy(String itemId,
                       double x,
                       double y,
                       double width,
                       double height);

    void setBackgroundColor(String backgroundColor);

    void showEmptyView(boolean showEmptyView);

    void add(BS3PaletteWidgetPresenter widget);

    void clear();

    void destroy();
}