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

package org.kie.workbench.common.stunner.client.widgets.palette;

import java.util.function.Consumer;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinition;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public interface PaletteWidget<D extends PaletteDefinition>
        extends Palette<D> {

    PaletteWidget<D> onItemDrop(Consumer<PaletteIDefinitionItemEvent> callback);

    PaletteWidget<D> onItemDragStart(Consumer<PaletteIDefinitionItemEvent> callback);

    PaletteWidget<D> onItemDragUpdate(Consumer<PaletteIDefinitionItemEvent> callback);

    void setVisible(boolean visible);

    void onScreenMaximized(ScreenMaximizedEvent event);

    void onScreenMinimized(ScreenMinimizedEvent event);

    HTMLElement getElement();

    Glyph getShapeGlyph(String definitionId);
}
