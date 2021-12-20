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
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Templated
@Dependent
public class BS3PaletteWidgetViewImpl implements BS3PaletteWidgetView,
                                                 IsElement {

    private ShapeGlyphDragHandler shapeGlyphDragHandler;

    @Inject
    @DataField("kie-palette")
    private Div palette;

    @Inject
    @DataField("list-group")
    private UnorderedList ul;

    private BS3PaletteWidget presenter;

    @Override
    public void init(BS3PaletteWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setShapeGlyphDragHandler(ShapeGlyphDragHandler shapeGlyphDragHandler) {
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void showDragProxy(String itemId,
                              double x,
                              double y,
                              double width,
                              double height) {
        final Glyph glyph = presenter.getShapeDragProxyGlyph(itemId);
        shapeGlyphDragHandler.show(new ShapeGlyphDragHandler.Item() {
                                       @Override
                                       public Glyph getShape() {
                                           return glyph;
                                       }

                                       @Override
                                       public int getWidth() {
                                           return (int) width;
                                       }

                                       @Override
                                       public int getHeight() {
                                           return (int) height;
                                       }
                                   },
                                   (int) x,
                                   (int) y,
                                   new ShapeGlyphDragHandler.Callback() {
                                       @Override
                                       public void onStart(int x,
                                                           int y) {
                                           presenter.onDragStart(itemId,
                                                                 x,
                                                                 y);
                                       }

                                       @Override
                                       public void onMove(int x,
                                                          int y) {
                                           presenter.onDragProxyMove(itemId,
                                                                     (double) x,
                                                                     (double) y);
                                       }

                                       @Override
                                       public void onComplete(int x,
                                                              int y) {
                                           presenter.onDragProxyComplete(itemId,
                                                                         (double) x,
                                                                         (double) y);
                                       }
                                   });
    }

    @Override
    public void add(BS3PaletteWidgetPresenter widget) {
        PortablePreconditions.checkNotNull("widget",
                                           widget);
        addElement(widget.getElement());
    }

    public final void addElement(Node widget) {
        ul.appendChild(widget);
    }

    @Override
    public void clear() {
        DOMUtil.removeAllChildren(ul);
        shapeGlyphDragHandler.clear();
    }

    @Override
    public void destroy() {
        DOMUtil.removeAllChildren(ul);
        DOMUtil.removeAllChildren(palette);
        shapeGlyphDragHandler.destroy();
        presenter = null;
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