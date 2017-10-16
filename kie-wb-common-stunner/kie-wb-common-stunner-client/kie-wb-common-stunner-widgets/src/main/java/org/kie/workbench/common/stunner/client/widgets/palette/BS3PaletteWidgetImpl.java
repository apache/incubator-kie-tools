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

package org.kie.workbench.common.stunner.client.widgets.palette;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BS3PaletteViewFactory;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class BS3PaletteWidgetImpl extends AbstractPalette<DefinitionSetPalette>
        implements BS3PaletteWidget<DefinitionSetPalette>,
                   IsElement {

    public static final String BG_COLOR = "#D3D3D3";

    private static final int GLYPH_ICON_SIZE = 30;
    private static final int PADDING = 10;

    protected final ClientFactoryService clientFactoryServices;
    protected ItemDropCallback itemDropCallback;
    protected ItemDragStartCallback itemDragStartCallback;
    protected ItemDragUpdateCallback itemDragUpdateCallback;
    private final ShapeGlyphDragHandler shapeGlyphDragHandler;

    private BS3PaletteViewFactory viewFactory;

    private ManagedInstance<DefinitionPaletteCategoryWidget> categoryWidgetInstance;
    private BS3PaletteWidgetView view;

    @Inject
    public BS3PaletteWidgetImpl(final ShapeManager shapeManager,
                                final ClientFactoryService clientFactoryServices,
                                final BS3PaletteWidgetView view,
                                final ShapeGlyphDragHandler shapeGlyphDragHandler,
                                final ManagedInstance<DefinitionPaletteCategoryWidget> categoryWidgetInstance) {
        super(shapeManager);
        this.clientFactoryServices = clientFactoryServices;
        this.view = view;
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
        this.categoryWidgetInstance = categoryWidgetInstance;
    }

    public static int getDefaultWidth() {
        return GLYPH_ICON_SIZE + PADDING;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setShapeGlyphDragHandler(shapeGlyphDragHandler);
        view.setBackgroundColor(BG_COLOR);
        view.showEmptyView(true);
    }

    @Override
    public BS3PaletteWidget onItemDrop(ItemDropCallback callback) {
        this.itemDropCallback = callback;
        return this;
    }

    @Override
    public BS3PaletteWidget onItemDragStart(final ItemDragStartCallback callback) {
        this.itemDragStartCallback = callback;
        return this;
    }

    @Override
    public BS3PaletteWidget onItemDragUpdate(final ItemDragUpdateCallback callback) {
        this.itemDragUpdateCallback = callback;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void onDragStart(final String definitionId,
                            final double x,
                            final double y) {
        if (null != itemDragStartCallback) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(definitionId);
            final ShapeFactory<?, ? extends Shape> factory = getShapeFactory();
            // Fire the callback as shape drag starts.
            itemDragStartCallback.onDragStartItem(definition,
                                                  factory,
                                                  x,
                                                  y);
        }
    }

    @Override
    public void onDragProxyMove(String definitionId,
                                double x,
                                double y) {
        if (null != itemDragUpdateCallback) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(definitionId);
            final ShapeFactory<?, ? extends Shape> factory = getShapeFactory();
            // Fire the callback as shape dragged over the target canvas.
            itemDragUpdateCallback.onDragUpdateItem(definition,
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

    public void unbind() {
        if (null != paletteDefinition) {
            view.clear();
            view.showEmptyView(true);
            this.paletteDefinition = null;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        view.showEmptyView(!visible);
    }

    protected ShapeFactory getShapeFactory() {
        final DefinitionSetPalette palette = paletteDefinition;
        return shapeManager.getDefaultShapeSet(palette.getDefinitionSetId()).getShapeFactory();
    }

    public double getIconSize() {
        return GLYPH_ICON_SIZE;
    }

    @Override
    protected void beforeBind() {
        view.clear();
        view.showEmptyView(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AbstractPalette<DefinitionSetPalette> bind() {
        final DefinitionSetPalette palette = paletteDefinition;
        if (null != palette) {
            palette.getItems().forEach(definitionPaletteCategory -> {
                final DefinitionPaletteCategoryWidget widget = categoryWidgetInstance.get();
                widget.initialize(definitionPaletteCategory,
                                  viewFactory,
                                  getShapeFactory(),
                                  (id, mouseX, mouseY, itemX, itemY) -> {
                                      PortablePreconditions.checkNotNull("id",
                                                                         id);
                                      final String catDefId = getDefinitionIdForCategory(id);
                                      if (null != catDefId) {
                                          BS3PaletteWidgetImpl.this.onPaletteItemMouseDown(catDefId,
                                                                                           mouseX,
                                                                                           mouseY);
                                          return true;
                                      }
                                      BS3PaletteWidgetImpl.this.onPaletteItemMouseDown(id,
                                                                                       mouseX,
                                                                                       mouseY);
                                      return true;
                                  });

                view.add(widget);
            });
        }
        return this;
    }

    @Override
    public void onDragProxyComplete(final String definitionId,
                                    final double x,
                                    final double y) {
        if (null != itemDropCallback) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(definitionId);
            final ShapeFactory<?, ? extends Shape> factory = getShapeFactory();
            // Fire the callback as shape dropped onto the target canvas.
            itemDropCallback.onDropItem(definition,
                                        factory,
                                        x,
                                        y);
        }
    }

    @Override
    public Glyph getShapeGlyph(final String definitionId) {
        return getShapeFactory().getGlyph(definitionId);
    }

    private String getDefinitionIdForCategory(final String id) {
        final DefinitionPaletteCategory categoryItem = getMainPaletteItem(id);
        return null != categoryItem ? categoryItem.getDefinitionId() : null;
    }

    private void onPaletteItemMouseDown(final String id,
                                        final double x,
                                        final double y) {
        showDragProxy(id,
                      x,
                      y);
    }

    private void showDragProxy(final String id,
                               final double x,
                               final double y) {
        // Show the drag proxy for the element at x, y.
        view.showDragProxy(id,
                           x,
                           y,
                           getIconSize(),
                           getIconSize());
    }

    private List<DefinitionPaletteCategory> getMainPaletteItems() {
        return paletteDefinition.getItems();
    }

    private DefinitionPaletteCategory getMainPaletteItem(final int index) {
        return getMainPaletteItems().get(index);
    }

    private DefinitionPaletteCategory getMainPaletteItem(final String id) {
        final List<DefinitionPaletteCategory> categories = getMainPaletteItems();
        if (null != categories && !categories.isEmpty()) {
            for (final DefinitionPaletteCategory category : categories) {
                if (category.getId().equals(id)) {
                    return category;
                }
            }
        }
        return null;
    }

    void onCanvasFocusedEvent(final @Observes CanvasFocusedEvent canvasFocusedEvent) {
        checkNotNull("canvasFocusedEvent",
                     canvasFocusedEvent);
    }

    void onCanvasElementSelectedEvent(final @Observes CanvasElementSelectedEvent canvasElementSelectedEvent) {
        checkNotNull("canvasElementSelectedEvent",
                     canvasElementSelectedEvent);
    }

    @PreDestroy
    @Override
    protected void doDestroy() {
        categoryWidgetInstance.destroyAll();
        viewFactory.destroy();
        view.destroy();
        this.itemDropCallback = null;
    }

    @Override
    protected String getPaletteItemId(final int index) {
        final DefinitionPaletteCategory item = getMainPaletteItem(index);
        return null != item ? item.getId() : null;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
