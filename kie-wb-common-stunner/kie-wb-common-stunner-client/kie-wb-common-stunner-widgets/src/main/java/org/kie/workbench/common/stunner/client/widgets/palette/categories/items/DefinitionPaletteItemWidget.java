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

package org.kie.workbench.common.stunner.client.widgets.palette.categories.items;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;

@Dependent
public class DefinitionPaletteItemWidget implements DefinitionPaletteItemWidgetView.Presenter,
                                                    IsElement {

    private DefinitionPaletteItemWidgetView view;

    private DefinitionPaletteItem item;
    private Palette.ItemMouseDownCallback itemMouseDownCallback;

    @Inject
    public DefinitionPaletteItemWidget(DefinitionPaletteItemWidgetView view) {
        this.view = view;
    }

    @PostConstruct
    public void setUp() {
        view.init(this);
    }

    public void initialize(DefinitionPaletteItem item,
                           PaletteWidget.IconRendererProvider iconRendererProvider,
                           Palette.ItemMouseDownCallback itemMouseDownCallback) {
        this.item = item;
        this.itemMouseDownCallback = itemMouseDownCallback;
        IconRenderer iconRenderer = iconRendererProvider.getDefinitionIconRenderer(item);
        iconRenderer.resize(IconRenderer.Size.SMALL);
        view.render(iconRenderer);
    }

    @Override
    public DefinitionPaletteItem getItem() {
        return item;
    }

    @Override
    public void onMouseDown(int clientX,
                            int clientY,
                            int x,
                            int y) {
        if (itemMouseDownCallback != null) {
            itemMouseDownCallback.onItemMouseDown(item.getId(),
                                                  clientX,
                                                  clientY,
                                                  x,
                                                  y);
        }
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
