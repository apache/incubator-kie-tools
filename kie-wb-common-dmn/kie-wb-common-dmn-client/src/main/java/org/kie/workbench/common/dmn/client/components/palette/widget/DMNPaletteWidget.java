/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.components.palette.widget;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BS3PaletteViewFactory;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class DMNPaletteWidget extends AbstractPalette<DefinitionsPalette>
        implements BS3PaletteWidget<DefinitionsPalette>,
                   IsElement {

    public static final String BG_COLOR = "#D3D3D3";

    private static final int GLYPH_ICON_SIZE = 30;

    protected final ClientFactoryService clientFactoryServices;
    protected ItemDropCallback itemDropCallback;
    protected ItemDragStartCallback itemDragStartCallback;
    protected ItemDragUpdateCallback itemDragUpdateCallback;

    private ManagedInstance<DMNPaletteItemWidget> paletteItemWidgets;

    private BS3PaletteViewFactory viewFactory;

    private DMNPaletteWidgetView view;

    @Inject
    public DMNPaletteWidget(final ShapeManager shapeManager,
                            final ClientFactoryService clientFactoryServices,
                            final DMNPaletteWidgetView view,
                            final ManagedInstance<DMNPaletteItemWidget> paletteItemWidgets) {
        super(shapeManager);
        this.clientFactoryServices = clientFactoryServices;
        this.view = view;
        this.paletteItemWidgets = paletteItemWidgets;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setBackgroundColor(BG_COLOR);
        view.showEmptyView(true);
    }

    @Override
    public BS3PaletteWidget<DefinitionsPalette> onItemDrop(final ItemDropCallback callback) {
        this.itemDropCallback = callback;
        return this;
    }

    @Override
    public BS3PaletteWidget<DefinitionsPalette> onItemDragStart(final ItemDragStartCallback callback) {
        this.itemDragStartCallback = callback;
        return this;
    }

    @Override
    public BS3PaletteWidget<DefinitionsPalette> onItemDragUpdate(final ItemDragUpdateCallback callback) {
        this.itemDragUpdateCallback = callback;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDragStart(final String definitionId,
                            final double x,
                            final double y) {
        if (null != itemDragStartCallback) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(definitionId);
            final ShapeFactory<?, ? extends Shape> factory = getShapeFactory();
            itemDragStartCallback.onDragStartItem(definition,
                                                  factory,
                                                  x,
                                                  y);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDragProxyMove(final String definitionId,
                                final double x,
                                final double y) {
        if (null != itemDragUpdateCallback) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(definitionId);
            final ShapeFactory<?, ? extends Shape> factory = getShapeFactory();
            itemDragUpdateCallback.onDragUpdateItem(definition,
                                                    factory,
                                                    x,
                                                    y);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDragProxyComplete(final String definitionId,
                                    final double x,
                                    final double y) {
        if (null != itemDropCallback) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(definitionId);
            final ShapeFactory<?, ? extends Shape> factory = getShapeFactory();
            itemDropCallback.onDropItem(definition,
                                        factory,
                                        x,
                                        y);
        }
    }

    @Override
    public BS3PaletteWidget setViewFactory(final BS3PaletteViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        return this;
    }

    @Override
    public Glyph getShapeGlyph(final String definitionId) {
        return getShapeFactory().getGlyph(definitionId);
    }

    @Override
    protected String getPaletteItemId(final int index) {
        return paletteDefinition.getItems().get(index).getId();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    protected ShapeFactory getShapeFactory() {
        return shapeManager.getDefaultShapeSet(paletteDefinition.getDefinitionSetId()).getShapeFactory();
    }

    @Override
    protected void beforeBind() {
        view.clear();
        view.showEmptyView(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AbstractPalette<DefinitionsPalette> bind() {
        final DefinitionsPalette palette = paletteDefinition;
        if (null != palette) {
            palette.getItems().forEach(pi -> {
                final DMNPaletteItemWidget paletteItemWidget = paletteItemWidgets.get();
                paletteItemWidget.initialize(pi,
                                             getShapeFactory(),
                                             (id, mouseX, mouseY, itemX, itemY) -> {
                                                 view.showDragProxy(id,
                                                                    mouseX,
                                                                    mouseY,
                                                                    GLYPH_ICON_SIZE,
                                                                    GLYPH_ICON_SIZE);
                                                 return true;
                                             });

                view.add(paletteItemWidget);
            });
        }
        return this;
    }

    @Override
    public void unbind() {
        if (null != paletteDefinition) {
            view.clear();
            view.showEmptyView(true);
            this.paletteDefinition = null;
        }
    }

    @PreDestroy
    @Override
    protected void doDestroy() {
        paletteItemWidgets.destroyAll();
        viewFactory.destroy();
        view.destroy();
        this.itemDropCallback = null;
    }
}
