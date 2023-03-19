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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * It describes and performs an action/operation that can be done
 * from a Toolbox.
 * <p>
 * It's displayed as a button for the ActionsToolbox.
 * @param <H> The canvas handler type.
 */
public interface ToolboxAction<H extends CanvasHandler> {

    /**
     * The glyph that is being rendered as for the toolbox button's shape/icon.
     * @param canvasHandler The toolbox' canvas handler instance.
     * @param uuid The toolbox' element identifier.
     * @return The glyph definition that will be rendered as for the toolbox button's shape/icon.
     */
    Glyph getGlyph(H canvasHandler,
                   String uuid);

    /**
     * The title to display for the toolbox' button.
     * @param canvasHandler The toolbox' canvas handler instance.
     * @param uuid The toolbox' element identifier.
     * @return The title to display for the toolbox' button.
     */
    String getTitle(H canvasHandler,
                    String uuid);

    /**
     * The operation to perform once clicking on the toolbox' button.
     * @param canvasHandler The toolbox' canvas handler instance.
     * @param uuid The toolbox' element identifier.
     * @return This instance (cascade).
     */
    ToolboxAction<H> onMouseClick(H canvasHandler,
                                  String uuid,
                                  MouseClickEvent event);
}
