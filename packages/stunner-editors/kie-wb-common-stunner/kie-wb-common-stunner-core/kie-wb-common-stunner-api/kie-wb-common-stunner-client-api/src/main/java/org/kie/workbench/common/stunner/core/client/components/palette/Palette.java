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

import org.uberfire.mvp.Command;

public interface Palette<I extends PaletteDefinition> {

    Palette<I> bind(I paletteDefinition);

    void unbind();

    Palette<I> onItemHover(Consumer<PaletteItemMouseEvent> callback);

    Palette<I> onItemOut(Consumer<PaletteItemEvent> callback);

    Palette<I> onItemMouseDown(Consumer<PaletteItemMouseEvent> callback);

    Palette<I> onItemClick(Consumer<PaletteItemMouseEvent> callback);

    Palette<I> onClose(Command callback);

    I getDefinition();

    void destroy();
}
