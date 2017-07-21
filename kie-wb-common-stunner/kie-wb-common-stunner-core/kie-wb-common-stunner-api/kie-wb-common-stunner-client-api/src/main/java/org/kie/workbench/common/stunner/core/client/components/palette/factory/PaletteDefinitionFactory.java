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

package org.kie.workbench.common.stunner.core.client.components.palette.factory;

import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;

public interface PaletteDefinitionFactory<B extends PaletteDefinitionBuilder, I extends HasPaletteItems, P extends Palette<I>> {

    /**
     * Returns if this provider accepts the given Definition Set identifier.
     */
    boolean accepts(final String defSetId);

    /**
     * Builds the palette definition for the given Definition Set identifier.
     */
    B newBuilder(final String defSetId);

    P newPalette();
}
