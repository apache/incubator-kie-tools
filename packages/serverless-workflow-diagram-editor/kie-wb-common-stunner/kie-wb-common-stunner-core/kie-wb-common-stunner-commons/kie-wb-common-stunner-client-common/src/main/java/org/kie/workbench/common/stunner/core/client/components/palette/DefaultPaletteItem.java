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

public class DefaultPaletteItem implements PaletteItem {

    private final String itemId;
    private final String title;
    private final String description;
    private final String tooltip;
    private String definitionId;
    private int iconSize;

    DefaultPaletteItem(final String itemId,
                       final String definitionId,
                       final String title,
                       final String description,
                       final String tooltip,
                       final int iconSize) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.tooltip = tooltip;
        this.definitionId = definitionId;
        this.iconSize = iconSize;
    }

    public void setDefinitionId(final String definitionId) {
        this.definitionId = definitionId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public int getIconSize() {
        return iconSize;
    }

    public DefaultPaletteItem setIconSize(final int iconSize) {
        this.iconSize = iconSize;
        return this;
    }

    @Override
    public String getId() {
        return itemId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultPaletteItem)) {
            return false;
        }
        DefaultPaletteItem that = (DefaultPaletteItem) o;
        return itemId != null && itemId.equals(that.itemId);
    }

    @Override
    public int hashCode() {
        return itemId == null ? 0 : ~~itemId.hashCode();
    }
}
