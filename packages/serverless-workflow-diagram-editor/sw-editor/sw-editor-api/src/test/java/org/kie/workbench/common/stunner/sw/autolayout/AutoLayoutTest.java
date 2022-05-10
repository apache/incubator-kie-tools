package org.kie.workbench.common.stunner.sw.autolayout;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.core.JsArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.sw.autolayout.elkjs.ELKEdge;
import org.kie.workbench.common.stunner.sw.autolayout.elkjs.ELKNode;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.marshall.BaseMarshallingTest;
import org.kie.workbench.common.stunner.sw.marshall.BuilderContext;
import org.kie.workbench.common.stunner.sw.marshall.Context;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class AutoLayoutTest extends BaseMarshallingTest {

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setId("workflow1")
                .setName("Workflow1")
                .setStart("State1")
                .setStates(new State[]{
                        new State()
                                .setName("State1")
                                .setTransition("State2"),
                        new State()
                                .setName("State2")
                                .setTransition("State3"),
                        new State()
                                .setName("State3")
                                .setEnd(true)
                });
    }

    @Before
    @Override
    public void setUp() {
        graphHandler = new TestingGraphMockHandler();
        context = new Context(graphHandler.graphIndex);
        builderContext = new BuilderContext(context,
                                            graphHandler.getDefinitionManager(),
                                            graphHandler.getFactoryManager());
        workflow = createWorkflow();
        Marshaller.LOAD_DETAILS = true;

        unmarshallWorkflow();
    }

    @Test
    @SuppressWarnings("all")
    public void testBuildElkInputNode() {
        final Graph<?, ?> graph = getGraph();
        final Object parentLayout = mock(Object.class);
        final Object nestedLayout = mock(Object.class);
        final ELKNode elkRootNode = AutoLayout.buildElkInputNode(graph,
                                                                 context.getWorkflowRootNode(),
                                                                 parentLayout,
                                                                 nestedLayout,
                                                                 false);
        // Root node
        assertEquals(parentLayout, elkRootNode.getLayoutOptions());
        assertEquals(context.getWorkflowRootNode().getUUID(), elkRootNode.getId());

        // Dummy nodes
        assertEquals(5, elkRootNode.getChildren().length);

        for (int i = 0; i < elkRootNode.getChildren().length; i++) {
            final ELKNode childElkNode = elkRootNode.getChildren().getAt(i);
            final Node<View, Edge> node = graph.getNode(childElkNode.getId());

            assertEquals(node.getContent().getBounds().getWidth(), childElkNode.getWidth(), 0);
            assertEquals(node.getContent().getBounds().getHeight(), childElkNode.getHeight(), 0);
        }

        // Dummy edges
        assertEquals(4, elkRootNode.getEdges().length);

        for (int j = 0; j < elkRootNode.getEdges().length; j++) {
            final ELKEdge elkEdge = elkRootNode.getEdges().getAt(j);
            final Node<View, Edge> sourceNode = graph.getNode(elkEdge.getSources().getAt(0));
            final Node<View, Edge> targetNode = graph.getNode(elkEdge.getTargets().getAt(0));

            // Source
            assertEquals(1, sourceNode.getOutEdges().size());
            assertEquals(sourceNode.getOutEdges().get(0).getSourceNode().getUUID(),
                         elkEdge.getSources().getAt(0));

            // Target
            assertEquals(2, targetNode.getInEdges().size());
            // Fist connection in the graph hierarchy points to root
            assertEquals(targetNode.getInEdges().get(0).getTargetNode().getUUID(),
                         elkEdge.getTargets().getAt(0));
            assertEquals(targetNode.getInEdges().get(0).getSourceNode().getUUID(),
                         elkRootNode.getId());
            // Connection between nodes
            assertEquals(targetNode.getInEdges().get(1).getTargetNode().getUUID(),
                         elkEdge.getTargets().getAt(0));
            assertEquals(targetNode.getInEdges().get(1).getSourceNode().getUUID(),
                         elkEdge.getSources().getAt(0));
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testBuildElkInputNodeSubset() {
        final Graph<?, ?> graph = getGraph();
        final Object parentLayout = mock(Object.class);
        final Object nestedLayout = mock(Object.class);
        final ELKNode elkRootNode = AutoLayout.buildElkInputNode(graph,
                                                                 context.getWorkflowRootNode(),
                                                                 parentLayout,
                                                                 nestedLayout,
                                                                 true); // Subset
        // Injected top level root
        assertEquals(parentLayout, elkRootNode.getLayoutOptions());
        assertEquals("root", elkRootNode.getId());

        // Container node
        assertEquals(1, elkRootNode.getChildren().length);

        final ELKNode childElkNode = elkRootNode.getChildren().getAt(0);
        final Node<View, Edge> node = graph.getNode(childElkNode.getId());

        assertEquals(5, childElkNode.getChildren().length, 0);
        assertEquals(nestedLayout, childElkNode.getLayoutOptions());
        assertEquals(950d ,node.getContent().getBounds().getWidth(), 0);
        assertEquals(950d, node.getContent().getBounds().getHeight(), 0);

        // Dummy edges
        assertEquals(0, elkRootNode.getEdges().length);
        assertEquals(4, childElkNode.getEdges().length);

        for (int j = 0; j < childElkNode.getEdges().length; j++) {
            final ELKEdge elkEdge = childElkNode.getEdges().getAt(j);
            final Node<View, Edge> sourceNode = graph.getNode(elkEdge.getSources().getAt(0));
            final Node<View, Edge> targetNode = graph.getNode(elkEdge.getTargets().getAt(0));

            // Source
            assertEquals(1, sourceNode.getOutEdges().size());
            assertEquals(sourceNode.getOutEdges().get(0).getSourceNode().getUUID(),
                         elkEdge.getSources().getAt(0));

            // Target
            assertEquals(2, targetNode.getInEdges().size());
            // Fist connection in the graph hierarchy points to root
            assertEquals(targetNode.getInEdges().get(0).getTargetNode().getUUID(),
                         elkEdge.getTargets().getAt(0));
            assertEquals(targetNode.getInEdges().get(0).getSourceNode().getUUID(),
                         childElkNode.getId());
            // Connection between nodes
            assertEquals(targetNode.getInEdges().get(1).getTargetNode().getUUID(),
                         elkEdge.getTargets().getAt(0));
            assertEquals(targetNode.getInEdges().get(1).getSourceNode().getUUID(),
                         elkEdge.getSources().getAt(0));
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testUpdateGraphNodeSizes() {
        final Graph<?, ?> graph = getGraph();
        final Object parentLayout = mock(Object.class);
        final Object nestedLayout = mock(Object.class);
        final ELKNode elkRootNode = AutoLayout.buildElkInputNode(graph,
                                                                 context.getWorkflowRootNode(),
                                                                 parentLayout,
                                                                 nestedLayout,
                                                                 false);
        simulateProcessedELKEResult(elkRootNode);

        AutoLayout.updateGraphNodeSizes(elkRootNode, graph);

        for (int i = 0; i < elkRootNode.getChildren().length; i++) {
            final ELKNode childElkNode = elkRootNode.getChildren().getAt(i);
            final Node<View, Edge> node = graph.getNode(childElkNode.getId());
            assertEquals(childElkNode.getWidth(), node.getContent().getBounds().getWidth(), 0);
            assertEquals(childElkNode.getHeight(), node.getContent().getBounds().getHeight(), 0);
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testUpdateNodesPosition() {
        final Graph<?, ?> graph = getGraph();
        final Object parentLayout = mock(Object.class);
        final Object nestedLayout = mock(Object.class);
        final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();
        final ELKNode elkRootNode = AutoLayout.buildElkInputNode(graph,
                                                                 context.getWorkflowRootNode(),
                                                                 parentLayout,
                                                                 nestedLayout,
                                                                 false);

        simulateProcessedELKEResult(elkRootNode);

        AutoLayout.updateGraphNodeSizes(elkRootNode, graph);
        AutoLayout.updateNodesPosition(elkRootNode, graph, layoutCommands);

        final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all =
                new CompositeCommand.Builder<>()
                        .addCommand(layoutCommands.build())
                        .build();

        all.execute(builderContext.buildExecutionContext());

        for (int i = 0; i < elkRootNode.getChildren().length; i++) {
            final ELKNode childElkNode = elkRootNode.getChildren().getAt(i);
            final Node<View, Edge> node = graph.getNode(childElkNode.getId());
            assertEquals(childElkNode.getX(), node.getContent().getBounds().getX(), 0);
            assertEquals(childElkNode.getY(), node.getContent().getBounds().getY(), 0);
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testCreateControlPoints() {
        final Graph<?, ?> graph = getGraph();
        final Object parentLayout = mock(Object.class);
        final Object nestedLayout = mock(Object.class);
        final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();
        final ELKNode elkRootNode = AutoLayout.buildElkInputNode(graph,
                                                                 context.getWorkflowRootNode(),
                                                                 parentLayout,
                                                                 nestedLayout,
                                                                 false);

        simulateProcessedELKEResult(elkRootNode);

        AutoLayout.updateGraphNodeSizes(elkRootNode, graph);
        AutoLayout.updateNodesPosition(elkRootNode, graph, layoutCommands);
        AutoLayout.createControlPoints(elkRootNode, layoutCommands);

        final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all =
                new CompositeCommand.Builder<>()
                        .addCommand(layoutCommands.build())
                        .build();

        all.execute(builderContext.buildExecutionContext());

        for (int i = 0; i < elkRootNode.getEdges().length; i++) {
            final ELKEdge elkEdge = elkRootNode.getEdges().getAt(i);
            final Edge edge = graphHandler.graphIndex.getEdge(elkEdge.getId());
            final HasControlPoints edgeControlPoints = (HasControlPoints) edge.getContent();

            assertEquals(elkEdge.getBendPoints().length, edgeControlPoints.getControlPoints().length);

            for (int j = 0; j < edgeControlPoints.getControlPoints().length; j++) {
                assertTrue(edgeControlPoints
                                   .getControlPoints()[j]
                                   .getLocation()
                                   .equals(elkEdge.getBendPoints().getAt(j)));
            }
        }
    }

    private static void simulateProcessedELKEResult(ELKNode elkRootNode) {
        // Node size and location
        for (int i = 0; i < elkRootNode.getChildren().length; i++) {
            ELKNode childElkNode = elkRootNode.getChildren().getAt(i);

            childElkNode.setWidth(childElkNode.getWidth() + 5d);
            childElkNode.setHeight(childElkNode.getHeight() + 5d);
            childElkNode.setX(300);
            childElkNode.setY(i * 100);
        }

        // BendPoints
        for (int i = 1; i <= elkRootNode.getEdges().length; i++) {
            final Point2D p0 = new Point2D(310, i * 50);
            final Point2D p1 = new Point2D(310, i * 55);
            final Point2D p2 = new Point2D(310, i * 50);
            final JsArray<Point2D> point2DJsArray = new JsArray<>();
            point2DJsArray.push(p0, p1, p2);
            elkRootNode.getEdges().getAt(i - 1).setBendPoints(point2DJsArray);
        }
    }
}
