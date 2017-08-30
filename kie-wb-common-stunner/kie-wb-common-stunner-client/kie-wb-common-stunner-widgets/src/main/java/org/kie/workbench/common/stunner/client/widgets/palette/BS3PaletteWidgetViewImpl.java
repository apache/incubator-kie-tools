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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.uberfire.commons.validation.PortablePreconditions;

@Templated
@Dependent
public class BS3PaletteWidgetViewImpl implements BS3PaletteWidgetView,
                                                 IsElement {

    private ShapeGlyphDragHandler shapeGlyphDragHandler;

    private BS3PaletteWidget presenter;

    @Inject
    @DataField("kie-palette")
    private Div palette;

    @Inject
    @DataField("list-group")
    private UnorderedList ul;

    @Override
    public void init(BS3PaletteWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setShapeGlyphDragHandler(ShapeGlyphDragHandler shapeGlyphDragHandler) {
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
    }

    @Override
    public void showDragProxy(String itemId,
                              double x,
                              double y,
                              double witdth,
                              double height) {
        final Glyph glyph = presenter.getShapeGlyph(itemId);
        presenter.onDragStart(itemId,
                              x,
                              y);

        shapeGlyphDragHandler.show(glyph,
                                   x,
                                   y,
                                   witdth,
                                   height,
                                   new ShapeGlyphDragHandler.Callback() {

                                       @Override
                                       public void onMove(final double x,
                                                          final double y) {
                                           presenter.onDragProxyMove(itemId,
                                                                     x,
                                                                     y);
                                       }

                                       @Override
                                       public void onComplete(final double x,
                                                              final double y) {
                                           presenter.onDragProxyComplete(itemId,
                                                                         x,
                                                                         y);
                                       }
                                   });
    }

    @Override
    public void add(DefinitionPaletteCategoryWidget widget) {
        PortablePreconditions.checkNotNull("widget",
                                           widget);
        ul.appendChild(widget.getElement());
    }

    @Override
    public void clear() {
        DOMUtil.removeAllChildren(ul);
    }

    @Override
    public void destroy() {
        clear();
    }

    @Override
    public void setBackgroundColor(String backgroundColor) {
        palette.getStyle().setProperty("background-color",
                                       backgroundColor);
    }

    @Override
    public void showEmptyView(boolean showEmptyView) {
        palette.setHidden(showEmptyView);
        ul.setHidden(showEmptyView);
    }
}
