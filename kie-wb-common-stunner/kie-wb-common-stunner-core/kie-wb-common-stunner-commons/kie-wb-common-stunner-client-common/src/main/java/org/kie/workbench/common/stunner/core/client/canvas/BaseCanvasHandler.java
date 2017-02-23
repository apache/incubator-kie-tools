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

package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * A base canvas handler type that provides implementations for most of the public API methods
 * for the <code>AbstractCanvasHandler</code> super-type.
 * You can use this type if:
 * - You need a custom graph index or graph index builder types.
 * - You need custom rule loading or do not want support for rules and their evaluations.
 * - You need custom draw logics.
 * @param <D> The diagram type.
 * @param <C> The handled canvas type.
 */
public abstract class BaseCanvasHandler<D extends Diagram, C extends AbstractCanvas>
        extends AbstractCanvasHandler<D, C> {

    private static Logger LOGGER = Logger.getLogger(BaseCanvasHandler.class.getName());

    private final DefinitionManager definitionManager;
    private final GraphUtils graphUtils;
    private final ShapeManager shapeManager;

    private C canvas;
    private D diagram;

    public BaseCanvasHandler(final DefinitionManager definitionManager,
                             final GraphUtils graphUtils,
                             final ShapeManager shapeManager) {
        this.definitionManager = definitionManager;
        this.graphUtils = graphUtils;
        this.shapeManager = shapeManager;
    }

    /**
     * Build the graph index instance using any concrete index/builder types.
     * This abstract implementation expects a not null instance for the graph index.
     * @param loadCallback Callback to run once load finishes. This kind of indexes could be loaded or
     * cached in/from server side as well.
     */
    protected abstract void buildGraphIndex(final Command loadCallback);

    /**
     * Load the necessary rules into the graph and model rules manager member instances.
     * No rules are supported, so subtypes targeted for read-only purposes or subtyes
     * that can ensure the commands and graph structure is always valid, can avoid adding
     * rules into the manager instances.
     * @param loadCallback Callback to run once rules have been loaded, if any.
     */
    protected abstract void loadRules(final Command loadCallback);

    /**
     * Delegates the draw behavior to the subtypes.
     * @param loadCallback Callback to run once draw has finished. It must provide a result for
     * the draw operation/s.
     */
    protected abstract void draw(final ParameterizedCommand<CommandResult<?>> loadCallback);

    /**
     * Destroys this instance' graph index.
     * @param loadCallback Callback to run once index has been destroyed.
     */
    protected abstract void destroyGraphIndex(final Command loadCallback);

    @Override
    public CanvasHandler<D, C> handle(final C canvas) {
        this.canvas = canvas;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void draw(final D diagram,
                     final ParameterizedCommand<CommandResult<?>> loadCallback) {
        if (null == this.canvas) {
            throw new IllegalStateException("No handled canvas instance.");
        }
        this.diagram = diagram;
        // Initialize the graph handler that provides processing and querying operations over the graph.
        buildGraphIndex(() -> loadRules(() -> draw(loadCallback)));
    }

    @Override
    public C getCanvas() {
        return canvas;
    }

    @Override
    public D getDiagram() {
        return diagram;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ShapeFactory<Object, AbstractCanvasHandler, Shape> getShapeFactory(final String shapeSetId) {
        ShapeSet<?> shapeSet = shapeManager.getShapeSet(shapeSetId);
        if (null == shapeSet) {
            LOGGER.log(Level.SEVERE, "ShapeSet [" + shapeSetId + "] not found. Using the default one,.");
        }
        shapeSet = shapeManager.getDefaultShapeSet(diagram.getMetadata().getDefinitionSetId());
        return shapeSet.getShapeFactory();
    }

    @Override
    protected void register(final Shape shape,
                            final Element<View<?>> candidate,
                            final boolean fireEvents) {
        // Add the shapes on canvas and fire events.
        addShape(shape);
        getCanvas().draw();
        if (fireEvents) {
            // Fire listeners.
            notifyCanvasElementAdded(candidate);
            // Fire updates.
            afterElementAdded(candidate,
                              shape);
        }
    }

    @Override
    protected void deregister(final Shape shape,
                              final Element element,
                              final boolean fireEvents) {
        if (fireEvents) {
            // Fire listeners.
            notifyCanvasElementRemoved(element);
            // Fire events.
            beforeElementDeleted(element,
                                 shape);
        }
        removeShape(shape);
        getCanvas().draw();
        if (fireEvents) {
            afterElementDeleted(element,
                                shape);
        }
    }

    public void addShape(final Shape shape) {
        getCanvas().addShape(shape);
    }

    public void removeShape(final Shape shape) {
        getCanvas().deleteShape(shape);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void applyElementMutation(final Shape shape,
                                        final Element candidate,
                                        final boolean applyPosition,
                                        final boolean applyProperties,
                                        final MutationContext mutationContext) {
        if (shape instanceof ElementShape) {
            final ElementShape graphShape = (ElementShape) shape;
            if (applyPosition) {
                graphShape.applyPosition(candidate,
                                         mutationContext);
            }
            if (applyProperties) {
                applyElementTitle(graphShape,
                                  candidate,
                                  mutationContext);
                graphShape.applyProperties(candidate,
                                           mutationContext);
            }
            beforeDraw(candidate,
                       graphShape);
            beforeElementUpdated(candidate,
                                 graphShape);
            getCanvas().draw();
            afterDraw(candidate,
                      graphShape);
            notifyCanvasElementUpdated(candidate);
            afterElementUpdated(candidate,
                                graphShape);
        }
    }

    @SuppressWarnings("unchecked")
    protected void applyElementTitle(final ElementShape shape,
                                     final Element candidate,
                                     final MutationContext mutationContext) {
        final Definition<Object> content = (Definition<Object>) candidate.getContent();
        final Object definition = content.getDefinition();
        final Object nameProperty = getDefinitionManager().adapters().forDefinition().getMetaProperty(PropertyMetaTypes.NAME,
                                                                                                      definition);
        if (null != nameProperty) {
            final String name = (String) getDefinitionManager().adapters().forProperty().getValue(nameProperty);
            shape.applyTitle(name,
                             candidate,
                             mutationContext);
        }
    }

    protected void beforeDraw(final Element element,
                              final Shape shape) {
        if (shape instanceof Lifecycle) {
            final Lifecycle lifecycle = (Lifecycle) shape;
            lifecycle.beforeDraw();
        }
    }

    protected void afterDraw(final Element element,
                             final Shape shape) {
        if (shape instanceof Lifecycle) {
            final Lifecycle lifecycle = (Lifecycle) shape;
            lifecycle.afterDraw();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addChild(final Element parent,
                         final Element child) {
        final Shape childShape = getCanvas().getShape(child.getUUID());
        if (!isCanvasRoot(parent)) {
            final Shape parentShape = getCanvas().getShape(parent.getUUID());
            getCanvas().addChildShape(parentShape,
                                      childShape);
        } else {
            // -- Special case when parent is the canvas root --
            // Ensure the shape is added into the layer, but no need to register it again and generate new
            // handlers ( f.i. using canvas#addShape() method ).
            getCanvas().getLayer().addShape(childShape.getShapeView());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeChild(final Element parent,
                            final Element child) {
        final String parentUUID = parent.getUUID();
        final String childUUID = child.getUUID();
        final Shape childShape = getCanvas().getShape(childUUID);
        if (!isCanvasRoot(parentUUID)) {
            final Shape parentShape = getCanvas().getShape(parentUUID);
            getCanvas().deleteChildShape(parentShape,
                                         childShape);
        } else {
            // -- Special case when parent is the canvas root --
            // Ensure the shape is removed from the layer, but no need to deregister any
            // handlers ( f.i. using canvas#removeShape() method ).
            getCanvas().getLayer().removeShape(childShape.getShapeView());
        }
    }

    protected boolean isCanvasRoot(final String pUUID) {
        return CanvasLayoutUtils.isCanvasRoot(getDiagram(),
                                              pUUID);
    }

    @Override
    public void dock(final Element parent,
                     final Element child) {
        if (!isCanvasRoot(parent)) {
            final Shape parentShape = getCanvas().getShape(parent.getUUID());
            final Shape childShape = getCanvas().getShape(child.getUUID());
            getCanvas().dock(parentShape,
                             childShape);
        }
    }

    @Override
    public void undock(final Element parent,
                       final Element child) {
        final String parentUUID = parent.getUUID();
        final String childUUID = child.getUUID();
        if (!isCanvasRoot(parentUUID)) {
            final Shape parentShape = getCanvas().getShape(parentUUID);
            final Shape childShape = getCanvas().getShape(childUUID);
            getCanvas().undock(parentShape,
                               childShape);
        }
    }

    protected void afterElementAdded(final Element element,
                                     final Shape shape) {
        // Implementations can do post operations here.
    }

    protected void beforeElementDeleted(final Element element,
                                        final Shape shape) {
        // Implementations can do post operations here.
    }

    protected void afterElementDeleted(final Element element,
                                       final Shape shape) {
        // Implementations can do post operations here.
    }

    protected void beforeElementUpdated(final Element element,
                                        final Shape shape) {
        // Implementations can do post operations here.
    }

    protected void afterElementUpdated(final Element element,
                                       final Shape shape) {
        // Implementations can do post operations here.
    }

    @Override
    public CanvasHandler<D, C> doClear() {
        destroyGraphIndex(() -> {
            diagram = null;
        });
        return this;
    }

    @Override
    public void doDestroy() {
        destroyGraphIndex(() -> {
            canvas = null;
            diagram = null;
        });
    }

    protected void showError(final ClientRuntimeError error) {
        final String message = error.getThrowable() != null ?
                error.getThrowable().getMessage() : error.getMessage();
        log(Level.SEVERE,
            message);
    }

    @Override
    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    public GraphUtils getGraphUtils() {
        return graphUtils;
    }

    public ShapeManager getShapeManager() {
        return shapeManager;
    }

    protected String getDefinitionId(final Object definition) {
        return definitionManager.adapters().forDefinition().getId(definition);
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
