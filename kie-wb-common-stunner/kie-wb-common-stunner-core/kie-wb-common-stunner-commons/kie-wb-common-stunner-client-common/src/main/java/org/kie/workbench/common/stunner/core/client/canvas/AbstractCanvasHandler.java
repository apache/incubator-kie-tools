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

package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasDomainObjectListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasCanvasListeners;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasDomainObjectListeners;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * This type is the canvas handler type used as default by Stunner, most of the component implementations
 * rely on this public API handler type.
 * If you need to provide a custom canvas handler for you Definition Set, is a good idea to create the handler
 * as a subtype for <code>AbstractCanvasHandler</code>, so most of the different IOC resolutions that Stunner requires
 * on other areas will be, at least, resolved with the default implementations that rely on subtypes
 * for <code>AbstractCanvasHandler</code>.
 * @param <D> The diagram type.
 * @param <C> The handled canvas type.
 */
public abstract class AbstractCanvasHandler<D extends Diagram, C extends AbstractCanvas>
        implements CanvasHandler<D, C>,
                   HasCanvasListeners<CanvasElementListener>,
                   HasDomainObjectListeners<CanvasDomainObjectListener> {

    private final String uuid;
    private final List<CanvasElementListener> listeners = new LinkedList<>();
    private final List<CanvasDomainObjectListener> domainObjectListeners = new LinkedList<>();
    private Supplier<GraphCommandExecutionContext> commandExecutionContextSupplier;

    public AbstractCanvasHandler() {
        this.uuid = UUID.uuid();
    }

    /**
     * Provides a definition manager instance in this context.
     */
    public abstract DefinitionManager getDefinitionManager();

    /**
     * Provides a Text Property Provider Factory instance in this context.
     */
    public abstract TextPropertyProviderFactory getTextPropertyProviderFactory();

    /**
     * Provides the rule manager instance.
     */
    public abstract RuleManager getRuleManager();

    /**
     * Provides the ruleSet instance for this handler.
     */
    public abstract RuleSet getRuleSet();

    /**
     * Returns the graph index instance to perform lookups over the graph structure
     * foe this canvas handler's diagram instance loaded.
     * Implementation can provide custom graph index types, if necessary targeted
     * and optimized for a concrete graph structure.
     */
    public abstract Index<?, ?> getGraphIndex();

    /**
     * Should return a graph execution context to perform the model updates applied by the graph command executions.
     * If the implementation is not going to perform model updates, the graph execution context can be
     * either <code>null</code> or an empty context type.
     */
    public GraphCommandExecutionContext getGraphExecutionContext() {
        return commandExecutionContextSupplier.get();
    }

    /**
     * This method sets the given <code>child</code> instance as children for the given
     * given <code>parent</code> instance.
     * It sets the shape for the <code>child</code> instance as child shape for the
     * <code>parent</code> instance's shape.
     * @param parent The parent graph element.
     * @param child The graph element to set as child.
     */
    public abstract void addChild(final Element parent,
                                  final Element child);

    /**
     * This method sets the given <code>child</code> instance as children for the given
     * given <code>parent</code> instance at the given <code>index</code>. The default
     * implementation adds the child to the parent and index is unused. The Child's
     * Shape is also set as a sibling of the Parent Shape.
     * @param parent The parent graph element.
     * @param child The graph element to set as child.
     * @param index The index of the child in the parent.
     */
    public abstract void addChild(final Element parent,
                                  final Element child,
                                  final int index);

    /**
     * This method removes the given <code>child</code> instance as children for the given
     * given <code>parent</code> instance.
     * It removes the shape for the <code>child</code> instance as child shape for the
     * <code>parent</code> instance's shape.
     * @param parent The parent graph element.
     * @param child The element to remove as a child from the parent.
     */
    public abstract void removeChild(final Element parent,
                                     final Element child);

    /**
     * Gets the Element at the specified Canvas coordinates
     * @param x The X canvas coordinate
     * @param y The Y canvas coordinate
     * @return Element at the coordinate
     */
    public abstract Optional<Element> getElementAt(final double x,
                                                   final double y);

    /**
     * This method sets the given <code>child</code> instance as docked child for the given
     * given <code>parent</code> instance.
     * It sets the shape for the <code>child</code> instance as a docked child shape for the
     * <code>parent</code> instance's shape.
     * @param parent The parent graph element.
     * @param child The graph element to set as a docked child.
     */
    public abstract boolean dock(final Element parent,
                                 final Element child);

    /**
     * This method removes the given <code>child</code> instance as docked child for the given
     * given <code>parent</code> instance.
     * It removes the shape for the <code>child</code> instance as a docked child shape for the
     * <code>parent</code> instance's shape.
     * @param parent The parent graph element.
     * @param child The element to remove as a docked child from the parent.
     */
    public abstract void undock(final Element parent,
                                final Element child);

    /**
     * Subtypes must clear this instance's state here.
     */
    public abstract CanvasHandler<D, C> doClear();

    /**
     * Subtypes must destroy this instance's state here.
     */
    public abstract void doDestroy();

    public abstract void register(final Shape shape,
                                  final Element<View<?>> candidate,
                                  final boolean fireEvents);

    public abstract void deregister(final Shape shape,
                                    final Element element,
                                    final boolean fireEvents);

    public abstract void applyElementMutation(final Shape shape,
                                              final Element candidate,
                                              final boolean applyPosition,
                                              final boolean applyProperties,
                                              final MutationContext mutationContext);

    public abstract ShapeFactory<Object, Shape> getShapeFactory(final String shapeSetId);

    /**
     * It does:
     * - Registers a new graph element into the structure
     * - Creates the shape for the element to register, using the shape factory provided
     * for the given <code>shapeSetId</code> value.
     * @param shapeSetId The identifier for the ShapeSet to use.
     * @param candidate The graph element to register.
     */
    @SuppressWarnings("unchecked")
    public void register(final String shapeSetId,
                         final Element<View<?>> candidate) {
        final ShapeFactory<Object, Shape> factory = getShapeFactory(shapeSetId);
        register(factory,
                 candidate,
                 true);
    }

    /**
     * It does:
     * - Registers a new graph element into the structure
     * - Creates the shape for the element to register, using the given shape factory.
     * @param factory The shape factory to use.
     * @param candidate The graph element to register.
     * @param fireEvents If canvas and canvas handled registration events must be fired.
     */
    @SuppressWarnings("unchecked")
    public void register(final ShapeFactory<Object, Shape> factory,
                         final Element<View<?>> candidate,
                         final boolean fireEvents) {
        assert factory != null && candidate != null;
        final Shape shape = factory.newShape(candidate.getContent().getDefinition());

        if (shape == null) {
            return;
        }

        // Set the same identifier as the graph element's one.
        if (null == shape.getUUID()) {
            shape.setUUID(candidate.getUUID());
        }
        register(shape,
                 candidate,
                 fireEvents);
    }

    /**
     * Deregisters an element from the graph structure and from the canvas.
     * @param element The element to deregister and remove from the canvas.
     */
    public void deregister(final Element element) {
        deregister(element,
                   true);
    }

    /**
     * De-registers an element from the graph structure and from the canvas.
     * @param element The element to de-register and remove from the canvas.
     * @param fireEvents If canvas and canvas handled registration events must be fired.
     */
    public void deregister(final Element element,
                           final boolean fireEvents) {
        final Shape shape = getCanvas().getShape(element.getUUID());
        if (Objects.isNull(shape)) {
            //already not exists on canvas
            return;
        }
        deregister(shape,
                   element,
                   fireEvents);
    }

    /**
     * When an element has been changed, this method produces to update the handler and the canvas
     * and mutates the shape bind to the given element using new properties and/or values.
     * This method checks all available element properties and can potentially change
     * the shape coordinates, size,or whatever property that produces any visual effect on the canvas.
     * @param element The element that has been updated.
     * @param mutationContext The context for the shape mutations.
     */
    public void applyElementMutation(final Element element,
                                     final MutationContext mutationContext) {
        applyElementMutation(element,
                             true,
                             true,
                             mutationContext);
    }

    /**
     * When an element has been changed, this method produces to update the coordinates for
     * the shape bind to the given element.
     * @param element The element that has been updated.
     * @param mutationContext The context for the shape mutations.
     */
    public void updateElementPosition(final Element element,
                                      final MutationContext mutationContext) {
        applyElementMutation(element,
                             true,
                             false,
                             mutationContext);
    }

    /**
     * When an element has been changed, this method produces to update the handler and the canvas
     * and mutates the shape bind to the given element using new properties and/or values.
     * This method checks all available element properties and can potentially change whatever property
     * that produces any visual effect on the canvas, but no produces coordinates or bounds size updates.
     * @param element The element that has been updated.
     * @param mutationContext The context for the shape mutations.
     */
    public void updateElementProperties(final Element element,
                                        final MutationContext mutationContext) {
        applyElementMutation(element,
                             false,
                             true,
                             mutationContext);
    }

    @SuppressWarnings("unchecked")
    public void applyElementMutation(final Element candidate,
                                     final boolean applyPosition,
                                     final boolean applyProperties,
                                     final MutationContext mutationContext) {
        if (null != candidate && !isCanvasRoot(candidate)) {
            final Shape shape = getCanvas().getShape(candidate.getUUID());
            applyElementMutation(shape,
                                 candidate,
                                 applyPosition,
                                 applyProperties,
                                 mutationContext);
        }
    }

    public void setGraphExecutionContext(final Supplier<GraphCommandExecutionContext> commandExecutionContextSupplier) {
        this.commandExecutionContextSupplier = commandExecutionContextSupplier;
    }

    /**
     * Adds a listener instance in order to be notified about graph element structure updates.
     * @param instance The listener instance.
     */
    @Override
    public HasCanvasListeners<CanvasElementListener> addRegistrationListener(final CanvasElementListener instance) {
        listeners.add(instance);
        return this;
    }

    /**
     * Removes a previously register listener instance.
     * @param instance The listener instance.
     */
    @Override
    public HasCanvasListeners<CanvasElementListener> removeRegistrationListener(final CanvasElementListener instance) {
        listeners.remove(instance);
        return this;
    }

    /**
     * Clears all the registered listeners.
     */
    @Override
    public HasCanvasListeners<CanvasElementListener> clearRegistrationListeners() {
        listeners.clear();
        return this;
    }

    @Override
    public HasDomainObjectListeners<CanvasDomainObjectListener> addDomainObjectListener(final CanvasDomainObjectListener instance) {
        domainObjectListeners.add(instance);
        return this;
    }

    @Override
    public HasDomainObjectListeners<CanvasDomainObjectListener> removeDomainObjectListener(final CanvasDomainObjectListener instance) {
        domainObjectListeners.remove(instance);
        return this;
    }

    @Override
    public HasDomainObjectListeners<CanvasDomainObjectListener> clearDomainObjectListeners() {
        domainObjectListeners.clear();
        return this;
    }

    /**
     * Notifies an element added to the listeners.
     */
    public void notifyCanvasElementAdded(final Element candidate) {
        for (final CanvasElementListener instance : listeners) {
            instance.register(candidate);
        }
    }

    /**
     * Notifies an element removed to the listeners.
     */
    public void notifyCanvasElementRemoved(final Element candidate) {
        for (final CanvasElementListener instance : listeners) {
            instance.deregister(candidate);
        }
    }

    /**
     * Notifies an element updated to the listeners.
     */
    public void notifyCanvasElementUpdated(final Element candidate) {
        for (final CanvasElementListener instance : listeners) {
            instance.update(candidate);
        }
    }

    /**
     * Notifies a clean canvas to the listeners.
     */
    public void notifyCanvasClear() {
        for (final CanvasElementListener instance : listeners) {
            instance.clear();
        }
    }

    /**
     * Notifies {@link CanvasDomainObjectListener}s that a {@link DomainObject} has been added.
     */
    public void notifyCanvasDomainObjectAdded(final DomainObject domainObject) {
        for (final CanvasDomainObjectListener instance : domainObjectListeners) {
            instance.register(domainObject);
        }
    }

    /**
     * Notifies {@link CanvasDomainObjectListener}s that a {@link DomainObject} has been removed.
     */
    public void notifyCanvasDomainObjectRemoved(final DomainObject domainObject) {
        for (final CanvasDomainObjectListener instance : domainObjectListeners) {
            instance.deregister(domainObject);
        }
    }

    /**
     * Notifies {@link CanvasDomainObjectListener}s that a {@link DomainObject} has been updated.
     */
    public void notifyCanvasDomainObjectUpdated(final DomainObject domainObject) {
        for (final CanvasDomainObjectListener instance : domainObjectListeners) {
            instance.update(domainObject);
        }
    }

    /**
     * Notifies {@link CanvasDomainObjectListener}s that the {@link Canvas} has been cleared.
     */
    public void notifyCanvasDomainObjectClear() {
        for (final CanvasDomainObjectListener instance : domainObjectListeners) {
            instance.clear();
        }
    }

    public void clearCanvas() {
        if (null != getCanvas()) {
            notifyCanvasClear();
            notifyCanvasDomainObjectClear();
            getCanvas().clear();
        }
    }

    /**
     * Clears this handler state and the handled canvas instance.
     * Other further diagrams can be loaded and displayed using this handler.
     */
    @Override
    public CanvasHandler<D, C> clear() {
        if (null != getCanvas()) {
            getCanvas().clear();
        }
        doClear();
        return this;
    }

    /**
     * Destroys this handler state and the handled canvas instance.
     * This instance cannot be longer used and should be eligible by the garbage collector.
     */
    @Override
    public void destroy() {
        getCanvas().destroy();
        doDestroy();
        listeners.clear();
        domainObjectListeners.clear();
        commandExecutionContextSupplier = null;
    }

    /**
     * Used to avoid forcing specifying the generic for the canvas type
     * from other beans.
     */
    public AbstractCanvas getAbstractCanvas() {
        return getCanvas();
    }

    public boolean isCanvasRoot(final Element parent) {
        return CanvasLayoutUtils.isCanvasRoot(getDiagram(),
                                              parent);
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractCanvasHandler)) {
            return false;
        }
        AbstractCanvasHandler that = (AbstractCanvasHandler) o;
        return getUuid().equals(that.getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid() == null ? 0 : ~~getUuid().hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" + getUuid() + "]";
    }
}
