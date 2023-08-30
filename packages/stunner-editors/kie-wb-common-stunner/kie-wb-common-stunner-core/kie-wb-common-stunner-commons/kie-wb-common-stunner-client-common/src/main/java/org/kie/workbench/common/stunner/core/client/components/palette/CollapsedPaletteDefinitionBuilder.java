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


package org.kie.workbench.common.stunner.core.client.components.palette;

import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.CollapsedItemBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * Builds a new single level palette model.
 */
@Dependent
@Typed(CollapsedPaletteDefinitionBuilder.class)
public class CollapsedPaletteDefinitionBuilder
        extends AbstractPaletteDefinitionBuilder<CollapsedPaletteDefinitionBuilder> {

    @Inject
    public CollapsedPaletteDefinitionBuilder(final DefinitionUtils definitionUtils,
                                             final DomainProfileManager profileManager,
                                             final DefinitionsCacheRegistry definitionsRegistry,
                                             final StunnerTranslationService translationService) {
        super(definitionUtils,
              profileManager,
              definitionsRegistry,
              translationService);
    }

    @Override
    protected DefaultPaletteItem createItem(final Object definition,
                                            final String categoryId,
                                            final Metadata metadata,
                                            final Function<String, DefaultPaletteItem> itemSupplier) {
        final DefinitionAdapter<Object> definitionAdapter = getDefinitionManager().adapters().forDefinition();
        final String id = definitionAdapter.getId(definition).value();
        final String title = definitionAdapter.getTitle(definition);
        // Notice it creates the item by using the title as for the item's tooltip property,
        // setting this an empty item title, in order to not display text once the rendered
        // item is displayed, just the icon with the given tooltip.
        return new CollapsedItemBuilder()
                .setItemId(id)
                .setDefinitionId(id)
                .setTooltip(title)
                .build();
    }
}
