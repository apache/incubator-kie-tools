/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.components.palette.widget;

import java.util.Objects;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.uberfire.commons.validation.PortablePreconditions;

@Templated
@Dependent
public class DMNPaletteWidgetViewImpl implements DMNPaletteWidgetView,
                                                 IsElement {

    private ShapeGlyphDragHandler shapeGlyphDragHandler;

    private BS3PaletteWidget<DefinitionsPalette> presenter;

    @Inject
    @DataField("kie-palette")
    private Div palette;

    @Inject
    @DataField("list-group")
    private UnorderedList ul;

    private DragProxy itemDragProxy;

    public DMNPaletteWidgetViewImpl() {
        //CDI proxy
    }

    @Inject
    public DMNPaletteWidgetViewImpl(final ShapeGlyphDragHandler shapeGlyphDragHandler) {
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
    }

    @Override
    public void init(final BS3PaletteWidget<DefinitionsPalette> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showDragProxy(final String itemId,
                              final double x,
                              final double y,
                              final double width,
                              final double height) {
        final Glyph glyph = presenter.getShapeGlyph(itemId);
        itemDragProxy = shapeGlyphDragHandler.show(new ShapeGlyphDragHandler.Item() {
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
                                                   new DragProxyCallback() {
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
    public void add(final DMNPaletteItemWidget widget) {
        PortablePreconditions.checkNotNull("widget",
                                           widget);
        ul.appendChild(widget.getElement());
    }

    @Override
    public void clear() {
        DOMUtil.removeAllChildren(ul);
        if (Objects.nonNull(itemDragProxy)) {
            itemDragProxy.clear();
        }
    }

    @Override
    public void destroy() {
        clear();
        if (Objects.nonNull(itemDragProxy)) {
            itemDragProxy.destroy();
        }
    }

    @Override
    public void setBackgroundColor(String backgroundColor) {
        palette.getStyle().setProperty("background-color",
                                       backgroundColor);
    }

    @Override
    public void showEmptyView(boolean showEmptyView) {
        palette.setHidden(showEmptyView);
    }
}
