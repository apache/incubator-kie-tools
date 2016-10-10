/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl;

import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteGroupBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;

import java.util.List;

public final class DefinitionPaletteCategoryImpl extends AbstractPaletteGroup<DefinitionPaletteItem> implements DefinitionPaletteCategory {

    private DefinitionPaletteCategoryImpl( final String itemId,
                                           final String title,
                                           final String description,
                                           final String tooltip,
                                           final String glyphDefinitionId,
                                           final List<DefinitionPaletteItem> items ) {
        super( itemId, title, description, tooltip, glyphDefinitionId, items );

    }

    static class DefinitionPaletteCategoryBuilder extends AbstractPaletteGroupBuilder<DefinitionPaletteCategoryBuilder,
            DefinitionPaletteCategoryImpl, DefinitionPaletteItem> {

        public DefinitionPaletteCategoryBuilder( final String id ) {
            super( id );
        }

        @Override
        protected DefinitionPaletteCategoryImpl doBuild( final List<DefinitionPaletteItem> items ) {
            if ( null == definitionId && !items.isEmpty() ) {
                final DefinitionPaletteItem item = items.get( 0 );
                this.definitionId = item.getDefinitionId();

            }
            return new DefinitionPaletteCategoryImpl( id, title, description, tooltip, definitionId, items );
        }

    }

}
