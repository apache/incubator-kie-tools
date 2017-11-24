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

package org.kie.workbench.common.dmn.client.widgets.grid;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class BaseExpressionGridRenderer extends BaseGridRenderer {

    protected static final int HEADER_HEIGHT = 64;

    protected static final int HEADER_ROW_HEIGHT = 64;

    protected final boolean hideHeader;

    public BaseExpressionGridRenderer(final boolean hideHeader) {
        super(new BaseExpressionGridTheme() {

            @Override
            public Rectangle getHeaderBackground(final GridColumn<?> column) {
                final Rectangle r = super.getHeaderBackground(column);
                if (hideHeader) {
                    r.setVisible(false);
                }
                return r;
            }

            @Override
            public Rectangle getHeaderLinkBackground(final GridColumn<?> column) {
                final Rectangle r = super.getHeaderLinkBackground(column);
                if (hideHeader) {
                    r.setVisible(false);
                }
                return r;
            }

            @Override
            public MultiPath getHeaderGridLine() {
                final MultiPath m = super.getHeaderGridLine();
                if (hideHeader) {
                    m.setVisible(false);
                }
                return m;
            }

            @Override
            public Text getHeaderText() {
                final Text t = super.getHeaderText();
                if (hideHeader) {
                    t.setVisible(false);
                }
                return t;
            }

            @Override
            public Line getGridHeaderBodyDivider() {
                final Line l = super.getGridHeaderBodyDivider();
                if (hideHeader) {
                    l.setVisible(false);
                }
                return l;
            }
        });
        this.hideHeader = hideHeader;
    }

    @Override
    public double getHeaderHeight() {
        return hideHeader ? 0.0 : HEADER_HEIGHT;
    }

    @Override
    public double getHeaderRowHeight() {
        return hideHeader ? 0.0 : HEADER_ROW_HEIGHT;
    }

    @Override
    public Group renderHeader(final GridData model,
                              final GridHeaderRenderContext context,
                              final BaseGridRendererHelper rendererHelper,
                              final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        if (hideHeader) {
            return new Group();
        }
        return super.renderHeader(model,
                                  context,
                                  rendererHelper,
                                  renderingInformation);
    }

    @Override
    public Group renderHeaderBodyDivider(final double width) {
        if (hideHeader) {
            return new Group();
        }
        return super.renderHeaderBodyDivider(width);
    }

    @Override
    public Group renderSelector(final double width,
                                final double height,
                                final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final Group g = new Group();
        final MultiPath selector = theme.getSelector()
                .M(0.5,
                   0.5)
                .L(0.5,
                   height)
                .L(width,
                   height)
                .L(width,
                   0.5)
                .L(0.5,
                   0.5)
                .Z()
                .setListening(false);
        g.add(selector);
        return g;
    }
}
