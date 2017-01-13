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

package org.kie.workbench.common.stunner.core.client.canvas.controls.palette;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.PaletteFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteGrid;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteView;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

public abstract class AbstractCanvasPaletteControl
        extends AbstractCanvasHandlerControl
        implements CanvasPaletteControl<AbstractCanvasHandler> {

    protected PaletteFactory<DefinitionSetPalette, ? extends Palette<DefinitionSetPalette>> paletteFactory;
    protected ElementBuilderControl<AbstractCanvasHandler> elementBuilderControl;
    protected ClientFactoryService factoryServices;
    protected ShapeManager shapeManager;

    protected ViewHandler<?> layerClickHandler;
    protected Palette<DefinitionSetPalette> palette;
    protected boolean paletteVisible;

    public AbstractCanvasPaletteControl(final PaletteFactory<DefinitionSetPalette, ? extends Palette<DefinitionSetPalette>> paletteFactory,
                                        final ElementBuilderControl<AbstractCanvasHandler> elementBuilderControl,
                                        final ClientFactoryService factoryServices,
                                        final ShapeManager shapeManager) {
        this.paletteFactory = paletteFactory;
        this.elementBuilderControl = elementBuilderControl;
        this.factoryServices = factoryServices;
        this.shapeManager = shapeManager;
        this.palette = null;
        this.paletteVisible = false;
    }

    protected abstract void attachPaletteView();

    protected abstract PaletteView getPaletteView();

    protected abstract PaletteGrid getGrid();

    @Override
    @SuppressWarnings("unchecked")
    public void enable(final AbstractCanvasHandler canvasHandler) {
        super.enable(canvasHandler);
        elementBuilderControl.enable(canvasHandler);
        final Layer layer = canvasHandler.getCanvas().getLayer();
        final MouseDoubleClickHandler doubleClickHandler = new MouseDoubleClickHandler() {

            @Override
            public void handle(final MouseDoubleClickEvent event) {
                if (isPaletteVisible()) {
                    hide();
                } else {
                    AbstractCanvasPaletteControl.this.show(event.getX(),
                                                           event.getY());
                }
            }
        };
        layer.addHandler(ViewEventType.MOUSE_DBL_CLICK,
                         doubleClickHandler);
        this.layerClickHandler = doubleClickHandler;
    }

    @Override
    protected void doDisable() {
        if (null != this.elementBuilderControl) {
            this.elementBuilderControl.disable();
            this.elementBuilderControl = null;
        }
        hide();
        this.palette = null;
        if (null != layerClickHandler) {
            canvasHandler.getCanvas().getLayer().removeHandler(layerClickHandler);
            this.layerClickHandler = null;
        }
    }

    private void initializePalette() {
        if (null == palette) {
            final String ssid = getShapeSetId(canvasHandler.getDiagram().getMetadata());
            this.palette = paletteFactory.newPalette(ssid,
                                                     getGrid());
            this.palette.onItemClick(AbstractCanvasPaletteControl.this::_onItemClick);
            this.palette.onClose(() -> {
                hide();
                return true;
            });
            attachPaletteView();
        }
    }

    private String getShapeSetId(final Metadata metadata) {
        final String ssid = metadata.getShapeSetId();
        if (null == ssid) {
            return shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
        }
        return ssid;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CanvasPaletteControl<AbstractCanvasHandler> show(final double x,
                                                            final double y) {
        this.paletteVisible = true;
        initializePalette();
        getPaletteView().setX(x);
        getPaletteView().setY(y);
        getPaletteView().show();
        return this;
    }

    @Override
    public CanvasPaletteControl<AbstractCanvasHandler> hide() {
        this.paletteVisible = false;
        if (null != getPaletteView()) {
            getPaletteView().hide();
        }
        return this;
    }

    private boolean _onItemClick(final String id,
                                 final double mouseX,
                                 final double mouseY,
                                 final double itemX,
                                 final double itemY) {
        factoryServices.newDefinition(id,
                                      new ServiceCallback<java.lang.Object>() {

                                          @Override
                                          public void onSuccess(final java.lang.Object def) {
                                              final ElementBuildRequest<AbstractCanvasHandler> request = new ElementBuildRequestImpl(itemX,
                                                                                                                                     itemY,
                                                                                                                                     def);
                                              elementBuilderControl.build(request,
                                                                          new BuilderControl.BuildCallback() {

                                                                              @Override
                                                                              public void onSuccess(final String uuid) {
                                                                                  onItemBuilt(uuid);
                                                                              }

                                                                              @Override
                                                                              public void onError(final ClientRuntimeError error) {
                                                                                  AbstractCanvasPaletteControl.this.onError(error);
                                                                              }
                                                                          });
                                          }

                                          @Override
                                          public void onError(final ClientRuntimeError error) {
                                              AbstractCanvasPaletteControl.this.onError(error);
                                          }
                                      });

        return true;
    }

    protected void onItemBuilt(final String uuid) {
        hide();
    }

    private void onError(final ClientRuntimeError error) {
        GWT.log("ERROR: " + error.toString());
    }

    private boolean isPaletteVisible() {
        return this.paletteVisible;
    }
}
