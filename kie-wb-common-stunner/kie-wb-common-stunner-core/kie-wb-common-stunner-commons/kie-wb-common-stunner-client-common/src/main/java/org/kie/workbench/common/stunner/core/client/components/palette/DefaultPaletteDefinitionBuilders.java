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

import java.util.LinkedList;

import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public class DefaultPaletteDefinitionBuilders {

    public static final int DEFAULT_ICON_SIZE = 15;

    public static abstract class AbstractItemBuilder<I extends DefaultPaletteItem, T extends AbstractItemBuilder> {

        protected String itemId;
        protected String title;
        protected String description;
        protected String tooltip;
        protected String definitionId;
        protected int iconSize;

        public AbstractItemBuilder() {
            this.itemId = null;
            this.title = "";
            this.description = "";
            this.tooltip = "";
            this.definitionId = null;
            this.iconSize = DEFAULT_ICON_SIZE;
        }

        public abstract I build();

        public T setItemId(final String itemId) {
            this.itemId = itemId;
            return cast();
        }

        public T setTitle(final String title) {
            this.title = title;
            return cast();
        }

        public T setDescription(final String description) {
            this.description = description;
            return cast();
        }

        public T setTooltip(final String tooltip) {
            this.tooltip = tooltip;
            return cast();
        }

        public T setDefinitionId(final String definitionId) {
            this.definitionId = definitionId;
            return cast();
        }

        public T setIconSize(final int iconSize) {
            this.iconSize = iconSize;
            return cast();
        }

        @SuppressWarnings("unchecked")
        private T cast() {
            return (T) this;
        }
    }

    public static class ItemBuilder extends AbstractItemBuilder<DefaultPaletteItem, ItemBuilder> {

        public DefaultPaletteItem build() {
            return new DefaultPaletteItem(itemId,
                                          definitionId,
                                          title,
                                          description,
                                          tooltip,
                                          iconSize);
        }
    }

    public static class GroupBuilder extends AbstractItemBuilder<DefaultPaletteGroup, GroupBuilder> {

        public DefaultPaletteGroup build() {
            return new DefaultPaletteGroup(itemId,
                                           definitionId,
                                           title,
                                           description,
                                           tooltip,
                                           iconSize,
                                           new LinkedList<>());
        }
    }

    public static class CategoryBuilder extends AbstractItemBuilder<DefaultPaletteCategory, CategoryBuilder> {

        private static final int ICON_SIZE = 20;

        private Glyph glyph;

        public CategoryBuilder() {
            this.iconSize = ICON_SIZE;
        }

        public CategoryBuilder setGlyph(final Glyph glyph) {
            this.glyph = glyph;
            return this;
        }

        public DefaultPaletteCategory build() {
            return new DefaultPaletteCategory(itemId,
                                              definitionId,
                                              title,
                                              description,
                                              tooltip,
                                              iconSize,
                                              new LinkedList<>(),
                                              glyph);
        }
    }
}
