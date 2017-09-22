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

package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BS3PaletteViewFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class DefinitionPaletteCategoryWidget implements DefinitionPaletteCategoryWidgetView.Presenter,
                                                        IsElement {

    private static final double ICON_WIDTH = 20;
    private static final double ICON_HEIGHT = 20;

    private DefinitionPaletteCategory category;
    private Palette.ItemMouseDownCallback itemMouseDownCallback;

    private DefinitionPaletteCategoryWidgetView view;
    private ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgetInstance;
    private ManagedInstance<DefinitionPaletteGroupWidget> definitionPaletteGroupWidgetInstance;

    @Inject
    public DefinitionPaletteCategoryWidget(DefinitionPaletteCategoryWidgetView view,
                                           ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgetInstance,
                                           ManagedInstance<DefinitionPaletteGroupWidget> definitionPaletteGroupWidgetInstance) {
        this.view = view;
        this.definitionPaletteItemWidgetInstance = definitionPaletteItemWidgetInstance;
        this.definitionPaletteGroupWidgetInstance = definitionPaletteGroupWidgetInstance;
    }

    @PostConstruct
    public void setUp() {
        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void initialize(final DefinitionPaletteCategory category,
                           final BS3PaletteViewFactory viewFactory,
                           final ShapeFactory<?, ?> shapeFactory,
                           final Palette.ItemMouseDownCallback itemMouseDownCallback) {
        this.category = category;
        this.itemMouseDownCallback = itemMouseDownCallback;
        final Glyph categoryGlyph = viewFactory.getCategoryGlyph(category.getId());
        view.render(categoryGlyph,
                    ICON_WIDTH,
                    ICON_HEIGHT);
        renderItems(category.getItems(),
                    shapeFactory);
    }

    private void renderItems(final List<DefinitionPaletteItem> items,
                             final ShapeFactory<?, ?> shapeFactory) {
        if (items != null && !items.isEmpty()) {
            items.forEach(item -> {
                if (item instanceof DefinitionPaletteGroup) {

                    renderGroup((DefinitionPaletteGroup) item,
                                shapeFactory);
                } else {
                    DefinitionPaletteItemWidget categoryItemWidget = definitionPaletteItemWidgetInstance.get();

                    categoryItemWidget.initialize(item,
                                                  shapeFactory,
                                                  itemMouseDownCallback);

                    view.addItem(categoryItemWidget);
                }
            });
        }
    }

    private void renderGroup(final DefinitionPaletteGroup group,
                             final ShapeFactory<?, ?> shapeFactory) {
        DefinitionPaletteGroupWidget groupWidget = definitionPaletteGroupWidgetInstance.get();

        groupWidget.initialize(group,
                               shapeFactory,
                               itemMouseDownCallback);

        view.addGroup(groupWidget);
    }

    public DefinitionPaletteCategoryWidgetView getView() {
        return view;
    }

    @Override
    public DefinitionPaletteCategory getCategory() {
        return category;
    }

    @Override
    public void onMouseDown(int clientX,
                            int clientY,
                            int x,
                            int y) {
        if (itemMouseDownCallback != null) {
            itemMouseDownCallback.onItemMouseDown(category.getId(),
                                                  clientX,
                                                  clientY,
                                                  x,
                                                  y);
        }
    }

    @PreDestroy
    public void destroy() {
        definitionPaletteItemWidgetInstance.destroyAll();
        definitionPaletteGroupWidgetInstance.destroyAll();
    }
}
