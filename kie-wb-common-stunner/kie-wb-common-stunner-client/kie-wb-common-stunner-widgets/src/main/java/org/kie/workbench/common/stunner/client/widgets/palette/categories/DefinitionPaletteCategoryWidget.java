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
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class DefinitionPaletteCategoryWidget implements DefinitionPaletteCategoryWidgetView.Presenter {

    private DefaultPaletteCategory category;
    private Consumer<PaletteItemMouseEvent> itemMouseDownCallback;

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

    @Override
    public void initialize(final DefaultPaletteCategory category,
                           final ShapeFactory<?, ?> shapeFactory,
                           final Consumer<PaletteItemMouseEvent> itemMouseDownCallback) {
        this.category = category;
        this.itemMouseDownCallback = itemMouseDownCallback;
        final Glyph categoryGlyph = category.getGlyph();
        view.render(categoryGlyph,
                    category.getIconSize(),
                    category.getIconSize());
        renderItems(category.getItems(),
                    shapeFactory);
    }

    private void renderItems(final List<DefaultPaletteItem> items,
                             final ShapeFactory<?, ?> shapeFactory) {
        if (items != null && !items.isEmpty()) {
            items.forEach(item -> {
                if (item instanceof PaletteGroup) {

                    renderGroup((DefaultPaletteGroup) item,
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

    private void renderGroup(final DefaultPaletteGroup group,
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
    public DefaultPaletteCategory getCategory() {
        return category;
    }

    @Override
    public void onMouseDown(int clientX,
                            int clientY,
                            int x,
                            int y) {
        if (itemMouseDownCallback != null) {
            itemMouseDownCallback.accept(new PaletteItemMouseEvent(category.getId(),
                                                                   clientX,
                                                                   clientY,
                                                                   x,
                                                                   y));
        }
    }

    @PreDestroy
    public void destroy() {
        definitionPaletteItemWidgetInstance.destroyAll();
        definitionPaletteGroupWidgetInstance.destroyAll();
    }
}
