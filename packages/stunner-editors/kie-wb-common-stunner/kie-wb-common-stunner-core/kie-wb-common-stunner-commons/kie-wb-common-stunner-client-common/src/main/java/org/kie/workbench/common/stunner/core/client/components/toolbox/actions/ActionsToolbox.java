/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * A default toolbox implementation that decouples
 * the actions/operations to perform for an element
 * and the concrete toolbox' view implementation.
 * <p>
 * Several <code>ToolboxAction</code> instances can be added in this
 * toolbox, depending on the current context. On the other hand,
 * this toolbox uses a concrete inmutable view, given at instance
 * construction time, which finally displays the toolbox' actions
 * as toolbox' button in the canvas.
 * @param <V> The actions toolbox' view type.
 */
public class ActionsToolbox<V extends ActionsToolboxView<?>>
        implements Toolbox<ActionsToolbox>,
                   Iterable<ToolboxAction> {

    private final List<ToolboxAction> actions = new LinkedList<>();
    private final Supplier<AbstractCanvasHandler> canvasHandlerSupplier;
    private final String uuid;
    private final V view;

    public ActionsToolbox(final Supplier<AbstractCanvasHandler> canvasHandlerSupplier,
                          final Element<?> element,
                          final V view) {
        this.uuid = element.getUUID();
        this.view = view;
        this.canvasHandlerSupplier = canvasHandlerSupplier;
    }

    public ActionsToolbox<V> init() {
        getView().init(this);
        return this;
    }

    public ActionsToolbox add(final ToolboxAction<AbstractCanvasHandler> action) {
        actions.add(action);
        return this;
    }

    @Override
    public Iterator<ToolboxAction> iterator() {
        return actions.iterator();
    }

    public int size() {
        return actions.size();
    }

    public String getElementUUID() {
        return uuid;
    }

    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandlerSupplier.get();
    }

    public AbstractCanvas getCanvas() {
        return getCanvasHandler().getAbstractCanvas();
    }

    public Shape<?> getShape() {
        return canvasHandlerSupplier.get().getCanvas().getShape(uuid);
    }

    private String lastToolBoxRendered = "";

    @Override
    public ActionsToolbox show() {

        if (lastToolBoxRendered.equals(this.uuid)) {
            return this;
        }

        lastToolBoxRendered = this.uuid;

        getView().show();
        return this;
    }

    @Override
    public ActionsToolbox hide() {
        getView().hide();
        return this;
    }

    @Override
    public void destroy() {
        getView().destroy();
        actions.clear();
    }

    @Override
    public void hideAndDestroy() {
        getView().hideAndDestroy();
        actions.clear();
    }

    public V getView() {
        return view;
    }

    public Glyph getGlyph(final ToolboxAction<AbstractCanvasHandler> action) {
        final AbstractCanvasHandler canvasHandler = canvasHandlerSupplier.get();
        return action.getGlyph(canvasHandler, uuid);
    }

    public String getTitle(final ToolboxAction<AbstractCanvasHandler> action) {
        final AbstractCanvasHandler canvasHandler = canvasHandlerSupplier.get();
        return action.getTitle(canvasHandler, uuid);
    }
}
