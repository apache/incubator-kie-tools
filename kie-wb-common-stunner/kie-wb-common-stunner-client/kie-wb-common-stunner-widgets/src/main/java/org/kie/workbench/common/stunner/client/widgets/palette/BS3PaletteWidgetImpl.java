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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedDefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteUtils;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;

@Dependent
public class BS3PaletteWidgetImpl
        extends AbstractPalette<DefaultPaletteDefinition>
        implements BS3PaletteWidget,
                   IsElement {

    private static final int GLYPH_ICON_SIZE = 30;
    private static final int PADDING = 10;

    private final ClientFactoryService clientFactoryServices;
    private final ShapeGlyphDragHandler shapeGlyphDragHandler;
    private final StunnerPreferencesRegistries preferencesRegistries;
    private final ManagedInstance<DefinitionPaletteCategoryWidget> categoryWidgetInstances;
    private final ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgetInstances;
    private final ManagedInstance<CollapsedDefinitionPaletteItemWidget> collapsedDefinitionPaletteItemWidgets;
    private Consumer<PaletteIDefinitionItemEvent> itemDropCallback;
    private Consumer<PaletteIDefinitionItemEvent> itemDragStartCallback;
    private Consumer<PaletteIDefinitionItemEvent> itemDragUpdateCallback;
    private Map<String, DefinitionPaletteCategoryWidget> categoryWidgets = new HashMap<>();

    private BS3PaletteWidgetView view;

    @Inject
    public BS3PaletteWidgetImpl(final ShapeManager shapeManager,
                                final ClientFactoryService clientFactoryServices,
                                final BS3PaletteWidgetView view,
                                final ShapeGlyphDragHandler shapeGlyphDragHandler,
                                final StunnerPreferencesRegistries preferencesRegistries,
                                final ManagedInstance<DefinitionPaletteCategoryWidget> categoryWidgetInstance,
                                final ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgets,
                                final ManagedInstance<CollapsedDefinitionPaletteItemWidget> collapsedDefinitionPaletteItemWidgets) {
        super(shapeManager);
        this.clientFactoryServices = clientFactoryServices;
        this.view = view;
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
        this.preferencesRegistries = preferencesRegistries;
        this.categoryWidgetInstances = categoryWidgetInstance;
        this.definitionPaletteItemWidgetInstances = definitionPaletteItemWidgets;
        this.collapsedDefinitionPaletteItemWidgets = collapsedDefinitionPaletteItemWidgets;
    }

    public static int getDefaultWidth() {
        return GLYPH_ICON_SIZE + PADDING;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setShapeGlyphDragHandler(shapeGlyphDragHandler);
        view.showEmptyView(true);
    }

    @Override
    public BS3PaletteWidget onItemDrop(final Consumer<PaletteIDefinitionItemEvent> callback) {
        this.itemDropCallback = callback;
        return this;
    }

    @Override
    public BS3PaletteWidget onItemDragStart(final Consumer<PaletteIDefinitionItemEvent> callback) {
        this.itemDragStartCallback = callback;
        return this;
    }

    @Override
    public BS3PaletteWidget onItemDragUpdate(final Consumer<PaletteIDefinitionItemEvent> callback) {
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
            itemDragStartCallback.accept(new PaletteIDefinitionItemEvent(definitionId,
                                                                         definition,
                                                                         factory,
                                                                         x,
                                                                         y));
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
            itemDragUpdateCallback.accept(new PaletteIDefinitionItemEvent(definitionId,
                                                                          definition,
                                                                          factory,
                                                                          x,
                                                                          y));
        }
    }

    public void unbind() {
        if (null != paletteDefinition) {
            view.clear();
            view.showEmptyView(true);
            this.paletteDefinition = null;
        }
    }

    @Override
    public void setVisible(final boolean visible) {
        view.showEmptyView(!visible);
    }

    @Override
    public void onScreenMaximized(final ScreenMaximizedEvent event) {
        setVisible(event.isDiagramScreen());
    }

    @Override
    public void onScreenMinimized(final ScreenMinimizedEvent event) {
        setVisible(true);
    }

    protected ShapeFactory getShapeFactory() {
        return shapeManager.getDefaultShapeSet(paletteDefinition.getDefinitionSetId()).getShapeFactory();
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
    protected AbstractPalette<DefaultPaletteDefinition> bind() {
        if (null != paletteDefinition) {
            final StunnerPreferences preferences = preferencesRegistries.get(paletteDefinition.getDefinitionSetId(), StunnerPreferences.class);
            final boolean autoHidePanel = preferences.getDiagramEditorPreferences().isAutoHidePalettePanel();
            paletteDefinition.getItems().forEach(item -> {
                BS3PaletteWidgetPresenter widget;
                if (item instanceof DefaultPaletteCategory) {
                    DefinitionPaletteCategoryWidget categoryWidget = newDefinitionPaletteCategoryWidget();
                    categoryWidget.setOnOpenCallback(category -> onOpenCategory(category.getId()));
                    categoryWidget.setOnCloseCallback(category -> onCloseCategory(category.getId()));
                    categoryWidget.setAutoHidePanel(autoHidePanel);
                    categoryWidgets.put(item.getId(),
                                        categoryWidget);
                    widget = categoryWidget;
                } else if (item instanceof CollapsedDefaultPaletteItem) {
                    widget = newCollapsedDefinitionPaletteItemWidget();
                } else {
                    widget = newDefinitionPaletteItemWidget();
                }
                final Consumer<PaletteItemMouseEvent> itemMouseEventHandler =
                        event -> handleMouseDownEvent(item,
                                                      event);
                widget.initialize(item,
                                  getShapeFactory(),
                                  itemMouseEventHandler);
                view.add(widget);
            });
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDragProxyComplete(final String definitionId,
                                    final double x,
                                    final double y) {
        if (null != itemDropCallback) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(definitionId);
            final ShapeFactory<?, ? extends Shape> factory = getShapeFactory();
            // Fire the callback as shape dropped onto the target canvas.
            itemDropCallback.accept(new PaletteIDefinitionItemEvent(definitionId,
                                                                    definition,
                                                                    factory,
                                                                    x,
                                                                    y));
        }
    }

    @Override
    public Glyph getShapeGlyph(final String definitionId) {
        return getShapeFactory().getGlyph(definitionId,
                                          AbstractPalette.PaletteGlyphConsumer.class);
    }

    @Override
    public Glyph getShapeDragProxyGlyph(final String definitionId) {
        return getShapeFactory().getGlyph(definitionId,
                                          AbstractPalette.PaletteDragProxyGlyphConsumer.class);
    }

    private void handleMouseDownEvent(final DefaultPaletteItem item,
                                      final PaletteItemMouseEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);
        if (event.getId().equals(item.getId())) {
            final String catDefId = item.getDefinitionId();
            BS3PaletteWidgetImpl.this.onPaletteItemMouseDown(catDefId,
                                                             event.getMouseX(),
                                                             event.getMouseY());
        } else {
            final String defId = getItemDefinitionId(event.getId());
            BS3PaletteWidgetImpl.this.onPaletteItemMouseDown(defId,
                                                             event.getMouseX(),
                                                             event.getMouseY());
        }
    }

    private String getItemDefinitionId(final String itemId) {
        return DefaultPaletteUtils.getPaletteItemDefinitionId(paletteDefinition,
                                                              itemId);
    }

    private void onPaletteItemMouseDown(final String id,
                                        final double x,
                                        final double y) {
        // Show the drag proxy for the element at x, y.
        view.showDragProxy(id,
                           x,
                           y,
                           getIconSize(),
                           getIconSize());
    }

    private void onOpenCategory(String categoryId) {
        categoryWidgets.entrySet()
                .stream()
                .filter(entry -> !Objects.equals(entry.getKey(), categoryId))
                .forEach(entry -> entry.getValue().setVisible(false));
        DefinitionPaletteCategoryWidget widget = categoryWidgets.get(categoryId);
        widget.setVisible(true);
    }

    private void onCloseCategory(String categoryId) {
        DefinitionPaletteCategoryWidget widget = categoryWidgets.get(categoryId);
        widget.setVisible(false);
    }

    @Override
    protected void doDestroy() {
        shapeGlyphDragHandler.destroy();
        view.destroy();
        categoryWidgetInstances.destroyAll();
        definitionPaletteItemWidgetInstances.destroyAll();
        collapsedDefinitionPaletteItemWidgets.destroyAll();
        itemDragStartCallback = null;
        itemDragUpdateCallback = null;
        itemDropCallback = null;
        closeCallback = null;
        paletteDefinition = null;
    }

    @Override
    protected String getPaletteItemId(final int index) {
        final DefaultPaletteItem item = paletteDefinition.getItems().get(index);
        return null != item ? item.getId() : null;
    }

    protected DefinitionPaletteCategoryWidget newDefinitionPaletteCategoryWidget() {
        return categoryWidgetInstances.get();
    }

    protected DefinitionPaletteItemWidget newDefinitionPaletteItemWidget() {
        return definitionPaletteItemWidgetInstances.get();
    }

    protected CollapsedDefinitionPaletteItemWidget newCollapsedDefinitionPaletteItemWidget() {
        return collapsedDefinitionPaletteItemWidgets.get();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
