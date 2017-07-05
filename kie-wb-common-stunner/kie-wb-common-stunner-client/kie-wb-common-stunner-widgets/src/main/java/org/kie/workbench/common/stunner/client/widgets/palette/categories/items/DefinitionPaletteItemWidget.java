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
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class DefinitionPaletteItemWidget implements DefinitionPaletteItemWidgetView.Presenter,
                                                    IsElement {

    public static final double ICON_WIDTH = 15;
    public static final double ICON_HEIGHT = 15;

    private final ShapeManager shapeManager;
    private final DefinitionPaletteItemWidgetView view;

    private DefinitionPaletteItem item;
    private Palette.ItemMouseDownCallback itemMouseDownCallback;

    @Inject
    public DefinitionPaletteItemWidget(final ShapeManager shapeManager,
                                       final DefinitionPaletteItemWidgetView view) {
        this.shapeManager = shapeManager;
        this.view = view;
    }

    @PostConstruct
    public void setUp() {
        view.init(this);
    }

    public void initialize(DefinitionPaletteItem item,
                           ShapeFactory<?, ?> shapeFactory,
                           Palette.ItemMouseDownCallback itemMouseDownCallback) {
        this.item = item;
        final Glyph glyph = shapeFactory.getGlyph(item.getDefinitionId());
        this.itemMouseDownCallback = itemMouseDownCallback;
        view.render(glyph,
                    ICON_WIDTH,
                    ICON_HEIGHT);
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
