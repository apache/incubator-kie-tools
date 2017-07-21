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

import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public abstract class BindableDefSetPaletteDefinitionFactory<I extends HasPaletteItems, P extends Palette<I>>
        extends BindablePaletteDefinitionFactory<DefinitionSetPaletteBuilder, I, P>
        implements DefSetPaletteDefinitionFactory<I, P> {


    public BindableDefSetPaletteDefinitionFactory(final ShapeManager shapeManager,
                                                  final DefinitionSetPaletteBuilder paletteBuilder,
                                                  final P palette) {
        super(shapeManager,
              paletteBuilder,
              palette);
    }

    /**
     * Returns the title to show for the category with the given id.
     */
    protected abstract String getCategoryTitle(final String id);

    /**
     * Returns the Definition type that will be created as by this category.
     * Return <code>null</code> if no definition associated with this category.
     */
    protected abstract Class<?> getCategoryTargetDefinitionId(final String id);

    /**
     * Returns the description to show for the category with the given id.
     */
    protected abstract String getCategoryDescription(final String id);

    /**
     * Returns the title to show for the morph group with the given id.
     */
    protected abstract String getMorphGroupTitle(final String morphBaseId,
                                                 final Object definition);

    /**
     * Returns the description to show for the morph group with the given id.
     */
    protected abstract String getMorphGroupDescription(final String morphBaseId,
                                                       final Object definition);

    @Override
    protected DefinitionSetPaletteBuilder newBuilder() {
        paletteBuilder.setCategoryProvider(new DefinitionSetPaletteBuilder.PaletteCategoryProvider() {

            @Override
            public String getTitle(final String id) {
                return getCategoryTitle(id);
            }

            @Override
            public String getDescription(final String id) {
                return getCategoryDescription(id);
            }

            @Override
            public String getDefinitionId(final String id) {
                final Class<?> type = getCategoryTargetDefinitionId(id);
                return null != type ? BindableAdapterUtils.getDefinitionId(type) : null;
            }
        });

        paletteBuilder.setMorphGroupProvider(new DefinitionSetPaletteBuilder.PaletteMorphGroupProvider() {

            @Override
            public String getTitle(final String morphBaseId,
                                   final Object definition) {
                return getMorphGroupTitle(morphBaseId,
                                          definition);
            }

            @Override
            public String getDescription(final String morphBaseId,
                                         final Object definition) {
                return getMorphGroupDescription(morphBaseId,
                                                definition);
            }
        });

        configureBuilder();
        return paletteBuilder;
    }

    protected void configureBuilder() {
    }

    protected void excludeDefinition(final Class<?> type) {
        final String id = BindableAdapterUtils.getDefinitionId(type);
        paletteBuilder.excludeDefinition(id);
    }

    protected void excludeCategory(final String id) {
        paletteBuilder.excludeCategory(id);
    }
}
