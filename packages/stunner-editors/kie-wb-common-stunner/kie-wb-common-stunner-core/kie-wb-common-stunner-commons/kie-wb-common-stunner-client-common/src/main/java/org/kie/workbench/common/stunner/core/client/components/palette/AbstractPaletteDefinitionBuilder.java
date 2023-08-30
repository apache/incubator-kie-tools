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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.DefaultItemMessageProvider;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

public abstract class AbstractPaletteDefinitionBuilder<T extends AbstractPaletteDefinitionBuilder>
        implements PaletteDefinitionBuilder<AbstractCanvasHandler, DefaultPaletteDefinition> {

    public interface ItemMessageProvider {

        String getTitle(String id);

        String getDescription(String id);
    }

    protected final DefinitionUtils definitionUtils;
    protected final DomainProfileManager profileManager;
    protected final DefinitionsCacheRegistry definitionsRegistry;
    protected final StunnerTranslationService translationService;

    protected Predicate<String> itemFilter;
    protected Function<Object, String> categoryProvider;
    protected Predicate<String> categoryFilter;
    protected ItemMessageProvider itemMessageProvider;

    protected AbstractPaletteDefinitionBuilder(final DefinitionUtils definitionUtils,
                                               final DomainProfileManager profileManager,
                                               final DefinitionsCacheRegistry definitionsRegistry,
                                               final StunnerTranslationService translationService) {
        this.definitionUtils = definitionUtils;
        this.profileManager = profileManager;
        this.definitionsRegistry = definitionsRegistry;
        this.translationService = translationService;
        initDefaults();
    }

    public T itemFilter(final Predicate<String> definitionItemFilter) {
        this.itemFilter = definitionItemFilter;
        return cast();
    }

    public T categoryProvider(final Function<Object, String> categoryProvider) {
        this.categoryProvider = categoryProvider;
        return cast();
    }

    public T categoryFilter(final Predicate<String> categoryFilter) {
        this.categoryFilter = categoryFilter;
        return cast();
    }

    public T itemMessages(final ItemMessageProvider provider) {
        this.itemMessageProvider = provider;
        return cast();
    }

    @Override
    public void build(final AbstractCanvasHandler canvasHandler,
                      final Consumer<DefaultPaletteDefinition> paletteDefinition) {
        build(canvasHandler.getDiagram().getMetadata(),
              paletteDefinition);
    }

    public Predicate<String> getItemFilter() {
        return itemFilter;
    }

    public Predicate<String> getCategoryFilter() {
        return categoryFilter;
    }

    public Function<Object, String> getCategoryProvider() {
        return categoryProvider;
    }

    public ItemMessageProvider getItemMessageProvider() {
        return itemMessageProvider;
    }

    protected abstract DefaultPaletteItem createItem(Object definition,
                                                     String categoryId,
                                                     Metadata metadata,
                                                     Function<String, DefaultPaletteItem> itemSupplier);

    private void build(final Metadata metadata,
                       final Consumer<DefaultPaletteDefinition> paletteDefinitionConsumer) {
        final String definitionSetId = metadata.getDefinitionSetId();
        final List<String> definitions = profileManager.getAllDefinitions(metadata);
        if (null != definitions) {
            final Map<String, DefaultPaletteItem> items = new LinkedHashMap<>();
            final Set<String> consumed = new HashSet<>(definitions);
            // Once all item definitions consumed, build the resulting palette definition
            // and let the consumer do its job.
            final Command checkConsumedAndComplete = () -> {
                if (consumed.isEmpty()) {
                    paletteDefinitionConsumer.accept(new DefaultPaletteDefinition(items.values().stream()
                                                                                          .collect(Collectors.toList()),
                                                                                  definitionSetId));
                }
            };
            for (final String defId : definitions) {
                consumed.remove(defId);
                // Check if this concrete definition is excluded from the palette model.
                if (itemFilter.test(defId)) {
                    final Object def = definitionsRegistry.getDefinitionById(defId);
                    buildItem(def,
                              metadata,
                              items);
                    checkConsumedAndComplete.execute();
                } else {
                    checkConsumedAndComplete.execute();
                }
            }
        }
    }

    protected void buildItem(final Object definition,
                             final Metadata metadata,
                             final Map<String, DefaultPaletteItem> items) {
        final String categoryId = categoryProvider.apply(definition);
        // Check if this concrete category excluded from the palette model.
        if (categoryFilter.test(categoryId)) {
            final DefaultPaletteItem item = createItem(definition,
                                                       categoryId,
                                                       metadata,
                                                       items::get);
            if (null != item) {
                items.put(item.getId(), item);
            }
        }
    }

    private void initDefaults() {
        this
                .itemFilter(id -> true)
                .categoryProvider(def -> getDefinitionManager().adapters().forDefinition().getCategory(def))
                .categoryFilter(id -> true)
                .itemMessages(new DefaultItemMessageProvider(translationService));
    }

    protected DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
