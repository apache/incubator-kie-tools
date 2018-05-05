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
package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasCanvasListeners;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class SessionPreviewCanvasHandlerProxy<D extends Diagram, C extends AbstractCanvas> extends BaseCanvasHandler<D, C> {

    private final BaseCanvasHandler<D, C> wrapped;

    @SuppressWarnings("unchecked")
    public SessionPreviewCanvasHandlerProxy(final BaseCanvasHandler wrapped,
                                            final DefinitionManager definitionManager,
                                            final GraphUtils graphUtils,
                                            final ShapeManager shapeManager,
                                            final TextPropertyProviderFactory textPropertyProviderFactory) {
        super(definitionManager,
              graphUtils,
              shapeManager,
              textPropertyProviderFactory);
        this.wrapped = wrapped;
    }

    public BaseCanvasHandler getWrapped() {
        return wrapped;
    }

    @Override
    public GraphCommandExecutionContext getGraphExecutionContext() {
        //Return null to prevent Graph commands from executing
        return null;
    }

    @Override
    protected void buildGraphIndex(final Command loadCallback) {
        // No index to build as it's using the index from the wrapped instance..
        loadCallback.execute();
    }

    @Override
    protected void destroyGraphIndex(final Command loadCallback) {
        // No index to destroy as it's using the index from the wrapped instance.
        loadCallback.execute();
    }

    @Override
    public RuleManager getRuleManager() {
        return wrapped.getRuleManager();
    }

    @Override
    public Index<?, ?> getGraphIndex() {
        return wrapped.getGraphIndex();
    }

    @Override
    public DefinitionManager getDefinitionManager() {
        return wrapped.getDefinitionManager();
    }

    @Override
    public TextPropertyProviderFactory getTextPropertyProviderFactory() {
        return wrapped.getTextPropertyProviderFactory();
    }

    @Override
    public void register(final Shape shape,
                         final Element<View<?>> candidate,
                         final boolean fireEvents) {
        wrapped.register(shape,
                         candidate,
                         fireEvents);
    }

    @Override
    public void register(final String shapeSetId,
                         final Element<View<?>> candidate) {
        wrapped.register(shapeSetId,
                         candidate);
    }

    @Override
    public void register(final ShapeFactory<Object, Shape> factory,
                         final Element<View<?>> candidate,
                         final boolean fireEvents) {
        wrapped.register(factory,
                         candidate,
                         fireEvents);
    }

    @Override
    public void deregister(final Shape shape,
                           final Element element,
                           final boolean fireEvents) {
        wrapped.deregister(shape,
                           element,
                           fireEvents);
    }

    @Override
    public void deregister(final Element element) {
        wrapped.deregister(element);
    }

    @Override
    public void deregister(final Element element,
                           final boolean fireEvents) {
        wrapped.deregister(element,
                           fireEvents);
    }

    @Override
    public void addChild(final Element parent,
                         final Element child) {
        wrapped.addChild(parent,
                         child);
    }

    @Override
    public void addChild(final Element parent,
                         final Element child,
                         final int index) {
        wrapped.addChild(parent,
                         child,
                         index);
    }

    @Override
    public void removeChild(final Element parent,
                            final Element child) {
        wrapped.removeChild(parent,
                            child);
    }

    @Override
    public Optional<Element> getElementAt(final double x,
                                          final double y) {
        return wrapped.getElementAt(x,
                                    y);
    }

    @Override
    public boolean dock(final Element parent,
                        final Element child) {
        return wrapped.dock(parent,
                            child);
    }

    @Override
    public void undock(final Element parent,
                       final Element child) {
        wrapped.undock(parent,
                       child);
    }

    @Override
    public CanvasHandler<D, C> clear() {
        return wrapped.clear();
    }

    @Override
    public CanvasHandler<D, C> doClear() {
        return wrapped.doClear();
    }

    @Override
    public void destroy() {
        wrapped.destroy();
    }

    @Override
    public void doDestroy() {
        wrapped.doDestroy();
    }

    @Override
    public void applyElementMutation(final Shape shape,
                                     final Element candidate,
                                     final boolean applyPosition,
                                     final boolean applyProperties,
                                     final MutationContext mutationContext) {
        wrapped.applyElementMutation(shape,
                                     candidate,
                                     applyPosition,
                                     applyProperties,
                                     mutationContext);
    }

    @Override
    public void applyElementMutation(final Element element,
                                     final MutationContext mutationContext) {
        wrapped.applyElementMutation(element,
                                     mutationContext);
    }

    @Override
    public void updateElementPosition(final Element element,
                                      final MutationContext mutationContext) {
        wrapped.updateElementPosition(element,
                                      mutationContext);
    }

    @Override
    public void updateElementProperties(final Element element,
                                        final MutationContext mutationContext) {
        wrapped.updateElementProperties(element,
                                        mutationContext);
    }

    @Override
    public void applyElementMutation(final Element candidate,
                                     final boolean applyPosition,
                                     final boolean applyProperties,
                                     final MutationContext mutationContext) {
        wrapped.applyElementMutation(candidate,
                                     applyPosition,
                                     applyProperties,
                                     mutationContext);
    }

    @Override
    public ShapeFactory<Object, Shape> getShapeFactory(final String shapeSetId) {
        return wrapped.getShapeFactory(shapeSetId);
    }

    @Override
    public CanvasHandler<D, C> handle(final C canvas) {
        return wrapped.handle(canvas);
    }

    @Override
    public void draw(final ParameterizedCommand<CommandResult<?>> loadCallback) {
        wrapped.draw(loadCallback);
    }

    @Override
    public void draw(final D diagram,
                     final ParameterizedCommand<CommandResult<?>> loadCallback) {
        wrapped.draw(diagram,
                     loadCallback);
    }

    @Override
    public D getDiagram() {
        return wrapped.getDiagram();
    }

    @Override
    public C getCanvas() {
        return wrapped.getCanvas();
    }

    @Override
    public HasCanvasListeners<CanvasElementListener> addRegistrationListener(final CanvasElementListener instance) {
        return wrapped.addRegistrationListener(instance);
    }

    @Override
    public HasCanvasListeners<CanvasElementListener> removeRegistrationListener(final CanvasElementListener instance) {
        return wrapped.removeRegistrationListener(instance);
    }

    @Override
    public HasCanvasListeners<CanvasElementListener> clearRegistrationListeners() {
        return wrapped.clearRegistrationListeners();
    }

    @Override
    public void notifyCanvasElementRemoved(final Element candidate) {
        wrapped.notifyCanvasElementRemoved(candidate);
    }

    @Override
    public void notifyCanvasElementAdded(final Element candidate) {
        wrapped.notifyCanvasElementAdded(candidate);
    }

    @Override
    public void notifyCanvasElementUpdated(final Element candidate) {
        wrapped.notifyCanvasElementUpdated(candidate);
    }

    @Override
    public void notifyCanvasClear() {
        wrapped.notifyCanvasClear();
    }

    @Override
    public void clearCanvas() {
        wrapped.clearCanvas();
    }

    @Override
    public AbstractCanvas getAbstractCanvas() {
        return wrapped.getAbstractCanvas();
    }

    @Override
    public boolean isCanvasRoot(final Element parent) {
        return wrapped.isCanvasRoot(parent);
    }

    @Override
    public String getUuid() {
        return wrapped.getUuid();
    }

    @Override
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public String toString() {
        return "Proxy for " + wrapped.toString();
    }
}
