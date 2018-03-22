/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.palette;

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.uberfire.mvp.Command;

public abstract class AbstractPalette<D extends PaletteDefinition> implements Palette<D> {

    protected final ShapeManager shapeManager;

    protected Command closeCallback;
    protected Consumer<PaletteItemMouseEvent> itemHoverCallback;
    protected Consumer<PaletteItemEvent> itemOutCallback;
    protected Consumer<PaletteItemMouseEvent> itemMouseDownCallback;
    protected Consumer<PaletteItemMouseEvent> itemClickCallback;
    protected D paletteDefinition;

    protected AbstractPalette() {
        this(null);
    }

    protected AbstractPalette(final ShapeManager shapeManager) {
        this.shapeManager = shapeManager;
    }

    protected abstract AbstractPalette<D> bind();

    protected abstract void doDestroy();

    protected abstract String getPaletteItemId(final int index);

    @Override
    @SuppressWarnings("unchecked")
    public AbstractPalette<D> bind(final D paletteDefinition) {
        this.paletteDefinition = paletteDefinition;
        beforeBind();
        bind();
        afterBind();
        return this;
    }

    protected void beforeBind() {
    }

    protected void afterBind() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractPalette<D> onClose(final Command callback) {
        this.closeCallback = callback;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractPalette<D> onItemHover(final Consumer<PaletteItemMouseEvent> callback) {
        this.itemHoverCallback = callback;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractPalette<D> onItemOut(final Consumer<PaletteItemEvent> callback) {
        this.itemOutCallback = callback;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractPalette<D> onItemMouseDown(final Consumer<PaletteItemMouseEvent> callback) {
        this.itemMouseDownCallback = callback;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractPalette<D> onItemClick(final Consumer<PaletteItemMouseEvent> callback) {
        this.itemClickCallback = callback;
        return this;
    }

    public boolean onClose() {
        doClose();
        if (null != closeCallback) {
            closeCallback.execute();
        }
        return true;
    }

    protected void doClose() {
    }

    public boolean onItemHover(final int index,
                               final double mouseX,
                               final double mouseY,
                               final double itemX,
                               final double itemY) {
        final String id = getPaletteItemId(index);
        doItemHover(id,
                    mouseX,
                    mouseY,
                    itemX,
                    itemY);
        if (null != itemHoverCallback) {
            itemHoverCallback.accept(new PaletteItemMouseEvent(getPaletteItemId(index),
                                                               mouseX,
                                                               mouseY,
                                                               itemX,
                                                               itemY));
        }
        return true;
    }

    protected void doItemHover(final String id,
                               final double mouseX,
                               final double mouseY,
                               final double itemX,
                               final double itemY) {
    }

    public boolean onItemOut(final int index) {
        if (null != itemOutCallback) {
            itemOutCallback.accept(new PaletteItemEvent(getPaletteItemId(index)));
        }
        return true;
    }

    public boolean onItemMouseDown(final int index,
                                   final double mouseX,
                                   final double mouseY,
                                   final double itemX,
                                   final double itemY) {
        if (null != itemMouseDownCallback) {
            final String id = getPaletteItemId(index);
            return this.onItemMouseDown(id,
                                        mouseX,
                                        mouseY,
                                        itemX,
                                        itemY);
        }
        return true;
    }

    public boolean onItemMouseDown(final String id,
                                   final double mouseX,
                                   final double mouseY,
                                   final double itemX,
                                   final double itemY) {
        if (null != itemMouseDownCallback) {
            itemMouseDownCallback.accept(new PaletteItemMouseEvent(id,
                                                                   mouseX,
                                                                   mouseY,
                                                                   itemX,
                                                                   itemY));
        }
        return true;
    }

    public boolean onItemClick(final int index,
                               final double mouseX,
                               final double mouseY,
                               final double itemX,
                               final double itemY) {
        if (null != itemClickCallback) {
            final String id = getPaletteItemId(index);
            itemClickCallback.accept(new PaletteItemMouseEvent(id,
                                                               mouseX,
                                                               mouseY,
                                                               itemX,
                                                               itemY));
        }
        return true;
    }

    public boolean onItemClick(final String id,
                               final double mouseX,
                               final double mouseY,
                               final double itemX,
                               final double itemY) {
        if (null != itemClickCallback) {
            itemClickCallback.accept(new PaletteItemMouseEvent(id,
                                                               mouseX,
                                                               mouseY,
                                                               itemX,
                                                               itemY));
        }
        return true;
    }

    @Override
    public D getDefinition() {
        return paletteDefinition;
    }

    @Override
    public void destroy() {
        doDestroy();
        this.closeCallback = null;
        this.itemHoverCallback = null;
        this.itemOutCallback = null;
        this.itemMouseDownCallback = null;
        this.itemClickCallback = null;
        this.paletteDefinition = null;
    }
}
