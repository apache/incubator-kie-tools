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

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
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
    private final TextPropertyProviderFactory textPropertyProviderFactory;

    private C canvas;
    private D diagram;
    private RuleSet ruleSet;

    public BaseCanvasHandler(final DefinitionManager definitionManager,
                             final GraphUtils graphUtils,
                             final ShapeManager shapeManager,
                             final TextPropertyProviderFactory textPropertyProviderFactory) {
        this.definitionManager = definitionManager;
        this.graphUtils = graphUtils;
        this.shapeManager = shapeManager;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
    }

    /**
     * Build the graph index instance using any concrete index/builder types.
     * This abstract implementation expects a not null instance for the graph index.
     * @param loadCallback Callback to run once load finishes. This kind of indexes could be loaded or
     * cached in/from server side as well.
     */
    protected abstract void buildGraphIndex(Command loadCallback);

    /**
     * Delegates the draw behavior to the subtypes.
     * @param loadCallback Callback to run once draw has finished. It must provide a result for
     * the draw operation/s.
     */
    public abstract void draw(ParameterizedCommand<CommandResult<?>> loadCallback);

    /**
     * Destroys this instance' graph index.
     * @param loadCallback Callback to run once index has been destroyed.
     */
    protected abstract void destroyGraphIndex(Command loadCallback);

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
        buildGraphIndex(() -> loadRuleSet(() -> draw(loadCallback)));
    }

    @Override
    public RuleSet getRuleSet() {
        return ruleSet;
    }

    @Override
    public C getCanvas() {
        return canvas;
    }

    @Override
    public D getDiagram() {
        return diagram;
    }

    protected void loadRuleSet(final Command callback) {
        final String id = getDiagram().getMetadata().getDefinitionSetId();
        final Object defSet = getDefinitionManager().definitionSets().getDefinitionSetById(id);
        this.ruleSet = definitionManager.adapters().forRules().getRuleSet(defSet);
        callback.execute();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ShapeFactory<Object, Shape> getShapeFactory(final String shapeSetId) {
        ShapeSet<?> shapeSet = shapeManager.getShapeSet(shapeSetId);
        if (null == shapeSet) {
            LOGGER.log(Level.SEVERE,
                       "ShapeSet [" + shapeSetId + "] not found. Using the default one,.");
        }
        shapeSet = shapeManager.getDefaultShapeSet(diagram.getMetadata().getDefinitionSetId());
        return shapeSet.getShapeFactory();
    }

    @Override
    public void register(final Shape shape,
                         final Element<View<?>> candidate,
                         final boolean fireEvents) {
        // Add the shapes on canvas and fire events.
        addShape(shape);
        if (fireEvents) {
            // Fire listeners.
            notifyCanvasElementAdded(candidate);
            // Fire updates.
            afterElementAdded(candidate,
                              shape);
        }
    }

    @Override
    public void deregister(final Shape shape,
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
    public void applyElementMutation(final Shape shape,
                                     final Element candidate,
                                     final boolean applyPosition,
                                     final boolean applyProperties,
                                     final MutationContext mutationContext) {
        if (shape instanceof ElementShape) {
            final ElementShape graphShape = (ElementShape) shape;
            this.applyElementMutation(graphShape,
                                      candidate,
                                      applyPosition,
                                      applyProperties,
                                      mutationContext);
        } else {
            LOGGER.log(Level.WARNING,
                       "The shape to handle must be type of [" + ElementShape.class.getName() + "]");
        }
    }

    @SuppressWarnings("unchecked")
    protected void applyElementMutation(final ElementShape graphShape,
                                        final Element candidate,
                                        final boolean applyPosition,
                                        final boolean applyProperties,
                                        final MutationContext mutationContext) {
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

        this.applyElementMutation(graphShape, candidate);
    }

    protected void applyElementMutation(final Shape shape, final Element candidate) {
        beforeDraw(candidate, shape);
        beforeElementUpdated(candidate, shape);
        afterDraw(candidate, shape);
        notifyCanvasElementUpdated(candidate);
        afterElementUpdated(candidate, shape);
    }

    @SuppressWarnings("unchecked")
    protected void applyElementTitle(final ElementShape shape,
                                     final Element candidate,
                                     final MutationContext mutationContext) {
        final TextPropertyProvider textPropertyProvider = textPropertyProviderFactory.getProvider(candidate);
        final String name = textPropertyProvider.getText(candidate);
        shape.applyTitle(name,
                         candidate,
                         mutationContext);
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
    public void addChild(final Element parent,
                         final Element child,
                         final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeChild(final Element parent,
                            final Element child) {
        final String parentUUID = parent.getUUID();
        final String childUUID = child.getUUID();
        final Shape childShape = getCanvas().getShape(childUUID);
        if(Objects.isNull(childShape)){
            return;
        }
        if (!isCanvasRoot(parentUUID)) {
            final Shape parentShape = getCanvas().getShape(parentUUID);
            if(Objects.isNull(parentShape)){
                return;
            }
            getCanvas().deleteChildShape(parentShape,
                                         childShape);
        } else {
            // -- Special case when parent is the canvas root --
            // Ensure the shape is removed from the layer, but no need to deregister any
            // handlers ( f.i. using canvas#removeShape() method ).
            getCanvas().getLayer().removeShape(childShape.getShapeView());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Element> getElementAt(final double x,
                                          final double y) {
        final Optional<Shape> shape = getCanvas().getShapeAt(x,
                                                             y);
        return shape.flatMap(s -> Optional.of(getGraphExecutionContext().getGraphIndex().getNode(s.getUUID())));
    }

    protected boolean isCanvasRoot(final String pUUID) {
        return CanvasLayoutUtils.isCanvasRoot(getDiagram(),
                                              pUUID);
    }

    @Override
    public boolean dock(final Element parent,
                        final Element child) {
        if (!isCanvasRoot(parent)) {
            final Shape parentShape = getCanvas().getShape(parent.getUUID());
            final Shape childShape = getCanvas().getShape(child.getUUID());
            try {
                getCanvas().dock(parentShape,
                                 childShape);
                return true;
            } catch (Exception e) {
                LOGGER.fine("Error docking node " + child.getUUID());
                return false;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void undock(final Element target,
                       final Element child) {
        final String targetUUID = target.getUUID();
        final String childUUID = child.getUUID();
        if (!isCanvasRoot(targetUUID)) {
            final Shape targetShape = getCanvas().getShape(targetUUID);
            final Shape childShape = getCanvas().getShape(childUUID);
            if (Objects.nonNull(targetShape) && Objects.nonNull(childShape)) {
                getCanvas().undock(targetShape,
                                   childShape);
            }
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
        destroyGraph(() -> {
            diagram = null;
            ruleSet = null;
        });
        return this;
    }

    @Override
    public void doDestroy() {
        destroyGraph(() -> {
            canvas = null;
            diagram = null;
            ruleSet = null;
        });
    }

    protected void destroyGraph(final Command callback) {
        destroyGraphIndex(() -> {
            if (null != diagram && null != diagram.getGraph()) {
                diagram.getGraph().clear();
            }
            callback.execute();
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

    @Override
    public TextPropertyProviderFactory getTextPropertyProviderFactory() {
        return this.textPropertyProviderFactory;
    }

    public GraphUtils getGraphUtils() {
        return graphUtils;
    }

    public ShapeManager getShapeManager() {
        return shapeManager;
    }

    protected String getDefinitionId(final Object definition) {
        return getDefinitionManager().adapters().forDefinition().getId(definition);
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
