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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3;

import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.uberfire.client.mvp.UberView;

@Dependent
public class BS3PaletteCategories
        extends AbstractPalette<HasPaletteItems<DefinitionPaletteCategory>>
        implements IsWidget {

    private static Logger LOGGER = Logger.getLogger(BS3PaletteCategories.class.getName());

    public interface View extends UberView<BS3PaletteCategories> {

        View setPadding(final int padding);

        View setIconWidth(final int iconSize);

        View setIconHeight(final int iconSize);

        View setBackgroundColor(final String color);

        View add(final String categoryId,
                 final String categoryTitle,
                 final String categoryGlyphId,
                 final IsWidget view);

        View clear();
    }

    View view;

    BS3PaletteWidgetImpl bs3PaletteWidget;

    @Inject
    public BS3PaletteCategories(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public BS3PaletteCategories setPadding(final int padding) {
        view.setPadding(padding);
        return this;
    }

    public BS3PaletteCategories setIconWidth(final int iconSize) {
        view.setIconWidth(iconSize);
        return this;
    }

    public BS3PaletteCategories setIconHeight(final int iconSize) {
        view.setIconHeight(iconSize);
        return this;
    }

    public BS3PaletteCategories setBackgroundColor(final String color) {
        view.setBackgroundColor(color);
        return this;
    }

    @Override
    protected AbstractPalette<HasPaletteItems<DefinitionPaletteCategory>> bind() {
        final HasPaletteItems<DefinitionPaletteCategory> categoryItems = paletteDefinition;
        final List<DefinitionPaletteCategory> categories = categoryItems.getItems();
        if (null != categories && !categories.isEmpty()) {
            for (final DefinitionPaletteCategory category : categories) {
                view.add(category.getId(),
                         category.getTitle(),
                         category.getDefinitionId(),
                         bs3PaletteWidget.getCategoryView(category.getId()));
            }
        } else {
            clear();
        }

        return null;
    }

    public void clear() {
        view.clear();
    }

    public View getView() {
        return view;
    }

    @Override
    protected void doDestroy() {
        view.clear();
        this.view = null;
    }

    @Override
    protected String getPaletteItemId(final int index) {
        final HasPaletteItems<DefinitionPaletteCategory> categoryItems = paletteDefinition;
        final List<DefinitionPaletteCategory> categories = categoryItems.getItems();
        if (null != categories && categories.size() > index) {
            return categories.get(index).getId();
        }
        return null;
    }

    void onItemHover(final String id,
                     final int mouseX,
                     final int mouseY,
                     final int itemX,
                     final int itemY) {
        if (null != itemHoverCallback) {
            itemHoverCallback.onItemHover(id,
                                          mouseX,
                                          mouseY,
                                          itemX,
                                          itemY);
        }
    }

    void onItemOut(final String id) {
        if (null != itemOutCallback) {
            itemOutCallback.onItemOut(id);
        }
    }

    void onItemClick(final String id,
                     final int mouseX,
                     final int mouseY,
                     final int itemX,
                     final int itemY) {
        if (null != itemClickCallback) {
            itemClickCallback.onItemClick(id,
                                          mouseX,
                                          mouseY,
                                          itemX,
                                          itemY);
        }
    }

    void onItemMouseDown(final String id,
                         final int mouseX,
                         final int mouseY,
                         final int itemX,
                         final int itemY) {
        if (null != itemMouseDownCallback) {
            itemMouseDownCallback.onItemMouseDown(id,
                                                  mouseX,
                                                  mouseY,
                                                  itemX,
                                                  itemY);
        }
    }

    private int getIndex(final String categoryId) {
        final List<DefinitionPaletteCategory> categories = paletteDefinition.getItems();
        if (null != categories && !categories.isEmpty()) {
            int x = 0;
            for (final DefinitionPaletteCategory category : categories) {
                if (category.getId().equals(categoryId)) {
                    return x;
                }
                x++;
            }
        }
        return -1;
    }
}
