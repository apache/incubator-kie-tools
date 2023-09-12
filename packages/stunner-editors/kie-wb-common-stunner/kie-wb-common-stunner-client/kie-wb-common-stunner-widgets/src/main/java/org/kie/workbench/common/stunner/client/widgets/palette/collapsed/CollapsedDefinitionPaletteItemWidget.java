/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.palette.collapsed;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedDefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class CollapsedDefinitionPaletteItemWidget implements CollapsedDefinitionPaletteItemWidgetView.Presenter {

    private final CollapsedDefinitionPaletteItemWidgetView view;

    private CollapsedDefaultPaletteItem item;
    private Consumer<PaletteItemMouseEvent> itemMouseDownCallback;

    @Inject
    public CollapsedDefinitionPaletteItemWidget(final CollapsedDefinitionPaletteItemWidgetView view) {
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
    public void initialize(final CollapsedDefaultPaletteItem item,
                           final ShapeFactory<?, ?> shapeFactory,
                           final Consumer<PaletteItemMouseEvent> itemMouseDownCallback) {
        this.item = item;
        final Glyph glyph = shapeFactory.getGlyph(item.getDefinitionId(),
                                                  AbstractPalette.PaletteGlyphConsumer.class);
        this.itemMouseDownCallback = itemMouseDownCallback;
        view.render(glyph,
                    item.getIconSize(),
                    item.getIconSize());
    }

    @Override
    public CollapsedDefaultPaletteItem getItem() {
        return item;
    }

    @Override
    public void onMouseDown(final int clientX,
                            final int clientY,
                            final int x,
                            final int y) {
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
