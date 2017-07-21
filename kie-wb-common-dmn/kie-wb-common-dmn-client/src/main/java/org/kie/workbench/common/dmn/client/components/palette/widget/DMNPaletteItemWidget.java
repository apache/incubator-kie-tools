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

package org.kie.workbench.common.dmn.client.components.palette.widget;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class DMNPaletteItemWidget implements DMNPaletteItemWidgetView.Presenter,
                                             IsElement {

    public static final double ICON_WIDTH = 32;
    public static final double ICON_HEIGHT = 32;

    private final DMNPaletteItemWidgetView view;

    private DefinitionPaletteItem item;
    private Palette.ItemMouseDownCallback itemMouseDownCallback;

    @Inject
    public DMNPaletteItemWidget(final DMNPaletteItemWidgetView view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void initialize(final DefinitionPaletteItem item,
                           final ShapeFactory<?, ?> shapeFactory,
                           final Palette.ItemMouseDownCallback itemMouseDownCallback) {
        this.item = item;
        this.itemMouseDownCallback = itemMouseDownCallback;

        final Glyph glyph = shapeFactory.getGlyph(item.getDefinitionId());
        view.render(glyph,
                    ICON_WIDTH,
                    ICON_HEIGHT);
    }

    @Override
    public DefinitionPaletteItem getItem() {
        return item;
    }

    @Override
    public void onMouseDown(final int clientX,
                            final int clientY,
                            final int x,
                            final int y) {
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
