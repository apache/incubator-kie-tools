/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.widget.panel.mediators;

import com.ait.lienzo.client.core.mediator.MouseBoxZoomMediator;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelImpl;
import com.ait.lienzo.client.widget.panel.impl.PreviewLayer;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;
import com.ait.lienzo.client.widget.panel.util.PanelTransformUtils;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.tooling.common.api.java.util.function.Consumer;
import com.ait.tooling.common.api.java.util.function.Supplier;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;

public class PanelPreviewMediator extends AbstractPanelMediator<PanelPreviewMediator> {

    private static final double PREVIEW_AREA_PADDING = 50d;
    private static final double PREVIEW_AREA_LOCATION = 250d;
    private static final double PREVIEW_AREA_SIZE = 350d;
    static final String PREVIEW_BG_COLOR = "#FFFFFF";

    private final MouseBoxZoomMediator mediator;
    private final Consumer<IsWidget> widget;
    private final LienzoPanel previewPanel;
    private final PreviewLayer previewLayer;
    private Supplier<BoundingBox> area;
    private double maxScale;

    public static PanelPreviewMediator build(final ScrollablePanel panel) {
        return new PanelPreviewMediator(new Supplier<LienzoBoundsPanel>() {
            @Override
            public LienzoBoundsPanel get() {
                return panel;
            }
        }, new Consumer<IsWidget>() {
            @Override
            public void accept(IsWidget previewPanel) {
                panel.getDomElementContainer().add(previewPanel);
            }
        })
                .setArea(new Supplier<BoundingBox>() {
                    @Override
                    public BoundingBox get() {
                        return new BoundingBox(PREVIEW_AREA_PADDING,
                                               PREVIEW_AREA_PADDING,
                                               panel.getScrollPanel().getOffsetWidth() - PREVIEW_AREA_PADDING,
                                               panel.getScrollPanel().getOffsetHeight() - PREVIEW_AREA_PADDING);
                    }
                });
    }

    public PanelPreviewMediator(final Supplier<LienzoBoundsPanel> panelSupplier,
                                final Consumer<IsWidget> widget) {
        this(panelSupplier,
             widget,
             new Supplier<LienzoPanel>() {
                 @Override
                 public LienzoPanel get() {
                     return new LienzoPanelImpl(1, 1);
                 }
             });
    }

    public PanelPreviewMediator(final Supplier<LienzoBoundsPanel> panelSupplier,
                                final Consumer<IsWidget> widget,
                                final Supplier<LienzoPanel> previewPanelBuilder) {
        super(panelSupplier);
        this.widget = widget;
        this.previewPanel = previewPanelBuilder.get();
        this.area = new Supplier<BoundingBox>() {
            @Override
            public BoundingBox get() {
                return new BoundingBox(PREVIEW_AREA_LOCATION,
                                       PREVIEW_AREA_LOCATION,
                                       PREVIEW_AREA_LOCATION + PREVIEW_AREA_SIZE,
                                       PREVIEW_AREA_LOCATION + PREVIEW_AREA_SIZE);
            }
        };
        this.previewLayer = new PreviewLayer(getBackgroundBounds(), getVisibleBounds());
        this.mediator = new MouseBoxZoomMediator();
        this.maxScale = Double.MAX_VALUE;
        init();
    }

    public PanelPreviewMediator setArea(final Supplier<BoundingBox> box) {
        this.area = box;
        return this;
    }

    public PanelPreviewMediator setMaxScale(final double maxScale) {
        this.maxScale = maxScale;
        return this;
    }

    @Override
    protected void onEnable() {
        final LienzoBoundsPanel panel = getPanel();
        final int panelWidthPx = panel.getWidthPx();
        final int panelHeightPx = panel.getHeightPx();
        final Style style = previewPanel.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setTop(0, Style.Unit.PX);
        style.setLeft(0, Style.Unit.PX);
        style.setBorderStyle(Style.BorderStyle.NONE);
        style.setBackgroundColor(PREVIEW_BG_COLOR);
        previewPanel.setPixelSize(panelWidthPx, panelHeightPx);
        previewPanel.setVisible(true);

        final BoundingBox areaBox = area.get();
        final double fitLevel = PanelTransformUtils.computeZoomLevelFitToWidth(areaBox.getWidth(),
                                                                               areaBox.getHeight(),
                                                                               panel);

        final Viewport viewport = previewLayer.getViewport();
        final Transform transform = new Transform();
        transform.translate(areaBox.getX(), areaBox.getY());
        transform.scale(fitLevel, fitLevel);
        viewport.setTransform(transform);

        getLayer().drawWithTransforms(previewLayer.getContext(),
                                      1,
                                      new BoundingBox(0, 0, panelWidthPx, panelHeightPx),
                                      new Supplier<Transform>() {
                                          @Override
                                          public Transform get() {
                                              return transform;
                                          }
                                      });

        mediator.setMaxScale(maxScale);
        mediator.setEnabled(true);
        getLayer().setVisible(false);
    }

    @Override
    public void onDisable() {
        mediator.cancel();
        mediator.setEnabled(false);
        previewLayer.clear();
        previewPanel.setPixelSize(1, 1);
        previewPanel.setVisible(false);
        previewLayer.getViewport().setTransform(new Transform());
        getLayer().setVisible(true);
    }

    public boolean isEnabled() {
        return mediator.isEnabled();
    }

    @Override
    public void onRemoveHandler() {
        super.onRemoveHandler();
        mediator.setOnTransform(null);
        previewPanel.removeFromParent();
        area = null;
    }

    private void init() {
        previewLayer.setListening(true).setTransformable(true);
        previewPanel.add(previewLayer);
        mediator.setEnabled(false);
        mediator
                .setRectangle(new Rectangle(1, 1).setStrokeColor(ColorName.RED))
                .setOnTransform(new Consumer<Transform>() {
                    @Override
                    public void accept(Transform transform) {
                        getLayer().getViewport().setTransform(transform.copy());
                        disable();
                    }
                });
        previewLayer.getViewport().getMediators().push(mediator);
        previewPanel.setVisible(false);

        widget.accept(previewPanel);
    }

    MouseBoxZoomMediator getMediator() {
        return mediator;
    }

    LienzoPanel getPreviewPanel() {
        return previewPanel;
    }

    PreviewLayer getPreviewLayer() {
        return previewLayer;
    }

    private Supplier<Bounds> getVisibleBounds() {
        return new Supplier<Bounds>() {
            @Override
            public Bounds get() {
                final BoundingBox areaBox = area.get();
                return Bounds.build(areaBox.getX(),
                                    areaBox.getY(),
                                    areaBox.getWidth(),
                                    areaBox.getHeight());
            }
        };
    }

    private Supplier<Bounds> getBackgroundBounds() {
        return new Supplier<Bounds>() {
            @Override
            public Bounds get() {
                return Bounds.build(0,
                                    0,
                                    getPanel().getWidthPx(),
                                    getPanel().getHeightPx());
            }
        };
    }
}
