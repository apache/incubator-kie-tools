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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasDomainObjectListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.rule.EmptyRuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseCanvasHandlerTest {

    private static final String PARENT_ID = "p1";
    private static final String CANDIDATE_ID = "c1";
    private static final String SHAPE_FACTORY_ID = "factory1";
    private static final RuleSet RULE_SET = new EmptyRuleSet();

    @Mock
    DefinitionManager definitionManager;
    @Mock
    AdapterManager adapterManager;
    @Mock
    TypeDefinitionSetRegistry definitionSetRegistry;
    @Mock
    DefinitionAdapter definitionAdapter;
    @Mock
    DefinitionSetRuleAdapter definitionSetRuleAdapter;
    @Mock
    GraphUtils graphUtils;
    @Mock
    ShapeManager shapeManager;
    @Mock
    TextPropertyProviderFactory textPropertyProviderFactory;
    @Mock
    TextPropertyProvider textPropertyProvider;
    @Mock
    RuleManager ruleManager;
    @Mock
    Index<?, ?> graphIndex;
    @Mock
    GraphCommandExecutionContext commandExecutionContext;
    @Mock
    ShapeSet shapeSet;
    @Mock
    ShapeFactory shapeFactory;
    @Mock
    Diagram diagram;
    @Mock
    Metadata metadata;
    @Mock
    AbstractCanvas canvas;
    @Mock
    Layer layer;
    @Mock
    ElementShape parentShape;
    @Mock
    ElementShape shape;
    @Mock
    ShapeView shapeView;
    @Mock
    Element parent;
    @Mock
    Node<?, Edge> candidate;
    @Mock
    Definition candidateContent;
    @Mock
    Object defSet;
    @Mock
    Object defBean;
    @Mock
    DomainObject domainObject;
    @Mock
    CanvasDomainObjectListener domainObjectListener;

    private BaseCanvasHandlerStub tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forRules()).thenReturn(definitionSetRuleAdapter);
        when(definitionSetRegistry.getDefinitionSetByType(any(Class.class))).thenReturn(defSet);
        when(definitionSetRuleAdapter.getRuleSet(eq(defSet))).thenReturn(RULE_SET);
        when(shapeManager.getShapeSet(eq(SHAPE_FACTORY_ID))).thenReturn(shapeSet);
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shape.getUUID()).thenReturn(CANDIDATE_ID);
        when(parent.getUUID()).thenReturn(PARENT_ID);
        when(candidate.getUUID()).thenReturn(CANDIDATE_ID);
        when(candidate.getContent()).thenReturn(candidateContent);
        when(candidateContent.getDefinition()).thenReturn(defBean);
        when(canvas.getLayer()).thenReturn(layer);
        when(canvas.getShape(eq(PARENT_ID))).thenReturn(parentShape);
        when(canvas.getShape(eq(CANDIDATE_ID))).thenReturn(shape);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn("ds1");
        when(textPropertyProviderFactory.getProvider(any(Element.class))).thenReturn(textPropertyProvider);
        this.tested = spy(new BaseCanvasHandlerStub());
        tested.handle(canvas);
        assertEquals(canvas,
                     tested.getCanvas());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDraw() {
        final ParameterizedCommand<CommandResult<?>> callback = mock(ParameterizedCommand.class);
        tested.draw(diagram,
                    callback);
        assertEquals(diagram,
                     tested.getDiagram());
        verify(tested,
               times(1)).draw(eq(callback));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegister() {
        tested.register(shape,
                        (Element<View<?>>) candidate,
                        false);
        verify(canvas,
               times(1)).addShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregister() {
        tested.deregister(shape,
                          candidate,
                          false);
        verify(canvas,
               times(1)).deleteShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddShape() {
        tested.addShape(shape);
        verify(canvas,
               times(1)).addShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveShape() {
        tested.removeShape(shape);
        verify(canvas,
               times(1)).deleteShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyMutation() {
        final MutationContext mutationContext = MutationContext.STATIC;
        tested.applyElementMutation(shape,
                                    candidate,
                                    true,
                                    true,
                                    mutationContext);
        verify(shape,
               times(1)).applyPosition(eq(candidate),
                                       eq(mutationContext));
        verify(shape,
               times(1)).applyProperties(eq(candidate),
                                         eq(mutationContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddChild() {
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        final ParameterizedCommand<CommandResult<?>> callback = mock(ParameterizedCommand.class);
        tested.draw(diagram,
                    callback);
        tested.addChild(parent,
                        candidate);
        verify(canvas,
               times(1)).addChildShape(eq(parentShape),
                                       eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddChildToRoot() {
        when(metadata.getCanvasRootUUID()).thenReturn(PARENT_ID);
        final ParameterizedCommand<CommandResult<?>> callback = mock(ParameterizedCommand.class);
        tested.draw(diagram,
                    callback);
        tested.addChild(parent,
                        candidate);
        verify(layer,
               times(1)).addShape(eq(shapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveChild() {
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        final ParameterizedCommand<CommandResult<?>> callback = mock(ParameterizedCommand.class);
        tested.draw(diagram,
                    callback);
        tested.removeChild(parent,
                           candidate);
        verify(canvas,
               times(1)).deleteChildShape(eq(parentShape),
                                          eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteChildFromRoot() {
        when(metadata.getCanvasRootUUID()).thenReturn(PARENT_ID);
        final ParameterizedCommand<CommandResult<?>> callback = mock(ParameterizedCommand.class);
        tested.draw(diagram,
                    callback);
        tested.removeChild(parent,
                           candidate);
        verify(layer,
               times(1)).removeShape(eq(shapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getElementAtWhenCanvasHasShapeAtPosition() {
        final Optional<Shape> oShape = Optional.of(mock(Shape.class));
        final Index index = mock(Index.class);
        final Node n = mock(Node.class);
        when(canvas.getShapeAt(anyDouble(),
                               anyDouble())).thenReturn(oShape);
        when(commandExecutionContext.getGraphIndex()).thenReturn(index);
        when(index.getNode(anyString())).thenReturn(n);

        final Optional<Element> oElement = tested.getElementAt(0.0,
                                                               0.0);
        assertTrue(oElement.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getElementAtWhenCanvasDoesNotHaveShapeAtPosition() {
        final Optional<Shape> oShape = Optional.empty();
        final Index index = mock(Index.class);
        when(canvas.getShapeAt(anyDouble(),
                               anyDouble())).thenReturn(oShape);
        when(commandExecutionContext.getGraphIndex()).thenReturn(index);

        final Optional<Element> oElement = tested.getElementAt(0.0,
                                                               0.0);
        assertFalse(oElement.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDock() {
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        final ParameterizedCommand<CommandResult<?>> callback = mock(ParameterizedCommand.class);
        tested.draw(diagram,
                    callback);
        tested.dock(parent,
                    candidate);
        verify(canvas,
               times(1)).dock(eq(parentShape),
                              eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndock() {
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        final ParameterizedCommand<CommandResult<?>> callback = mock(ParameterizedCommand.class);
        tested.draw(diagram,
                    callback);
        tested.undock(parent,
                      candidate);
        verify(canvas,
               times(1)).undock(eq(parentShape),
                                eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.clear();
        assertNull(tested.getDiagram());
        verify(tested,
               times(1)).destroyGraphIndex(any(Command.class));
        verify(canvas,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.destroy();
        assertNull(tested.getDiagram());
        verify(tested,
               times(1)).destroyGraphIndex(any(Command.class));
        verify(canvas,
               times(1)).destroy();
    }

    @Test
    public void testClearCanvas() {
        tested.clearCanvas();

        verify(tested).notifyCanvasClear();
        verify(tested).notifyCanvasDomainObjectClear();
        verify(canvas).clear();
    }

    @Test
    public void testNotifyCanvasDomainObjectAdded() {
        tested.addDomainObjectListener(domainObjectListener);

        tested.notifyCanvasDomainObjectAdded(domainObject);

        verify(domainObjectListener).register(domainObject);
    }

    @Test
    public void testNotifyCanvasDomainObjectRemoved() {
        tested.addDomainObjectListener(domainObjectListener);

        tested.notifyCanvasDomainObjectRemoved(domainObject);

        verify(domainObjectListener).deregister(domainObject);
    }

    @Test
    public void testNotifyCanvasDomainObjectUpdated() {
        tested.addDomainObjectListener(domainObjectListener);

        tested.notifyCanvasDomainObjectUpdated(domainObject);

        verify(domainObjectListener).update(domainObject);
    }

    @Test
    public void testNotifyCanvasDomainObjectClear() {
        tested.addDomainObjectListener(domainObjectListener);

        tested.notifyCanvasDomainObjectClear();

        verify(domainObjectListener).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyElementMutation() throws Exception {
        AbstractShape abstractShape = mock(AbstractShape.class);

        tested.applyElementMutation(abstractShape,
                                    candidate);

        verify(abstractShape,
               times(1)).beforeDraw();
        verify(abstractShape,
               times(1)).afterDraw();

    }

    private class BaseCanvasHandlerStub extends BaseCanvasHandler<Diagram, AbstractCanvas> {

        public BaseCanvasHandlerStub() {
            super(definitionManager,
                  graphUtils,
                  shapeManager,
                  textPropertyProviderFactory);
        }

        @Override
        public RuleManager getRuleManager() {
            return ruleManager;
        }

        @Override
        protected void buildGraphIndex(final Command loadCallback) {
            loadCallback.execute();
        }

        @Override
        public void draw(final ParameterizedCommand<CommandResult<?>> loadCallback) {
            loadCallback.execute(CanvasCommandResultBuilder.SUCCESS);
        }

        @Override
        protected void destroyGraphIndex(final Command loadCallback) {

        }

        @Override
        public Index<?, ?> getGraphIndex() {
            return graphIndex;
        }

        @Override
        public GraphCommandExecutionContext getGraphExecutionContext() {
            return commandExecutionContext;
        }
    }
}
