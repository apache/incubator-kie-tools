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

package org.kie.workbench.common.stunner.core.client.components.palette.model.definition;

import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;

public interface DefinitionSetPaletteBuilder extends PaletteDefinitionBuilder<Object, DefinitionSetPalette, ClientRuntimeError> {

    interface PaletteCategoryProvider {

        String getTitle( String id );

        String getDescription( String id );

        String getDefinitionId( String id );

    }

    interface PaletteMorphGroupProvider {

        String getTitle( String morphBaseId, Object definition );

        String getDescription( String morphBaseId, Object definition );

    }

    DefinitionSetPaletteBuilder setCategoryProvider( PaletteCategoryProvider categoryProvider );

    DefinitionSetPaletteBuilder setMorphGroupProvider( PaletteMorphGroupProvider groupProvider );

}
