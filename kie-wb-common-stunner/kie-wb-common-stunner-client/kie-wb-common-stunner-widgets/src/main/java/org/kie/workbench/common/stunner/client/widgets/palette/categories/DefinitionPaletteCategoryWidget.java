/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;

@Dependent
public class DefinitionPaletteCategoryWidget implements DefinitionPaletteCategoryWidgetView.Presenter,
                                                        IsElement {

  private DefinitionPaletteCategory category;
  private Palette.ItemMouseDownCallback itemMouseDownCallback;
  private PaletteWidget.IconRendererProvider iconRendererProvider;

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

  public void initialize(DefinitionPaletteCategory category,
                         PaletteWidget.IconRendererProvider iconRendererProvider,
                         Palette.ItemMouseDownCallback itemMouseDownCallback) {
    this.category = category;
    this.itemMouseDownCallback = itemMouseDownCallback;
    this.iconRendererProvider = iconRendererProvider;

    IconRenderer iconRenderer = iconRendererProvider.getCategoryIconRenderer(category);

    iconRenderer.resize(IconRenderer.Size.LARGE);

    view.render(iconRenderer);

    renderItems(category.getItems());
  }

  private void renderItems(List<DefinitionPaletteItem> items) {
    if (items != null && !items.isEmpty()) {
      items.forEach(item -> {
        if (item instanceof DefinitionPaletteGroup) {

          renderGroup((DefinitionPaletteGroup) item);
        } else {
          DefinitionPaletteItemWidget categoryItemWidget = definitionPaletteItemWidgetInstance.get();

          categoryItemWidget.initialize(item,
                                        iconRendererProvider,
                                        itemMouseDownCallback);

          view.addItem(categoryItemWidget);
        }
      });
    }
  }

  private void renderGroup(DefinitionPaletteGroup group) {
    DefinitionPaletteGroupWidget groupWidget = definitionPaletteGroupWidgetInstance.get();

    groupWidget.initialize(group,
                           iconRendererProvider,
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
