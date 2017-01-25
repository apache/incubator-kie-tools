/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;

/**
 * A factory type for building canvas, canvas handlers and canvas control instances.
 * A Definition Set that specifies a qualifier can create a qualified managed bean subtype
 * and provide different canvas, handler or any control types that are required for
 * the model semantics.
 * @param <C> The canvas produced type.
 * @param <H> The canvas handler produced type.
 */
public interface CanvasFactory<C extends Canvas, H extends CanvasHandler> {

    /**
     * Produces a new Canvas instance.
     */
    C newCanvas();

    /**
     * Produces a new CanvasHandler instance.
     */
    H newCanvasHandler();

    /**
     * Produce the CanvasControl instance given for the type <code>type</code>.
     */
    <A extends CanvasControl> A newControl(final Class<A> type);
}
