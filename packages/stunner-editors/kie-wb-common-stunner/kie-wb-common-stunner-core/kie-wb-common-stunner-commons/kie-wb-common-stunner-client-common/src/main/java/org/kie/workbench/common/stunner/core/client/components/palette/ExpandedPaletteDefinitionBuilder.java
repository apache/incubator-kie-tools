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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.CategoryBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.GroupBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.ItemBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.DefaultMessageProvider;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.DefaultMorphGroupMessageProvider;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * Builds a new palette model which has a well defined tree structure, composed as:
 * - Category (xN)
 * -- Group (xN)
 * --- Item (xN)
 * The resulting palette model instance is auto-generate by extrapolating structural
 * information from the default Stunner's domain, this way:
 * - Categories bind to the Definition Set's categories specified for each Definition
 * - Groups bind to each of the base types for beans which allow morphing
 * - Items bind to each of the Definitions present
 */
@Dependent
@Default
public class ExpandedPaletteDefinitionBuilder
        extends AbstractPaletteDefinitionBuilder<ExpandedPaletteDefinitionBuilder> {

    private Function<Object, MorphDefinition> morphDefinitionProvider;
    private Predicate<String> groupFilter;
    private Function<String, String> categoryDefinitionIdProvider;
    private Function<String, Glyph> categoryGlyphProvider;
    private ItemMessageProvider groupMessageProvider;
    private ItemMessageProvider categoryMessageProvider;
    private Function<String, String> customGroupIdProvider;
    private ItemMessageProvider customGroupMessageProvider;

    public ExpandedPaletteDefinitionBuilder categoryDefinitionIdProvider(final Function<String, String> categoryDefinitionIdProvider) {
        this.categoryDefinitionIdProvider = categoryDefinitionIdProvider;
        return this;
    }

    public ExpandedPaletteDefinitionBuilder morphDefinitionProvider(final Function<Object, MorphDefinition> morphDefinitionProvider) {
        this.morphDefinitionProvider = morphDefinitionProvider;
        return this;
    }

    public ExpandedPaletteDefinitionBuilder groupFilter(final Predicate<String> groupFilter) {
        this.groupFilter = groupFilter;
        return this;
    }

    public ExpandedPaletteDefinitionBuilder categoryGlyphProvider(final Function<String, Glyph> categoryGlyphProvider) {
        this.categoryGlyphProvider = categoryGlyphProvider;
        return this;
    }

    public ExpandedPaletteDefinitionBuilder groupMessages(final ItemMessageProvider provider) {
        this.groupMessageProvider = provider;
        return this;
    }

    public ExpandedPaletteDefinitionBuilder categoryMessages(final ItemMessageProvider provider) {
        this.categoryMessageProvider = provider;
        return this;
    }

    public ExpandedPaletteDefinitionBuilder customGroupIdProvider(final Function<String, String> customGroupIdProvider) {
        this.customGroupIdProvider = customGroupIdProvider;
        return this;
    }

    public ExpandedPaletteDefinitionBuilder customGroupMessages(final ItemMessageProvider provider) {
        this.customGroupMessageProvider = provider;
        return this;
    }

    @Inject
    public ExpandedPaletteDefinitionBuilder(final DefinitionUtils definitionUtils,
                                            final DomainProfileManager profileManager,
                                            final DefinitionsCacheRegistry definitionsRegistry,
                                            final StunnerTranslationService translationService) {
        super(definitionUtils, profileManager, definitionsRegistry, translationService);
        initDefaults();
    }

    @Override
    protected DefaultPaletteItem createItem(final Object definition,
                                            final String categoryId,
                                            final Metadata metadata,
                                            final Function<String, DefaultPaletteItem> itemSupplier) {
        final DefinitionAdapter<Object> definitionAdapter = getDefinitionManager().adapters().forDefinition();
        final String id = definitionAdapter.getId(definition).value();
        DefaultPaletteCategory result = null;
        DefaultPaletteCategory category = (DefaultPaletteCategory) itemSupplier.apply(categoryId);
        if (null == category) {
            final String catDefId = categoryDefinitionIdProvider.apply(categoryId);
            final String catTitle = categoryMessageProvider.getTitle(categoryId);
            final String catDesc = categoryMessageProvider.getDescription(categoryId);
            final Glyph categoryGlyph = categoryGlyphProvider.apply(categoryId);
            category = new CategoryBuilder()
                    .setItemId(categoryId)
                    .setDefinitionId(catDefId)
                    .setTitle(catTitle)
                    .setDescription(catDesc)
                    .setTooltip(catTitle)
                    .setGlyph(categoryGlyph)
                    .build();
            result = category;
        }
        final MorphDefinition morphDefinition = morphDefinitionProvider.apply(definition);
        final boolean hasMorphBase = null != morphDefinition;
        String morphDefault = null;
        DefaultPaletteGroup group = null;
        if (hasMorphBase) {
            final String morphBaseId = morphDefinition.getBase();
            if (groupFilter.test(morphBaseId)) {
                morphDefault = morphDefinition.getDefault();
                final Optional<DefaultPaletteItem> groupOp = category.getItems().stream()
                        .filter(g -> g.getId().equals(morphBaseId))
                        .findFirst();
                if (!groupOp.isPresent()) {
                    final String groupTitle = groupMessageProvider.getTitle(morphBaseId);
                    final String groupDesc = groupMessageProvider.getDescription(morphBaseId);
                    group = new GroupBuilder()
                            .setItemId(morphBaseId)
                            .setDefinitionId(morphDefault)
                            .setTitle(groupTitle)
                            .setDescription(groupDesc)
                            .build();
                    category.getItems().add(group);
                } else {
                    group = (DefaultPaletteGroup) groupOp.get();
                }
            }
        } else {
            //item has no morph base, but might belong to a custom group
            final String customGroupId = customGroupIdProvider != null ? customGroupIdProvider.apply(id) : null;
            if (customGroupId != null && groupFilter.test(customGroupId)) {
                final Optional<DefaultPaletteItem> groupOp = category.getItems().stream()
                        .filter(g -> g.getId().equals(customGroupId))
                        .findFirst();
                if (!groupOp.isPresent()) {
                    final String groupTitle = customGroupMessageProvider.getTitle(customGroupId);
                    final String groupDesc = groupMessageProvider.getDescription(customGroupId);
                    group = new GroupBuilder()
                            .setItemId(customGroupId)
                            .setTitle(groupTitle)
                            .setDescription(groupDesc)
                            .build();
                    category.getItems().add(group);
                } else {
                    group = (DefaultPaletteGroup) groupOp.get();
                }
            }
        }

        final String title = definitionAdapter.getTitle(definition);
        final String description = definitionAdapter.getDescription(definition);
        final DefaultPaletteItem item =
                new ItemBuilder()
                        .setItemId(id)
                        .setDefinitionId(id)
                        .setTitle(title)
                        .setDescription(description)
                        .build();

        if (null != group) {
            if (null != morphDefault && morphDefault.equals(id)) {
                group.getItems().add(0, item);
            } else {
                group.getItems().add(item);
            }
        } else {
            category.getItems().add(item);
        }
        return result;
    }

    private void initDefaults() {
        this
                .morphDefinitionProvider(definitionUtils::getMorphDefinition)
                .groupFilter(id -> true)
                .categoryDefinitionIdProvider(id -> null)
                .categoryGlyphProvider(DefaultPaletteDefinitionProviders.DEFAULT_CATEGORY_GLYPH_PROVIDER)
                .groupMessages(new DefaultMorphGroupMessageProvider(translationService))
                .categoryMessages(new DefaultMessageProvider());
    }

    public Function<Object, MorphDefinition> getMorphDefinitionProvider() {
        return morphDefinitionProvider;
    }

    public Predicate<String> getGroupFilter() {
        return groupFilter;
    }

    public ItemMessageProvider getCategoryMessageProvider() {
        return categoryMessageProvider;
    }

    public ItemMessageProvider getGroupMessageProvider() {
        return groupMessageProvider;
    }

    public Function<String, Glyph> getCategoryGlyphProvider() {
        return categoryGlyphProvider;
    }

    public Function<String, String> getCategoryDefinitionIdProvider() {
        return categoryDefinitionIdProvider;
    }
}
