/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.palette.view;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.AbstractLienzoPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element.LienzoGlyphPaletteItemView;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element.LienzoPaletteElementView;
import org.kie.workbench.common.stunner.core.client.components.palette.view.AbstractPaletteView;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteGrid;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;
import org.kie.workbench.common.stunner.lienzo.palette.AbstractPalette;
import org.kie.workbench.common.stunner.lienzo.palette.HoverPalette;

public abstract class AbstractLienzoPaletteView<V extends LienzoPaletteView>
        extends AbstractPaletteView<V, Layer, LienzoPaletteElementView>
        implements LienzoPaletteView<V, LienzoPaletteElementView> {

    protected double animationDuration = 200;
    protected AbstractLienzoPalette presenter;
    protected AbstractPalette<? extends AbstractPalette> palette;
    protected IPrimitive<?> colExpButton;
    protected final HandlerRegistrationImpl handlerRegistrationManager = new HandlerRegistrationImpl();

    protected abstract AbstractPalette<? extends AbstractPalette> buildPalette();

    protected AbstractPalette<? extends AbstractPalette> getPalette() {
        if (null == palette) {
            this.palette = buildPalette();
            initPaletteCallbacks();
        }
        return palette;
    }

    public void setPresenter(final AbstractLienzoPalette presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void doClear() {
        if (getPalette().isVisible() && getPalette().getAlpha() > 0) {
            getPalette().setAlpha(0);
            draw();
        }
    }

    protected boolean isExpandable() {
        return presenter.isExpandable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V attach(final Layer layer) {
        if (null == colExpButton && isExpandable()) {
            colExpButton = createExpandCollapseButton();
            layer.add(colExpButton);
        }
        layer.add(getPalette());
        return (V) this;
    }

    public Layer getLayer() {
        return getPalette().getLayer();
    }

    public void draw() {
        getPalette().redraw();
    }

    @SuppressWarnings("unchecked")
    public V show() {
        if (null == getPalette().getParent()) {
            throw new IllegalStateException("Palette must be attached to a layer before calling #show.");
        }
        if (!items.isEmpty()) {
            final AbstractPalette.Item[] primitives = new AbstractPalette.Item[items.size()];
            int _x = 0;
            for (final LienzoPaletteElementView paletteItemView : items) {
                final AbstractPalette.Item i = buildLienzoPaletteItem(paletteItemView);
                primitives[_x] = i;
                _x++;
            }
            double paletteStartY = 0;
            if (null != colExpButton && isExpandable()) {
                colExpButton.setX(x + getGrid().getPadding());
                colExpButton.setY(y);
                paletteStartY = colExpButton.getBoundingBox().getHeight() + getGrid().getPadding();
            }
            getPalette().setX(x);
            getPalette().setY(paletteStartY + y);
            getPalette().setRows(getGrid().getRows());
            getPalette().setColumns(getGrid().getColumns());
            getPalette().setIconSize(getGrid().getIconSize());
            getPalette().setPadding(getGrid().getPadding());
            getPalette().build(primitives);
            getPalette().setAlpha(0);
            getPalette().animate(AnimationTweener.LINEAR,
                                 AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)),
                                 animationDuration);
            draw();
        } else {
            clear();
        }
        return (V) this;
    }

    @Override
    public V hide() {
        getPalette().clearItems();
        getLayer().batch();
        return (V) this;
    }

    protected AbstractPalette.Item buildLienzoPaletteItem(final LienzoPaletteElementView paletteItemView) {
        AbstractPalette.ItemDecorator decorator = null;
        if (paletteItemView instanceof LienzoGlyphPaletteItemView) {
            final LienzoGlyphPaletteItemView.Decorator d = ((LienzoGlyphPaletteItemView) paletteItemView).getDecorator();
            if (null != d) {
                decorator = AbstractPalette.ItemDecorator.DEFAULT;
            }
        }
        return new AbstractPalette.Item(paletteItemView.getView(),
                                        decorator);
    }

    @Override
    public double getWidth() {
        return presenter.computePaletteSize()[0];
    }

    @Override
    public double getHeight() {
        return presenter.computePaletteSize()[1];
    }

    @Override
    public V clear() {
        removeExpandCollapseButton();
        return super.clear();
    }

    @Override
    public void destroy() {
        removeExpandCollapseButton();
        if (null != palette) {
            palette.setItemCallback(null);
            palette.clear();
            palette.removeFromParent();
            palette = null;
        }
    }

    protected IPrimitive<?> createExpandCollapseButton() {
        final boolean isExpanded = presenter.isExpanded();
        final double w = getGrid().getIconSize();
        final double h = getGrid().getIconSize() / 1.5;
        final Rectangle rectangle = new Rectangle(w,
                                                  h)
                .setFillAlpha(0.01)
                .setStrokeWidth(0)
                .setStrokeAlpha(0);
        final Arrow expandArrow =
                new Arrow(new Point2D(0,
                                      h / 2),
                          new Point2D(w,
                                      h / 2),
                          h / 2,
                          h,
                          45,
                          90,
                          ArrowType.AT_END_TAPERED)
                        .setFillColor(getArrowOutColor())
                        .setFillAlpha(0.5)
                        .setVisible(!isExpanded);
        final Arrow collapseArrow =
                new Arrow(new Point2D(w,
                                      h / 2),
                          new Point2D(0,
                                      h / 2),
                          h / 2,
                          h,
                          45,
                          90,
                          ArrowType.AT_END_TAPERED)
                        .setFillColor(getArrowOutColor())
                        .setFillAlpha(0.5)
                        .setVisible(isExpanded);
        handlerRegistrationManager.register(
                rectangle.addNodeMouseClickHandler(nodeMouseClickEvent -> {
                    if (presenter.isExpanded()) {
                        expandArrow.setVisible(true);
                        collapseArrow.setVisible(false);
                        presenter.collapse();
                    } else {
                        expandArrow.setVisible(false);
                        collapseArrow.setVisible(true);
                        presenter.expand();
                    }
                })
        );
        handlerRegistrationManager.register(
                rectangle.addNodeMouseEnterHandler(nodeMouseEnterEvent -> {
                    stopHoverTimeoutPalette();
                    if (presenter.isExpanded()) {
                        animate(collapseArrow,
                                getArrowHoverColor(),
                                1,
                                1);
                    } else {
                        animate(expandArrow,
                                getArrowHoverColor(),
                                1,
                                1);
                    }
                })
        );
        handlerRegistrationManager.register(
                rectangle.addNodeMouseExitHandler(nodeMouseExitEvent -> {
                    startHoverTimeoutPalette();
                    if (presenter.isExpanded()) {
                        animate(collapseArrow,
                                getArrowOutColor(),
                                0.5,
                                0.5);
                    } else {
                        animate(expandArrow,
                                getArrowOutColor(),
                                0.5,
                                0.5);
                    }
                })
        );
        return new Group()
                .add(expandArrow)
                .add(collapseArrow)
                .add(rectangle.moveToTop());
    }

    private void animate(final IPrimitive<?> primitive,
                         final String fillColor,
                         final double fillAlpha,
                         final double strokeAlpha) {
        primitive.animate(
                AnimationTweener.LINEAR,
                AnimationProperties.toPropertyList(
                        AnimationProperty.Properties.FILL_COLOR(fillColor),
                        AnimationProperty.Properties.FILL_ALPHA(fillAlpha),
                        AnimationProperty.Properties.STROKE_ALPHA(strokeAlpha)
                ),
                animationDuration
        );
    }

    protected void removeExpandCollapseButton() {
        handlerRegistrationManager.removeHandler();
        if (null != colExpButton) {
            colExpButton.removeFromParent();
            colExpButton = null;
        }
    }

    protected void initPaletteCallbacks() {
        getPalette().setItemCallback(new AbstractPalette.Callback() {

            @Override
            public void onItemHover(final int index,
                                    final double mouseX,
                                    final double mouseY,
                                    final double itemX,
                                    final double itemY) {
                if (null != presenter) {
                    presenter.onItemHover(index,
                                          mouseX,
                                          mouseY,
                                          itemX,
                                          itemY);
                }
            }

            @Override
            public void onItemOut(final int index) {
                if (null != presenter) {
                    presenter.onItemOut(index);
                }
            }

            @Override
            public void onItemMouseDown(final int index,
                                        final double mouseX,
                                        final double mouseY,
                                        final double itemX,
                                        final double itemY) {
                if (null != presenter) {
                    presenter.onItemMouseDown(index,
                                              mouseX,
                                              mouseY,
                                              itemX,
                                              itemY);
                }
            }

            @Override
            public void onItemClick(final int index,
                                    final double mouseX,
                                    final double mouseY,
                                    final double itemX,
                                    final double itemY) {
                if (null != presenter) {
                    presenter.onItemClick(index,
                                          mouseX,
                                          mouseY,
                                          itemX,
                                          itemY);
                }
            }
        });
    }

    protected void stopHoverTimeoutPalette() {
        if (palette instanceof HoverPalette) {
            final HoverPalette hoverPalette = (HoverPalette) palette;
            hoverPalette.stopTimeout();
        }
    }

    protected void startHoverTimeoutPalette() {
        if (palette instanceof HoverPalette) {
            final HoverPalette hoverPalette = (HoverPalette) palette;
            hoverPalette.startTimeout();
        }
    }

    protected String getArrowHoverColor() {
        return ColorName.DARKBLUE.getColorString();
    }

    protected String getArrowOutColor() {
        return ColorName.LIGHTBLUE.getColorString();
    }

    protected PaletteGrid getGrid() {
        return presenter.getGrid();
    }
}
