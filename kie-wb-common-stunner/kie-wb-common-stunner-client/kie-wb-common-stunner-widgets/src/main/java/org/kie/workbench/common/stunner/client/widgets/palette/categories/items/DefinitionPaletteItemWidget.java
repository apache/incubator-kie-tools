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

package org.kie.workbench.common.stunner.client.widgets.palette.categories.items;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class DefinitionPaletteItemWidget implements DefinitionPaletteItemWidgetView.Presenter {

    private final DefinitionPaletteItemWidgetView view;

    private DefaultPaletteItem item;
    private Consumer<PaletteItemMouseEvent> itemMouseDownCallback;

    @Inject
    public DefinitionPaletteItemWidget(final DefinitionPaletteItemWidgetView view) {
        this.view = view;
    }

    @PostConstruct
    public void setUp() {
        view.init(this);
    }

    @PreDestroy
    public void destroy() {
        item = null;
        itemMouseDownCallback = null;
    }

    @Override
    public void initialize(DefaultPaletteItem item,
                           ShapeFactory<?, ?> shapeFactory,
                           Consumer<PaletteItemMouseEvent> itemMouseDownCallback) {
        this.item = item;
        final Glyph glyph = shapeFactory.getGlyph(item.getDefinitionId());
        this.itemMouseDownCallback = itemMouseDownCallback;
        view.render(glyph,
                    item.getIconSize(),
                    item.getIconSize());
    }

    @Override
    public DefaultPaletteItem getItem() {
        return item;
    }

    @Override
    public void onMouseDown(int clientX,
                            int clientY,
                            int x,
                            int y) {
        if (itemMouseDownCallback != null) {
            itemMouseDownCallback.accept(new PaletteItemMouseEvent(item.getId(),
                                                                   clientX,
                                                                   clientY,
                                                                   x,
                                                                   y));
        }
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
