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

package org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * Provides a palette builder for a DefinitionSetPalette.
 * Build method considers two arguments:
 * - Object if it's the model object for the definition set
 * - String if it's the definition set identifier
 */
@Dependent
public class DefinitionSetPaletteBuilderImpl
        extends AbstractPaletteDefinitionBuilder<PaletteDefinitionBuilder.Configuration, DefinitionSetPalette, ClientRuntimeError>
        implements DefinitionSetPaletteBuilder {

    private final DefinitionUtils definitionUtils;
    private final ClientFactoryService clientFactoryServices;

    private PaletteCategoryProvider paletteCategoryProvider;
    private PaletteMorphGroupProvider paletteMorphGroupProvider;

    protected DefinitionSetPaletteBuilderImpl() {
        this(null,
             null);
    }

    @Inject
    public DefinitionSetPaletteBuilderImpl(final DefinitionUtils definitionUtils,
                                           final ClientFactoryService clientFactoryServices) {
        this.definitionUtils = definitionUtils;
        this.clientFactoryServices = clientFactoryServices;
        this.paletteCategoryProvider = CATEGORY_PROVIDER;
        this.paletteMorphGroupProvider = MORPH_GROUP_PROVIDER;
    }

    public void build(final PaletteDefinitionBuilder.Configuration configuration,
                      final Callback<DefinitionSetPalette, ClientRuntimeError> callback) {
        final String defSetId = configuration.getDefinitionSetId();
        final Collection<String> definitions = configuration.getDefinitionIds();

        if (null != definitions) {
            final List<DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder> categoryBuilders = new LinkedList<>();
            for (final String defId : definitions) {
                // Check if this concrete definition is excluded from the palette model.
                if (!isDefinitionExcluded(defId)) {
                    clientFactoryServices.newDefinition(defId,
                                                        new ServiceCallback<Object>() {
                                                            @Override
                                                            public void onSuccess(final Object definition) {
                                                                final String id = getDefinitionManager().adapters().forDefinition().getId(definition);
                                                                final String category = getDefinitionManager().adapters().forDefinition().getCategory(definition);
                                                                final String categoryId = toValidId(category);
                                                                // Check if this concrete category excluded from the palette model.
                                                                if (!isCategoryExcluded(categoryId)) {
                                                                    DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder categoryGroupBuilder = getItemBuilder(categoryBuilders,
                                                                                                                                                                         categoryId);
                                                                    if (null == categoryGroupBuilder) {
                                                                        categoryGroupBuilder = new DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder(categoryId)
                                                                                .definitionId(paletteCategoryProvider.getDefinitionId(categoryId))
                                                                                .title(paletteCategoryProvider.getTitle(categoryId))
                                                                                .tooltip(paletteCategoryProvider.getTitle(categoryId))
                                                                                .description(paletteCategoryProvider.getDescription(categoryId));
                                                                        categoryBuilders.add(categoryGroupBuilder);
                                                                    }
                                                                    final MorphDefinition morphDefinition = definitionUtils.getMorphDefinition(definition);
                                                                    final boolean hasMorphBase = null != morphDefinition;
                                                                    DefinitionPaletteGroupImpl.DefinitionPaletteGroupBuilder morphGroupBuilder = null;
                                                                    String morphDefault = null;
                                                                    if (hasMorphBase) {
                                                                        final String morphBase = morphDefinition.getBase();
                                                                        morphDefault = morphDefinition.getDefault();
                                                                        final String morphBaseId = toValidId(morphBase);
                                                                        morphGroupBuilder = (DefinitionPaletteGroupImpl.DefinitionPaletteGroupBuilder) categoryGroupBuilder.getItem(morphBaseId);
                                                                        if (null == morphGroupBuilder) {
                                                                            morphGroupBuilder = new DefinitionPaletteGroupImpl.DefinitionPaletteGroupBuilder(morphBaseId)
                                                                                    .definitionId(morphDefault)
                                                                                    .title(paletteMorphGroupProvider.getTitle(morphBase,
                                                                                                                              morphDefinition))
                                                                                    .description(paletteMorphGroupProvider.getDescription(morphBase,
                                                                                                                                          morphDefinition))
                                                                                    .tooltip(paletteMorphGroupProvider.getTitle(morphBase,
                                                                                                                                morphDefinition));
                                                                            categoryGroupBuilder.addItem(morphGroupBuilder);
                                                                        }
                                                                    }
                                                                    final String title = getDefinitionManager().adapters().forDefinition().getTitle(definition);
                                                                    final String description = getDefinitionManager().adapters().forDefinition().getDescription(definition);
                                                                    final DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder itemBuilder = new DefinitionPaletteItemImpl.DefinitionPaletteItemBuilder(id)
                                                                            .definitionId(id)
                                                                            .title(title)
                                                                            .description(description)
                                                                            .tooltip(description);
                                                                    if (null != morphGroupBuilder) {
                                                                        if (null != morphDefault && morphDefault.equals(id)) {
                                                                            morphGroupBuilder.addItem(0,
                                                                                                      itemBuilder);
                                                                        } else {
                                                                            morphGroupBuilder.addItem(itemBuilder);
                                                                        }
                                                                    } else {
                                                                        categoryGroupBuilder.addItem(itemBuilder);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(final ClientRuntimeError error) {
                                                                callback.onError(error);
                                                            }
                                                        });
                }
            }
            if (!categoryBuilders.isEmpty()) {
                final List<DefinitionPaletteCategory> categories = new LinkedList<>();
                for (final DefinitionPaletteCategoryImpl.DefinitionPaletteCategoryBuilder builder : categoryBuilders) {
                    categories.add(builder.build());
                }
                final DefinitionSetPaletteImpl definitionPalette = new DefinitionSetPaletteImpl(categories,
                                                                                                defSetId);
                callback.onSuccess(definitionPalette);
            } else {
                callback.onError(new ClientRuntimeError("No categories found."));
            }
        } else {
            callback.onError(new ClientRuntimeError("Missing definition argument."));
        }
    }

    static final PaletteCategoryProvider CATEGORY_PROVIDER = new PaletteCategoryProvider() {

        @Override
        public String getTitle(final String id) {
            return id;
        }

        @Override
        public String getDescription(final String id) {
            return id;
        }

        @Override
        public String getDefinitionId(final String id) {
            return null;
        }
    };

    static final PaletteMorphGroupProvider MORPH_GROUP_PROVIDER = new PaletteMorphGroupProvider() {

        @Override
        public String getTitle(final String morphBaseId,
                               final Object definition) {
            return morphBaseId;
        }

        @Override
        public String getDescription(final String morphBaseId,
                                     final Object definition) {
            return morphBaseId;
        }
    };

    protected DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

    @Override
    public DefinitionSetPaletteBuilder setCategoryProvider(final PaletteCategoryProvider categoryProvider) {
        this.paletteCategoryProvider = categoryProvider;
        return this;
    }

    @Override
    public DefinitionSetPaletteBuilder setMorphGroupProvider(final PaletteMorphGroupProvider groupProvider) {
        this.paletteMorphGroupProvider = groupProvider;
        return this;
    }
}
