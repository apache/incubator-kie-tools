/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPaletteDefinitionBuilder.ItemMessageProvider;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public class DefaultPaletteDefinitionProviders {

    public static Predicate<String> isType(final Class<?> type) {
        return id -> getId(type).equals(id);
    }

    public static String getId(final Class<?> type) {
        return BindableAdapterUtils.getGenericClassName(type);
    }

    /**
     * It uses  the identifier argument as title and description.
     */
    public static class DefaultMessageProvider implements ItemMessageProvider {

        @Override
        public String getTitle(final String id) {
            return id;
        }

        @Override
        public String getDescription(final String id) {
            return id;
        }
    }

    /**
     * It uses the translator service to obtain the category message,
     * by following the convention <code>Class<Category> + "." + id</code>
     */
    public static class DefaultCategoryMessageProvider implements ItemMessageProvider {

        private final Class<?> categoryTYpe;
        private final StunnerTranslationService translationService;

        public DefaultCategoryMessageProvider(final Class<?> categoryTYpe,
                                              final StunnerTranslationService translationService) {
            this.categoryTYpe = categoryTYpe;
            this.translationService = translationService;
        }

        @Override
        public String getTitle(final String id) {
            return getText(id);
        }

        @Override
        public String getDescription(final String id) {
            return getText(id);
        }

        private String getText(final String id) {
            final String value = translationService.getValue(categoryTYpe.getName() + "." + id);
            return null != value && value.trim().length() > 0 ? value : id;
        }
    }

    /**
     * It uses the translator service to obtain the definition's title and description,
     */
    public static class DefaultItemMessageProvider implements ItemMessageProvider {

        private final StunnerTranslationService translationService;

        public DefaultItemMessageProvider(final StunnerTranslationService translationService) {
            this.translationService = translationService;
        }

        @Override
        public String getTitle(final String id) {
            return translationService.getDefinitionTitle(id);
        }

        @Override
        public String getDescription(final String id) {
            return translationService.getDefinitionDescription(id);
        }
    }

    /**
     * It uses the translator service to obtain the group message as for a morph base type (class)
     * by following the convention <code>Class<BaseType></code>
     */
    public static class DefaultMorphGroupMessageProvider implements ItemMessageProvider {

        private final StunnerTranslationService translationService;

        public DefaultMorphGroupMessageProvider(final StunnerTranslationService translationService) {
            this.translationService = translationService;
        }

        @Override
        public String getTitle(final String id) {
            return translationService.getValue(id);
        }

        @Override
        public String getDescription(final String id) {
            return translationService.getValue(id);
        }
    }

    public static final Function<String, Glyph> DEFAULT_CATEGORY_GLYPH_PROVIDER =
            category -> SvgDataUriGlyph.Builder.build(StunnerCommonImageResources.INSTANCE.gears().getSafeUri());

    /**
     * The attributes holder for rendering a category in the palette.
     */
    public static class CategoryDefinitionProvider {

        private final Class<?> categoryTYpe;
        private final Map<String, CategoryDefinition> definitions;

        public CategoryDefinitionProvider(final Class<?> categoryTYpe) {
            this.categoryTYpe = categoryTYpe;
            this.definitions = new LinkedHashMap<>();
        }

        public CategoryDefinitionProvider put(final String categoryId,
                                              final Consumer<CategoryDefinition> definitionConsumer) {
            if (definitions.containsKey(categoryId)) {
                throw new IllegalArgumentException("Setting the definition for an already existing palette category. " +
                                                           "[category=" + categoryId + "]");
            }
            CategoryDefinition definition = new CategoryDefinition(categoryId);
            definitions.put(categoryId, definition);
            definitionConsumer.accept(definition);
            return this;
        }

        public Function<String, String> definitionIdProvider() {
            return catId -> {
                final CategoryDefinition definition = definitions.get(catId);
                return null != definition ?
                        getDefinitionId(definition.defaultDefinitionType.orElse(null)) :
                        null;
            };
        }

        public Function<String, Glyph> glyphProvider() {
            return catId -> {
                final CategoryDefinition definition = definitions.get(catId);
                return null != definition ? definition.glyph.orElse(null) : null;
            };
        }

        public ItemMessageProvider categoryMessageProvider(final StunnerTranslationService translationService) {
            return new DefaultCategoryMessageProvider(categoryTYpe,
                                                      translationService);
        }
    }

    public static class CategoryDefinition {

        private final String id;
        private Optional<Class<?>> defaultDefinitionType;
        private Optional<Glyph> glyph;

        private CategoryDefinition(final String id) {
            this.id = id;
            this.defaultDefinitionType = Optional.empty();
            this.glyph = Optional.empty();
        }

        public CategoryDefinition bindToDefinition(final Class<?> type) {
            this.defaultDefinitionType = Optional.of(type);
            return this;
        }

        public CategoryDefinition useGlyph(final Glyph glyph) {
            this.glyph = Optional.of(glyph);
            return this;
        }
    }

    private static String getDefinitionId(final Class<?> type) {
        return null != type ? BindableAdapterUtils.getGenericClassName(type) : null;
    }
}
