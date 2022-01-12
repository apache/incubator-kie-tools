/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.canvas;

import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.util.CursorMap;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasView;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public abstract class LienzoCanvasView<L extends LienzoLayer>
        extends AbstractCanvasView<LienzoCanvasView> {

    static final String BG_COLOR = "#FFFFFF";

    private BiFunction<Integer, Integer, IPrimitive<?>> decoratorFactory;

    public LienzoCanvasView() {
        this(LienzoCanvasDecoratorFactory.AUTHORING);
    }

    public LienzoCanvasView(final BiFunction<Integer, Integer, IPrimitive<?>> decoratorFactory) {
        this.decoratorFactory = decoratorFactory;
    }

    public abstract L getLayer();

    @Override
    protected LienzoCanvasView<L> doInitialize(final CanvasSettings canvasSettings) {
        getLienzoPanel()
                .show(getLayer())
                .asWidget()
                .getElement()
                .getStyle()
                .setBackgroundColor(BG_COLOR);
        getLayer()
                .getTopLayer()
                .add(decoratorFactory.apply(1200, 800));
        return this;
    }

    @Override
    public LienzoCanvasView<L> add(final ShapeView<?> shape) {
        getLayer().add((IPrimitive<?>) shape);
        return this;
    }

    @Override
    public LienzoCanvasView<L> delete(final ShapeView<?> shape) {
        getLayer().delete((IPrimitive<?>) shape);
        return this;
    }

    @Override
    protected void setViewCursor(final AbstractCanvas.Cursors cursor) {
        getLienzoPanel().getView().setCursor(CursorMap.get().lookup(toViewCursor(cursor).getCssName()));
        super.setViewCursor(cursor);
    }

    @Override
    public LienzoCanvasView<L> setGrid(final CanvasGrid grid) {
        if (null != grid) {
            GridLayer gridLayer = LienzoGridLayerBuilder.getLienzoGridFor(grid);
            getLienzoPanel().setBackgroundLayer(gridLayer);
        } else {
            getLienzoPanel().setBackgroundLayer(null);
        }
        return this;
    }

    public LienzoCanvasView<L> setDecoratorFactory(final BiFunction<Integer, Integer, IPrimitive<?>> decoratorFatory) {
        this.decoratorFactory = decoratorFatory;
        return this;
    }

    @Override
    public LienzoCanvasView<L> clear() {
        getLayer().clear();
        return this;
    }

    @Override
    public Transform getTransform() {
        return getLayer().getTransform();
    }

    public LienzoPanel getLienzoPanel() {
        return (LienzoPanel) getPanel();
    }

    @Override
    protected void doDestroy() {
        getLayer().destroy();
        decoratorFactory = null;
    }
}
