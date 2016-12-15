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

package org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl;

import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteItemBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;

public final class DefinitionPaletteItemImpl
        extends AbstractPaletteItem
        implements DefinitionPaletteItem {

    private final String definitionId;

    private DefinitionPaletteItemImpl( final String itemId,
                                       final String title,
                                       final String description,
                                       final String tooltip,
                                       final String definitionId ) {
        super( itemId, title, description, tooltip );
        this.definitionId = definitionId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    static class DefinitionPaletteItemBuilder
            extends AbstractPaletteItemBuilder<DefinitionPaletteItemBuilder, DefinitionPaletteItemImpl> {

        private String definitionId;

        public DefinitionPaletteItemBuilder( final String id ) {
            super( id );
        }

        public DefinitionPaletteItemBuilder definitionId( final String definitionId ) {
            this.definitionId = definitionId;
            return this;
        }

        @Override
        public DefinitionPaletteItemImpl build() {
            return new DefinitionPaletteItemImpl( id, title, description, tooltip, definitionId );
        }

    }

}
