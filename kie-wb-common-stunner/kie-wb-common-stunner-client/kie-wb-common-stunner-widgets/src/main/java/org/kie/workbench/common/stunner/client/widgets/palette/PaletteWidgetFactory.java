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

package org.kie.workbench.common.stunner.client.widgets.palette;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.PaletteFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinition;

/**
 * Provides a widget available to add into the DOM for a given palette.
 * @param <I> The palette definition type.
 * @param <P> The palette type wrapped by the widget.
 */
public interface PaletteWidgetFactory<I extends PaletteDefinition, P extends PaletteWidget<I>> extends PaletteFactory<I, P> {

    /**
     * Builds a new palette widget.
     * This widget produces events, such as dropping elements from the palette intoa canvas,
     * and bind those events to the canvas' handler instance.
     * @param shapeSetId The shape set identifier.
     * @param canvasHandler The canvas' handler.
     */
    P newPalette(final String shapeSetId,
                 final CanvasHandler canvasHandler);
}
